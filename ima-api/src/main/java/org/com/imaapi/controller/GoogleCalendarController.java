package org.com.imaapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.com.imaapi.exception.MissingScopeException;
import org.com.imaapi.model.evento.EventoDTO;
import org.com.imaapi.service.GoogleCalendarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/calendar/eventos")
public class GoogleCalendarController {
    private final GoogleCalendarService googleCalendarService;

    public GoogleCalendarController(GoogleCalendarService googleCalendarService) {
        this.googleCalendarService = googleCalendarService;
    }

    @PostMapping
    public ResponseEntity<?> criarEvento(@RequestBody @Valid EventoDTO eventoDTO,
                                         Authentication authentication,
                                         HttpServletRequest request) throws GeneralSecurityException, IOException {
        try{
            String state = "incremental-" + UUID.randomUUID();
            request.getSession().setAttribute("OAUTH2_STATE", state);

            String redirectUri = request.getRequestURL().toString()
                    .replace(request.getRequestURI(), "/oauth2/googlecallback");

            googleCalendarService.criarEventoParaUsuario(
                    eventoDTO.getTitulo(),
                    eventoDTO.getDescricao(),
                    eventoDTO.getInicio(),
                    eventoDTO.getFim(),
                    authentication,
                    state,
                    redirectUri
            );

            return ResponseEntity.ok("Evento criado com sucesso!");
        } catch (MissingScopeException e) {
            request.getSession().setAttribute("ORIGINAL_URL", request.getRequestURI());
            Map<String, String> resposta = new HashMap<>();
            resposta.put("redirectUrl", e.getIncrementalAuthUrl());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(resposta);
        }
    }

    @PostMapping("/meet")
    public ResponseEntity<?> criarEventoComMeet(@Valid @RequestBody EventoDTO eventoDTO,
                                                Authentication authentication,
                                                HttpServletRequest request) throws GeneralSecurityException, IOException {
        try {
            String state = "incremental-" + UUID.randomUUID();
            request.getSession().setAttribute("OAUTH2_STATE", state);

            String redirectUri = request.getRequestURL().toString()
                    .replace(request.getRequestURI(), "/oauth2/googlecallback");

            String linkMeet = googleCalendarService.criarEventoComMeetParaUsuario(
                    eventoDTO.getTitulo(),
                    eventoDTO.getDescricao(),
                    eventoDTO.getInicio(),
                    eventoDTO.getFim(),
                    authentication,
                    state,
                    redirectUri
            );

            return ResponseEntity.ok(Collections.singletonMap("linkMeet", linkMeet));
        } catch (MissingScopeException e) {
            request.getSession().setAttribute("ORIGINAL_URL", request.getRequestURI());
            Map<String, String> resposta = new HashMap<>();
            resposta.put("redirectUrl", e.getIncrementalAuthUrl());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(resposta);
        }
    }
}
