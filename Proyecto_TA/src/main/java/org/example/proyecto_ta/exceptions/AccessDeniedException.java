package org.example.proyecto_ta.exceptions;

/**
 * Excepción lanzada cuando se intenta acceder a una cámara de celular
 * a través de API REST o cualquier protocolo HTTP
 */
public class AccessDeniedException extends Exception {
    
    public AccessDeniedException(String mensaje) {
        super(mensaje);
    }

    public AccessDeniedException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
