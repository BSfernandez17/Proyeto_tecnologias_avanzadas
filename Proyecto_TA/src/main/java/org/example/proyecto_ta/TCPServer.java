package org.example.proyecto_ta;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;

@Component
public class TCPServer {

    private static final int PORT = 9090;

    @PostConstruct
    public void start() {
        new Thread(this::runServer, "TCPServer-Thread").start();
    }

    private void runServer() {
        try {
            Path videosDir = Paths.get("videos");
            Path framesDir = Paths.get("frames");
            if (!Files.exists(videosDir)) Files.createDirectories(videosDir);
            if (!Files.exists(framesDir)) Files.createDirectories(framesDir);

            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor TCP escuchando en puerto " + PORT);

            while (true) {
                Socket client = serverSocket.accept();
                new Thread(() -> handleConnection(client, videosDir, framesDir)).start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void handleConnection(Socket socket, Path videosDir, Path framesDir) {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {

            // 1. Leer encabezado CAMERA|FILENAME
            String header = dis.readUTF();
            String[] parts = header.split("\\|");

            String cameraId = parts[0].trim();
            String filename = parts[1].trim();

            System.out.println("Recibiendo video de camera=" + cameraId + " -> " + filename);

            // 2. Crear directorio por cámara
            Path cameraDir = videosDir.resolve(cameraId);
            if (!Files.exists(cameraDir)) Files.createDirectories(cameraDir);

            // 3. Ruta final
            Path outputFile = cameraDir.resolve(filename);

            // 4. Leer tamaño y bytes
            long size = dis.readLong();

            try (OutputStream os = Files.newOutputStream(outputFile)) {
                byte[] buffer = new byte[8192];
                long read = 0;

                while (read < size) {
                    int n = dis.read(buffer);
                    if (n == -1) break;
                    os.write(buffer, 0, n);
                    read += n;
                }
            }

            System.out.println("Video guardado: " + outputFile);

            // Aquí luego agregamos: extraer frames y guardarlos en framesDir

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
