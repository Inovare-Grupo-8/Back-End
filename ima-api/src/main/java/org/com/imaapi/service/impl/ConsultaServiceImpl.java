package org.com.imaapi.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.com.imaapi.model.consulta.AvaliacaoConsulta;
import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.model.consulta.MotivoCancelamento;
import org.com.imaapi.model.consulta.RemarcarConsulta;
import org.com.imaapi.model.consulta.FeedbackConsulta;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.model.usuario.Especialidade;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.model.enums.StatusConsulta;
import org.com.imaapi.repository.AvaliacaoConsultaRepository;
import org.com.imaapi.repository.ConsultaRepository;
import org.com.imaapi.repository.EspecialidadeRepository;
import org.com.imaapi.repository.FeedbackConsultaRepository;
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
}