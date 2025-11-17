package org.example.proyecto_ta.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    String nombre;
    String email;
    String contrasena;
    String rol;
    Boolean status=false;
    String ip;
}
