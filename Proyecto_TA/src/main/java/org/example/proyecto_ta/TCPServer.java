package org.example.proyecto_ta;

import jakarta.annotation.PostConstruct;
import org.example.proyecto_ta.Services.VideoService;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;

@Component
public class TCPServer {

    private static final int PORT = 9000;

    private final VideoService videoService;

    public TCPServer(VideoService videoService){
        this.videoService = videoService;
    }

    @PostConstruct
    public void init() {
        new Thread(this::startServer, "TCP-Video-Server").start();
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor TCP esperando streams en puerto " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        try (InputStream rawIn = socket.getInputStream()) {
            // read possible header line terminated by '\n' (up to 1024 bytes)
            BufferedInputStream bin = new BufferedInputStream(rawIn);
            bin.mark(2048);
            ByteArrayOutputStream headerBuf = new ByteArrayOutputStream();
            int b;
            boolean hasHeader = false;
            while ((b = bin.read()) != -1) {
                headerBuf.write(b);
                if (b == '\n') { hasHeader = true; break; }
                if (headerBuf.size() > 1024) break;
            }

            String cameraId = null;
            if (hasHeader) {
                String headerLine = new String(headerBuf.toByteArray(), java.nio.charset.StandardCharsets.UTF_8).trim();
                if (headerLine.startsWith("CAMERA:")) {
                    cameraId = headerLine.substring("CAMERA:".length()).trim();
                } else {
                    // Not a recognized header: reset to start
                    bin.reset();
                }
            } else {
                // no header found, reset to start
                bin.reset();
            }

            String baseName = "video_recibido_" + (cameraId != null ? cameraId + "_" : "") + timestamp;
            String outName = baseName + ".h264";
            Path outPath = Paths.get(outName);
            try (FileOutputStream fos = new FileOutputStream(outPath.toFile())) {
                System.out.println("Recibiendo stream" + (cameraId != null ? " from camera=" + cameraId : "") + "... saving to " + outPath);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = bin.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                fos.flush();
            }

            System.out.println("Stream finalizado, guardado en: " + outPath.toAbsolutePath());

            // Encolar procesamiento asíncrono: conversión y persistencia en VideoService
            try {
                if (cameraId != null) {
                    videoService.procesarYPersistirSegmento(outPath, cameraId, "Segment " + timestamp);
                    System.out.println("Segmento enviado a VideoService para procesar: " + outPath);
                } else {
                    System.out.println("No se recibió cameraId en el header; no se persiste el video.");
                    Files.deleteIfExists(outPath);
                }
            } catch (Exception ex) {
                System.out.println("Error al encolar procesamiento del video: " + ex.getMessage());
                try { Files.deleteIfExists(outPath); } catch (IOException ignore) {}
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Conversion ahora manejada por VideoService (asíncrono)
}
