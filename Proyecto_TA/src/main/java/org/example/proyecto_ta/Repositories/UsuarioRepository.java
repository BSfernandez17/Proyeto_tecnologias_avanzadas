package org.example.proyecto_ta.Repositories;

import org.example.proyecto_ta.model.Rol;
import org.example.proyecto_ta.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario,Integer> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findById(Integer id);
}
