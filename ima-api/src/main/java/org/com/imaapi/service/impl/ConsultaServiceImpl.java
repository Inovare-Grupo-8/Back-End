package org.com.imaapi.service.impl;

import org.com.imaapi.model.consulta.AvaliacaoConsulta;
import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.model.consulta.FeedbackConsulta;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.output.AvaliacaoFeedbackOutput;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.model.usuario.Especialidade;
import org.com.imaapi.model.usuario.Usuario;
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
import java.util.ArrayList;
import java.util.List;
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

        Especialidade especialidade = especialidadeRepository.findById(consultaInput.getIdEspecialidade())
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada"));
        consulta.setEspecialidade(especialidade);

        Usuario assistido = usuarioRepository.findById(consultaInput.getIdAssistido())
                .orElseThrow(() -> new RuntimeException("Usuário assistido não encontrado"));
        consulta.setAssistido(assistido);

        Usuario voluntario = usuarioRepository.findById(consultaInput.getIdVoluntario())
                .orElseThrow(() -> new RuntimeException("Usuário voluntário não encontrado"));
        consulta.setVoluntario(voluntario);

        return consulta;
    }

    private ConsultaOutput gerarObjetoEventoOutput(Consulta consulta) {
        ConsultaOutput consultaOutput = new ConsultaOutput();

        consultaOutput.setIdConsulta(Long.valueOf(consulta.getIdConsulta()));
        consultaOutput.setHorario(consulta.getHorario());
        consultaOutput.setStatus(consulta.getStatus());
        consultaOutput.setModalidade(consulta.getModalidade());
        consultaOutput.setLocal(consulta.getLocal());
        consultaOutput.setObservacoes(consulta.getObservacoes());
        consultaOutput.setFeedbackStatus(consulta.getFeedbackStatus());
        consultaOutput.setAvaliacaoStatus(consulta.getAvaliacaoStatus());

        ConsultaOutput.VoluntarioInfo voluntarioInfo = new ConsultaOutput.VoluntarioInfo();
        voluntarioInfo.setId(Long.valueOf(consulta.getVoluntario().getIdUsuario()));
        voluntarioInfo.setNome(consulta.getVoluntario().getFicha().getNome());
        voluntarioInfo.setSobrenome(consulta.getVoluntario().getFicha().getSobrenome());
        voluntarioInfo.setEmail(consulta.getVoluntario().getEmail());
        consultaOutput.setVoluntario(voluntarioInfo);

        ConsultaOutput.AssistidoInfo assistidoInfo = new ConsultaOutput.AssistidoInfo();
        assistidoInfo.setId(Long.valueOf(consulta.getAssistido().getIdUsuario()));
        assistidoInfo.setNome(consulta.getAssistido().getFicha().getNome());
        assistidoInfo.setSobrenome(consulta.getAssistido().getFicha().getSobrenome());
        assistidoInfo.setEmail(consulta.getAssistido().getEmail());
        consultaOutput.setAssistido(assistidoInfo);

        ConsultaOutput.EspecialidadeInfo especialidadeInfo = new ConsultaOutput.EspecialidadeInfo();
        especialidadeInfo.setId(Long.valueOf(consulta.getEspecialidade().getIdEspecialidade()));
        especialidadeInfo.setNome(consulta.getEspecialidade().getNome());
        especialidadeInfo.setDescricao("");
        consultaOutput.setEspecialidade(especialidadeInfo);

        return consultaOutput;
    }

    @Override
    public ResponseEntity<List<ConsultaOutput>> getConsultasDia(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                return ResponseEntity.badRequest().build();
            }

            LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fim = inicio.plusDays(1);

            List<Consulta> consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetween(
                    user.equals("voluntario") ? getUsuarioLogado().getIdUsuario() : null,
                    inicio,
                    fim
            );

            List<ConsultaOutput> consultasOutput = consultas.stream()
                .map(this::gerarObjetoEventoOutput)
                .collect(Collectors.toList());

            return ResponseEntity.ok(consultasOutput);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas do dia: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<ConsultaOutput>> getConsultasSemana(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                return ResponseEntity.badRequest().build();
            }

            LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fim = inicio.plusWeeks(1);

            List<Consulta> consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetween(
                    user.equals("voluntario") ? getUsuarioLogado().getIdUsuario() : null,
                    inicio,
                    fim
            );

            List<ConsultaOutput> consultasOutput = consultas.stream()
                .map(this::gerarObjetoEventoOutput)
                .collect(Collectors.toList());

            return ResponseEntity.ok(consultasOutput);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas da semana: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<ConsultaOutput>> getConsultasMes(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                return ResponseEntity.badRequest().build();
            }

            LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fim = inicio.plusMonths(1);

            List<Consulta> consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetween(
                    user.equals("voluntario") ? getUsuarioLogado().getIdUsuario() : null,
                    inicio,
                    fim
            );

            List<ConsultaOutput> consultasOutput = consultas.stream()
                .map(this::gerarObjetoEventoOutput)
                .collect(Collectors.toList());

            return ResponseEntity.ok(consultasOutput);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas do mês: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<AvaliacaoFeedbackOutput>> getAvaliacoesFeedback(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
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

            List<AvaliacaoFeedbackOutput> outputs = new ArrayList<>();

            for (FeedbackConsulta feedback : feedbacks) {
                Consulta consulta = feedback.getConsulta();
                AvaliacaoConsulta avaliacao = avaliacoes.stream()
                    .filter(a -> a.getConsulta().getIdConsulta().equals(consulta.getIdConsulta()))
                    .findFirst()
                    .orElse(null);
                outputs.add(criarAvaliacaoFeedbackOutput(consulta, feedback, avaliacao));
            }

            for (AvaliacaoConsulta avaliacao : avaliacoes) {
                if (feedbacks.stream().noneMatch(f -> f.getConsulta().getIdConsulta().equals(avaliacao.getConsulta().getIdConsulta()))) {
                    outputs.add(criarAvaliacaoFeedbackOutput(avaliacao.getConsulta(), null, avaliacao));
                }
            }

            return ResponseEntity.ok(outputs);
        } catch (Exception e) {
            logger.error("Erro ao buscar avaliações e feedbacks: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<List<ConsultaOutput>> getConsultasRecentes(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                return ResponseEntity.badRequest().build();
            }

            LocalDateTime inicio = LocalDateTime.now().minusMonths(1);
            LocalDateTime fim = LocalDateTime.now();

            List<Consulta> consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetween(
                    user.equals("voluntario") ? getUsuarioLogado().getIdUsuario() : null,
                    inicio,
                    fim
            );

            List<ConsultaOutput> consultasOutput = consultas.stream()
                .map(this::gerarObjetoEventoOutput)
                .collect(Collectors.toList());

            return ResponseEntity.ok(consultasOutput);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas recentes: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<AvaliacaoFeedbackOutput> adicionarFeedback(Long id, String feedback) {
        try {
            if (id == null || feedback == null || feedback.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Consulta consulta = consultaRepository.findById(id.intValue())
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

            FeedbackConsulta feedbackConsulta = new FeedbackConsulta();
            feedbackConsulta.setConsulta(consulta);
            feedbackConsulta.setComentario(feedback);
            feedbackConsulta.setDtFeedback(LocalDateTime.now());

            feedbackConsulta = feedbackRepository.save(feedbackConsulta);

            consulta.setFeedbackStatus("ENVIADO");
            consultaRepository.save(consulta);
            AvaliacaoConsulta avaliacao = avaliacaoRepository.findFirstByConsultaIdConsulta(consulta.getIdConsulta())
                .orElse(null);

            return ResponseEntity.ok(criarAvaliacaoFeedbackOutput(consulta, feedbackConsulta, avaliacao));
        } catch (Exception e) {
            logger.error("Erro ao adicionar feedback: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<AvaliacaoFeedbackOutput> adicionarAvaliacao(Long id, String avaliacao) {
        try {
            if (id == null || avaliacao == null || avaliacao.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            int nota;
            try {
                nota = Integer.parseInt(avaliacao.trim());
                if (nota < 1 || nota > 5) {
                    return ResponseEntity.badRequest().build();
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().build();
            }

            Consulta consulta = consultaRepository.findById(id.intValue())
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

            AvaliacaoConsulta avaliacaoConsulta = new AvaliacaoConsulta();
            avaliacaoConsulta.setConsulta(consulta);
            avaliacaoConsulta.setNota(nota);
            avaliacaoConsulta.setDtAvaliacao(LocalDateTime.now());

            avaliacaoConsulta = avaliacaoRepository.save(avaliacaoConsulta);

            consulta.setAvaliacaoStatus("ENVIADO");
            consultaRepository.save(consulta);
            FeedbackConsulta feedback = feedbackRepository.findFirstByConsultaIdConsulta(consulta.getIdConsulta())
                .orElse(null);

            return ResponseEntity.ok(criarAvaliacaoFeedbackOutput(consulta, feedback, avaliacaoConsulta));
        } catch (Exception e) {
            logger.error("Erro ao adicionar avaliação: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private Usuario getUsuarioLogado() {
        return null;
    }

    private AvaliacaoFeedbackOutput criarAvaliacaoFeedbackOutput(Consulta consulta, FeedbackConsulta feedback, AvaliacaoConsulta avaliacao) {
        AvaliacaoFeedbackOutput output = new AvaliacaoFeedbackOutput();

        AvaliacaoFeedbackOutput.ConsultaBasicaInfo consultaInfo = new AvaliacaoFeedbackOutput.ConsultaBasicaInfo();
        consultaInfo.setIdConsulta(Long.valueOf(consulta.getIdConsulta()));
        consultaInfo.setHorario(consulta.getHorario());
        consultaInfo.setEspecialidadeNome(consulta.getEspecialidade().getNome());
        output.setConsulta(consultaInfo);

        if (feedback != null) {
            AvaliacaoFeedbackOutput.FeedbackInfo feedbackInfo = new AvaliacaoFeedbackOutput.FeedbackInfo();
            feedbackInfo.setId(Long.valueOf(feedback.getIdFeedback()));
            feedbackInfo.setComentario(feedback.getComentario());
            feedbackInfo.setDtFeedback(feedback.getDtFeedback());
            feedbackInfo.setStatus(consulta.getFeedbackStatus());
            output.setFeedback(feedbackInfo);
        }

        if (avaliacao != null) {
            AvaliacaoFeedbackOutput.AvaliacaoInfo avaliacaoInfo = new AvaliacaoFeedbackOutput.AvaliacaoInfo();
            avaliacaoInfo.setId(Long.valueOf(avaliacao.getIdAvaliacao()));
            avaliacaoInfo.setNota(avaliacao.getNota());
            avaliacaoInfo.setDtAvaliacao(avaliacao.getDtAvaliacao());
            avaliacaoInfo.setStatus(consulta.getAvaliacaoStatus());
            output.setAvaliacao(avaliacaoInfo);
        }
        return output;
    }
}