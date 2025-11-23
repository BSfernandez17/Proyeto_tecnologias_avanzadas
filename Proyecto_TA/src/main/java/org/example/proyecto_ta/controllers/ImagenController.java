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

import org.example.proyecto_ta.Services.ImagenService;
import org.example.proyecto_ta.model.Camara;
import org.example.proyecto_ta.model.Imagen;
// ...existing code...

/**
 *
 * @author crism
 */
@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {
    private final ImagenService imagenServicio;

    public ImagenController(ImagenService imagenServicio){
        this.imagenServicio = imagenServicio;
    }

    /**
     * Endpoint para obtener imágenes por id de cámara
     */
    @GetMapping("/ImagenPorCamara/{idCamara}")
    public List<Imagen> obtenerImagenesPorCamara(@PathVariable String idCamara) {
        return imagenServicio.obtenerPorCamaraId(idCamara);
    }

    /**
     * Endpoint para guardar una imagen
     */
    @PostMapping("/guardarImagen")
    public void guardarImagen(@RequestBody Imagen imagen) {
        imagenServicio.guardarImagen(imagen);
    }

    // ...existing code (guardarImagen)...
}
