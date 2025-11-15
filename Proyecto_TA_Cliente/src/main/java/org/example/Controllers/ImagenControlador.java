package org.example.Controllers;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import static org.example.Configuracion.Configuracion.ipServidor;
import org.example.Model.Camara;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import marvin.image.MarvinImage;
import marvin.plugin.MarvinImagePlugin;
import marvin.util.MarvinPluginLoader;

/**
 *
 * @author crism
 */
public class ImagenControlador {

    public  BufferedImage aplicarEscalaGrises(BufferedImage imagenOriginal) {
        MarvinImage imagen = new MarvinImage(imagenOriginal);
        MarvinImagePlugin plugin = MarvinPluginLoader.loadImagePlugin("org.marvinproject.image.color.grayScale.jar");
        plugin.process(imagen.clone(), imagen);
        return imagen.getBufferedImageNoAlpha();
    }


    public  BufferedImage aplicarInvertirColor(BufferedImage imagenOriginal) {
        MarvinImage imagen = new MarvinImage(imagenOriginal);
        MarvinImagePlugin plugin = MarvinPluginLoader.loadImagePlugin("org.marvinproject.image.color.invert.jar");
        plugin.process(imagen.clone(), imagen);
        return imagen.getBufferedImageNoAlpha();
    }


    public  BufferedImage aplicarBrilloYContraste(BufferedImage imagenOriginal, int brillo, int contraste) {
        MarvinImage imagen = new MarvinImage(imagenOriginal);
        MarvinImagePlugin plugin = MarvinPluginLoader.loadImagePlugin("org.marvinproject.image.color.brightnessAndContrast.jar");
        plugin.setAttribute("brightness", brillo);
        plugin.setAttribute("contrast", contraste);
        plugin.process(imagen.clone(), imagen);
        return imagen.getBufferedImageNoAlpha();
    }

    private void crearDirectorioSiNoExiste(String directorio) {
        try {
            Path path = Paths.get(directorio);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            System.err.println("Error creando directorio: " + e.getMessage());
        }
    }

    public void guardarImagen(BufferedImage imagen, Camara camara, String filtro) {
        String direccionServidor = ipServidor;
        int puerto = 6000;

        try (Socket socket = new Socket(direccionServidor, puerto);
             OutputStream out = socket.getOutputStream();
             DataOutputStream dos = new DataOutputStream(out)){
            String encabezado = camara.getNombre()+":"+camara.getId()+":"+camara.getUsuario().getNombre()+":"+ camara.getUsuario().getId()+":"+filtro;

            byte[] encabezadoBytes = encabezado.getBytes("UTF-8");

            dos.writeInt(encabezadoBytes.length);
            dos.write(encabezadoBytes);

            if (imagen != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(imagen, "jpg", baos);
                baos.flush();
                byte[] imagenBytes = baos.toByteArray();

                String DIRECTORIO = "Imagenes/"+camara.getUsuario().getId()+"-"+camara.getUsuario().getNombre()+"/"+camara.getId()+"-"+camara.getNombre();
                crearDirectorioSiNoExiste(DIRECTORIO);

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String safeNombreUsuario = camara.getUsuario().getNombre().replaceAll("[^a-zA-Z0-9_\\-]", "_");
                String safeNombreCamara = camara.getNombre().replaceAll("[^a-zA-Z0-9_\\-]", "_");

                String nombreArchivo = String.format(
                        "imagen_%s_uid%s_%s_cam%s_%s.jpg",
                        timestamp, camara.getUsuario().getId(), safeNombreUsuario, camara.getId(), safeNombreCamara
                );

                File archivo = new File(DIRECTORIO,nombreArchivo);
                ImageIO.write(imagen, "jpg", archivo);
                dos.writeInt(imagenBytes.length);
                dos.write(imagenBytes);
            } else {
                System.out.println("Imagen es nula.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
