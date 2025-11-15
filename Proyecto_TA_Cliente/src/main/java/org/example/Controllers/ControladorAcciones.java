package org.example.Controllers;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import org.example.Factories.DialogosFabrica;
import org.example.Factories.VentanaFabrica;
import org.example.Model.Camara;
import org.example.View.FramePrincipal;
import org.example.View.InterfazPrincipal;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 *
 * @author crism
 */
public class ControladorAcciones {

    private final VentanaFabrica ventanaFabrica;
    private final FramePrincipal framePrincipal;
    private final DialogosFabrica dialogosFabrica;
    private JPanel panelContenido;

    public ControladorAcciones(VentanaFabrica ventanaFabrica, FramePrincipal framePrincipal, DialogosFabrica dialogosFabrica){
        this.ventanaFabrica = ventanaFabrica;
        this.framePrincipal = framePrincipal;
        this.dialogosFabrica = dialogosFabrica;
    }

    public void setPanelContenido(JPanel panelContenido){
        this.panelContenido = panelContenido;
    }

    public void abrirVentana(String tipo){
        JPanel panel = ventanaFabrica.crearVentana(tipo);

        if(panel instanceof InterfazPrincipal interfazPrincipal){
            ventanaFabrica.crearVentana("camaras");
            ventanaFabrica.crearVentana("mapa");
        }
        if(panel != null){
            framePrincipal.cambiarPanel(panel);
        }
    }

    public void mostrarPanelPrincipal(String tipo){
        JPanel panelNuevo = ventanaFabrica.crearVentana(tipo);
        panelContenido.removeAll();
        panelContenido.add(panelNuevo, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    public JPanel obtenerPanel(String tipo){
        JPanel panel = ventanaFabrica.crearVentana(tipo);
        return panel;
    }

    public void abrirDialogo(String tipo, String mensaje){
        JDialog dialogo = dialogosFabrica.crearDialogoTexto(tipo, framePrincipal, true, mensaje);

        if(dialogo != null){
            dialogo.setVisible(true);
        }
    }

    public void abrirDialogoFiltro(BufferedImage imagen, Camara camara){
        JDialog dialogo = dialogosFabrica.crearDialogoFiltro(framePrincipal,imagen, camara);

        if(dialogo != null){
            dialogo.setVisible(true);
        }
    }

}
