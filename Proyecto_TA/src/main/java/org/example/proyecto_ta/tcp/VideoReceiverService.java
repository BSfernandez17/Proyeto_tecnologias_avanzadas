package org.example.proyecto_ta.tcp;

import jakarta.annotation.PostConstruct;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

@Service
public class VideoReceiverService {


    private static final int PORT = 5000;
    private static final int SEGUNDOS_POR_ARCHIVO = 60;

    @PostConstruct
    public void startServer() {
        new Thread(this::listen).start();
    }

    private void listen() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor escuchando en puerto: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado.");

                new Thread(() -> handleStream(clientSocket)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleStream(Socket socket) {

        FFmpegFrameRecorder recorder = null;

        try {
            System.out.println("Iniciando recepción de video...");

            InputStream inputStream = socket.getInputStream();
            DataInputStream dis = new DataInputStream(inputStream);

            Java2DFrameConverter converter = new Java2DFrameConverter();

            // Crear primer archivo
            recorder = crearNuevoArchivo();
            long startTime = System.currentTimeMillis();

            while (true) {

                // Leer tamaño del frame enviado por el cliente
                int len;
                try {
                    len = dis.readInt();
                } catch (Exception ex) {
                    System.out.println("Fin de transmisión");
                    break;
                }

                if (len <= 0) continue;

                byte[] imgBytes = dis.readNBytes(len);

                BufferedImage img = ImageIO.read(new java.io.ByteArrayInputStream(imgBytes));
                Frame frame = converter.convert(img);

                long ahora = System.currentTimeMillis();

                // Cada 60s → cerrar archivo y crear otro nuevo
                if ((ahora - startTime) >= SEGUNDOS_POR_ARCHIVO * 1000) {
                    recorder.stop();
                    recorder.release();

                    recorder = crearNuevoArchivo();
                    startTime = ahora;

                    System.out.println("Nuevo archivo creado.");
                }

                recorder.record(frame);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (recorder != null) {
                    recorder.stop();
                    recorder.release();
                    System.out.println("Último archivo guardado.");
                }
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private FFmpegFrameRecorder crearNuevoArchivo() throws Exception {
        String filename = "video_" + System.currentTimeMillis() + ".mp4";

        FFmpegFrameRecorder recorder =
                new FFmpegFrameRecorder(filename, 640, 480);

        recorder.setFormat("mp4");
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFrameRate(25);
        recorder.start();

        System.out.println("Creando archivo: " + filename);
        return recorder;
    }


}
