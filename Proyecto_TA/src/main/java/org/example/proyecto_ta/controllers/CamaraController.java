package org.example.proyecto_ta.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.proyecto_ta.DTO.CamaraDTO;
import org.example.proyecto_ta.DTO.UsuarioDTO;
import org.example.proyecto_ta.Services.CamaraService;
import org.example.proyecto_ta.model.Camara;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Controller
@RequestMapping("/api/camaras")
public class CamaraController {

    private final CamaraService camaraServicio;

    public CamaraController(CamaraService camaraServicio){
        this.camaraServicio = camaraServicio;
    }

    @PostMapping("/guardarCamara")
    public Camara guardarCamara(@RequestBody Camara camara){
        Camara nuevaCamara = camaraServicio.guardarCamara(camara);
        return nuevaCamara;
    }

    @GetMapping("/obtenerCamarasPorUsuario/{id}")
    public List<CamaraDTO> obtenerCamarasPorUsuario(@PathVariable int id){
        List<Camara> camaras = camaraServicio.obtenerCamarasPorUsuario(id);

        return camaras.stream()
                .map(c -> new CamaraDTO(c.getId(), new UsuarioDTO(c.getUsuario().getId(), c.getUsuario().getNombre(), c.getUsuario().getEmail(), c.getUsuario().getRol(),c.getUsuario().getStatus(),c.getUsuario().getIp()),
                        c.getNombre(), c.getIp(), c.getUbicacion(), c.getEstado()))
                .collect(Collectors.toList());
    }

    @GetMapping("/obtenerCamaraPorUsuarioYip")
    public CamaraDTO obtenerCamaraPorUsuarioYip(@RequestParam int id, @RequestParam String ip){

        Optional<Camara> camaraOpt = camaraServicio.obtenerCamaraPorUsuarioYip(id, ip);
        Camara camara = camaraOpt.get();

        return new CamaraDTO(camara.getId(), new UsuarioDTO(camara.getUsuario().getId(), camara.getUsuario().getNombre(), camara.getUsuario().getEmail(), camara.getUsuario().getRol(),camara.getUsuario().getStatus(),camara.getIp()),
                camara.getNombre(), camara.getIp(), camara.getUbicacion(), camara.getEstado());

    }


    public Camara obtenerCamaraPorId(@PathVariable int id){
        Optional<Camara> camaraOpt = camaraServicio.obtenerCamaraPorId(id);
        Camara camara = camaraOpt.get();
        return camara;
    }


    @DeleteMapping("/eliminarCamara/{id}")
    public void eliminarCamara(@PathVariable int id){
        camaraServicio.eliminarCamara(id);
    }



}
