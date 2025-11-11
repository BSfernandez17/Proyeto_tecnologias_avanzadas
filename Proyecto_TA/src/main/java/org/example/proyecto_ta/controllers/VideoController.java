package org.example.proyecto_ta.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.example.proyecto_ta.Services.VideoService;
import org.example.proyecto_ta.model.Camara;
import org.example.proyecto_ta.model.Video;
import org.springframework.stereotype.Controller;


@Controller
public class VideoController {

    private final VideoService videoServicio;

    public VideoController(VideoService videoServicio){
        this.videoServicio= videoServicio;
    }

    public void guardarVideo(Path rutaArchivo, Camara camara, String titulo) {
        try {
            byte[] videoBytes = Files.readAllBytes(rutaArchivo);
            String mime = Files.probeContentType(rutaArchivo);
            String nombreArchivo = rutaArchivo.getFileName().toString();

            Video video = new Video();
            video.setCamara(camara);
            video.setTitulo(titulo);
            video.setVideo(videoBytes);
            video.setTipoMime(mime);
            video.setNombreArchivo(nombreArchivo);

            videoServicio.guardarVideo(video);
        } catch (IOException e) {
        }
    }

}
