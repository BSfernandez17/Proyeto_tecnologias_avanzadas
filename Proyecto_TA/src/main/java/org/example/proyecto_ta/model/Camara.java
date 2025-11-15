package org.example.proyecto_ta.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="camara")
public class Camara {

    @Id
    @Column(length = 100)
    private String id;  // ADB Device ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(length = 100)
    private String serverHost;

    @Column
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
}
