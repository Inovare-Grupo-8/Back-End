package org.com.imaapi.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.com.imaapi.model.consulta.AvaliacaoConsulta;
import org.com.imaapi.model.consulta.AvaliacaoConsulta;
import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.model.consulta.MotivoCancelamento;
import org.com.imaapi.model.consulta.RemarcarConsulta;
import org.com.imaapi.model.consulta.FeedbackConsulta;
import org.com.imaapi.model.consulta.FeedbackConsulta;
import org.com.imaapi.model.consulta.dto.ConsultaDto;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.mapper.ConsultaMapper;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.model.enums.StatusConsulta;
import org.com.imaapi.model.especialidade.Especialidade;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.enums.StatusConsulta;
import org.com.imaapi.repository.AvaliacaoConsultaRepository;
import org.com.imaapi.repository.AvaliacaoConsultaRepository;
import org.com.imaapi.repository.ConsultaRepository;
import org.com.imaapi.repository.EspecialidadeRepository;
import org.com.imaapi.repository.FeedbackConsultaRepository;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.repository.*;
import org.com.imaapi.service.ConsultaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private FeedbackConsultaRepository feedbackRepository;

    @Autowired
    private AvaliacaoConsultaRepository avaliacaoRepository;

    @Autowired
    private FeedbackConsultaRepository feedbackRepository;

    @Autowired
    private AvaliacaoConsultaRepository avaliacaoRepository;

    @Override
    public ResponseEntity<ConsultaOutput> criarEvento(ConsultaInput consultaInput) {
        try {
            if (consultaInput == null) {
                logger.error("Objeto ConsultaInput é nulo");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            logger.info("Input de consulta recebido: {}", consultaInput);

            boolean isEspecialidadeValid = consultaInput.getIdEspecialidade() != null || consultaInput.getEspecialidade() != null;
            boolean isAssistidoValid = consultaInput.getIdAssistido() != null || consultaInput.getAssistido() != null;
            boolean isVoluntarioValid = consultaInput.getIdVoluntario() != null || consultaInput.getVoluntario() != null;

            if (!isEspecialidadeValid || !isAssistidoValid || !isVoluntarioValid) {
                logger.error("Campos obrigatórios faltando no input da consulta. Informe IDs ou objetos completos.");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            logger.info("Registrando a criação do evento");
            Consulta consulta;
            try {
                consulta = ConsultaMapper.toEntity(consultaInput);
            } catch (IllegalArgumentException e) {
                logger.error("Erro de validação ao converter consulta: {}", e.getMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            try {
                if (consulta.getEspecialidade() == null) {
                    if (consultaInput.getIdEspecialidade() == null) {
                        logger.error("ID da especialidade é nulo");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }

                    consulta.setEspecialidade(especialidadeRepository.findById(consultaInput.getIdEspecialidade())
                            .orElseThrow(() -> new RuntimeException("Especialidade não encontrada com ID " + consultaInput.getIdEspecialidade())));

                    logger.debug("Especialidade recuperada do banco: {}", consulta.getEspecialidade());
                } else {
                    if (consulta.getEspecialidade().getId() == null) {
                        logger.error("Especialidade fornecida sem ID válido");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }

                    if (!especialidadeRepository.existsById(consulta.getEspecialidade().getId())) {
                        logger.error("Especialidade não encontrada com ID {}", consulta.getEspecialidade().getId());
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }

                    logger.debug("Especialidade validada do input: {}", consulta.getEspecialidade());
                }
            } catch (Exception e) {
                logger.error("Erro ao processar especialidade: {}", e.getMessage());

                ConsultaOutput errorOutput = new ConsultaOutput();
                errorOutput.setObservacoes("Erro ao processar especialidade: " + e.getMessage());

                return new ResponseEntity<>(errorOutput, HttpStatus.BAD_REQUEST);
            }

            try {
                if (consulta.getAssistido() == null) {
                    if (consultaInput.getIdAssistido() == null) {
                        logger.error("ID do assistido é nulo");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }

                    consulta.setAssistido(usuarioRepository.findById(consultaInput.getIdAssistido())
                            .orElseThrow(() -> new RuntimeException("Usuário assistido não encontrado com ID " + consultaInput.getIdAssistido())));

                    logger.debug("Assistido recuperado do banco: {}", consulta.getAssistido());
                } else {
                    if (consulta.getAssistido().getIdUsuario() == null) {
                        logger.error("Assistido fornecido sem ID válido");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }

                    if (!usuarioRepository.existsById(consulta.getAssistido().getIdUsuario())) {
                        logger.error("Assistido não encontrado com ID {}", consulta.getAssistido().getIdUsuario());
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }

                    logger.debug("Assistido validado do input: {}", consulta.getAssistido());
                }
            } catch (Exception e) {
                logger.error("Erro ao processar assistido: {}", e.getMessage());

                ConsultaOutput errorOutput = new ConsultaOutput();
                errorOutput.setObservacoes("Erro ao processar assistido: " + e.getMessage());

                return new ResponseEntity<>(errorOutput, HttpStatus.BAD_REQUEST);
            }

            try {
                if (consulta.getVoluntario() == null) {
                    if (consultaInput.getIdVoluntario() == null) {
                        logger.error("ID do voluntário é nulo");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
            if (consultaInput.getIdEspecialidade() == null ||
                    consultaInput.getIdAssistido() == null ||
                    consultaInput.getIdVoluntario() == null) {
                logger.error("Campos obrigatórios não podem ser nulos.");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

                    consulta.setVoluntario(usuarioRepository.findById(consultaInput.getIdVoluntario())
                            .orElseThrow(() -> new RuntimeException("Usuário voluntário não encontrado com ID " + consultaInput.getIdVoluntario())));

                    logger.debug("Voluntário recuperado do banco: {}", consulta.getVoluntario());
                } else {
                    if (consulta.getVoluntario().getIdUsuario() == null) {
                        logger.error("Voluntário fornecido sem ID válido");
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }

                    if (!usuarioRepository.existsById(consulta.getVoluntario().getIdUsuario())) {
                        logger.error("Voluntário não encontrado com ID {}", consulta.getVoluntario().getIdUsuario());
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }

                    logger.debug("Voluntário validado do input: {}", consulta.getVoluntario());
                }
            } catch (Exception e) {
                logger.error("Erro ao processar voluntário: {}", e.getMessage());

                ConsultaOutput errorOutput = new ConsultaOutput();
                errorOutput.setObservacoes("Erro ao processar voluntário: " + e.getMessage());

                return new ResponseEntity<>(errorOutput, HttpStatus.BAD_REQUEST);
            }
            Consulta consultaSalva;
            try {
                consultaSalva = consultaRepository.save(consulta);
                logger.info("Evento cadastrado com sucesso: ID = {}", consultaSalva.getIdConsulta());
            } catch (Exception e) {
                logger.error("Erro ao salvar consulta no banco: {}", e.getMessage());

                ConsultaOutput errorOutput = new ConsultaOutput();
                errorOutput.setObservacoes("Erro ao salvar consulta no banco: " + e.getMessage());

                return new ResponseEntity<>(errorOutput, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Integer idEspecialidade = consultaSalva.getEspecialidade().getId();
            Integer idAssistido = consultaSalva.getAssistido().getIdUsuario();
            Integer idVoluntario = consultaSalva.getVoluntario().getIdUsuario();

            ConsultaOutput output = new ConsultaOutput();

            try {
                Especialidade especialidadeCompleta = especialidadeRepository.findById(idEspecialidade)
                        .orElseThrow(() -> new RuntimeException("Especialidade não encontrada após salvar"));

                Usuario assistidoCompleto = usuarioRepository.findById(idAssistido)
                        .orElseThrow(() -> new RuntimeException("Usuário assistido não encontrado após salvar"));

                Usuario voluntarioCompleto = usuarioRepository.findById(idVoluntario)
                        .orElseThrow(() -> new RuntimeException("Usuário voluntário não encontrado após salvar"));

                output.setHorario(consultaSalva.getHorario());
                output.setStatus(consultaSalva.getStatus());
                output.setModalidade(consultaSalva.getModalidade());
                output.setLocal(consultaSalva.getLocal());
                output.setObservacoes(consultaSalva.getObservacoes());

                output.setEspecialidade(especialidadeCompleta);
                output.setAssistido(assistidoCompleto);
                output.setVoluntario(voluntarioCompleto);

                output.setIdConsulta(consultaSalva.getIdConsulta());

            ConsultaOutput eventoResponse = gerarObjetoEventoOutput(salvarConsulta);
            return new ResponseEntity<>(eventoResponse, HttpStatus.CREATED);
                logger.debug("Output da consulta criada: {}", output);
            } catch (Exception e) {
                logger.error("Erro ao carregar detalhes completos para resposta: {}", e.getMessage());

                output.setIdConsulta(consultaSalva.getIdConsulta());
                output.setHorario(consultaSalva.getHorario());
                output.setStatus(consultaSalva.getStatus());
                output.setModalidade(consultaSalva.getModalidade());
                output.setLocal(consultaSalva.getLocal());
                output.setObservacoes(consultaSalva.getObservacoes());

                output.setEspecialidade(consultaSalva.getEspecialidade());
                output.setAssistido(consultaSalva.getAssistido());
                output.setVoluntario(consultaSalva.getVoluntario());
            }
            return new ResponseEntity<>(output, HttpStatus.CREATED);
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
            erro.printStackTrace();

            ConsultaOutput errorOutput = new ConsultaOutput();
            errorOutput.setObservacoes("Erro ao cadastrar evento: " + erro.getMessage());
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

            return new ResponseEntity<>(errorOutput, HttpStatus.INTERNAL_SERVER_ERROR);
        }

}


    @Override
    public ResponseEntity<?> getConsultasDia(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                return ResponseEntity.badRequest().body("Tipo de usuário inválido");
            }

            LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fim = inicio.plusDays(1);

            List<Consulta> consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetween(
                    user.equals("voluntario") ? getUsuarioLogado().getIdUsuario() : null,
                    inicio,
                    fim
            );

            return ResponseEntity.ok(consultas);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas do dia: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<?> getConsultasSemana(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                return ResponseEntity.badRequest().body("Tipo de usuário inválido");
            }

            LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fim = inicio.plusWeeks(1);

            List<Consulta> consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetween(
                    user.equals("voluntario") ? getUsuarioLogado().getIdUsuario() : null,
                    inicio,
                    fim
            );

            return ResponseEntity.ok(consultas);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas da semana: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<?> getConsultasMes(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                return ResponseEntity.badRequest().body("Tipo de usuário inválido");
            }

            LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fim = inicio.plusMonths(1);

            List<Consulta> consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetween(
                    user.equals("voluntario") ? getUsuarioLogado().getIdUsuario() : null,
                    inicio,
                    fim
            );

            return ResponseEntity.ok(consultas);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas do mês: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<?> getAvaliacoesFeedback(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                return ResponseEntity.badRequest().body("Tipo de usuário inválido");
            }

            Integer userId = getUsuarioLogado().getIdUsuario();

            List<FeedbackConsulta> feedbacks;
            List<AvaliacaoConsulta> avaliacoes;

            if (user.equals("voluntario")) {
                feedbacks = feedbackRepository.findByConsulta_Voluntario_IdUsuario(userId);
                avaliacoes = avaliacaoRepository.findByConsulta_Voluntario_IdUsuario(userId);
            } else {
                feedbacks = feedbackRepository.findByConsulta_Assistido_IdUsuario(userId);
                avaliacoes = avaliacaoRepository.findByConsulta_Assistido_IdUsuario(userId);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("feedbacks", feedbacks);
            response.put("avaliacoes", avaliacoes);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao buscar avaliações e feedbacks: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<?> getConsultasRecentes(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                return ResponseEntity.badRequest().body("Tipo de usuário inválido");
            }

            LocalDateTime inicio = LocalDateTime.now().minusMonths(1);
            LocalDateTime fim = LocalDateTime.now();

            List<Consulta> consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetween(
                    user.equals("voluntario") ? getUsuarioLogado().getIdUsuario() : null,
                    inicio,
                    fim
            );

            return ResponseEntity.ok(consultas.stream().map(Consulta::getIdConsulta).collect(Collectors.toList()));
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas recentes: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<?> adicionarFeedback(Long id, String feedback) {
        try {
            if (id == null || feedback == null || feedback.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("ID e feedback são obrigatórios");
            }

            Consulta consulta = consultaRepository.findById(id.intValue())
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

            FeedbackConsulta feedbackConsulta = new FeedbackConsulta();
            feedbackConsulta.setConsulta(consulta);
            feedbackConsulta.setComentario(feedback);
            feedbackConsulta.setDtFeedback(LocalDateTime.now());

            feedbackRepository.save(feedbackConsulta);

            consulta.setFeedbackStatus("ENVIADO");
            consultaRepository.save(consulta);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Erro ao adicionar feedback: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<?> adicionarAvaliacao(Long id, String avaliacao) {
        try {
            if (id == null || avaliacao == null || avaliacao.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("ID e avaliação são obrigatórios");
            }

            int nota;
            try {
                nota = Integer.parseInt(avaliacao.trim());
                if (nota < 1 || nota > 5) {
                    return ResponseEntity.badRequest().body("A nota deve estar entre 1 e 5");
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("A avaliação deve ser um número entre 1 e 5");
            }

            Consulta consulta = consultaRepository.findById(id.intValue())
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

            AvaliacaoConsulta avaliacaoConsulta = new AvaliacaoConsulta();
            avaliacaoConsulta.setConsulta(consulta);
            avaliacaoConsulta.setNota(nota);
            avaliacaoConsulta.setDtAvaliacao(LocalDateTime.now());

            avaliacaoRepository.save(avaliacaoConsulta);

            consulta.setAvaliacaoStatus("ENVIADO");
            consultaRepository.save(consulta);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Erro ao adicionar avaliação: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private Usuario getUsuarioLogado() {
        return null;
    }

    @Override
    public ResponseEntity<List<ConsultaDto>> getConsultasDia(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                logger.error("Tipo de usuário inválido: {}", user);
                return ResponseEntity.badRequest().build();
            }

            LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime fim = inicio.plusDays(1).minusNanos(1);

            Integer userId = getUsuarioLogado().getIdUsuario();
            logger.debug("Buscando consultas do dia para {} com ID: {}, período: {} até {}",
                    user, userId, inicio, fim);

            List<Consulta> consultas;

            List<StatusConsulta> statusFiltro = List.of(
                    StatusConsulta.AGENDADA,
                    StatusConsulta.REAGENDADA,
                    StatusConsulta.EM_ANDAMENTO);

            if (user.equals("voluntario")) {
                consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetweenAndStatusIn(
                        userId, inicio, fim, statusFiltro);
            } else {
                consultas = consultaRepository.findByAssistido_IdUsuarioAndHorarioBetweenAndStatusIn(
                        userId, inicio, fim, statusFiltro);
            }

            logger.debug("Encontradas {} consultas do dia para {} com ID: {}",
                    consultas.size(), user, userId);
            List<ConsultaDto> dtos = consultas.stream()
                    .map(consulta -> ConsultaMapper.toDto(consulta))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas do dia: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<ConsultaDto>> getConsultasSemana(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                logger.error("Tipo de usuário inválido: {}", user);
                return ResponseEntity.badRequest().build();
            }

            DayOfWeek diaSemanaAtual = LocalDateTime.now().getDayOfWeek();

            LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

            int diasAteDomingo = DayOfWeek.SUNDAY.getValue() - diaSemanaAtual.getValue();
            if (diasAteDomingo <= 0) {
                diasAteDomingo += 7;
            }

            LocalDateTime fim = inicio.plusDays(diasAteDomingo).withHour(23).withMinute(59).withSecond(59);

            Integer userId = getUsuarioLogado().getIdUsuario();
            logger.debug("Buscando consultas da semana para {} com ID: {}, período: {} até {}",
                    user, userId, inicio, fim);

            List<Consulta> consultas;

            List<StatusConsulta> statusFiltro = List.of(StatusConsulta.AGENDADA, StatusConsulta.EM_ANDAMENTO);

            if (user.equals("voluntario")) {
                consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetweenAndStatusIn(
                        userId, inicio, fim, statusFiltro);
            } else {
                consultas = consultaRepository.findByAssistido_IdUsuarioAndHorarioBetweenAndStatusIn(
                        userId, inicio, fim, statusFiltro);
            }

            logger.debug("Encontradas {} consultas da semana para {} com ID: {}",
                    consultas.size(), user, userId);
            List<ConsultaDto> dtos = consultas.stream()
                    .sorted(Comparator.comparing(Consulta::getHorario))
                    .map(consulta -> ConsultaMapper.toDto(consulta))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas da semana: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<ConsultaDto>> getConsultasMes(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                logger.error("Tipo de usuário inválido: {}", user);
                return ResponseEntity.badRequest().build();
            }

            LocalDateTime fim = LocalDateTime.now();
            LocalDateTime inicio = fim.minusDays(30);

            Integer userId = getUsuarioLogado().getIdUsuario();
            logger.debug("Buscando consultas dos últimos 30 dias para {} com ID: {}, período: {} até {}",
                    user, userId, inicio, fim);

            List<Consulta> consultas;

            if (user.equals("voluntario")) {
                consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetween(
                        userId, inicio, fim);
            } else {
                consultas = consultaRepository.findByAssistido_IdUsuarioAndHorarioBetween(
                        userId, inicio, fim);
            }

            logger.debug("Encontradas {} consultas dos últimos 30 dias para {} com ID: {}",
                    consultas.size(), user, userId);
            List<ConsultaDto> dtos = consultas.stream()
                    .sorted(Comparator.comparing(Consulta::getHorario).reversed())
                    .map(consulta -> ConsultaMapper.toDto(consulta))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas dos últimos 30 dias: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAvaliacoesFeedback(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                logger.error("Tipo de usuário inválido: {}", user);
                return ResponseEntity.badRequest().build();
            }
            Integer userId = getUsuarioLogado().getIdUsuario();

            List<FeedbackConsulta> feedbacks;
            List<AvaliacaoConsulta> avaliacoes;

            if (user.equals("voluntario")) {
                feedbacks = feedbackRepository.findByConsulta_Voluntario_IdUsuario(userId);
                avaliacoes = avaliacaoRepository.findByConsulta_Voluntario_IdUsuario(userId);
            } else {
                feedbacks = feedbackRepository.findByConsulta_Assistido_IdUsuario(userId);
                avaliacoes = avaliacaoRepository.findByConsulta_Assistido_IdUsuario(userId);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("feedbacks", feedbacks);
            response.put("avaliacoes", avaliacoes);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao buscar avaliações e feedbacks: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<ConsultaDto>> getConsultasRecentes(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                logger.error("Tipo de usuário inválido: {}", user);
                return ResponseEntity.badRequest().build();
            }

            Integer userId = getUsuarioLogado().getIdUsuario();
            logger.info("Buscando consultas recentes para {} com ID: {}", user, userId);

            List<Consulta> todasConsultasUsuario;
            if (user.equals("voluntario")) {
                todasConsultasUsuario = consultaRepository.findByVoluntario_IdUsuario(userId);
            } else {
                todasConsultasUsuario = consultaRepository.findByAssistido_IdUsuario(userId);
            }
            logger.info("Total de consultas encontradas para {} com ID {}: {}", user, userId, todasConsultasUsuario.size());

            if (!todasConsultasUsuario.isEmpty()) {
                Map<StatusConsulta, Long> statusCount = todasConsultasUsuario.stream()
                        .collect(Collectors.groupingBy(Consulta::getStatus, Collectors.counting()));
                logger.info("Status das consultas encontradas: {}", statusCount);
            }

            List<Consulta> consultasFinalizadas = todasConsultasUsuario.stream()
                    .filter(consulta -> List.of(
                            StatusConsulta.REALIZADA,
                            StatusConsulta.CONCLUIDA,
                            StatusConsulta.CANCELADA
                    ).contains(consulta.getStatus()))
                    .collect(Collectors.toList());

            List<Consulta> consultasParaMostrar;

            if (consultasFinalizadas.size() >= 3) {
                consultasParaMostrar = consultasFinalizadas;
                logger.info("Mostrando apenas consultas finalizadas: {}", consultasFinalizadas.size());
            } else {
                consultasParaMostrar = todasConsultasUsuario;
                logger.info("Mostrando todas as consultas disponíveis: {}", todasConsultasUsuario.size());
            }

            List<Consulta> consultasOrdenadas = consultasParaMostrar.stream()
                    .sorted(Comparator.comparing(Consulta::getHorario).reversed())
                    .limit(3)
                    .collect(Collectors.toList());

            logger.info("Retornando {} consultas recentes para {} com ID: {}", consultasOrdenadas.size(), user, userId);

            List<ConsultaDto> dtos = consultasOrdenadas.stream()
                    .map(consulta -> ConsultaMapper.toDto(consulta))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas recentes: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<ConsultaDto> adicionarFeedback(Integer id, String feedback) {
        try {
            if (id == null || feedback == null || feedback.trim().isEmpty()) {
                logger.error("ID e feedback são obrigatórios");
                return ResponseEntity.badRequest().build();
            }

            Consulta consulta = consultaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

            FeedbackConsulta feedbackConsulta = new FeedbackConsulta();
            feedbackConsulta.setConsulta(consulta);
            feedbackConsulta.setComentario(feedback);
            feedbackConsulta.setDtFeedback(LocalDateTime.now());

            feedbackRepository.save(feedbackConsulta);

            consulta.setFeedbackStatus("ENVIADO");
            consultaRepository.save(consulta);

            return ResponseEntity.ok(ConsultaMapper.toDto(consulta));
        } catch (Exception e) {
            logger.error("Erro ao adicionar feedback: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<ConsultaDto> adicionarAvaliacao(Integer id, String avaliacao) {
        try {
            if (id == null || avaliacao == null || avaliacao.trim().isEmpty()) {
                logger.error("ID e avaliação são obrigatórios");
                return ResponseEntity.badRequest().build();
            }

            int nota;
            try {
                nota = Integer.parseInt(avaliacao.trim());
                if (nota < 1 || nota > 5) {
                    logger.error("A nota deve estar entre 1 e 5");
                    return ResponseEntity.badRequest().build();
                }
            } catch (NumberFormatException e) {
                logger.error("A avaliação deve ser um número entre 1 e 5");
                return ResponseEntity.badRequest().build();
            }

            Consulta consulta = consultaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

            AvaliacaoConsulta avaliacaoConsulta = new AvaliacaoConsulta();
            avaliacaoConsulta.setConsulta(consulta);
            avaliacaoConsulta.setNota(nota);
            avaliacaoConsulta.setDtAvaliacao(LocalDateTime.now());

            avaliacaoRepository.save(avaliacaoConsulta);

            consulta.setAvaliacaoStatus("ENVIADO");
            consultaRepository.save(consulta);

            return ResponseEntity.ok(ConsultaMapper.toDto(consulta));
        } catch (Exception e) {
            logger.error("Erro ao adicionar avaliação: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<ConsultaDto>> getTodasConsultas() {
        try {
            logger.info("Buscando todas as consultas no sistema");

            List<Consulta> consultas = consultaRepository.findAll();
            logger.debug("Encontradas {} consultas no sistema", consultas.size());

            List<ConsultaDto> dtos = consultas.stream()
                    .sorted(Comparator.comparing(Consulta::getHorario).reversed())
                    .map(ConsultaMapper::toDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Erro ao buscar todas as consultas: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private Usuario getUsuarioLogado() {
        try {
            org.springframework.security.core.Authentication authentication =
                    org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.getPrincipal() instanceof org.com.imaapi.model.usuario.output.UsuarioDetalhesOutput) {
                org.com.imaapi.model.usuario.output.UsuarioDetalhesOutput userDetails =
                        (org.com.imaapi.model.usuario.output.UsuarioDetalhesOutput) authentication.getPrincipal();

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(userDetails.getIdUsuario());
                usuario.setEmail(userDetails.getEmail());
                usuario.setTipo(userDetails.getTipo());

                logger.debug("Usuário logado recuperado: ID={}, Email={}, Tipo={}",
                        usuario.getIdUsuario(), usuario.getEmail(), usuario.getTipo());

                return usuario;
            } else {
                logger.error("Não foi possível obter os detalhes do usuário autenticado");
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(1);
                return usuario;
            }
        } catch (Exception e) {
            logger.error("Erro ao recuperar usuário autenticado: {}", e.getMessage());
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(1);
            return usuario;
        }
    }
}