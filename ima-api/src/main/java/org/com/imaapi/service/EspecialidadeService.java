package org.com.imaapi.service;

import org.com.imaapi.model.especialidade.dto.EspecialidadeDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EspecialidadeService {
    ResponseEntity<EspecialidadeDto> criar(EspecialidadeDto especialidadeDto);

    ResponseEntity<EspecialidadeDto> atualizar(Integer id, EspecialidadeDto especialidadeDto);

    ResponseEntity<EspecialidadeDto> buscarPorId(Integer id);

    ResponseEntity<List<EspecialidadeDto>> listarTodas();

    ResponseEntity<Void> deletar(Integer id);
}
