package org.example.Factories;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import javax.swing.JPanel;
import org.example.Controllers.ControladorAcciones;
import org.example.Controllers.CamaraControlador;
import org.example.Controllers.ControladorCamaraIP;
import org.example.Controllers.UsuarioControlador;
import org.example.Controllers.MapaControlador;
import org.example.Model.GestorSesion;
import org.example.Model.GestorAplicacion;
import org.example.View.Camaras;
import org.example.View.Mapa;
import org.example.View.InterfazInicio;
import org.example.View.InterfazPrincipal;
import org.example.View.registrarCamara;

/**
 *
 * @author crism
 */
public class VentanaFabrica{

    private static VentanaFabrica instancia;
    private GestorAplicacion gestorAplicacion;
    private ControladorAcciones controladorAcciones;
    private GestorSesion gestorSesion;
    private CamaraControlador camaraControlador;
    private ControladorCamaraIP controladorCamaraIP;
    private UsuarioControlador usuarioControlador;
    private MapaControlador mapaControlador;
    private Camaras camaras;
    private Mapa mapa;

    private VentanaFabrica() {}

    public static VentanaFabrica getInstancia() {
        if (instancia == null) {
            instancia = new VentanaFabrica();
        }
        return instancia;
    }

    public void setDependencias(GestorAplicacion gestorAplicacion, ControladorAcciones controladorAcciones, GestorSesion gestorSesion,CamaraControlador camaraControlador,ControladorCamaraIP controladorCamaraIP,UsuarioControlador usuarioControlador, MapaControlador mapaControlador ){
        this.controladorAcciones = controladorAcciones;
        this.gestorSesion = gestorSesion;
        this.camaraControlador = camaraControlador;
        this.controladorCamaraIP = controladorCamaraIP;
        this.usuarioControlador = usuarioControlador;
        this.gestorAplicacion = gestorAplicacion;
        this.mapaControlador = mapaControlador;
    }

    public CamaraControlador getCamaraControlador(){
        return camaraControlador;
    }

    public void cerrarVentanas(){
        camaraControlador.eliminarObservadores();
        camaras = null;
        mapa = null;

    }


    public JPanel crearVentana(String tipo){

        JPanel ventana = switch(tipo.toLowerCase()){

            case "inicio" -> new InterfazInicio(usuarioControlador, controladorAcciones);
            case "principal" -> new InterfazPrincipal(controladorAcciones, gestorSesion, gestorAplicacion);
            case "camaras" -> {
                if (camaras == null) {
                    camaras = new Camaras(controladorCamaraIP, camaraControlador, controladorAcciones);
                }
                yield camaras;
            }
            case "registrarcamara" -> new registrarCamara(camaraControlador,controladorAcciones);
            case "mapa" -> {
                if (mapa == null) {
                    mapa = new Mapa(camaraControlador, mapaControlador);
                }
                yield mapa;
            }
            default -> {
                System.err.println("Tipo de ventana desconocido: " + tipo);
                yield null;
            }

        };

        return ventana;
    }
}
