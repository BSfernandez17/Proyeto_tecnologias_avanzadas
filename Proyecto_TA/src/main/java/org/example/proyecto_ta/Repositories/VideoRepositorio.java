package org.example.proyecto_ta.Repositories;
import java.util.List;

import org.example.proyecto_ta.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VideoRepositorio extends JpaRepository<Video,Integer> {
    List<Video> findByCamara_Id(String id);

    Video findById(int id);
}