package org.example.proyecto_ta.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.example.proyecto_ta.Services.ImagenService;
import org.example.proyecto_ta.model.Camara;
import org.example.proyecto_ta.model.Imagen;
import org.springframework.stereotype.Controller;

/**
 *
 * @author crism
 */
@Controller
public class ImagenController {

    private final ImagenService imagenServicio;

    public ImagenController(ImagenService imagenServicio){
        this.imagenServicio = imagenServicio;
    }

    public void guardarImagen(Path rutaArchivo, Camara camara, String titulo, String filtro ){
        try {
            byte[] videoBytes = Files.readAllBytes(rutaArchivo);
            String mime = Files.probeContentType(rutaArchivo);
            String nombreArchivo = rutaArchivo.getFileName().toString();

            Imagen imagen = new Imagen();
            imagen.setCamara(camara);
            imagen.setTitulo(titulo);
            imagen.setFiltro(filtro);
            imagen.setImagen(videoBytes);
            imagen.setTipoMime(mime);
            imagen.setNombreArchivo(nombreArchivo);

            imagenServicio.guardarImagen(imagen);
        } catch (IOException e) {
        }
    }
}
