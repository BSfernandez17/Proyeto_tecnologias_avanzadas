package org.example.proyecto_ta.Services;
import org.example.proyecto_ta.Repositories.ImagenRepositorio;
import org.example.proyecto_ta.model.Camara;
import org.example.proyecto_ta.model.Imagen;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class ImagenService {

    private final ImagenRepositorio imagenRepositorio;
    private final CamaraService camaraServicio;

    public ImagenService(ImagenRepositorio imagenRepositorio, CamaraService camaraServicio){
        this.imagenRepositorio = imagenRepositorio;
        this.camaraServicio = camaraServicio;
    }

    public void guardarImagen(Imagen imagen){
        imagenRepositorio.save(imagen);
    }

    public List<Imagen> obtenerPorCamaraId(int id){
        return imagenRepositorio.findByCamara_Id(id);
    }

    public void eliminarPorId(int id){
        imagenRepositorio.deleteById(id);
    }

    public List<Imagen> obtenerImagenPorUsuario(int usuarioId) {
        List<Camara> camaras = camaraServicio.obtenerCamarasPorUsuario(usuarioId);
        List<Imagen> imagenes = new ArrayList<>();
        for (Camara camara : camaras) {
            imagenes.addAll(obtenerPorCamaraId(camara.getId()));
        }
        return imagenes;
    }

    public Imagen obtenerImagenPorId(int id) {
        return imagenRepositorio.findById(id);
    }
}
