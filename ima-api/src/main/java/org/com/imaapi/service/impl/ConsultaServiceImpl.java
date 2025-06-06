package org.com.imaapi.service.impl;

import org.com.imaapi.model.consulta.AvaliacaoConsulta;
import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.model.consulta.FeedbackConsulta;
import org.com.imaapi.model.consulta.dto.ConsultaDto;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.mapper.ConsultaMapper;
import org.com.imaapi.model.consulta.output.ConsultaOutput;
import org.com.imaapi.model.enums.StatusConsulta;
import org.com.imaapi.model.especialidade.Especialidade;
import org.com.imaapi.model.usuario.Usuario;
import org.com.imaapi.repository.*;
import org.com.imaapi.service.ConsultaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Comparator;
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

    public ResponseEntity<?> getHorariosDisponiveis(LocalDate data, Integer idVoluntario) {
        try {
            // Define início e fim do dia
            LocalDateTime inicioDia = data.atStartOfDay();
            LocalDateTime fimDia = data.atTime(23, 59, 59);

            // Busca consultas existentes para o voluntário neste dia
            List<Consulta> consultasMarcadas = consultaRepository
                    .findByVoluntario_IdUsuarioAndHorarioBetween(idVoluntario, inicioDia, fimDia);

            // Horários padrão de atendimento (exemplo: 9h às 17h, intervalo de 1h)
            List<LocalTime> horariosDisponiveis = Arrays.asList(
                    LocalTime.of(0, 0),
                    LocalTime.of(1, 0),
                    LocalTime.of(2, 0),
                    LocalTime.of(3, 0),
                    LocalTime.of(4, 0),
                    LocalTime.of(5, 0),
                    LocalTime.of(6, 0),
                    LocalTime.of(7, 0),
                    LocalTime.of(8, 0),
                    LocalTime.of(9, 0),
                    LocalTime.of(10, 0),
                    LocalTime.of(11, 0),
                    LocalTime.of(12, 0),
                    LocalTime.of(13, 0),
                    LocalTime.of(14, 0),
                    LocalTime.of(15, 0),
                    LocalTime.of(16, 0),
                    LocalTime.of(17, 0),
                    LocalTime.of(18, 0),
                    LocalTime.of(19, 0),
                    LocalTime.of(20, 0),
                    LocalTime.of(21, 0),
                    LocalTime.of(22, 0),
                    LocalTime.of(23, 0)
            );

            // Lista de horários ocupados
            List<LocalTime> horariosOcupados = consultasMarcadas.stream()
                    .map(consulta -> consulta.getHorario().toLocalTime())
                    .collect(Collectors.toList());

            // Filtra os horários disponíveis removendo os ocupados
            List<LocalDateTime> horariosLivres = horariosDisponiveis.stream()
                    .filter(horario -> !horariosOcupados.contains(horario))
                    .map(horario -> LocalDateTime.of(data, horario))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(horariosLivres);
        } catch (Exception e) {
            logger.error("Erro ao buscar horários disponíveis: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private Consulta gerarObjetoEvento(ConsultaInput consultaInput) {
        try {
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

            return consulta;
        } catch (Exception e) {
            logger.error("Erro ao gerar objeto evento: {}", e.getMessage());
            throw new RuntimeException("Erro ao gerar objeto evento: " + e.getMessage());
        }
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
   public ResponseEntity<ConsultaOutput> getProximaConsulta(Integer idUsuario) {
       try {
           // Busca próximas consultas como voluntário e assistido
           List<Consulta> consultasVoluntario = consultaRepository
               .findByVoluntario_IdUsuarioAndHorarioAfterOrderByHorarioAsc(idUsuario, LocalDateTime.now());

           List<Consulta> consultasAssistido = consultaRepository
               .findByAssistido_IdUsuarioAndHorarioAfterOrderByHorarioAsc(idUsuario, LocalDateTime.now());

           // Encontra a próxima consulta mais próxima
           Consulta proximaConsulta = null;

           if (!consultasVoluntario.isEmpty() && !consultasAssistido.isEmpty()) {
               proximaConsulta = consultasVoluntario.get(0).getHorario()
                   .isBefore(consultasAssistido.get(0).getHorario())
                   ? consultasVoluntario.get(0)
                   : consultasAssistido.get(0);
           } else if (!consultasVoluntario.isEmpty()) {
               proximaConsulta = consultasVoluntario.get(0);
           } else if (!consultasAssistido.isEmpty()) {
               proximaConsulta = consultasAssistido.get(0);
           }

           if (proximaConsulta == null) {
               return ResponseEntity.notFound().build();
           }

           ConsultaOutput output = new ConsultaOutput();
           output.setHorario(proximaConsulta.getHorario());
           output.setStatus(proximaConsulta.getStatus());
           output.setModalidade(proximaConsulta.getModalidade());
           output.setLocal(proximaConsulta.getLocal());
           output.setObservacoes(proximaConsulta.getObservacoes());
           output.setEspecialidade(proximaConsulta.getEspecialidade());
           output.setAssistido(proximaConsulta.getAssistido());
           output.setVoluntario(proximaConsulta.getVoluntario());

           return ResponseEntity.ok(output);

       } catch (Exception e) {
           logger.error("Erro ao buscar próxima consulta: {}", e.getMessage());
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