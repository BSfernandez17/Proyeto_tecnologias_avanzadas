package org.example.Model;

import org.example.Pool.IPoolableObject;

public class Camara implements IPoolableObject {

    private String id;           // ADB Device ID
    private Usuario usuario;
    private String nombre;
    private String serverHost;
    private Integer serverPort;

    public Camara() {
    }

    public Camara(String id, Usuario usuario, String nombre, String serverHost, Integer serverPort) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    // Builder
    public static class Builder {
        private String id;
        private Usuario usuario;
        private String nombre;
        private String serverHost;
        private Integer serverPort;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder usuario(Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public Builder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public Builder serverHost(String serverHost) {
            this.serverHost = serverHost;
            return this;
        }

        public Builder serverPort(Integer serverPort) {
            this.serverPort = serverPort;
            return this;
        }

        public Camara build() {
            return new Camara(id, usuario, nombre, serverHost, serverPort);
        }
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    // Poolable operation: reset lightweight state (no heavy network teardown here)
    @Override
    public void operation() {
        // Example reset logic; extend as needed
        this.usuario = null;
        this.nombre = null;
        this.serverHost = null;
        this.serverPort = null;
    }
}
