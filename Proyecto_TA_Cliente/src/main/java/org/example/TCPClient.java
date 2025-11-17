package org.example;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class TCPClient {

    private final String serverHost;
    private final int serverPort;

    public TCPClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void sendVideo(String cameraId, Path videoFile) throws IOException {
        try (Socket socket = new Socket(serverHost, serverPort);
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            // Enviar encabezado CAMERA|FILENAME
            String header = cameraId + "|" + videoFile.getFileName().toString();
            dos.writeUTF(header);

            // Enviar tamaño del archivo
            long fileSize = Files.size(videoFile);
            dos.writeLong(fileSize);

            // Enviar contenido del archivo
            try (var inputStream = Files.newInputStream(videoFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("Video enviado: " + videoFile);
        }
    }

    // Streaming de un flujo H.264 ya abierto (por ejemplo desde adb exec-out)
    public void enviarStreamH264(InputStream h264Stream, String host, int puerto) {
        try (Socket socket = new Socket(host, puerto);
             OutputStream out = socket.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            System.out.println("Enviando stream H.264 al servidor...");
            while ((bytesRead = h264Stream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
            System.out.println("Stream H.264 finalizado");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Sobrecarga que usa los valores configurados en esta instancia (serverHost, serverPort)
    public void enviarStreamH264(InputStream h264Stream) {
        enviarStreamH264(h264Stream, this.serverHost, this.serverPort);
    }

    // Inicia captura de pantalla del dispositivo Android vía ADB y la envía como stream H.264.
    // deviceId puede ser null o vacío para usar el primer dispositivo conectado.
    // Variante completa (mantener compatibilidad): permite especificar host/puerto.
    public void iniciarYEnviarStreamDesdeAdb(String deviceId, String host, int puerto) {
        ProcessBuilder pb;
        if (deviceId != null && !deviceId.isBlank()) {
            pb = new ProcessBuilder("adb", "-s", deviceId, "exec-out", "screenrecord", "--output-format=h264", "-");
        } else {
            pb = new ProcessBuilder("adb", "exec-out", "screenrecord", "--output-format=h264", "-");
        }
        pb.redirectErrorStream(true);
        try {
            Process proc = pb.start();
            try (InputStream h264 = proc.getInputStream()) {
                enviarStreamH264(h264, host, puerto);
            }
            int exit = proc.waitFor();
            System.out.println("Proceso adb terminado con código: " + exit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Variante de conveniencia: usar los host/port configurados en la instancia
    public void iniciarYEnviarStreamDesdeAdb(String deviceId) {
        iniciarYEnviarStreamDesdeAdb(deviceId, this.serverHost, this.serverPort);
    }

    // Variante sin deviceId (usa primer dispositivo) y usa host/port de la instancia
    public void iniciarYEnviarStreamDesdeAdb() {
        iniciarYEnviarStreamDesdeAdb(null, this.serverHost, this.serverPort);
    }

    // --- Non-blocking start that returns a handle to stop the adb stream ---
    public AdbStreamHandle startAdbStream(String deviceId) throws IOException {
        ProcessBuilder pb;
        if (deviceId != null && !deviceId.isBlank()) {
            pb = new ProcessBuilder("adb", "-s", deviceId, "exec-out", "screenrecord", "--output-format=h264", "-");
        } else {
            pb = new ProcessBuilder("adb", "exec-out", "screenrecord", "--output-format=h264", "-");
        }
        pb.redirectErrorStream(true);
        Process proc = pb.start();

        Socket socket = new Socket(this.serverHost, this.serverPort);
        OutputStream out = socket.getOutputStream();

        Thread pump = new Thread(() -> {
            try (InputStream in = proc.getInputStream(); OutputStream o = out; Socket s = socket) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    o.write(buffer, 0, read);
                    o.flush();
                }
            } catch (IOException e) {
                // Stream ended or socket closed
            } finally {
                try { proc.destroy(); } catch (Exception ignored) {}
                try { socket.close(); } catch (Exception ignored) {}
            }
        }, "AdbStreamPump-" + deviceId);
        pump.setDaemon(true);
        pump.start();

        return new AdbStreamHandle(proc, socket, pump);
    }

    // Start segmented adb stream: writes incoming h264 bytes into rotating files of `segmentSeconds` length
    // and uploads each file to the server using sendVideo. Returns a handle to stop the overall process.
    public AdbSegmentHandle startSegmentedAdbStream(String deviceId, String cameraId, int segmentSeconds) throws IOException {
        ProcessBuilder pb;
        if (deviceId != null && !deviceId.isBlank()) {
            pb = new ProcessBuilder("adb", "-s", deviceId, "exec-out", "screenrecord", "--output-format=h264", "-");
        } else {
            pb = new ProcessBuilder("adb", "exec-out", "screenrecord", "--output-format=h264", "-");
        }
        pb.redirectErrorStream(true);
        Process proc = pb.start();

        java.util.concurrent.atomic.AtomicBoolean running = new java.util.concurrent.atomic.AtomicBoolean(true);

        Thread reader = new Thread(() -> {
            try (InputStream in = proc.getInputStream()) {
                byte[] buf = new byte[4096];
                long segmentMillis = segmentSeconds * 1000L;
                long segmentStart = System.currentTimeMillis();
                Socket sock = new Socket(this.serverHost, this.serverPort);
                OutputStream out = sock.getOutputStream();
                // send a short identification header so server can associate the segment
                try {
                    String header = "CAMERA:" + cameraId + "\n";
                    out.write(header.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    out.flush();
                } catch (Exception ignored) {}
                System.out.println("Segment streaming started to " + this.serverHost + ":" + this.serverPort);
                try {
                    int r;
                    while (running.get() && (r = in.read(buf)) != -1) {
                        out.write(buf, 0, r);
                        out.flush();
                        long now = System.currentTimeMillis();
                        if (now - segmentStart >= segmentMillis) {
                            // close current segment socket to let server finalize this segment
                            try { out.flush(); } catch (Exception ignored) {}
                            try { sock.close(); } catch (Exception ignored) {}
                            System.out.println("Segment sent, rotating socket (camera=" + cameraId + ")");
                            // start a new connection for next segment
                            sock = new Socket(this.serverHost, this.serverPort);
                            out = sock.getOutputStream();
                            segmentStart = now;
                        }
                    }
                } finally {
                    try { if (out != null) out.close(); } catch (Exception ignored) {}
                    try { if (sock != null && !sock.isClosed()) sock.close(); } catch (Exception ignored) {}
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try { if (proc.isAlive()) proc.destroy(); } catch (Exception ignored) {}
            }
        }, "AdbSegmentReader-" + deviceId);
        reader.setDaemon(true);
        reader.start();

        return new AdbSegmentHandle(proc, reader, running);
    }

    public static class AdbStreamHandle {
        private final Process process;
        private final Socket socket;
        private final Thread pumpThread;

        private AdbStreamHandle(Process process, Socket socket, Thread pumpThread) {
            this.process = process;
            this.socket = socket;
            this.pumpThread = pumpThread;
        }

        public boolean isAlive() {
            return process != null && process.isAlive();
        }

        public int waitFor() throws InterruptedException {
            return process.waitFor();
        }

        public void stop() {
            try {
                if (process.isAlive()) process.destroy();
            } catch (Exception ignored) {}
            try {
                socket.close();
            } catch (Exception ignored) {}
            try {
                pumpThread.join(2000);
            } catch (InterruptedException ignored) {}
        }
    }

    public static class AdbSegmentHandle {
        private final Process process;
        private final Thread readerThread;
        private final java.util.concurrent.atomic.AtomicBoolean running;

        private AdbSegmentHandle(Process process, Thread readerThread, java.util.concurrent.atomic.AtomicBoolean running) {
            this.process = process;
            this.readerThread = readerThread;
            this.running = running;
        }

        public boolean isAlive() {
            return process != null && process.isAlive();
        }

        public int waitFor() throws InterruptedException {
            if (process == null) return -1;
            return process.waitFor();
        }

        public void stop() {
            try { running.set(false); } catch (Exception ignored) {}
            try { if (process.isAlive()) process.destroy(); } catch (Exception ignored) {}
            try { readerThread.join(3000); } catch (InterruptedException ignored) {}
        }
    }
}