package org.example.proyecto_ta.controllers;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.example.proyecto_ta.Services.VideoService;
import org.example.proyecto_ta.model.Camara;
import org.example.proyecto_ta.model.Video;
// ...existing code...


@RestController
@RequestMapping("/api/videos")
public class VideoController {
    private final VideoService videoServicio;

    public VideoController(VideoService videoServicio){
        this.videoServicio= videoServicio;
    }

    /**
     * Endpoint para obtener videos por id de cámara
     */
    @GetMapping("/VideoPorCamara/{idCamara}")
    public List<Video> obtenerVideosPorCamara(@PathVariable String idCamara) {
        return videoServicio.obtenerVideosPorCamaraId(idCamara);
    }

    /**
     * Endpoint para guardar un video
     */
    @PostMapping("/guardarVideo")
    public void guardarVideo(@RequestBody Video video) {
        videoServicio.guardarVideo(video);
    }

    // ...existing code (guardarVideo)...

}
