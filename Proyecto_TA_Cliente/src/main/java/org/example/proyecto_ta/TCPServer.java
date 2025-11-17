package org.example.proyecto_ta;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Servidor TCP que:
 *  - Escucha un flujo H.264 en el puerto 9000 (ingest)
 *  - Reenvía ese flujo a todos los clientes conectados al puerto 9001 (viewers)
 *  - Guarda el stream en un archivo local con timestamp.
 */
public class TCPServer {

    private static final int INGEST_PORT = 5000;
    private static final int VIEW_PORT = 6000;

    private final List<Socket> viewers = new CopyOnWriteArrayList<>();
    private volatile boolean running = true;

    public void start() {
        new Thread(this::startViewerAcceptLoop, "TCP-View-Accept").start();
        new Thread(this::startIngestLoop, "TCP-Ingest-Accept").start();
    }

    private void startViewerAcceptLoop() {
        try (ServerSocket viewServer = new ServerSocket(VIEW_PORT)) {
            System.out.println("Servidor de viewers esperando conexiones en puerto " + VIEW_PORT);
            while (running) {
                Socket s = viewServer.accept();
                System.out.println("Viewer conectado: " + s.getInetAddress());
                viewers.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startIngestLoop() {
        try (ServerSocket ingestServer = new ServerSocket(INGEST_PORT)) {
            System.out.println("Servidor de ingest esperando stream en puerto " + INGEST_PORT);
            while (running) {
                Socket ingestSocket = ingestServer.accept();
                System.out.println("Fuente de video conectada: " + ingestSocket.getInetAddress());
                new Thread(() -> handleIncomingUpload(ingestSocket), "Ingest-Handler").start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Accept both raw ingest streams and file-upload protocol (DataOutputStream writeUTF header + writeLong size + file bytes)
    private void handleIncomingUpload(Socket socket) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        try (java.io.InputStream in = socket.getInputStream()) {
            socket.setSoTimeout(2000);
            java.io.DataInputStream dis = new java.io.DataInputStream(in);
            try {
                // Try to read header (blocking with timeout)
                String header = dis.readUTF();
                long fileSize = dis.readLong();
                System.out.println("Recibiendo archivo desde cliente: header=" + header + ", size=" + fileSize);
                String[] parts = header != null ? header.split("\\|") : new String[]{"unknown","upload.h264"};
                String camId = parts.length > 0 ? parts[0] : "unknown";
                String filename = parts.length > 1 ? parts[1] : ("uploaded_" + camId + "_" + timestamp + ".h264");
                Path out = Path.of("received_" + filename);
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(out.toFile())) {
                    byte[] buf = new byte[8192];
                    long remaining = fileSize;
                    while (remaining > 0) {
                        int toRead = (int) Math.min(buf.length, remaining);
                        int r = dis.read(buf, 0, toRead);
                        if (r == -1) break;
                        fos.write(buf, 0, r);
                        remaining -= r;
                    }
                    fos.flush();
                }
                System.out.println("Archivo recibido y guardado en: " + out.toAbsolutePath());
                // Process received video: store metadata and extract frames + filters
                processReceivedVideo(out, camId);
            } catch (java.io.InterruptedIOException ste) {
                // No header arrived within timeout - treat as raw H.264 ingest stream
                System.out.println("No se recibió header UTF; asumiendo stream H.264 bruto...");
                Path output = Path.of("video_recibido_" + timestamp + ".h264");
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(output.toFile())) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                        broadcast(buffer, bytesRead);
                    }
                    System.out.println("Stream finalizado, guardado en: " + output.toAbsolutePath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private void processReceivedVideo(Path videoFile, String camId) {
        System.out.println("Procesando video: " + videoFile + " para camId=" + camId);
        try {
            Path baseDir = Path.of("processed_videos");
            if (!java.nio.file.Files.exists(baseDir)) java.nio.file.Files.createDirectories(baseDir);
            String baseName = videoFile.getFileName().toString().replaceAll("\\.h264$", "");
            Path workDir = baseDir.resolve(baseName + "_" + System.currentTimeMillis());
            java.nio.file.Files.createDirectories(workDir);

            // 1) Save metadata (simulated DB) in a simple JSON file
            Path meta = workDir.resolve("metadata.json");
            String json = "{\"cameraId\":\"" + camId + "\",\"receivedAt\":\"" + new Date().toString() + "\",\"file\":\"" + videoFile.toString() + "\"}";
            java.nio.file.Files.writeString(meta, json, java.nio.file.StandardOpenOption.CREATE);

            // 2) Extract frames using ffmpeg into frames/orig
            Path framesOrig = workDir.resolve("frames_orig");
            java.nio.file.Files.createDirectories(framesOrig);
            // ffmpeg -i input.h264 frames_orig/frame_%04d.png
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-y", "-i", videoFile.toAbsolutePath().toString(), framesOrig.resolve("frame_%04d.png").toAbsolutePath().toString());
            pb.redirectErrorStream(true);
            Process p = pb.start();
            try (java.io.BufferedReader r = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
                String line; while ((line = r.readLine()) != null) { System.out.println("[ffmpeg] " + line); }
            }
            p.waitFor();

            // 3) For each frame, apply filters and save in separate directories
            java.util.List<String> filters = java.util.Arrays.asList("grayscale", "reduced", "bright", "rot45", "rot90", "rot180");
            for (String f : filters) {
                Path dir = workDir.resolve("frames_" + f);
                java.nio.file.Files.createDirectories(dir);
            }

            java.util.List<java.nio.file.Path> frames = new java.util.ArrayList<>();
            try (java.util.stream.Stream<java.nio.file.Path> s = java.nio.file.Files.list(framesOrig)) {
                s.filter(pth -> pth.getFileName().toString().toLowerCase().endsWith(".png")).forEach(frames::add);
            }

            for (java.nio.file.Path frame : frames) {
                String fname = frame.getFileName().toString();
                // grayscale
                new ProcessBuilder("ffmpeg", "-y", "-i", frame.toAbsolutePath().toString(), "-vf", "format=gray", workDir.resolve("frames_grayscale").resolve(fname).toAbsolutePath().toString())
                        .inheritIO().start().waitFor();
                // reduced size (scale to 50% width)
                new ProcessBuilder("ffmpeg", "-y", "-i", frame.toAbsolutePath().toString(), "-vf", "scale=iw/2:ih/2", workDir.resolve("frames_reduced").resolve(fname).toAbsolutePath().toString())
                        .inheritIO().start().waitFor();
                // brightness +0.1
                new ProcessBuilder("ffmpeg", "-y", "-i", frame.toAbsolutePath().toString(), "-vf", "eq=brightness=0.1", workDir.resolve("frames_bright").resolve(fname).toAbsolutePath().toString())
                        .inheritIO().start().waitFor();
                // rotate 45 deg (in radians PI/4)
                new ProcessBuilder("ffmpeg", "-y", "-i", frame.toAbsolutePath().toString(), "-vf", "rotate=PI/4:ow=rotw(iw):oh=roth(ih)", workDir.resolve("frames_rot45").resolve(fname).toAbsolutePath().toString())
                        .inheritIO().start().waitFor();
                // rotate 90
                new ProcessBuilder("ffmpeg", "-y", "-i", frame.toAbsolutePath().toString(), "-vf", "transpose=1", workDir.resolve("frames_rot90").resolve(fname).toAbsolutePath().toString())
                        .inheritIO().start().waitFor();
                // rotate 180
                new ProcessBuilder("ffmpeg", "-y", "-i", frame.toAbsolutePath().toString(), "-vf", "transpose=2,transpose=2", workDir.resolve("frames_rot180").resolve(fname).toAbsolutePath().toString())
                        .inheritIO().start().waitFor();
            }

            System.out.println("Procesamiento finalizado para: " + videoFile + " en " + workDir.toAbsolutePath());

        } catch (Exception e) {
            System.err.println("Error procesando video " + videoFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void broadcast(byte[] data, int length) {
        for (Socket viewer : viewers) {
            try {
                viewer.getOutputStream().write(data, 0, length);
                viewer.getOutputStream().flush();
            } catch (IOException e) {
                System.out.println("Eliminando viewer desconectado: " + viewer.getInetAddress());
                try { viewer.close(); } catch (IOException ignored) {}
                viewers.remove(viewer);
            }
        }
    }

    public void stop() {
        running = false;
        for (Socket s : viewers) {
            try { s.close(); } catch (IOException ignored) {}
        }
        viewers.clear();
        System.out.println("Servidor detenido");
    }
}
