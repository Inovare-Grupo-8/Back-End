package org.com.imaapi.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.model.consulta.MotivoCancelamento;
import org.com.imaapi.model.consulta.RemarcarConsulta;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.model.usuario.Especialidade;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.enums.StatusConsulta;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public ResponseEntity<ConsultaOutput> detalhar(Integer id) {
        return ConsultaService.super.detalhar(id);
    }

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
        ConsultaOutput output = new ConsultaOutput();
        output.setHorario(salvarConsulta.getHorario());
        output.setStatus(salvarConsulta.getStatus());
        output.setModalidade(salvarConsulta.getModalidade());
        output.setLocal(salvarConsulta.getLocal());
        output.setObservacoes(salvarConsulta.getObservacoes());
        output.setEspecialidade(salvarConsulta.getEspecialidade());
        output.setAssistido(salvarConsulta.getAssistido());
        output.setVoluntario(salvarConsulta.getVoluntario());
        return output;
    }

    private Consulta gerarObjetoEvento(ConsultaInput input) {
        Usuario voluntario = usuarioRepository.findById(input.getIdVoluntario())
                .orElseThrow(() -> new IllegalArgumentException("Voluntário não encontrado"));

        Usuario assistido = usuarioRepository.findById(input.getIdAssistido())
                .orElseThrow(() -> new IllegalArgumentException("Assistido não encontrado"));

        Especialidade especialidade = especialidadeRepository.findById(input.getIdEspecialidade())
                .orElseThrow(() -> new IllegalArgumentException("Especialidade não encontrada"));

        Consulta consulta = new Consulta();
        consulta.setVoluntario(voluntario);
        consulta.setAssistido(assistido);
        consulta.setEspecialidade(especialidade);
        consulta.setHorario(input.getHorario());
        consulta.setLocal(input.getLocal());
        consulta.setObservacoes(input.getObservacoes());
        consulta.setStatus(StatusConsulta.AGENDADA); // Define um status inicial, por exemplo

        return consulta;
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
    }

    @Override
    public ResponseEntity<ConsultaOutput> cancelar(Integer id) {
        Optional<Consulta> consultaOptional = consultaRepository.findById(id);
        if (consultaOptional.isEmpty()) {
            logger.error("Consulta com ID {} não encontrada para cancelar.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Consulta consulta = consultaOptional.get();
            consulta.setStatus(StatusConsulta.CANCELADA);
            Consulta consultaAtualizada = consultaRepository.save(consulta);

            ConsultaOutput output = gerarObjetoEventoOutput(consultaAtualizada);
            return ResponseEntity.ok(output);
        } catch (Exception e) {
            logger.error("Erro ao cancelar consulta com ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ConsultaOutput> buscarDetalhes(Integer id) {
        Optional<Consulta> consultaOptional = consultaRepository.findById(id);
        if (consultaOptional.isEmpty()) {
            logger.error("Consulta com ID {} não encontrada para detalhar.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ConsultaOutput output = gerarObjetoEventoOutput(consultaOptional.get());
        return ResponseEntity.ok(output);
    }
    @Override
    public List<ConsultaOutput> listarHistoricoPorAssistido(Integer idAssistido) {
        List<Consulta> consultas = consultaRepository.findByAssistidoId(idAssistido);
        return consultas.stream()
                .map(this::gerarObjetoEventoOutput)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<Void> registrarMotivoCancelamento(Integer id, MotivoCancelamento motivoCancelamentoInput) {
        Optional<Consulta> consultaOptional = consultaRepository.findById(id);
        if (consultaOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Consulta consulta = consultaOptional.get();
        consulta.setMotivoCancelamento(motivoCancelamentoInput.getMotivo());
        consulta.setStatus(StatusConsulta.CANCELADA);
        consultaRepository.save(consulta);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    @Override
    public List<ConsultaOutput> listarConsultasPorDia(Integer voluntarioId, LocalDateTime data) {
        LocalDateTime startOfDay = data.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        List<Consulta> consultas = consultaRepository.findByVoluntarioIdAndHorarioBetween(voluntarioId, startOfDay, endOfDay);

        return consultas.stream()
                .map(consulta -> new ConsultaOutput(
                        consulta.getIdConsulta(),
                        consulta.getHorario(),
                        consulta.getStatus(),
                        consulta.getModalidade(),
                        consulta.getLocal(),
                        consulta.getObservacoes(),
                        consulta.getEspecialidade(),
                        consulta.getAssistido(),
                        consulta.getVoluntario()
                ))
                .collect(Collectors.toList());
    }
    @Override
    public List<ConsultaOutput> listarHistoricoConsultas(Integer voluntarioId) {
        LocalDateTime agora = LocalDateTime.now();
        List<Consulta> consultas = consultaRepository.findByVoluntarioIdAndHorarioBefore(voluntarioId, agora);

        return consultas.stream()
                .map(consulta -> new ConsultaOutput(
                        consulta.getIdConsulta(),
                        consulta.getHorario(),
                        consulta.getStatus(),
                        consulta.getModalidade(),
                        consulta.getLocal(),
                        consulta.getObservacoes(),
                        consulta.getEspecialidade(),
                        consulta.getAssistido(),
                        consulta.getVoluntario()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ConsultaOutput buscarConsultaPorIdEVoluntario(Integer consultaId, Integer voluntarioId) {
        Consulta consulta = consultaRepository.findByIdConsultaAndVoluntarioId(consultaId, voluntarioId)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada para o voluntário informado."));

        return new ConsultaOutput(
                consulta.getIdConsulta(),
                consulta.getHorario(),
                consulta.getStatus(),
                consulta.getModalidade(),
                consulta.getLocal(),
                consulta.getObservacoes(),
                consulta.getEspecialidade(),
                consulta.getAssistido(),
                consulta.getVoluntario()
        );
    }
    @Override
    public List<ConsultaOutput> listarProximasConsultas(Integer voluntarioId) {
        LocalDateTime agora = LocalDateTime.now();
        List<Consulta> consultas = consultaRepository.findTop3ByVoluntarioIdAndHorarioAfterOrderByHorarioAsc(voluntarioId, agora);

        return consultas.stream()
                .map(consulta -> new ConsultaOutput(
                        consulta.getIdConsulta(),
                        consulta.getHorario(),
                        consulta.getStatus(),
                        consulta.getModalidade(),
                        consulta.getLocal(),
                        consulta.getObservacoes(),
                        consulta.getEspecialidade(),
                        consulta.getAssistido(),
                        consulta.getVoluntario()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ConsultaOutput buscarConsultaPorIdEAssistido(Integer consultaId, Integer assistidoId) {
        Consulta consulta = consultaRepository.findByIdConsultaAndAssistidoId(consultaId, assistidoId)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada para o assistido informado."));

        return new ConsultaOutput(
                consulta.getIdConsulta(),
                consulta.getHorario(),
                consulta.getStatus(),
                consulta.getModalidade(),
                consulta.getLocal(),
                consulta.getObservacoes(),
                consulta.getEspecialidade(),
                consulta.getAssistido(),
                consulta.getVoluntario()
        );
    }
    @Override
    public List<ConsultaOutput> listarProximasConsultasAssistido(Integer assistidoId) {
        LocalDateTime agora = LocalDateTime.now();
        List<Consulta> consultas = consultaRepository.findTop3ByAssistidoIdAndHorarioAfterOrderByHorarioAsc(assistidoId, agora);

        return consultas.stream()
                .map(consulta -> new ConsultaOutput(
                        consulta.getIdConsulta(),
                        consulta.getHorario(),
                        consulta.getStatus(),
                        consulta.getModalidade(),
                        consulta.getLocal(),
                        consulta.getObservacoes(),
                        consulta.getEspecialidade(),
                        consulta.getAssistido(),
                        consulta.getVoluntario()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ConsultaOutput remarcarConsulta(Integer id, RemarcarConsulta dto) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada"));

        if (dto.getHorario() != null) {
            consulta.setHorario(dto.getHorario());
        }
        if (dto.getLocal() != null) {
            consulta.setLocal(dto.getLocal());
        }
        if (dto.getModalidade() != null) {
            consulta.setModalidade(dto.getModalidade());
        }

        consultaRepository.save(consulta);

        return new ConsultaOutput(
                consulta.getIdConsulta(),
                consulta.getHorario(),
                consulta.getStatus(),
                consulta.getModalidade(),
                consulta.getLocal(),
                consulta.getObservacoes(),
                consulta.getEspecialidade(),
                consulta.getAssistido(),
                consulta.getVoluntario()
        );
    }


}
