package org.com.imaapi.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.com.imaapi.model.Usuario.output.EnderecoOutput;
import org.com.imaapi.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    public EnderecoOutput buscaEndereco(String cep){

        String api = "https://viacep.com.br/ws/"+ URLEncoder.encode(cep)+"/json/";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api))
                .build();

        try {
            HttpResponse<String> response = HttpClient
                    .newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            return new Gson().fromJson(response.body(), EnderecoOutput.class);
        }catch (Exception erro){
            throw new RuntimeException("Não consegui obter o endereço com esse CEP");
        }
    }
}