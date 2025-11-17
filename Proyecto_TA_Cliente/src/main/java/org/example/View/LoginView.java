package org.example.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.example.AppContext;
import org.example.ConexionApi.UsuarioApi;
import org.example.Model.Usuario;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LoginView {

    public LoginView() {
        JFrame frame = new JFrame("HawkEye - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(64, 64, 64)); // Gris oscuro

        JLabel titleLabel = new JLabel("HawkEye", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(173, 216, 230)); // Azul claro
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel subtitleLabel = new JLabel("¡Bienvenidos al mejor gestor de cámaras!", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(new Color(64, 64, 64));

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        JTextField emailField = new JTextField();

        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setForeground(Color.WHITE);
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Iniciar Sesión");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(100, 149, 237)); // Azul intermedio
        loginButton.setForeground(Color.WHITE);

        JButton registerButton = new JButton("Registrarse");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(100, 149, 237));
        registerButton.setForeground(Color.WHITE);

        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(loginButton);
        formPanel.add(registerButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subtitleLabel, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        frame.add(panel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                // Limpiar el token global antes de intentar autenticar
                UsuarioApi.setJwtToken(null);

                if (login(email, password)) {
                    frame.dispose();
                    SwingUtilities.invokeLater(() -> new CameraListView());
                } else {
                    JOptionPane.showMessageDialog(frame, "Credenciales inválidas", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = JOptionPane.showInputDialog(frame, "Ingrese su nombre:", "Registro", JOptionPane.PLAIN_MESSAGE);
                String email = JOptionPane.showInputDialog(frame, "Ingrese su email:", "Registro", JOptionPane.PLAIN_MESSAGE);
                String contrasena = JOptionPane.showInputDialog(frame, "Ingrese su contraseña:", "Registro", JOptionPane.PLAIN_MESSAGE);

                if (nombre != null && email != null && contrasena != null) {
                    UsuarioApi usuarioApi = new UsuarioApi();
                    boolean registroExitoso = usuarioApi.registrarUsuario(nombre, email, contrasena);

                    if (registroExitoso) {
                        JOptionPane.showMessageDialog(frame, "Registro exitoso. Ahora puede iniciar sesión.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Error al registrar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        frame.setVisible(true);
    }

   private boolean login(String email, String password) {
    try {
        UsuarioApi usuarioApi = new UsuarioApi();
        String response = usuarioApi.autenticarUsuario(email, password);
        
        if (response == null) {
            JOptionPane.showMessageDialog(null, "No se recibió respuesta del servidor.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String[] parts = response.split("\\.");
        if (parts.length != 3) {
            JOptionPane.showMessageDialog(null, "Token inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            System.out.println("Payload decodificado: " + payload); // Debug
            JsonObject payloadJson = JsonParser.parseString(payload).getAsJsonObject();
           

            JsonObject tokenData = JsonParser.parseString(payload).getAsJsonObject();
            String status = tokenData.has("status") ? tokenData.get("status").getAsString() : null;

            // Obtener el campo 'id' del payload decodificado
            int idUsuario = payloadJson.has("id") ? payloadJson.get("id").getAsInt() : -1;
            System.out.println("ID del usuario: " + idUsuario); // Debug
            Usuario usuario = new Usuario(idUsuario, null, null, null, null, false, null); // Constructor con valores predeterminados
            AppContext.getInstance().setUsuario(usuario);
            if ("true".equalsIgnoreCase(status)) {
                UsuarioApi.setJwtToken(response);
                AppContext.getInstance().setToken(response); // Actualizar el token en AppContext
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Acceso no autorizado por el administrador.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (IllegalArgumentException | com.google.gson.JsonSyntaxException ex) {
            JOptionPane.showMessageDialog(null, "Error al decodificar el token.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al autenticar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        return false;
    }
}

}