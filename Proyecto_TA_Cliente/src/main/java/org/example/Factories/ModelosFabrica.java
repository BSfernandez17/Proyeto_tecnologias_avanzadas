package org.example.Factories;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import org.example.Model.Camara;
import org.example.Model.GestorSesion;

public class ModelosFabrica {

    private static ModelosFabrica instancia;

    private ModelosFabrica(){}

    public static ModelosFabrica getInstancia(){
        if(instancia==null){
            instancia = new ModelosFabrica();
        }
        return instancia;
    }

    public Camara.Builder crearCamara(){
        return new Camara.Builder();
    }

    public GestorSesion gestorSesion(){
        return GestorSesion.getInstancia();
    }

}
