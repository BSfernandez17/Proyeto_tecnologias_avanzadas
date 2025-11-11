package org.example.proyecto_ta.Services;

import java.util.ArrayList;
import java.util.List;

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

    public void guardarVideo(Video video) {
        videoRepositorio.save(video);
    }

    public List<Video> obtenerVideosPorCamaraId(int id){
        return videoRepositorio.findByCamara_Id(id);
    }

    public void eliminarPorId(int id){
        videoRepositorio.deleteById(id);
    }

    public List<Video> obtenerVideosPorUsuario(int usuarioId) {
        List<Camara> camaras = camaraServicio.obtenerCamarasPorUsuario(usuarioId);
        List<Video> videos = new ArrayList<>();
        for (Camara camara : camaras) {
            videos.addAll(obtenerVideosPorCamaraId(camara.getId()));
        }
        return videos;
    }

    public Video obtenerVideoPorId(int id) {
        return videoRepositorio.findById(id);
    }
}
