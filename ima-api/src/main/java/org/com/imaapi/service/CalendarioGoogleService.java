package org.com.imaapi.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class CalendarioGoogleService {

    /* Nome da aplicação. */
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";

    /* Instância global da fábrica de JSON. */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /* Diretório para armazenar tokens de autorização para esta aplicação. */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /*
     * Instância global dos escopos necessários por este quickstart.
     * Se modificar esses escopos, exclua sua pasta de tokens salva anteriormente.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_JSON = System.getenv("CREDENTIALS_JSON");

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        String credentialsJson = CREDENTIALS_JSON;
        if (credentialsJson == null || credentialsJson.isEmpty()) {
            throw new FileNotFoundException("Environment variable CREDENTIALS_JSON not set or empty");
        }
        InputStream in = new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static Calendar getCalendarService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static List<Event> getUpcomingEvents(Calendar service, int maxResults) throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
                .setMaxResults(maxResults)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        return events.getItems();
    }

    public static void printUpcomingEvents(List<Event> events) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        if (events.isEmpty()) {
            System.out.println("Nenhum evento futuro encontrado.");
        } else {
            System.out.println("Próximos eventos:");
            for (Event event : events) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(start.getValue()),
                        ZoneId.of("America/Sao_Paulo"));
                System.out.printf("%s (%s)\n", event.getSummary(), dateTime.format(formatter));
            }
        }
    }
}