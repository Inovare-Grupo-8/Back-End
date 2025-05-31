package org.com.imaapi.service.impl;

import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.model.usuario.Especialidade;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.repository.ConsultaRepository;
import org.com.imaapi.repository.EspecialidadeRepository;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.service.ConsultaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConsultaServiceImpl implements ConsultaService {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaServiceImpl.class);

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    @Override
    public ResponseEntity<ConsultaOutput> criarEvento(ConsultaInput consultaInput) {
        try {
            if (consultaInput.getIdEspecialidade() == null ||
                    consultaInput.getIdAssistido() == null ||
                    consultaInput.getIdVoluntario() == null) {
                logger.error("Campos obrigatórios não podem ser nulos.");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            logger.info("Registrando a criação do evento: {}", consultaInput);
            Consulta consultaSalvo = gerarObjetoEvento(consultaInput);
            Consulta salvarConsulta = consultaRepository.save(consultaSalvo);
            logger.info("Evento cadastrado com sucesso: {}", salvarConsulta);

            ConsultaOutput eventoResponse = gerarObjetoEventoOutput(salvarConsulta);
            return new ResponseEntity<>(eventoResponse, HttpStatus.CREATED);
        } catch (Exception erro) {
            logger.error("Erro ao cadastrar evento: {}", erro.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ConsultaOutput gerarObjetoEventoOutput(Consulta salvarConsulta) {
        return null;
    }

    private Consulta gerarObjetoEvento(ConsultaInput consultaInput) {
        return null;
    }

    @Override
    public ResponseEntity<ConsultaOutput> remarcar(Integer id, ConsultaInput consultaInput) {
        Optional<Consulta> consultaOptional = consultaRepository.findById(id);
        if (consultaOptional.isEmpty()) {
            logger.error("Consulta com ID {} não encontrada para remarcar.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Consulta consulta = consultaOptional.get();
            consulta.setHorario(consultaInput.getHorario());
            consulta.setLocal(consultaInput.getLocal());
            consulta.setObservacoes(consultaInput.getObservacoes());

            Consulta consultaAtualizada = consultaRepository.save(consulta);
            ConsultaOutput consultaOutput = gerarObjetoEventoOutput(consultaAtualizada);
            return ResponseEntity.ok(consultaOutput);
        } catch (Exception e) {
            logger.error("Erro ao remarcar consulta com ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }