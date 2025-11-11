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

    public Camara guardarCamara(Camara camara) {
        return camaraRepositorio.save(camara);
    }

    public List<Camara> obtenerCamarasPorUsuario(int id){
        return camaraRepositorio.findByUsuario_Id(id);
    }
    public Optional<Camara> obtenerCamaraPorId(int id){
        return camaraRepositorio.findById(id);
    }

    public List<Camara> listarTodas(){
        return camaraRepositorio.findAll();
    }

    public void eliminarCamara(int id){
        camaraRepositorio.deleteById(id);
    }

    public Optional<Camara> obtenerCamaraPorUsuarioYip(int idUsuario, String ip){
        return camaraRepositorio.findByUsuarioIdAndIp(idUsuario, ip);
    }

    public List<CamaraDTO> listarCamaras(){
        List<Camara> camaras = listarTodas();

        return camaras.stream()
                .map(c -> new CamaraDTO(c.getId(), new UsuarioDTO(c.getUsuario().getId(), c.getUsuario().getNombre(), c.getUsuario().getEmail(), c.getUsuario().getRol(),c.getUsuario().getStatus(),c.getUsuario().getIp()),
                        c.getNombre(), c.getIp(), c.getUbicacion(), c.getEstado()))
                .collect(Collectors.toList());
    }
}
