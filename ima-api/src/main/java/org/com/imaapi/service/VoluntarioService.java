package org.com.imaapi.service;

import org.com.imaapi.model.Voluntario;
import org.com.imaapi.model.input.VoluntarioInput;
import org.com.imaapi.repository.VoluntarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class VoluntarioService {

    private static final Logger logger = LoggerFactory.getLogger(VoluntarioService.class);

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @PostMapping
    public ResponseEntity<Voluntario> cadastrarVoluntario(@RequestBody VoluntarioInput voluntarioInput) {
        try {
            Voluntario voluntario = new Voluntario();
            voluntario.setFkUsuario(voluntarioInput.getFkUsuario());
            voluntario.setFuncao(voluntarioInput.getFuncao());
            Voluntario salvarVoluntario = voluntarioRepository.save(voluntario);
            return new ResponseEntity<>(salvarVoluntario, HttpStatus.CREATED);
        } catch (Exception erro) {
            logger.error("Erro ao cadastrar voluntário: {}", erro.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}