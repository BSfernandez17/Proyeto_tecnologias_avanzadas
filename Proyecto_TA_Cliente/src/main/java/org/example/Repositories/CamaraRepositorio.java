package org.example.Repositories;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

import org.example.Model.Camara;
import java.util.List;

/**
 *
 * @author crism
 */
public interface CamaraRepositorio {
    Camara guardarCamara(Camara camara)throws Exception;
    List<Camara> obtenerCamarasPorUsuario(int id) throws Exception;
    Camara obtenerCamaraPorUsuarioYip(int id, String ip) throws Exception;
    boolean eliminarCamaraPorId(int id) throws Exception;
}
