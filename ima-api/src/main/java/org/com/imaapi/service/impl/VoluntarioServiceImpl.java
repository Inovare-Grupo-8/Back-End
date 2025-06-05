package org.com.imaapi.service.impl;

import org.com.imaapi.model.usuario.Voluntario;
import org.com.imaapi.model.usuario.input.VoluntarioInput;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.repository.VoluntarioRepository;
import org.com.imaapi.service.VoluntarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class VoluntarioServiceImpl implements VoluntarioService {

    private static final Logger logger = LoggerFactory.getLogger(VoluntarioServiceImpl.class);

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void cadastrarVoluntario(VoluntarioInput voluntarioInput) {
        try {
            Voluntario voluntario = gerarObjetoVoluntario(voluntarioInput);
            voluntarioRepository.save(voluntario);
            logger.info("Voluntário cadastrado com sucesso: {}", voluntario);
        } catch (Exception erro) {
            logger.error("Erro ao cadastrar voluntário: {}", erro.getMessage());
            throw erro; 
        }
    }    
    
    public void excluirVoluntario(Integer id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("ID do voluntário não pode ser nulo");
            }
            if (!voluntarioRepository.existsById(id)) {
                throw new IllegalArgumentException("Voluntário não encontrado com o ID: " + id);
            }
            voluntarioRepository.deleteById(id);
            logger.info("Na tabela de voluntario com ID {} foi deletado com sucesso", id);
        } catch (IllegalArgumentException e) {
            logger.error("Erro de validação ao excluir voluntário: {}", e.getMessage());
            throw e;
        } catch (Exception erro) {
            logger.error("Erro ao excluir voluntário: {}", erro.getMessage());
            throw new RuntimeException("Erro ao excluir voluntário: " + erro.getMessage()); 
        }
    }

    private Voluntario gerarObjetoVoluntario(VoluntarioInput voluntarioInput) {
        Voluntario voluntario = new Voluntario();
        voluntario.setFuncao(voluntarioInput.getFuncao());
        voluntario.setDataCadastro(LocalDate.now());
        voluntario.setFkUsuario(voluntarioInput.getFkUsuario());
        voluntario.setIdVoluntario(voluntarioInput.getFkUsuario());
        voluntario.setUsuario(usuarioRepository.findById(voluntarioInput.getFkUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado")));
        return voluntario;
    }
}