package org.example.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import org.example.Camera;
import org.example.CameraManager;

public class CameraListView {

    public CameraListView() {
        JFrame frame = new JFrame("HawkEye - Gestión de Cámaras");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(64, 64, 64)); // Gris oscuro

        JLabel titleLabel = new JLabel("Gestión de Cámaras", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(173, 216, 230)); // Azul claro
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Listado de cámaras
        DefaultListModel<Camera> cameraListModel = new DefaultListModel<>();
        JList<Camera> cameraList = new JList<>(cameraListModel);
        cameraList.setBackground(new Color(100, 149, 237)); // Azul intermedio
        cameraList.setForeground(Color.WHITE);
        cameraList.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(cameraList);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Botón para agregar cámaras
        JButton addCameraButton = new JButton("Registrar Cámara");
        addCameraButton.setFont(new Font("Arial", Font.BOLD, 16));
        addCameraButton.setBackground(new Color(100, 149, 237));
        addCameraButton.setForeground(Color.WHITE);
        addCameraButton.addActionListener((ActionEvent e) -> {
            SwingUtilities.invokeLater(() -> {
                JFrame registrarFrame = new JFrame("Registrar Cámara");
                registrarFrame.setSize(400, 300);
                registrarFrame.add(new RegistrarCamaraPanel());
                registrarFrame.setVisible(true);
            });
        });

        // Botón para refrescar cámaras
        JButton refreshButton = new JButton("Refrescar");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 16));
        refreshButton.setBackground(new Color(100, 149, 237));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener((ActionEvent e) -> {
            cameraListModel.clear();
            // Refresh from API first
            try {
                CameraManager.get().refreshFromApi();
            } catch (Exception ex) {
                // refreshFromApi handles exceptions internally, but log if needed
                ex.printStackTrace();
            }
            List<Camera> cameras = CameraManager.get().list();
            for (Camera camera : cameras) {
                cameraListModel.addElement(camera);
            }
        });

        // Botón para iniciar stream
        JButton startStreamButton = new JButton("Iniciar Stream");
        startStreamButton.setFont(new Font("Arial", Font.BOLD, 16));
        startStreamButton.setBackground(new Color(100, 149, 237));
        startStreamButton.setForeground(Color.WHITE);
        startStreamButton.addActionListener((ActionEvent e) -> {
            Camera selectedCamera = cameraList.getSelectedValue();
            if (selectedCamera != null) {
                boolean started = CameraManager.get().startCamera(selectedCamera.getId());
                if (started) {
                    JOptionPane.showMessageDialog(frame, "Stream iniciado para: " + selectedCamera.getName(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    // Aquí se puede agregar lógica para mostrar el stream
                } else {
                    JOptionPane.showMessageDialog(frame, "No se pudo iniciar el stream.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Selecciona una cámara para iniciar el stream.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Botón para detener stream
        JButton stopStreamButton = new JButton("Detener Stream");
        stopStreamButton.setFont(new Font("Arial", Font.BOLD, 16));
        stopStreamButton.setBackground(new Color(100, 149, 237));
        stopStreamButton.setForeground(Color.WHITE);
        stopStreamButton.addActionListener((ActionEvent e) -> {
            Camera selectedCamera = cameraList.getSelectedValue();
            if (selectedCamera != null) {
                boolean stopped = CameraManager.get().stopCamera(selectedCamera.getId());
                if (stopped) {
                    JOptionPane.showMessageDialog(frame, "Stream detenido para: " + selectedCamera.getName(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "No se pudo detener el stream (no estaba en ejecución).", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Selecciona una cámara para detener el stream.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(64, 64, 64));
        buttonPanel.add(addCameraButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(startStreamButton);
        buttonPanel.add(stopStreamButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Cargar cámaras registradas (obtener desde API)
        try {
            CameraManager.get().refreshFromApi();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        List<Camera> cameras = CameraManager.get().list();
        for (Camera camera : cameras) {
            cameraListModel.addElement(camera);
        }

        frame.add(panel);
        frame.setVisible(true);
    }
}