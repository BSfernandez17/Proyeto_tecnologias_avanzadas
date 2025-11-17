package org.example.proyecto_ta.controllers;

import org.example.proyecto_ta.DTO.CamaraDTO;
import org.example.proyecto_ta.DTO.UsuarioDTO;
import org.example.proyecto_ta.Services.CamaraService;
import org.example.proyecto_ta.model.Camara;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/camaras")
public class CamaraController {

    private final CamaraService camaraServicio;

    public CamaraController(CamaraService camaraServicio) {
        this.camaraServicio = camaraServicio;
    }

    // Guardar nueva cámara
    @PostMapping("/guardarCamara")
    public ResponseEntity<?> guardarCamara(@RequestBody Camara camara) {
        // Validar que el usuario no sea nulo
        if (camara.getUsuario() == null ) {
            return ResponseEntity.badRequest().body("El usuario es obligatorio");
        }

        try {
            Camara nuevaCamara = camaraServicio.guardarCamara(camara);
            return ResponseEntity.status(HttpStatus.CREATED).body(new CamaraDTO(
                    nuevaCamara.getId(),
                    new UsuarioDTO(
                            nuevaCamara.getUsuario().getId(),
                            nuevaCamara.getUsuario().getNombre(),
                            nuevaCamara.getUsuario().getEmail(),
                            nuevaCamara.getUsuario().getRol(),
                            nuevaCamara.getUsuario().getStatus(),
                            nuevaCamara.getUsuario().getIp()
                    ),
                    nuevaCamara.getNombre(),
                    nuevaCamara.getServerHost(),
                    nuevaCamara.getServerPort()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la cámara: " + e.getMessage());
        }
    }

    // Obtener cámaras por usuario
    @GetMapping("/obtenerCamarasPorUsuario/{idUsuario}")
    public ResponseEntity<List<CamaraDTO>> obtenerCamarasPorUsuario(@PathVariable int idUsuario) {
        List<Camara> camaras = camaraServicio.obtenerCamarasPorUsuario(idUsuario);

        List<CamaraDTO> camarasDTO = camaras.stream()
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

        return ResponseEntity.ok(camarasDTO);
    }

    // Obtener cámara por usuario y ID de dispositivo (ADB ID)
    @GetMapping("/obtenerCamaraPorUsuarioYid")
    public ResponseEntity<?> obtenerCamaraPorUsuarioYid(@RequestParam int idUsuario, @RequestParam String idCamara) {
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
    public ResponseEntity<?> obtenerCamaraPorId(@PathVariable String id) {
        Optional<Camara> camaraOpt = camaraServicio.obtenerCamaraPorId(id);

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

    // Eliminar cámara por ID
    @DeleteMapping("/eliminarCamara/{id}")
    public ResponseEntity<?> eliminarCamara(@PathVariable String id) {
        try {
            camaraServicio.eliminarCamara(id);
            return ResponseEntity.ok("Cámara eliminada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la cámara: " + e.getMessage());
        }
    }
}