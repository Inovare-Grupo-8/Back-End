package org.com.imaapi.service;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Optional;

public interface GoogleCalendarService {
    String buscarOuCriarCalendario(Calendar service) throws IOException;
    Optional<String> buscarCalendario(Calendar service) throws IOException;
    String criarCalendario(Calendar service);
    Event criarEvento(String titulo, String descricao, LocalDateTime inicio, LocalDateTime fim) throws GeneralSecurityException, IOException;
    void inserirEvento(Calendar service, String idCalendario, Event evento) throws IOException;
    void criarEventoParaUsuario(String titulo, String descricao, LocalDateTime inicio, LocalDateTime fim, Authentication authentication, String state, String redirectUri) throws GeneralSecurityException, IOException;
    Event criarEventoComMeet(String titulo, String descricao, LocalDateTime inicio, LocalDateTime fim);
    Event inserirEventoComMeet(Calendar service, String idCalendario, Event evento) throws IOException;
    String criarEventoComMeetParaUsuario(String titulo, String descricao, LocalDateTime inicio, LocalDateTime fim, Authentication authentication, String state, String redirectUri) throws GeneralSecurityException, IOException;
    Calendar construirCalendarService(Authentication authentication, String state, String redirectUri) throws GeneralSecurityException, IOException;
    String extrairLinkMeet(Event evento);
}
