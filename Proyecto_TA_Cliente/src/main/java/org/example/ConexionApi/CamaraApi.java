package org.example.ConexionApi;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import static org.example.Configuracion.Configuracion.ipServidor;

import org.example.Model.Camara;
import org.example.Repositories.CamaraRepositorio;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.lang.reflect.Type;


/**
 *
 * @author crism
 */
public class CamaraApi implements CamaraRepositorio {

    private static final String API_URL = "http://"+ipServidor+":8080/api/camaras/";
    private static final Gson gson = new Gson();
    private String token;

    public CamaraApi(String token) {
        this.token = token;
    }

    @Override
    public Camara guardarCamara(Camara camara) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        final String jsonCamara = camara.toJson();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "guardarCamara"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(BodyPublishers.ofString(jsonCamara))
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        System.out.println("Enviando datos de cámara: " + jsonCamara);
        System.out.println("Encabezado de autorización: Bearer " + token);
        System.out.println("Respuesta del servidor: " + response.body());
        System.out.println("Código de estado: " + response.statusCode());
        System.out.println("Cuerpo de la respuesta: " + response.body());

        if (response.statusCode() != 201) {
            System.err.println("Error al guardar cámara. Código de estado: " + response.statusCode());
            return null;
        }

        return gson.fromJson(response.body(), Camara.class);
    }

    @Override
    public List<Camara> obtenerCamarasPorUsuario(int id) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String url = API_URL + "obtenerCamarasPorUsuario/"+id;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
            .header("Accept", "application/json")
            .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String json = response.body();

            Type tipoLista = new TypeToken<List<Camara>>(){}.getType();
            List<Camara> camaras = gson.fromJson(json, tipoLista);

            return camaras;
        } else {
            return null;
        }

    }

    @Override
    public boolean eliminarCamaraPorId(int id) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String url = API_URL + "eliminarCamara/"+id;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200;
    }

    @Override
    public Camara obtenerCamaraPorUsuarioYip(int id, String ip) throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        String url = API_URL + "obtenerCamaraPorUsuarioYip?id="+id+"&ip="+ip;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();

            Camara usuario = gson.fromJson(responseBody, Camara.class);
            return usuario;
        } else {
            return null;
        }
    }


}
