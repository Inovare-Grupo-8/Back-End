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

@Service
public class ConsultaServiceImpl implements ConsultaService {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaServiceImpl.class);

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EspecialidadeRepository especialidadeRepository;

    public ResponseEntity<ConsultaOutput> criarEvento(ConsultaInput consultaInput) {
        try {
            if (consultaInput.getIdEspecialidade() == null) {
                logger.error("O campo idEspecialidade é obrigatório e não pode ser nulo.");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (consultaInput.getIdAssistido() == null) {
                logger.error("O campo idAssistido é obrigatório e não pode ser nulo.");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (consultaInput.getIdVoluntario() == null) {
                logger.error("O campo idVoluntario é obrigatório e não pode ser nulo.");
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

    private Consulta gerarObjetoEvento(ConsultaInput consultaInput) {
        Consulta consulta = new Consulta();
        consulta.setHorario(consultaInput.getHorario());
        consulta.setStatus(consultaInput.getStatus());
        consulta.setModalidade(consultaInput.getModalidade());
        consulta.setLocal(consultaInput.getLocal());
        consulta.setObservacoes(consultaInput.getObservacoes());

        // Busca a especialidade pelo ID
        Especialidade especialidade = especialidadeRepository.findById(consultaInput.getIdEspecialidade())
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada"));
        consulta.setEspecialidade(especialidade);

        // Busca o assistido pelo ID
        Usuario assistido = usuarioRepository.findById(consultaInput.getIdAssistido())
                .orElseThrow(() -> new RuntimeException("Usuário assistido não encontrado"));
        consulta.setAssistido(assistido);

        // Busca o voluntário pelo ID
        Usuario voluntario = usuarioRepository.findById(consultaInput.getIdVoluntario())
                .orElseThrow(() -> new RuntimeException("Usuário voluntário não encontrado"));
        consulta.setVoluntario(voluntario);

        return consulta;
    }

    private ConsultaOutput gerarObjetoEventoOutput(Consulta consulta) {
        ConsultaOutput consultaOutput = new ConsultaOutput();
        consultaOutput.setHorario(consulta.getHorario());
        consultaOutput.setStatus(consulta.getStatus());
        consultaOutput.setModalidade(consulta.getModalidade());
        consultaOutput.setLocal(consulta.getLocal());
        consultaOutput.setObservacoes(consulta.getObservacoes());
        consultaOutput.setEspecialidade(consulta.getEspecialidade());
        consultaOutput.setAssistido(consulta.getAssistido());
        consultaOutput.setVoluntario(consulta.getVoluntario());
        return consultaOutput;
    }
}