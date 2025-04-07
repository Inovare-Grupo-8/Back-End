package org.com.imaapi.service.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

//public class CalendarioServiceImpl {
//    // Nome da Aplicação
//    private static final String APPLICATION_NAME = "Google Calendar API";
//    // Instância do JSON Factory (Para manipular arquivos JSON)
//    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
//    // Diretório dos tokens de autorização
//    private static final String TOKENS_DIRECTORY_PATH = "tokens";
//    // Escopos requeridos para a aplicação
//    private static final List<String> SCOPES = List.of(CalendarScopes.CALENDAR_READONLY, CalendarScopes.CALENDAR_APP_CREATED);
//    // Diretório das credenciais do cliente
//    private static final String CREDENTIALS_DIRECTORY_PATH = "/credentials.json";
//
//
//    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
//        // Carregar client secrets
//        // InputStream: Lê o arquivo de credenciais byte a byte
//        InputStream in = CalendarioServiceImpl.class.getResourceAsStream(CREDENTIALS_DIRECTORY_PATH);
//        if (in == null) {
//            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_DIRECTORY_PATH);
//        }
//        // InputStreamReader: Lê um InputStream em caracteres
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        //Construir fluxo e disparar requisição de autorização de usuário
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
//                .setAccessType("offline")
//                .build();
//        )
//
//    }
//}
