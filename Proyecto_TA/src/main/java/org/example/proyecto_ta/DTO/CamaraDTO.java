package org.example.proyecto_ta.DTO;

public class CamaraDTO {

    private String id;           // ADB Device ID
    private UsuarioDTO usuario;
    private String nombre;
    private String serverHost;
    private Integer serverPort;

    public CamaraDTO() {
    }

    public CamaraDTO(String id, UsuarioDTO usuario, String nombre, String serverHost, Integer serverPort) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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
