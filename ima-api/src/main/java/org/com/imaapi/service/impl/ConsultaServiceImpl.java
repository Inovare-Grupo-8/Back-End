package org.com.imaapi.service.impl;

import org.com.imaapi.model.consulta.AvaliacaoConsulta;
import org.com.imaapi.model.consulta.Consulta;
import org.com.imaapi.model.consulta.FeedbackConsulta;
import org.com.imaapi.model.consulta.dto.ConsultaDto;
import org.com.imaapi.model.consulta.input.ConsultaInput;
import org.com.imaapi.model.consulta.input.ConsultaRemarcarInput;
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

import java.util.Collections;

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

            LocalDateTime inicioDia = data.atStartOfDay();
            LocalDateTime fimDia = data.atTime(23, 59, 59);

            List<Consulta> consultasMarcadas = consultaRepository
                    .findByVoluntario_IdUsuarioAndHorarioBetween(idVoluntario, inicioDia, fimDia);

            // Gera dinamicamente os horários de 00:00 até 23:00
            List<LocalTime> horariosDisponiveis =
                java.util.stream.IntStream.rangeClosed(0, 23)
                    .mapToObj(h -> LocalTime.of(h, 0))
                    .collect(Collectors.toList());

            List<LocalTime> horariosOcupados = consultasMarcadas.stream()
                    .map(consulta -> consulta.getHorario().toLocalTime())
                    .collect(Collectors.toList());

            LocalDateTime agora = LocalDateTime.now();

            List<LocalDateTime> horariosLivres = horariosDisponiveis.stream()
                    .filter(horario -> !horariosOcupados.contains(horario))
                    .map(horario -> LocalDateTime.of(data, horario))
                    .filter(horario -> !data.equals(LocalDate.now()) || horario.isAfter(agora))
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
                    user, userId, inicio, fim);            List<Consulta> consultas;

            // Remover filtro de status para incluir todas as consultas do dia
            if (user.equals("voluntario")) {
                consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetween(
                        userId, inicio, fim);
            } else {
                consultas = consultaRepository.findByAssistido_IdUsuarioAndHorarioBetween(
                        userId, inicio, fim);
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
    }    @Override
    public ResponseEntity<List<ConsultaDto>> getConsultasSemana(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                logger.error("Tipo de usuário inválido: {}", user);
                return ResponseEntity.badRequest().build();
            }

            // Calcular o início e fim da semana atual (segunda a domingo)
            LocalDateTime agora = LocalDateTime.now();
            DayOfWeek diaSemanaAtual = agora.getDayOfWeek();
            
            // Calcular quantos dias voltar para chegar na segunda-feira
            int diasParaSegunda = diaSemanaAtual.getValue() - DayOfWeek.MONDAY.getValue();
            LocalDateTime inicio = agora.minusDays(diasParaSegunda).withHour(0).withMinute(0).withSecond(0).withNano(0);
            
            // Domingo da semana atual
            LocalDateTime fim = inicio.plusDays(6).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

            Integer userId = getUsuarioLogado().getIdUsuario();
            logger.debug("Buscando consultas da semana atual para {} com ID: {}, período: {} até {}",
                    user, userId, inicio, fim);            List<Consulta> consultas;

            // Remover filtro de status para incluir todas as consultas da semana
            if (user.equals("voluntario")) {
                consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetween(
                        userId, inicio, fim);
            } else {
                consultas = consultaRepository.findByAssistido_IdUsuarioAndHorarioBetween(
                        userId, inicio, fim);
            }

            logger.debug("Encontradas {} consultas da semana atual para {} com ID: {}",
                    consultas.size(), user, userId);
            List<ConsultaDto> dtos = consultas.stream()
                    .sorted(Comparator.comparing(Consulta::getHorario))
                    .map(consulta -> ConsultaMapper.toDto(consulta))
                    .collect(Collectors.toList());            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas da semana atual: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }    @Override
    public ResponseEntity<List<ConsultaDto>> getConsultasMes(String user) {
        try {
            if (!user.equals("voluntario") && !user.equals("assistido")) {
                logger.error("Tipo de usuário inválido: {}", user);
                return ResponseEntity.badRequest().build();
            }

            // Calcular o início e fim do mês atual
            LocalDateTime agora = LocalDateTime.now();
            LocalDateTime inicio = agora.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime fim = agora.withDayOfMonth(agora.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

            Integer userId = getUsuarioLogado().getIdUsuario();
            logger.debug("Buscando consultas do mês atual para {} com ID: {}, período: {} até {}",
                    user, userId, inicio, fim);

            List<Consulta> consultas;

            if (user.equals("voluntario")) {
                consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetween(
                        userId, inicio, fim);
            } else {
                consultas = consultaRepository.findByAssistido_IdUsuarioAndHorarioBetween(
                        userId, inicio, fim);
            }            logger.debug("Encontradas {} consultas do mês atual para {} com ID: {}",
                    consultas.size(), user, userId);
            List<ConsultaDto> dtos = consultas.stream()
                    .sorted(Comparator.comparing(Consulta::getHorario).reversed())
                    .map(consulta -> ConsultaMapper.toDto(consulta))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas do mês atual: {}", e.getMessage());
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

    @Override
    public ResponseEntity<List<ConsultaDto>> getConsultasUsuarioLogado() {
        try {
            Integer userId = getUsuarioLogado().getIdUsuario();
            logger.info("Buscando todas as consultas do usuário logado com ID: {}", userId);

            List<Consulta> consultas = consultaRepository.findByAssistido_IdUsuarioOrVoluntario_IdUsuario(userId, userId);
            
            logger.debug("Encontradas {} consultas para o usuário {}", consultas.size(), userId);
            
            List<ConsultaDto> dtos = consultas.stream()
                    .sorted(Comparator.comparing(Consulta::getHorario).reversed())
                    .map(ConsultaMapper::toDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Erro ao buscar consultas do usuário: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<ConsultaDto> getConsultaPorId(Integer id) {
        try {
            if (id == null) {
                logger.error("ID da consulta é obrigatório");
                return ResponseEntity.badRequest().build();
            }

            Integer userId = getUsuarioLogado().getIdUsuario();
            logger.info("Buscando consulta com ID: {} para usuário: {}", id, userId);

            Consulta consulta = consultaRepository.findById(id)
                    .orElse(null);

            if (consulta == null) {
                logger.error("Consulta não encontrada com ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            // Verifica se o usuário logado é o assistido ou voluntário da consulta
            if (!consulta.getAssistido().getIdUsuario().equals(userId) && 
                !consulta.getVoluntario().getIdUsuario().equals(userId)) {
                logger.error("Usuário {} não tem permissão para acessar a consulta {}", userId, id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok(ConsultaMapper.toDto(consulta));
        } catch (Exception e) {
            logger.error("Erro ao buscar consulta por ID: {}", e.getMessage());
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
        }    }
    
    // Implementação dos novos métodos
    @Override
    public List<ConsultaOutput> buscarConsultasPorDia(String user, LocalDate data) {
        logger.info("Buscando consultas para o usuário {} na data {}", user, data);
        
        // Obter o ID do usuário a partir do username
        Usuario usuario = obterUsuarioPorTipo(user);
        if (usuario == null) {
            logger.error("Usuário não encontrado: {}", user);
            return Collections.emptyList();
        }
        
        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.atTime(23, 59, 59);
        
        List<Consulta> consultas;
        if (user.equalsIgnoreCase("voluntario")) {
            consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioBetween(
                    usuario.getIdUsuario(), inicioDia, fimDia);
        } else if (user.equalsIgnoreCase("assistido")) {
            consultas = consultaRepository.findByAssistido_IdUsuarioAndHorarioBetween(
                    usuario.getIdUsuario(), inicioDia, fimDia);
        } else {
            logger.error("Tipo de usuário não suportado: {}", user);
            return Collections.emptyList();
        }
        
        return mapConsultasToConsultaOutput(consultas);
    }

    @Override
    public List<ConsultaOutput> buscarHistoricoConsultas(String user) {
        logger.info("Buscando histórico de consultas para o usuário {}", user);
        
        // Obter o ID do usuário a partir do username
        Usuario usuario = obterUsuarioPorTipo(user);
        if (usuario == null) {
            logger.error("Usuário não encontrado: {}", user);
            return Collections.emptyList();
        }
          List<StatusConsulta> statusConcluidos = Arrays.asList(
                StatusConsulta.REALIZADA, 
                StatusConsulta.CONCLUIDA,
                StatusConsulta.CANCELADA);
        
        List<Consulta> consultas;
        if (user.equalsIgnoreCase("voluntario")) {
            consultas = consultaRepository.findByVoluntario_IdUsuarioAndStatusIn(
                    usuario.getIdUsuario(), statusConcluidos);
        } else if (user.equalsIgnoreCase("assistido")) {
            consultas = consultaRepository.findByAssistido_IdUsuarioAndStatusIn(
                    usuario.getIdUsuario(), statusConcluidos);
        } else {
            logger.error("Tipo de usuário não suportado: {}", user);
            return Collections.emptyList();
        }
        
        return mapConsultasToConsultaOutput(consultas);
    }

    @Override
    public ConsultaOutput buscarConsultaPorIdEUsuario(Integer consultaId, String user) {
        logger.info("Buscando consulta com ID {} para o usuário {}", consultaId, user);
        
        // Obter o ID do usuário a partir do username
        Usuario usuario = obterUsuarioPorTipo(user);
        if (usuario == null) {
            logger.error("Usuário não encontrado: {}", user);
            return null;
        }
        
        // Buscar a consulta pelo ID
        Consulta consulta = consultaRepository.findById(consultaId)
                .orElse(null);
        
        if (consulta == null) {
            logger.error("Consulta não encontrada com ID: {}", consultaId);
            return null;
        }
        
        // Verificar se o usuário tem permissão para ver essa consulta
        boolean autorizado = false;
        if (user.equalsIgnoreCase("voluntario") && 
                usuario.getIdUsuario().equals(consulta.getVoluntario().getIdUsuario())) {
            autorizado = true;
        } else if (user.equalsIgnoreCase("assistido") && 
                usuario.getIdUsuario().equals(consulta.getAssistido().getIdUsuario())) {
            autorizado = true;
        }
        
        if (!autorizado) {
            logger.error("Usuário {} não tem permissão para acessar a consulta {}", user, consultaId);
            return null;
        }
        
        return mapConsultaToConsultaOutput(consulta);
    }

    @Override
    public List<ConsultaOutput> buscarProximasConsultas(String user) {
        logger.info("Buscando próximas 3 consultas para o usuário {}", user);
        
        // Obter o ID do usuário a partir do username
        Usuario usuario = obterUsuarioPorTipo(user);
        if (usuario == null) {
            logger.error("Usuário não encontrado: {}", user);
            return Collections.emptyList();
        }
        
        LocalDateTime agora = LocalDateTime.now();        List<StatusConsulta> statusPendentes = Arrays.asList(
                StatusConsulta.AGENDADA, 
                StatusConsulta.REAGENDADA,
                StatusConsulta.EM_ANDAMENTO);
        
        List<Consulta> consultas;
        if (user.equalsIgnoreCase("voluntario")) {
            consultas = consultaRepository.findByVoluntario_IdUsuarioAndHorarioAfterOrderByHorarioAsc(
                    usuario.getIdUsuario(), agora);
        } else if (user.equalsIgnoreCase("assistido")) {
            consultas = consultaRepository.findByAssistido_IdUsuarioAndHorarioAfterOrderByHorarioAsc(
                    usuario.getIdUsuario(), agora);
        } else {
            logger.error("Tipo de usuário não suportado: {}", user);
            return Collections.emptyList();
        }
        
        // Filtrar apenas consultas com status pendentes e limitar a 3
        List<Consulta> consultasFiltradas = consultas.stream()
                .filter(c -> statusPendentes.contains(c.getStatus()))
                .limit(3)
                .collect(Collectors.toList());
        
        return mapConsultasToConsultaOutput(consultasFiltradas);
    }

    @Override
    public void remarcarConsulta(Integer id, ConsultaRemarcarInput input) {
        logger.info("Remarcando consulta com ID {} para um novo horário {}", id, input.getNovoHorario());
        
        // Buscar a consulta pelo ID
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Consulta não encontrada com ID: {}", id);
                    return new RuntimeException("Consulta não encontrada");
                });
        
        // Atualizar os dados da consulta
        consulta.setHorario(input.getNovoHorario());
        
        if (input.getModalidade() != null) {
            consulta.setModalidade(input.getModalidade());
        }
        
        if (input.getLocal() != null && !input.getLocal().isEmpty()) {
            consulta.setLocal(input.getLocal());
        }
        
        if (input.getObservacoes() != null && !input.getObservacoes().isEmpty()) {
            consulta.setObservacoes(input.getObservacoes());
        }
          // Alterar o status para REAGENDADA
        consulta.setStatus(StatusConsulta.REAGENDADA);
        
        // Salvar a consulta atualizada
        consultaRepository.save(consulta);
        
        logger.info("Consulta remarcada com sucesso para {}", input.getNovoHorario());
    }
    
    @Override
    public ResponseEntity<ConsultaDto> cancelarConsulta(Integer id) {
        logger.info("Cancelando consulta com ID {}", id);
        
        try {
            // Buscar a consulta pelo ID
            Consulta consulta = consultaRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Consulta não encontrada com ID: {}", id);
                        return new RuntimeException("Consulta não encontrada");
                    });
            
            // Verificar se a consulta pode ser cancelada (não está já cancelada ou realizada)
            if (consulta.getStatus() == StatusConsulta.CANCELADA) {
                logger.warn("Tentativa de cancelar consulta já cancelada com ID: {}", id);
                return ResponseEntity.badRequest().build();
            }
            
            if (consulta.getStatus() == StatusConsulta.REALIZADA || 
                consulta.getStatus() == StatusConsulta.CONCLUIDA) {
                logger.warn("Tentativa de cancelar consulta já realizada com ID: {}", id);
                return ResponseEntity.badRequest().build();
            }
            
            // Verificar se o usuário logado tem permissão para cancelar esta consulta
            Usuario usuarioLogado = getUsuarioLogado();
            if (usuarioLogado == null) {
                logger.error("Usuário não autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            boolean podeCancel = false;
            if (consulta.getAssistido() != null && 
                usuarioLogado.getIdUsuario().equals(consulta.getAssistido().getIdUsuario())) {
                podeCancel = true;
            } else if (consulta.getVoluntario() != null && 
                       usuarioLogado.getIdUsuario().equals(consulta.getVoluntario().getIdUsuario())) {
                podeCancel = true;
            }
            
            if (!podeCancel) {
                logger.error("Usuário {} não tem permissão para cancelar a consulta {}", 
                           usuarioLogado.getIdUsuario(), id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Atualizar o status da consulta para CANCELADA
            consulta.setStatus(StatusConsulta.CANCELADA);
            
            // Salvar a consulta atualizada
            Consulta consultaCancelada = consultaRepository.save(consulta);
            
            // Mapear para DTO e retornar
            ConsultaDto consultaDto = ConsultaMapper.toDto(consultaCancelada);
            
            logger.info("Consulta cancelada com sucesso. ID: {}", id);
            return ResponseEntity.ok(consultaDto);
            
        } catch (Exception e) {
            logger.error("Erro ao cancelar consulta com ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Método auxiliar para mapear Lista de Consultas para Lista de ConsultaOutput
    private List<ConsultaOutput> mapConsultasToConsultaOutput(List<Consulta> consultas) {
        return consultas.stream()
                .map(this::mapConsultaToConsultaOutput)
                .collect(Collectors.toList());
    }
    
    // Método auxiliar para mapear uma Consulta para ConsultaOutput
    private ConsultaOutput mapConsultaToConsultaOutput(Consulta consulta) {
        ConsultaOutput output = new ConsultaOutput();
        output.setIdConsulta(consulta.getIdConsulta());
        output.setHorario(consulta.getHorario());
        output.setStatus(consulta.getStatus());
        output.setModalidade(consulta.getModalidade());
        output.setLocal(consulta.getLocal());
        output.setObservacoes(consulta.getObservacoes());
        output.setEspecialidade(consulta.getEspecialidade());
        output.setAssistido(consulta.getAssistido());
        output.setVoluntario(consulta.getVoluntario());
        return output;
    }
    
    // Método auxiliar para obter um usuário por tipo
    private Usuario obterUsuarioPorTipo(String tipoUsuario) {
        Usuario usuarioLogado = getUsuarioLogado();
        if (usuarioLogado == null) {
            return null;
        }
        
        // Se o tipo do usuário logado coincide com o tipo solicitado, retorna o usuário logado
        if ((tipoUsuario.equalsIgnoreCase("voluntario") && usuarioLogado.isVoluntario()) ||
            (tipoUsuario.equalsIgnoreCase("assistido") && !usuarioLogado.isVoluntario())) {
            return usuarioLogado;
        }
        
        // Caso contrário, retorna nulo
        return null;
    }
}