package org.com.imaapi.service;

import org.com.imaapi.model.usuario.input.VoluntarioInput;

public interface VoluntarioService {
    public void cadastrarVoluntario(VoluntarioInput voluntarioInput);

    public void atualizarVoluntario(VoluntarioInput voluntarioInput);

    public void excluirVoluntario(Integer id);
}
