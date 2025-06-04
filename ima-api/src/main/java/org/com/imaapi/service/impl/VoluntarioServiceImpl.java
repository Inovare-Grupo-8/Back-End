package org.com.imaapi.service.impl;

import org.com.imaapi.model.usuario.Voluntario;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.usuario.input.VoluntarioInput;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.repository.VoluntarioRepository;
import org.com.imaapi.service.VoluntarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class VoluntarioServiceImpl implements VoluntarioService {

    private static final Logger logger = LoggerFactory.getLogger(VoluntarioServiceImpl.class);

    @Autowired
    private VoluntarioRepository voluntarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void cadastrarVoluntario(VoluntarioInput voluntarioInput) {
        try {
            logger.info("Iniciando cadastro de voluntário com função: {}", voluntarioInput.getFuncao());
            
            Usuario usuario = usuarioRepository.findById(voluntarioInput.getFkUsuario())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado para ID: " + voluntarioInput.getFkUsuario()));
            
            // Verifica se já existe um registro de voluntário para este usuário
            Optional<Voluntario> voluntarioExistente = voluntarioRepository.findByUsuario(usuario);
            if (voluntarioExistente.isPresent()) {
                logger.info("Atualizando registro de voluntário existente para usuário ID: {}", usuario.getIdUsuario());
                Voluntario voluntario = voluntarioExistente.get();
                voluntario.setFuncao(voluntarioInput.getFuncao());
                voluntarioRepository.save(voluntario);
            } else {
                logger.info("Criando novo registro de voluntário para usuário ID: {}", usuario.getIdUsuario());                Voluntario voluntario = new Voluntario();
                voluntario.setIdVoluntario(usuario.getIdUsuario()); // Set the same ID as user
                voluntario.setUsuario(usuario);
                voluntario.setFuncao(voluntarioInput.getFuncao());
                voluntario.setDataCadastro(LocalDate.now());
                voluntarioRepository.save(voluntario);
            }
            
            logger.info("Cadastro de voluntário concluído com sucesso para usuário ID: {}", usuario.getIdUsuario());
        } catch (Exception erro) {
            logger.error("Erro ao cadastrar voluntário: {}", erro.getMessage());
            throw erro;
        }
    }

    @Override
    public void excluirVoluntario(Integer id) {
        try {
            voluntarioRepository.deleteById(id);
            logger.info("Voluntário com ID {} foi excluído com sucesso", id);
        } catch (Exception erro) {
            logger.error("Erro ao excluir voluntário: {}", erro.getMessage());
            throw erro;
        }
    }
}