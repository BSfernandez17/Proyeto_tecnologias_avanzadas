package org.example.proyecto_ta.Repositories;

import java.util.List;
import java.util.Optional;

import org.example.proyecto_ta.model.Camara;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CamaraRepositorio extends JpaRepository<Camara, String> {

    // Obtener todas las cámaras de un usuario
    List<Camara> findByUsuario_Id(int idUsuario);

    // Obtener cámara por usuario y ID de dispositivo
    Optional<Camara> findByUsuarioIdAndId(int idUsuario, String idCamara);
}
