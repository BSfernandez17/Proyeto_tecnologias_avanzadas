package org.example.Services;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import org.example.Model.Camara;
import org.example.Repositories.CamaraRepositorio;
import org.example.Pool.CamaraPool;
import org.example.Pool.IPoolableObject;
import java.util.List;

/**
 *
 * @author crism
 */
public class CamaraServicio {

    private final CamaraRepositorio camaraRepositorio;
    private final CamaraPool camaraPool;


    public CamaraServicio(CamaraRepositorio camaraRepositorio){
        this.camaraRepositorio = camaraRepositorio;
        // Pool configuration: min 2, max 10, timeout 2000ms
        this.camaraPool = new CamaraPool(2,10,2000);
    }

    // Obtain an empty Camara instance from pool to populate
    public Camara obtenerCamaraPooled() throws Exception {
        return (Camara) camaraPool.getObject();
    }

    public void liberarCamara(Camara camara){
        camaraPool.releaseObject(camara);
    }

    public Camara guardarCamara(Camara camara) throws Exception{
        Camara toSave = camara;
        if(toSave == null){
            toSave = obtenerCamaraPooled();
        }
        Camara persisted = camaraRepositorio.guardarCamara(toSave);
        // After persistence we can release (state is now server-side)
        liberarCamara(toSave);
        return persisted;
    }

    public List<Camara> obtenerCamarasPorUsuario(int id) throws Exception{
        return camaraRepositorio.obtenerCamarasPorUsuario(id);
    }

    public Camara obtenerCamaraPorUsuarioYip(int id, String ip) throws Exception{
        return camaraRepositorio.obtenerCamaraPorUsuarioYip(id , ip);
    }

    public boolean eliminarCamaraPorId(int id) throws Exception{
        return camaraRepositorio.eliminarCamaraPorId(id);
    }
}
