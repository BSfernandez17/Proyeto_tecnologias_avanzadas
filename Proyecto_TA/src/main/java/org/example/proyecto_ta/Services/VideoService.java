package org.example.proyecto_ta.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.example.proyecto_ta.Repositories.VideoRepositorio;
import org.example.proyecto_ta.model.Camara;
import org.example.proyecto_ta.model.Video;
import org.springframework.stereotype.Service;

@Service
public class VideoService {

    private final VideoRepositorio videoRepositorio;
    private final CamaraService camaraServicio;

    public VideoService(VideoRepositorio videoRepositorio, CamaraService camaraServicio){
        this.videoRepositorio = videoRepositorio;
        this.camaraServicio = camaraServicio;
    }

    // Guardar video
    public void guardarVideo(Video video) {
        videoRepositorio.save(video);
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
