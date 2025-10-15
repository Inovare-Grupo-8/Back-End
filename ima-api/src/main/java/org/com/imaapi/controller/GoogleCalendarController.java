package org.com.imaapi.controller;

import jakarta.validation.Valid;
import org.com.imaapi.model.evento.EventoDTO;
import org.com.imaapi.service.GoogleCalendarService;
import org.com.imaapi.service.GoogleTokenService;
import org.com.imaapi.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Set;

@RestController
@RequestMapping("/calendar/eventos")
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;
    private final UsuarioService usuarioService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final GoogleTokenService googleTokenService;

    public GoogleCalendarController(GoogleCalendarService googleCalendarService,
                                    UsuarioService usuarioService,
                                    OAuth2AuthorizedClientService authorizedClientService,
                                    GoogleTokenService googleTokenService) {
        this.googleCalendarService = googleCalendarService;
        this.usuarioService = usuarioService;
        this.authorizedClientService = authorizedClientService;
        this.googleTokenService = googleTokenService;
    }

    @PostMapping
    public ResponseEntity<?> criarEvento(@RequestBody @Valid EventoDTO eventoDTO,
                                         Authentication authentication) throws GeneralSecurityException, IOException {
        String clientId = "google";
        Set<String> escoposNecessarios = Set.of(
                "https://www.googleapis.com/auth/calendar.app.created",
                "https://www.googleapis.com/auth/calendar.calendarlist");

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(clientId, authentication.getName());

        if (client == null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/oauth2/authorization/google")
                    .build();
        }

        if (!googleTokenService.contemEscoposNecessarios(escoposNecessarios, clientId, authentication)) {
            String urlIncremental = googleTokenService.construirUrlIncremental(escoposNecessarios, authentication, clientId);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", urlIncremental)
                    .build();
        }

        String email = authentication.getName();
        Integer idUsuario = usuarioService.buscarDadosPrimeiraFase(email).getIdUsuario();

        googleCalendarService.criarEventoParaUsuario(
                idUsuario,
                eventoDTO.getTitulo(),
                eventoDTO.getDescricao(),
                eventoDTO.getInicio(),
                eventoDTO.getFim(),
                authentication);

        return ResponseEntity.ok("Evento criado com sucesso!");
    }

    @PostMapping("/meet")
    public ResponseEntity<?> criarEventoComMeet(@Valid @RequestBody EventoDTO eventoDTO,
                                                Authentication authentication) throws GeneralSecurityException, IOException {
        String clientId = "google";
        Set<String> escoposNecessarios = Set.of(
                "https://www.googleapis.com/auth/calendar.app.created",
                "https://www.googleapis.com/auth/calendar.calendarlist");

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(clientId, authentication.getName());

        if (client == null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/oauth2/authorization/google")
                    .build();
        }

        if (!googleTokenService.contemEscoposNecessarios(escoposNecessarios, clientId, authentication)) {
            String urlIncremental = googleTokenService.construirUrlIncremental(escoposNecessarios, authentication, clientId);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", urlIncremental)
                    .build();
        }

        String email = authentication.getName();
        Integer idUsuario = usuarioService.buscarDadosPrimeiraFase(email).getIdUsuario();

        String linkMeet = googleCalendarService.criarEventoComMeetParaUsuario(
                idUsuario,
                eventoDTO.getTitulo(),
                eventoDTO.getDescricao(),
                eventoDTO.getInicio(),
                eventoDTO.getFim(),
                authentication);

        return ResponseEntity.ok(Collections.singletonMap("linkMeet", linkMeet));
    }
}
