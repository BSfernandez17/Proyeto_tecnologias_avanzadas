package org.example.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import org.example.Camera;
import org.example.CameraManager;
import org.example.Model.Camara;
import org.example.Model.Usuario;
import org.example.Services.CamaraServicio;
import org.example.ConexionApi.CamaraApi;
import org.example.AppContext;

public class RegistrarCamaraPanel extends JPanel {

    private final JTextField txtName = new JTextField();
    private final JTextField txtDeviceId = new JTextField();
    private final JTextField txtServer = new JTextField("localhost:9000");

    String token = AppContext.getInstance().getToken(); // Obtener el token desde el contexto general

    public RegistrarCamaraPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        add(new JLabel("Nombre:"), c);
        c.gridx = 1;
        add(txtName, c);

        c.gridx = 0;
        c.gridy = 1;
        add(new JLabel("Device ID (adb):"), c);
        c.gridx = 1;
        add(txtDeviceId, c);

        c.gridx = 0;
        c.gridy = 2;
        add(new JLabel("Servidor (host:port):"), c);
        c.gridx = 1;
        add(txtServer, c);

        JButton btnProbar = new JButton("Probar ADB");
        JButton btnRegistrar = new JButton("Registrar Cámara");

        btnProbar.addActionListener(this::onProbar);
        btnRegistrar.addActionListener(this::onRegistrar);

        c.gridx = 0;
        c.gridy = 3;
        add(btnProbar, c);
        c.gridx = 1;
        add(btnRegistrar, c);
    }

    private void onProbar(ActionEvent e) {
        String device = txtDeviceId.getText().trim();
        if (device.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa deviceId.");
            return;
        }
        try {
            Process p = new ProcessBuilder("adb", "-s", device, "get-state").start();
            int r = p.waitFor();
            if (r == 0) JOptionPane.showMessageDialog(this, "Dispositivo conectado (estado OK).");
            else JOptionPane.showMessageDialog(this, "Fallo al conectar. Revisa adb devices.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void onRegistrar(ActionEvent e) {
        String name = txtName.getText().trim();
        String device = txtDeviceId.getText().trim();
        String server = txtServer.getText().trim();

        if (name.isEmpty() || device.isEmpty() || server.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.");
            return;
        }

        String[] parts = server.split(":");
        String host = parts[0];
        int port = 9000;
        if (parts.length > 1) {
            try {
                port = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Puerto inválido.");
                return;
            }
        }

        try {
            CamaraServicio camaraServicio = new CamaraServicio(new CamaraApi(token));

            // Obtener una instancia vacía desde el pool
            Camara pooled = camaraServicio.obtenerCamaraPooled();
            pooled.setId(device);
            pooled.setNombre(name);
            pooled.setServerHost(host);
            pooled.setServerPort(port);
            Usuario usuario = new Usuario(0, null, null, null, null, false, null); // Constructor con valores predeterminados
            usuario.setId(AppContext.getInstance().getUsuario().getId());
            pooled.setUsuarioId(usuario.getId()); // Enviar solo el ID del usuario

            // Guardar en el repositorio (API) — el servicio liberará el objeto al final
            Camara persisted = camaraServicio.guardarCamara(pooled);

            if (persisted == null) {
                JOptionPane.showMessageDialog(this, "Error: No se pudo guardar la cámara en el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Registrar una instancia runtime para la gestión local (CameraManager usa la clase runtime)
            Camera runtime = new Camera(persisted.getId(), persisted.getNombre(), persisted.getServerHost(), persisted.getServerPort());
            CameraManager.get().register(runtime);

            JOptionPane.showMessageDialog(this, "Cámara registrada: " + runtime);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar la cámara: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}