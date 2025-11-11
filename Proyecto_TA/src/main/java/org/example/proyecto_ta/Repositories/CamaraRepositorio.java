package org.example.proyecto_ta.Repositories;

import java.util.List;
import java.util.Optional;

import org.example.proyecto_ta.model.Camara;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CamaraRepositorio extends JpaRepository<Camara, Integer>{
    List<Camara> findByUsuario_Id(int idUsuario);
    Optional<Camara> findByUsuarioIdAndIp(int idUsuario, String ip);

}