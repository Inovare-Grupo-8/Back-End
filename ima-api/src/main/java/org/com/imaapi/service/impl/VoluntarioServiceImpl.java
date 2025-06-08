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
            // Verifica se já existe um voluntário para este usuário
            Voluntario voluntarioExistente = voluntarioRepository.findByUsuario_IdUsuario(voluntarioInput.getFkUsuario());
            if (voluntarioExistente != null) {
                logger.info("Voluntário já existe para o usuário ID: {}, atualizando dados...", voluntarioInput.getFkUsuario());
                // Atualizar dados do voluntário existente
                voluntarioExistente.setFuncao(voluntarioInput.getFuncao());
                voluntarioRepository.save(voluntarioExistente);
                logger.info("Voluntário atualizado com sucesso: {}", voluntarioExistente);
                return;
            }

            // Se não existir, criar novo voluntário
            Voluntario voluntario = gerarObjetoVoluntario(voluntarioInput);
            voluntarioRepository.save(voluntario);
            logger.info("Voluntário cadastrado com sucesso: {}", voluntario);
        } catch (Exception erro) {
            logger.error("Erro ao cadastrar/atualizar voluntário: {}", erro.getMessage());
            throw erro;
        }
    }

    @Override
    public void atualizarVoluntario(VoluntarioInput voluntarioInput) {
        try {
            logger.info("Iniciando atualização de voluntário para usuário ID: {}", voluntarioInput.getFkUsuario());

            // Buscar voluntário existente
            Voluntario voluntarioExistente = voluntarioRepository.findByUsuario_IdUsuario(voluntarioInput.getFkUsuario());
            if (voluntarioExistente == null) {
                logger.error("Voluntário não encontrado para usuário ID: {}", voluntarioInput.getFkUsuario());
                throw new IllegalArgumentException("Voluntário não encontrado para atualização");
            }

            // Atualizar apenas os campos necessários
            voluntarioExistente.setFuncao(voluntarioInput.getFuncao());
            voluntarioRepository.save(voluntarioExistente);
            logger.info("Voluntário atualizado com sucesso: {}", voluntarioExistente);

        } catch (Exception erro) {
            logger.error("Erro ao atualizar voluntário: {}", erro.getMessage());
            throw erro;
        }
    }

    @Override
    public void excluirVoluntario(Integer id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("ID do voluntário não pode ser nulo");
            }
            if (voluntarioRepository.existsById(id)) {
                voluntarioRepository.deleteById(id);
                logger.info("Na tabela de voluntario com ID {} foi deletado com sucesso", id);
            } else {
                logger.warn("Voluntário com ID {} não encontrado, possível deleção em cascata anterior", id);
            }
        } catch (Exception erro) {
            logger.error("Erro ao excluir voluntário: {}", erro.getMessage());
            throw erro;
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