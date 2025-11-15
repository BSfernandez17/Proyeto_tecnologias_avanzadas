package org.example.Controllers;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import org.example.Factories.ModelosFabrica;
import org.example.ConexionApi.UsuarioApi;
import org.example.Services.UsuarioServicio;
import org.example.Services.CamaraServicio;

/**
 *
 * @author crism
 */
public class UsuarioControlador {
    private final ModelosFabrica modelosFabrica;
    private final UsuarioServicio usuarioServicio;
    private final CamaraServicio camaraServicio;

    public UsuarioControlador(UsuarioServicio usuarioServicio, ModelosFabrica modelosFabrica, CamaraServicio camaraServicio) {
        this.usuarioServicio = usuarioServicio;
        this.modelosFabrica = modelosFabrica;
        this.camaraServicio = camaraServicio;
    }


    public String iniciarSesion(String email, String contrasena){
        try {
            UsuarioApi usuarioApi = new UsuarioApi();
            String token = usuarioApi.autenticarUsuario(email, contrasena);

            if (token != null && !token.isEmpty()) {
                modelosFabrica.gestorSesion().guardarToken(token);
                return "sesion iniciada correctamente"; // Aquí puedes ajustar el rol según el token o la lógica del backend
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }



}
