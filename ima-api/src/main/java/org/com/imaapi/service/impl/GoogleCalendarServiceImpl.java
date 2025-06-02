package org.com.imaapi.service.impl;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.repository.OauthTokenRepository;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.GoogleCalendarService;
import org.com.imaapi.service.OauthTokenService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
public class GoogleCalendarServiceImpl implements GoogleCalendarService {

    private static final String NOME_CALENDARIO = "IMA";
    private static final String TIMEZONE_CALENDARIO = "America/Sao_Paulo";
    private static final String NOME_APLICACAO = "IMA API";
    private static final String DESCRICAO_CALENDARIO = "Eventos IMA";
    private final OauthTokenService oauthTokenService;
    private final UsuarioRepository usuarioRepository;

    public GoogleCalendarServiceImpl(OauthTokenRepository oauthTokenRepository,
                                     OauthTokenService oauthTokenService, UsuarioRepository usuarioRepository) {
        this.oauthTokenService = oauthTokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public String criarCalendario(Calendar service) {
        try {
            com.google.api.services.calendar.model.Calendar novoCalendario =
                    new com.google.api.services.calendar.model.Calendar();

            novoCalendario.setSummary(NOME_CALENDARIO);
            novoCalendario.setTimeZone(TIMEZONE_CALENDARIO);
            novoCalendario.setDescription(DESCRICAO_CALENDARIO);

            com.google.api.services.calendar.model.Calendar calendarioCriado = service.calendars().insert(novoCalendario).execute();
            return calendarioCriado.getId();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao criar calendário", e);
        }
    }


    @Override
    public String buscarOuCriarCalendario(Calendar service) throws IOException {
        Optional<String> idCalendario = buscarCalendario(service);

        return idCalendario.orElseGet(() -> criarCalendario(service));
    }

    @Override
    public Optional<String> buscarCalendario(Calendar service) throws IOException {
        String paginaToken = null;
        do {
            CalendarList calendarList = service.calendarList().list().setPageToken(paginaToken).execute();
            for (CalendarListEntry calendario : calendarList.getItems()) {
                if (NOME_CALENDARIO.equals(calendario.getSummary())) {
                    return Optional.of(calendario.getId());
                }
            }
            paginaToken = calendarList.getNextPageToken();
        } while (paginaToken != null);

        return Optional.empty();
    }

    @Override
    public Event criarEvento(String titulo,
                             String descricao,
                             LocalDateTime inicio,
                             LocalDateTime fim) {

        ZoneId zoneId = ZoneId.of(TIMEZONE_CALENDARIO);

        Event event = new Event()
                .setSummary(titulo)
                .setDescription(descricao);

        EventDateTime start = new EventDateTime()
                .setDateTime(new DateTime(inicio.atZone(zoneId).toString()))
                .setTimeZone(TIMEZONE_CALENDARIO);

        EventDateTime end = new EventDateTime()
                .setDateTime(new DateTime(fim.atZone(zoneId).toString()))
                .setTimeZone(TIMEZONE_CALENDARIO);

        event.setStart(start);
        event.setEnd(end);

        return event;
    }

    @Override
    public void inserirEvento(Calendar service, String idCalendario, Event evento) throws IOException {
        service.events().insert(idCalendario, evento).execute();
    }

    @Override
    public void criarEventoParaUsuario(Integer idUsuario,
                                       String titulo,
                                       String descricao,
                                       LocalDateTime inicio,
                                       LocalDateTime fim) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        try {
            GoogleCredentials credentials = oauthTokenService.buscarCredenciaisValidasGoogle(usuario);

            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            Calendar service = new Calendar.Builder(
                    httpTransport,
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            )
                    .setApplicationName(NOME_APLICACAO)
                    .build();

            String idCalendario = buscarOuCriarCalendario(service);
            Event evento = criarEvento(titulo, descricao, inicio, fim);
            service.events().insert(idCalendario, evento).execute();

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Erro ao criar evento: " + e.getMessage(), e);
        }
    }

    @Override
    public Event criarEventoComMeet(String titulo, String descricao, LocalDateTime inicio, LocalDateTime fim) {
        Event evento = criarEvento(titulo, descricao, inicio, fim);

        CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest()
                .setRequestId(UUID.randomUUID().toString())
                .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet"));

        ConferenceData conferenceData = new ConferenceData()
                .setCreateRequest(createConferenceRequest);

        evento.setConferenceData(conferenceData);

        return evento;
    }

    @Override
    public Event inserirEventoComMeet(Calendar service, String idCalendario, Event evento) throws IOException {
        return service.events().insert(idCalendario, evento).setConferenceDataVersion(1).execute();
    }

    @Override
    public String criarEventoComMeetParaUsuario(Integer idUsuario, String titulo, String descricao, LocalDateTime inicio, LocalDateTime fim) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        GoogleCredentials credentials = oauthTokenService.buscarCredenciaisValidasGoogle(usuario);

        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            Calendar service = new Calendar.Builder(
                    httpTransport,
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            )
                    .setApplicationName(NOME_APLICACAO)
                    .build();

            String idCalendario = buscarOuCriarCalendario(service);

            Event evento = criarEventoComMeet(titulo, descricao, inicio, fim);

            Event eventoInserido = inserirEventoComMeet(service, idCalendario, evento);

            return extrairLinkMeet(eventoInserido);

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Erro ao criar evento com Meet: " + e.getMessage(), e);
        }
    }

    @Override
    public String extrairLinkMeet(Event evento) {
        if (evento.getConferenceData() != null &&
            evento.getConferenceData().getEntryPoints() != null) {

            for (EntryPoint entryPoint : evento.getConferenceData().getEntryPoints()) {
                if ("video".equals(entryPoint.getEntryPointType())) {
                    return entryPoint.getUri();
                }
            }
        }
        throw new RuntimeException("Link do meet não encontrado no evento: " + evento);
    }
}
