package org.example.Services;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import org.example.Model.Usuario;
import org.example.Repositories.UsuarioRepositorio;

/**
 *
 * @author crism
 */
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio){
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public Usuario obtenerUsuarioPorEmail(String email)throws Exception{
        return usuarioRepositorio.obtenerUsuarioPorEmail(email);
    }

}
