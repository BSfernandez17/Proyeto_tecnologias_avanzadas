package org.example;

import org.example.Model.Usuario;

public class AppContext implements IContext {
    private static AppContext instance;
    private String token;
    private Usuario usuario;
    private AppContext() {}

    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    @Override
    public void setToken(String token) {
        if (token == null || !token.contains(".")) {
            throw new IllegalArgumentException("Token JWT inválido: " + token);
        }
        this.token = token;
    }
    @Override
    public void setUsuario(Usuario usuario){
        this.usuario = usuario;
    }

    @Override
    public String getToken() {
        return token;
    }
    @Override
    public Usuario getUsuario(){
        return usuario;
    }
}