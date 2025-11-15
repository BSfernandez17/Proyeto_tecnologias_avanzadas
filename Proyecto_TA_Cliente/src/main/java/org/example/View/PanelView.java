package org.example.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;

public class PanelView {

    public PanelView() {
        JFrame frame = new JFrame("HawkEye - Gestión de Cámaras");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(64, 64, 64)); // Gris oscuro

        JLabel titleLabel = new JLabel("Gestión de Cámaras", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(173, 216, 230)); // Azul claro
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JList<String> cameraList = new JList<>(getRegisteredCameras());
        cameraList.setFont(new Font("Arial", Font.PLAIN, 16));
        cameraList.setBackground(new Color(100, 149, 237)); // Azul intermedio
        cameraList.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(cameraList);

        JButton registerButton = new JButton("Registrar Cámara");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(173, 216, 230)); // Azul claro
        registerButton.setForeground(Color.BLACK);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(registerButton, BorderLayout.SOUTH);

        frame.add(panel);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Funcionalidad de registro pendiente", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        frame.setVisible(true);
    }

    private String[] getRegisteredCameras() {
        // TODO: Implement API call to fetch registered cameras
        return new String[]{"Cámara 1", "Cámara 2", "Cámara 3"}; // Placeholder data
    }
}