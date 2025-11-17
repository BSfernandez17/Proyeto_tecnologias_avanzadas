package org.example.Configuracion;

public class SesionManager {

    private static String jwtToken;

    // Método para guardar el token JWT
    public static void guardarToken(String token) {
        jwtToken = token;
    }

    // Método para obtener el token JWT
    public static String obtenerToken() {
        return jwtToken;
    }

    // Método para limpiar el token JWT
    public static void limpiarToken() {
        jwtToken = null;
    }
}