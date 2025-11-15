package org.example.proyecto_ta.Services;

import org.example.proyecto_ta.Repositories.ImagenRepositorio;
import org.example.proyecto_ta.model.Camara;
import org.example.proyecto_ta.model.Imagen;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImagenService {

    private final ImagenRepositorio imagenRepositorio;
    private final CamaraService camaraServicio;

    public ImagenService(ImagenRepositorio imagenRepositorio, CamaraService camaraServicio){
        this.imagenRepositorio = imagenRepositorio;
        this.camaraServicio = camaraServicio;
    }

    // Guardar imagen
    public void guardarImagen(Imagen imagen){
        imagenRepositorio.save(imagen);
    }

    // Obtener imágenes por ID de cámara (ADB ID)
    public List<Imagen> obtenerPorCamaraId(String idCamara){
        return imagenRepositorio.findByCamara_Id(idCamara);
    }

    // Eliminar imagen por ID
    public void eliminarPorId(Integer id){
        imagenRepositorio.deleteById(id);
    }

    // Obtener todas las imágenes de un usuario
    public List<Imagen> obtenerImagenPorUsuario(int usuarioId) {
        List<Camara> camaras = camaraServicio.obtenerCamarasPorUsuario(usuarioId);
        List<Imagen> imagenes = new ArrayList<>();
        for (Camara camara : camaras) {
            imagenes.addAll(obtenerPorCamaraId(camara.getId()));
        }
        return imagenes;
    }

    // Obtener imagen por ID
    public Optional<Imagen> obtenerImagenPorId(Integer id) {
        return imagenRepositorio.findById(id);
    }
}
