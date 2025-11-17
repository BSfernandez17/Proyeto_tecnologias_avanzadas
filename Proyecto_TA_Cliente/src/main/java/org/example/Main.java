package org.example;

import org.example.View.LoginView;
import org.example.proyecto_ta.TCPServer;

public class Main {
    public static void main(String[] args) {
        // Iniciar servidor TCP para recibir y retransmitir stream H.264
        TCPServer server = new TCPServer();
        server.start();

        // Iniciar la vista de login
        new LoginView();
    }
}
