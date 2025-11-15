package org.example.proyecto_ta.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.proyecto_ta.DTO.CamaraDTO;
import org.example.proyecto_ta.DTO.UsuarioDTO;
import org.example.proyecto_ta.Services.CamaraService;
import org.example.proyecto_ta.exceptions.AccessDeniedException;
import org.example.proyecto_ta.model.Camara;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/camaras")
public class CamaraController {

    private final CamaraService camaraServicio;

    public CamaraController(CamaraService camaraServicio){
        this.camaraServicio = camaraServicio;
    }

    // Guardar nueva cámara
    @PostMapping("/guardarCamara")
    public ResponseEntity<Camara> guardarCamara(@RequestBody Camara camara){
        Camara nuevaCamara = camaraServicio.guardarCamara(camara);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCamara);
    }

    // Obtener cámaras por usuario
    @GetMapping("/obtenerCamarasPorUsuario/{idUsuario}")
    public List<CamaraDTO> obtenerCamarasPorUsuario(@PathVariable int idUsuario){
        List<Camara> camaras = camaraServicio.obtenerCamarasPorUsuario(idUsuario);

        return camaras.stream()
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

    // Obtener cámara por usuario y ID de dispositivo (ADB ID)
    @GetMapping("/obtenerCamaraPorUsuarioYid")
    public ResponseEntity<?> obtenerCamaraPorUsuarioYid(@RequestParam int idUsuario, @RequestParam String idCamara){
        Optional<Camara> camaraOpt = camaraServicio.obtenerCamaraPorUsuarioYid(idUsuario, idCamara);

        if (!camaraOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cámara no encontrada");
        }

        Camara camara = camaraOpt.get();

        return ResponseEntity.ok(new CamaraDTO(
                camara.getId(),
                new UsuarioDTO(
                        camara.getUsuario().getId(),
                        camara.getUsuario().getNombre(),
                        camara.getUsuario().getEmail(),
                        camara.getUsuario().getRol(),
                        camara.getUsuario().getStatus(),
                        camara.getUsuario().getIp()
                ),
                camara.getNombre(),
                camara.getServerHost(),
                camara.getServerPort()
        ));
    }

    // Obtener cámara por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCamaraPorId(@PathVariable String id){
        Optional<Camara> camaraOpt = camaraServicio.obtenerCamaraPorId(id);

        if (!camaraOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cámara no encontrada");
        }

        return ResponseEntity.ok(camaraOpt.get());
    }

    // Eliminar cámara por ID
    @DeleteMapping("/eliminarCamara/{id}")
    public ResponseEntity<?> eliminarCamara(@PathVariable String id){
        camaraServicio.eliminarCamara(id);
        return ResponseEntity.ok("Cámara eliminada correctamente");
    }
}
