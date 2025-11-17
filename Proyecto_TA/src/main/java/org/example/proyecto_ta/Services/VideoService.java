package org.example.proyecto_ta.Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.proyecto_ta.Repositories.VideoRepositorio;
import org.example.proyecto_ta.model.Camara;
import org.example.proyecto_ta.model.Video;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class VideoService {

    private final VideoRepositorio videoRepositorio;
    private final CamaraService camaraServicio;

    public VideoService(VideoRepositorio videoRepositorio, CamaraService camaraServicio){
        this.videoRepositorio = videoRepositorio;
        this.camaraServicio = camaraServicio;
    }

    // Guardar video de forma directa
    public void guardarVideo(Video video) {
        videoRepositorio.save(video);
    }

    // Procesar .h264 -> .mp4 y persistir en BD (asíncrono)
    @Async
    public void procesarYPersistirSegmento(Path h264Path, String cameraId, String titulo) {
        String baseName = h264Path.toString().replaceAll("\\.h264$", "");
        Path mp4Path = Path.of(baseName + ".mp4");
        try {
            boolean converted = convertH264ToMp4(h264Path, mp4Path);
            if (!converted) {
                System.out.println("VideoService: conversión fallida para " + h264Path);
                return;
            }

            Optional<Camara> camOpt = camaraServicio.obtenerCamaraPorId(cameraId);
            if (camOpt.isEmpty()) {
                System.out.println("VideoService: camara no encontrada para id=" + cameraId);
                return;
            }

            byte[] videoBytes = Files.readAllBytes(mp4Path);
            String mime = Files.probeContentType(mp4Path);
            String nombreArchivo = mp4Path.getFileName().toString();

            Video video = new Video();
            video.setCamara(camOpt.get());
            video.setTitulo(titulo);
            video.setVideo(videoBytes);
            video.setTipoMime(mime != null ? mime : "video/mp4");
            video.setNombreArchivo(nombreArchivo);

            videoRepositorio.save(video);
            System.out.println("VideoService: video guardado en BD: " + nombreArchivo);

        } catch (Exception e) {
            System.out.println("VideoService error al procesar/persistir: " + e.getMessage());
        } finally {
            try {
                Files.deleteIfExists(h264Path);
                Files.deleteIfExists(mp4Path);
            } catch (IOException ex) {
                System.out.println("VideoService: no se pudieron borrar archivos temporales: " + ex.getMessage());
            }
        }
    }

    private boolean convertH264ToMp4(Path h264Path, Path mp4Path) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-y",
                "-f", "h264",
                "-i", h264Path.toAbsolutePath().toString(),
                "-c", "copy",
                mp4Path.toAbsolutePath().toString()
        );
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = r.readLine()) != null) {
                System.out.println("ffmpeg: " + line);
            }
        }
        int exit = p.waitFor();
        return exit == 0 && Files.exists(mp4Path);
    }

    // Obtener videos por ID de cámara (ADB Device ID)
    public List<Video> obtenerVideosPorCamaraId(String idCamara){
        return videoRepositorio.findByCamara_Id(idCamara);
    }

    // Eliminar video por ID
    public void eliminarPorId(Integer id){
        videoRepositorio.deleteById(id);
    }

    // Obtener todos los videos de un usuario
    public List<Video> obtenerVideosPorUsuario(int usuarioId) {
        List<Camara> camaras = camaraServicio.obtenerCamarasPorUsuario(usuarioId);
        List<Video> videos = new ArrayList<>();
        for (Camara camara : camaras) {
            videos.addAll(obtenerVideosPorCamaraId(camara.getId()));
        }
        return videos;
    }

    // Obtener video por ID
    public Optional<Video> obtenerVideoPorId(Integer id) {
        return videoRepositorio.findById(id);
    }
}
