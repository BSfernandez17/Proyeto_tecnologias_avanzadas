package org.example.ConexionApi;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import static org.example.Configuracion.Configuracion.ipServidor;
import org.example.Model.Usuario;
import org.example.Repositories.UsuarioRepositorio;
import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonObject;

/**
 *
 * @author alexi
 */
public class UsuarioApi implements UsuarioRepositorio {
    private static final String API_URL = "http://" + ipServidor + ":8080/auth/";
    private static final Gson gson = new Gson();
    // JWT token is stored in AppContext; no local static needed

    @Override
    public Usuario obtenerUsuarioPorEmail(String email) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String url = API_URL + "obtenerUsuarioPorEmail?email=" +
                java.net.URLEncoder.encode(email, "UTF-8");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + org.example.AppContext.getInstance().getToken()) // Incluir el token JWT
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();

            Usuario usuario = gson.fromJson(responseBody, Usuario.class);
            return usuario;
        } else {
            return null;
        }

    }

    public Usuario registrarUsuario(Usuario usuario) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Convertir el objeto Usuario a JSON
        String json = gson.toJson(usuario);

        // Log para verificar el cuerpo de la solicitud
        System.out.println("Cuerpo de la solicitud: " + json);

        // Crear la solicitud POST al backend
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL + "registro"))  // Asegúrate de que el endpoint coincida con el del backend
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        // Enviar la solicitud y registrar la respuesta
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Estado de la respuesta: " + response.statusCode());
        System.out.println("Cuerpo de la respuesta: " + response.body());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            // Si el backend respondió correctamente, convertir la respuesta a un objeto Usuario
            return gson.fromJson(response.body(), Usuario.class);
        } else {
            System.err.println("Error al registrar usuario: " + response.statusCode());
            return null;
        }
    }

    public String autenticarUsuario(String email, String contrasena) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Log para confirmar la ejecución del método
        System.out.println("Ejecutando el método autenticarUsuario");

        // Crear el cuerpo de la solicitud con las credenciales
        String json = gson.toJson(new Usuario(email, contrasena));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        // Enviar la solicitud y registrar la respuesta
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Estado de la respuesta: " + response.statusCode());
        System.out.println("Cuerpo de la respuesta: " + response.body());

        // Extraer el token del JSON de la respuesta
        String jwtToken = null;
        try {
            String responseBody = response.body();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            jwtToken = jsonResponse.get("token").getAsString();

            // Verificar el formato del token
            if (jwtToken.split("\\.").length != 3) {
                throw new IllegalArgumentException("El token recibido no tiene el formato JWT válido.");
            }

            // Store token in AppContext
            org.example.AppContext.getInstance().setToken(jwtToken);
            System.out.println("Token extraído y válido: " + jwtToken);
        } catch (Exception e) {
            System.err.println("Error al procesar la respuesta del servidor: " + e.getMessage());
            throw e;
        }

        // Retornar el token JWT si todo es exitoso
        return jwtToken;
    }

    public boolean registrarUsuario(String nombre, String email, String contrasena) {
        try {
            // Crear el JSON para el registro
            JsonObject registroJson = new JsonObject();
            registroJson.addProperty("nombre", nombre);
            registroJson.addProperty("email", email);
            registroJson.addProperty("contrasena", contrasena);

            // Configurar la solicitud HTTP
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(registroJson.toString()))
                .build();

            // Enviar la solicitud y obtener la respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Depuración: Imprimir el cuerpo de la respuesta
            System.out.println("Estado de la respuesta: " + response.statusCode());
            System.out.println("Cuerpo de la respuesta: " + response.body());

            // Verificar el estado de la respuesta y manejar errores
            if (response.statusCode() == 200) {
                System.out.println("Usuario registrado exitosamente.");
                return true;
            } else {
                System.out.println("Error al registrar usuario. Código de estado: " + response.statusCode());
                System.out.println("Cuerpo de la respuesta: " + response.body());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}