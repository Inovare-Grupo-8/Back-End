package org.com.imaapi.service.impl;

import org.com.imaapi.model.consulta.AvaliacaoConsulta;
import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.model.consulta.FeedbackConsulta;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.model.usuario.Especialidade;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.repository.*;
import org.com.imaapi.service.ConsultaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    @Autowired
    private FeedbackConsultaRepository feedbackRepository;

    @Autowired
    private AvaliacaoConsultaRepository avaliacaoRepository;

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