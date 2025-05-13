package org.com.imaapi.service;

import org.com.imaapi.model.usuario.Voluntario;
import org.com.imaapi.model.usuario.input.VoluntarioInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface VoluntarioService {
    public void cadastrarVoluntario(VoluntarioInput voluntarioInput);
    public void excluirVoluntario(Integer id);
}
