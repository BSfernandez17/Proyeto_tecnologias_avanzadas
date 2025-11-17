package org.example.proyecto_ta.Services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.proyecto_ta.DTO.CamaraDTO;
import org.example.proyecto_ta.DTO.UsuarioDTO;
import org.example.proyecto_ta.Repositories.CamaraRepositorio;
import org.example.proyecto_ta.model.Camara;
import org.springframework.stereotype.Service;

@Service
public class CamaraService {

    private final CamaraRepositorio camaraRepositorio;

    public CamaraService(CamaraRepositorio camaraRepositorio){
        this.camaraRepositorio = camaraRepositorio;
    }

    // Guardar o actualizar una cámara
    public Camara guardarCamara(Camara camara) {
        return camaraRepositorio.save(camara);
    }

    // Obtener todas las cámaras de un usuario
    public List<Camara> obtenerCamarasPorUsuario(int idUsuario){
        return camaraRepositorio.findByUsuario_Id(idUsuario);
    }

    // Obtener cámara por ID
    public Optional<Camara> obtenerCamaraPorId(String id) {
        return camaraRepositorio.findById(id);
    }

    // Listar todas las cámaras
    public List<Camara> listarTodas(){
        return camaraRepositorio.findAll();
    }

    // Eliminar cámara por ID
    public void eliminarCamara(String id) {
        camaraRepositorio.findById(id).ifPresent(c -> camaraRepositorio.deleteById(id));
    }

    // Obtener cámara por usuario e ID de dispositivo
    public Optional<Camara> obtenerCamaraPorUsuarioYid(int idUsuario, String idCamara) {
        return camaraRepositorio.findByUsuarioIdAndId(idUsuario, idCamara);
    }

    // Convertir todas las cámaras a DTOs
    public List<CamaraDTO> listarCamaras() {
        return listarTodas().stream()
                .map(c -> new CamaraDTO(
                        c.getId(),
                        new UsuarioDTO(
                                c.getUsuario().getId(),
                                c.getUsuario().getNombre(),
                                c.getUsuario().getEmail(),
                                c.getUsuario().getRol(),
                                c.getUsuario().getStatus(),
                                c.getUsuario().getIp()
                        ),
                        c.getNombre(),
                        c.getServerHost(),
                        c.getServerPort()
                ))
                .collect(Collectors.toList());
    }
}
