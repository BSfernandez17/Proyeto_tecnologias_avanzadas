package org.example.proyecto_ta.DTO;

public class CamaraDTO {
    private int id;
    private UsuarioDTO usuario;
    private String nombre;
    private String ip;
    private String ubicacion;
    private Boolean estado;


    public CamaraDTO() {}


    public CamaraDTO(int id, UsuarioDTO usuario, String nombre, String ip, String ubicacion, Boolean estado) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.ip = ip;
        this.ubicacion = ubicacion;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }



}

