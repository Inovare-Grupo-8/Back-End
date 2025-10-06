package org.com.imaapi.controller;

import jakarta.validation.Valid;
import org.com.imaapi.model.evento.EventoDTO;
import org.com.imaapi.service.impl.GoogleCalendarServiceImpl;
import org.com.imaapi.service.impl.UsuarioServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@RestController
@RequestMapping("/calendar/eventos")
public class GoogleCalendarController {
    private final GoogleCalendarServiceImpl googleCalendarService;
    private final UsuarioServiceImpl usuarioService;

    public GoogleCalendarController(GoogleCalendarServiceImpl googleCalendarService,
                                    UsuarioServiceImpl usuarioService) {
        this.googleCalendarService = googleCalendarService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<?> criarEvento(@RequestBody @Valid EventoDTO eventoDTO,
                                         Authentication authentication) throws GeneralSecurityException, IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User usuarioOauth = oauthToken.getPrincipal();
        String email = usuarioOauth.getAttribute("email");

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
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User usuarioOauth = oauthToken.getPrincipal();
        String email = usuarioOauth.getAttribute("email");


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
