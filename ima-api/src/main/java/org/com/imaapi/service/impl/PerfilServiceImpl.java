package org.com.imaapi.service.impl;

import org.com.imaapi.model.usuario.*;
import org.com.imaapi.model.usuario.input.UsuarioInputAtualizacaoDadosPessoais;
import org.com.imaapi.model.usuario.input.VoluntarioDadosProfissionaisInput;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.model.usuario.output.UsuarioDadosPessoaisOutput;
import org.com.imaapi.model.usuario.output.UsuarioOutput;
import org.com.imaapi.model.especialidade.Especialidade;
import org.com.imaapi.model.enums.TipoUsuario;
import org.com.imaapi.model.enums.Funcao;
import org.com.imaapi.repository.*;
import org.com.imaapi.service.EnderecoService;
import org.com.imaapi.service.FotoService;
import org.com.imaapi.service.PerfilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PerfilServiceImpl implements PerfilService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PerfilServiceImpl.class);

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EnderecoService enderecoService;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FotoService fotoService;
    @Autowired
    private VoluntarioRepository voluntarioRepository;
    @Autowired
    private TelefoneRepository telefoneRepository;
    @Autowired
    private EspecialidadeRepository especialidadeRepository;
    @Autowired
    private VoluntarioEspecialidadeRepository voluntarioEspecialidadeRepository;

    @Override
    public UsuarioDadosPessoaisOutput buscarDadosPessoaisPorId(Integer usuarioId) {
        LOGGER.info("Buscando dados pessoais para o usuário com ID: {}", usuarioId);
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        if (usuario == null) {
            LOGGER.warn("Usuário não encontrado para o ID: {}", usuarioId);
            return null;
        }

        Ficha ficha = usuario.getFicha();
        if (ficha == null) {
            LOGGER.warn("Ficha não encontrada para o usuário com ID: {}", usuarioId);
            return null;
        }

        UsuarioDadosPessoaisOutput dadosPessoais = new UsuarioDadosPessoaisOutput();
        dadosPessoais.setNome(ficha.getNome());
        dadosPessoais.setSobrenome(ficha.getSobrenome());
        dadosPessoais.setCpf(ficha.getCpf());
        dadosPessoais.setDataNascimento(ficha.getDtNascim());
        dadosPessoais.setEmail(usuario.getEmail());
        dadosPessoais.setTipo(usuario.getTipo().toString());

        // Mapear gênero da ficha
        if (ficha.getGenero() != null) {
            dadosPessoais.setGenero(ficha.getGenero());
        }

        // Buscar telefone
        List<Telefone> telefones = telefoneRepository.findByFichaIdFicha(ficha.getIdFicha());
        if (!telefones.isEmpty()) {
            Telefone telefone = telefones.get(0);
            if (telefone.getDdd() != null && telefone.getPrefixo() != null && telefone.getSufixo() != null) {
                String telefoneFormatado = String.format("(%s) %s-%s",
                        telefone.getDdd(),
                        telefone.getPrefixo(),
                        telefone.getSufixo());
                dadosPessoais.setTelefone(telefoneFormatado);
                LOGGER.info("Telefone encontrado: {}", telefoneFormatado);
            } else {
                LOGGER.warn("Telefone encontrado, mas incompleto: DDD={}, Prefixo={}, Sufixo={}",
                        telefone.getDdd(), telefone.getPrefixo(), telefone.getSufixo());
                dadosPessoais.setTelefone("");
            }
        } else {
            LOGGER.warn("Nenhum telefone encontrado para a ficha com ID: {}", ficha.getIdFicha());
            dadosPessoais.setTelefone("");
        }

        // Buscar dados profissionais se for administrador (assistente social)
        Voluntario voluntario = voluntarioRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());
        if (voluntario != null) {
            dadosPessoais.setCrp(voluntario.getRegistroProfissional());
            dadosPessoais.setBio(voluntario.getBiografiaProfissional());
            if (voluntario.getFuncao() != null) {
                dadosPessoais.setEspecialidade(voluntario.getFuncao().getValue());
            }
        }

        // Incluir URL da foto de perfil
        if (usuario.getFotoUrl() != null) {
            dadosPessoais.setFotoUrl(usuario.getFotoUrl());
        }

        return dadosPessoais;
    }

    @Override
    public UsuarioOutput atualizarDadosPessoais(Integer usuarioId, UsuarioInputAtualizacaoDadosPessoais dadosPessoais) {
        LOGGER.info("Atualizando dados pessoais para o usuário com ID: {}", usuarioId);
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        if (usuario == null) {
            LOGGER.warn("Usuário não encontrado para o ID: {}", usuarioId);
            return null;
        }

        Ficha ficha = usuario.getFicha();
        if (ficha == null) {
            LOGGER.warn("Ficha não encontrada para o usuário com ID: {}", usuarioId);
            return null;
        }

        // Atualizar dados básicos da ficha
        if (dadosPessoais.getNome() != null) {
            ficha.setNome(dadosPessoais.getNome());
        }
        if (dadosPessoais.getSobrenome() != null) {
            ficha.setSobrenome(dadosPessoais.getSobrenome());
        }
        if (dadosPessoais.getDataNascimento() != null) {
            ficha.setDtNascim(dadosPessoais.getDataNascimento());
        }
        
        // Atualizar gênero se fornecido
        if (dadosPessoais.getGenero() != null) {
            ficha.setGenero(dadosPessoais.getGenero());
            LOGGER.info("Gênero atualizado para: {}", dadosPessoais.getGenero());
        }

        // Atualizar email do usuário
        if (dadosPessoais.getEmail() != null && !dadosPessoais.getEmail().trim().isEmpty()) {
            usuario.setEmail(dadosPessoais.getEmail());
            LOGGER.info("Email atualizado para: {}", dadosPessoais.getEmail());
        }

        // Atualizar telefone se fornecido
        if (dadosPessoais.getTelefone() != null && !dadosPessoais.getTelefone().trim().isEmpty()) {
            List<Telefone> telefones = telefoneRepository.findByFichaIdFicha(ficha.getIdFicha());
            Telefone telefone;

            if (telefones.isEmpty()) {
                telefone = new Telefone();
                telefone.setFicha(ficha);
                LOGGER.info("Criando novo telefone para ficha ID: {}", ficha.getIdFicha());
            } else {
                telefone = telefones.get(0);
                LOGGER.info("Atualizando telefone existente ID: {}", telefone.getIdTelefone());
            }

            // Parse do telefone formato "(11) 98765-4321" ou "(11) 9876-5432"
            String telefoneCompleto = dadosPessoais.getTelefone().replaceAll("[()\\s-]", "");

            if (telefoneCompleto.length() >= 10) {
                String ddd = telefoneCompleto.substring(0, 2);
                String numero = telefoneCompleto.substring(2);

                telefone.setDdd(ddd);

                // Dividir número em prefixo e sufixo
                if (numero.length() == 9) { // Celular com 9 dígitos
                    telefone.setPrefixo(numero.substring(0, 5));
                    telefone.setSufixo(numero.substring(5));
                } else if (numero.length() == 8) { // Fixo com 8 dígitos
                    telefone.setPrefixo(numero.substring(0, 4));
                    telefone.setSufixo(numero.substring(4));
                } else {
                    // Fallback para outros tamanhos
                    int splitPoint = numero.length() - 4;
                    telefone.setPrefixo(numero.substring(0, splitPoint));
                    telefone.setSufixo(numero.substring(splitPoint));
                }

                if (telefone.getWhatsapp() == null) {
                    telefone.setWhatsapp(true); // Default para WhatsApp
                }

                telefoneRepository.save(telefone);
                LOGGER.info("Telefone atualizado: DDD={}, Prefixo={}, Sufixo={}", telefone.getDdd(), telefone.getPrefixo(), telefone.getSufixo());
            } else {
                LOGGER.warn("Telefone inválido fornecido: {}", dadosPessoais.getTelefone());
            }
        }

        usuarioRepository.save(usuario);
        LOGGER.info("Dados pessoais atualizados com sucesso para o usuário cgit om ID: {}", usuarioId);

        return new UsuarioOutput(
                ficha.getNome(),
                ficha.getCpf(),
                usuario.getEmail(),
                ficha.getDtNascim(),
                usuario.getTipo()
        );
    }

    @Override
    public UsuarioDadosPessoaisOutput atualizarDadosPessoaisCompleto(Integer usuarioId, UsuarioInputAtualizacaoDadosPessoais dadosPessoais) {
        LOGGER.info("Atualizando dados pessoais completos para o usuário com ID: {}", usuarioId);
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        if (usuario == null) {
            LOGGER.warn("Usuário não encontrado para o ID: {}", usuarioId);
            return null;
        }

        Ficha ficha = usuario.getFicha();
        if (ficha == null) {
            LOGGER.warn("Ficha não encontrada para o usuário com ID: {}", usuarioId);
            return null;
        }

        // Atualizar dados básicos da ficha
        if (dadosPessoais.getNome() != null) {
            ficha.setNome(dadosPessoais.getNome());
        }
        if (dadosPessoais.getSobrenome() != null) {
            ficha.setSobrenome(dadosPessoais.getSobrenome());
        }        if (dadosPessoais.getDataNascimento() != null) {
            ficha.setDtNascim(dadosPessoais.getDataNascimento());
        }
        
        // Atualizar gênero se fornecido
        if (dadosPessoais.getGenero() != null) {
            ficha.setGenero(dadosPessoais.getGenero());
            LOGGER.info("Gênero atualizado para: {}", dadosPessoais.getGenero());
        }

        // Atualizar email do usuário
        if (dadosPessoais.getEmail() != null && !dadosPessoais.getEmail().trim().isEmpty()) {
            usuario.setEmail(dadosPessoais.getEmail());
            LOGGER.info("Email atualizado para: {}", dadosPessoais.getEmail());
        }

        // Atualizar telefone
        if (dadosPessoais.getTelefone() != null && !dadosPessoais.getTelefone().trim().isEmpty()) {
            List<Telefone> telefones = telefoneRepository.findByFichaIdFicha(ficha.getIdFicha());
            Telefone telefone;

            if (telefones.isEmpty()) {
                telefone = new Telefone();
                telefone.setFicha(ficha);
                LOGGER.info("Criando novo telefone para ficha ID: {}", ficha.getIdFicha());
            } else {
                telefone = telefones.get(0);
                LOGGER.info("Atualizando telefone existente ID: {}", telefone.getIdTelefone());
            }

            // Parse do telefone formato "(11) 98765-4321" ou "(11) 9876-5432"
            String telefoneCompleto = dadosPessoais.getTelefone().replaceAll("[()\\s-]", "");

            if (telefoneCompleto.length() >= 10) {
                String ddd = telefoneCompleto.substring(0, 2);
                String numero = telefoneCompleto.substring(2);

                telefone.setDdd(ddd);

                // Dividir número em prefixo e sufixo
                if (numero.length() == 9) { // Celular com 9 dígitos
                    telefone.setPrefixo(numero.substring(0, 5));
                    telefone.setSufixo(numero.substring(5));
                } else if (numero.length() == 8) { // Fixo com 8 dígitos
                    telefone.setPrefixo(numero.substring(0, 4));
                    telefone.setSufixo(numero.substring(4));
                } else {
                    // Fallback para outros tamanhos
                    int splitPoint = numero.length() - 4;
                    telefone.setPrefixo(numero.substring(0, splitPoint));
                    telefone.setSufixo(numero.substring(splitPoint));
                }

                if (telefone.getWhatsapp() == null) {
                    telefone.setWhatsapp(true); // Default para WhatsApp
                }

                telefoneRepository.save(telefone);
                LOGGER.info("Telefone atualizado: DDD={}, Prefixo={}, Sufixo={}", telefone.getDdd(), telefone.getPrefixo(), telefone.getSufixo());
            } else {
                LOGGER.warn("Telefone inválido fornecido: {}", dadosPessoais.getTelefone());
            }        }

        // Atualizar dados profissionais se for administrador (assistente social)
        if (usuario.getTipo() == TipoUsuario.ADMINISTRADOR) {
            Voluntario voluntario = voluntarioRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());
            if (voluntario != null) {
                boolean voluntarioAtualizado = false;

                if (dadosPessoais.getCrp() != null) {
                    voluntario.setRegistroProfissional(dadosPessoais.getCrp());
                    voluntarioAtualizado = true;
                    LOGGER.info("CRP atualizado para: {}", dadosPessoais.getCrp());
                }

                if (dadosPessoais.getBio() != null) {
                    voluntario.setBiografiaProfissional(dadosPessoais.getBio());
                    voluntarioAtualizado = true;
                    LOGGER.info("Bio profissional atualizada");
                }
                if (dadosPessoais.getEspecialidade() != null) {
                    try {
                        Funcao funcao = Funcao.fromValue(dadosPessoais.getEspecialidade());
                        voluntario.setFuncao(funcao);
                        voluntarioAtualizado = true;
                        LOGGER.info("Especialidade atualizada para: {}", dadosPessoais.getEspecialidade());
                    } catch (Exception e) {
                        LOGGER.warn("Especialidade inválida fornecida: {}", dadosPessoais.getEspecialidade());
                    }
                }

                if (voluntarioAtualizado) {
                    voluntarioRepository.save(voluntario);
                }
            }
        }

        // Salvar as alterações

        usuarioRepository.save(usuario);
        LOGGER.info("Dados pessoais completos atualizados com sucesso para o usuário com ID: {}", usuarioId);        // Retornar dados atualizados
        UsuarioDadosPessoaisOutput output = new UsuarioDadosPessoaisOutput();
        output.setNome(ficha.getNome());
        output.setSobrenome(ficha.getSobrenome());
        output.setCpf(ficha.getCpf());
        output.setDataNascimento(ficha.getDtNascim());
        output.setEmail(usuario.getEmail());
        output.setTipo(usuario.getTipo().toString());
        
        // Incluir gênero no output
        if (ficha.getGenero() != null) {
            output.setGenero(ficha.getGenero());
        }// Buscar telefone atualizado para retorno
        List<Telefone> telefonesAtualizados = telefoneRepository.findByFichaIdFicha(ficha.getIdFicha());
        if (!telefonesAtualizados.isEmpty()) {
            Telefone telefoneAtualizado = telefonesAtualizados.get(0);
            String telefoneFormatado = String.format("(%s) %s-%s",
                    telefoneAtualizado.getDdd(),
                    telefoneAtualizado.getPrefixo(),
                    telefoneAtualizado.getSufixo());
            output.setTelefone(telefoneFormatado);
        } else {
            output.setTelefone("");
        }        // Incluir dados profissionais atualizados se for administrador
        if (usuario.getTipo() == TipoUsuario.ADMINISTRADOR) {
            Voluntario voluntario = voluntarioRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());
            if (voluntario != null) {
                output.setCrp(voluntario.getRegistroProfissional());
                output.setBio(voluntario.getBiografiaProfissional());
                if (voluntario.getFuncao() != null) {
                    output.setEspecialidade(voluntario.getFuncao().getValue());
                }
            }
        }
        return output;
    }

    @Override
    public EnderecoOutput buscarEnderecoPorId(Integer usuarioId) {
        LOGGER.info("Buscando endereço para o usuário com ID: {}", usuarioId);
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        if (usuario == null) {
            LOGGER.warn("Usuário não encontrado para o ID: {}", usuarioId);
            return null;
        }

        Ficha ficha = usuario.getFicha();
        if (ficha == null || ficha.getEndereco() == null) {
            LOGGER.warn("Ficha ou endereço não encontrado para o usuário com ID: {}", usuarioId);
            return null;
        }

        Endereco endereco = ficha.getEndereco();
        EnderecoOutput enderecoOutput = new EnderecoOutput();
        enderecoOutput.setCep(endereco.getCep());
        enderecoOutput.setNumero(endereco.getNumero());
        enderecoOutput.setComplemento(endereco.getComplemento());
        enderecoOutput.setLogradouro(endereco.getLogradouro());
        enderecoOutput.setBairro(endereco.getBairro());
        enderecoOutput.setLocalidade(endereco.getCidade());
        enderecoOutput.setUf(endereco.getUf());

        LOGGER.info("Endereço encontrado - CEP: {}, Número: {}, Cidade: {}",
                endereco.getCep(), endereco.getNumero(), endereco.getCidade());

        return enderecoOutput;
    }

    @Override
    public boolean atualizarEnderecoPorUsuarioId(Integer usuarioId, String cep, String numero, String complemento) {
        LOGGER.info("Iniciando atualização de endereço para o usuário com ID: {}", usuarioId);
        try {
            Usuario usuario = buscarUsuarioPorId(usuarioId);
            if (usuario == null) {
                LOGGER.warn("Usuário não encontrado para o ID: {}", usuarioId);
                return false;
            }

            Ficha ficha = usuario.getFicha();
            if (ficha == null) {
                LOGGER.warn("Ficha não encontrada para o usuário com ID: {}", usuarioId);
                return false;
            }

            EnderecoOutput enderecoApi = enderecoService.buscaEndereco(cep, numero, complemento).getBody();
            if (enderecoApi == null) {
                LOGGER.warn("Endereço não encontrado na API para o CEP: {}", cep);
                return false;
            }

            Endereco endereco = ficha.getEndereco();
            if (endereco == null) {
                endereco = new Endereco();
                ficha.setEndereco(endereco);
            }

            endereco.setCep(enderecoApi.getCep());
            endereco.setLogradouro(enderecoApi.getLogradouro());
            endereco.setBairro(enderecoApi.getBairro());
            endereco.setCidade(enderecoApi.getLocalidade());
            endereco.setUf(enderecoApi.getUf());
            endereco.setNumero(numero);
            endereco.setComplemento(complemento);

            enderecoRepository.save(endereco);
            usuarioRepository.save(usuario);

            LOGGER.info("Endereço atualizado com sucesso para o usuário com ID: {}", usuarioId);
            return true;
        } catch (Exception e) {
            LOGGER.error("Erro ao atualizar endereço para o usuário com ID: {}", usuarioId, e);
            return false;
        }
    }

    @Override
    public String salvarFoto(Integer usuarioId, String tipo, MultipartFile file) throws IOException {
        LOGGER.info("Salvando foto para o usuário com ID: {}", usuarioId);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        String fotoUrl = fotoService.salvarFoto(tipo, usuarioId, file);
        usuario.setFotoUrl(fotoUrl);
        usuarioRepository.save(usuario);

        LOGGER.info("Foto salva com sucesso para o usuário com ID: {}", usuarioId);
        return fotoUrl;
    }

    @Override
    @Transactional
    public boolean atualizarDadosProfissionais(Integer usuarioId, VoluntarioDadosProfissionaisInput dadosProfissionais) {
        LOGGER.info("Atualizando dados profissionais para o voluntário com ID de usuário: {}", usuarioId);
        LOGGER.debug("Dados recebidos: {}", dadosProfissionais);

        try {
            Voluntario voluntario = voluntarioRepository.findByUsuario_IdUsuario(usuarioId);
            if (voluntario == null) {
                LOGGER.warn("Voluntário não encontrado para o ID de usuário: {}", usuarioId);
                return false;
            }

            // Atualizar dados profissionais
            if (dadosProfissionais.getRegistroProfissional() != null) {
                LOGGER.debug("Atualizando registro profissional: {}", dadosProfissionais.getRegistroProfissional());
                voluntario.setRegistroProfissional(dadosProfissionais.getRegistroProfissional());
            }

            if (dadosProfissionais.getBiografiaProfissional() != null) {
                LOGGER.debug("Atualizando biografia profissional");
                voluntario.setBiografiaProfissional(dadosProfissionais.getBiografiaProfissional());
            }

            // Atualizar função apenas se for fornecida
            if (dadosProfissionais.getFuncao() != null) {
                LOGGER.debug("Atualizando função: {}", dadosProfissionais.getFuncao());
                try {
                    voluntario.setFuncao(dadosProfissionais.getFuncao());
                } catch (Exception e) {
                    LOGGER.error("Erro ao atualizar função: {}", e.getMessage());
                }
            }

            // Atualizar especialidades
            if (dadosProfissionais.getEspecialidade() != null) {
                LOGGER.debug("Atualizando especialidade principal: {}", dadosProfissionais.getEspecialidade());
                atualizarEspecialidadesVoluntario(
                        voluntario,
                        dadosProfissionais.getEspecialidade(),
                        dadosProfissionais.getEspecialidades()
                );
            }

            voluntarioRepository.save(voluntario);
            LOGGER.info("Dados profissionais atualizados com sucesso para o voluntário com ID de usuário: {}", usuarioId);
            return true;
        } catch (Exception e) {
            LOGGER.error("Erro ao atualizar dados profissionais: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean criarDisponibilidade(Integer usuarioId, Map<String, Object> disponibilidade) {
        LOGGER.info("Criando disponibilidade para o voluntário com ID de usuário: {}", usuarioId);
        Voluntario voluntario = voluntarioRepository.findByUsuario_IdUsuario(usuarioId);
        if (voluntario == null) {
            LOGGER.warn("Voluntário não encontrado para o ID de usuário: {}", usuarioId);
            return false;
        }

        // Lógica para criar disponibilidade (exemplo: salvar no banco)
        // Exemplo: voluntario.setDisponibilidade(disponibilidade);

        voluntarioRepository.save(voluntario);
        LOGGER.info("Disponibilidade criada com sucesso para o voluntário com ID de usuário: {}", usuarioId);
        return true;
    }

    @Override
    public boolean atualizarDisponibilidade(Integer usuarioId, Map<String, Object> disponibilidade) {
        LOGGER.info("Atualizando disponibilidade para o voluntário com ID de usuário: {}", usuarioId);
        Voluntario voluntario = voluntarioRepository.findByUsuario_IdUsuario(usuarioId);
        if (voluntario == null) {
            LOGGER.warn("Voluntário não encontrado para o ID de usuário: {}", usuarioId);
            return false;
        }

        // Lógica para atualizar disponibilidade (exemplo: atualizar campos específicos)
        // Exemplo: voluntario.setDisponibilidade(disponibilidade);

        voluntarioRepository.save(voluntario);
        LOGGER.info("Disponibilidade atualizada com sucesso para o voluntário com ID de usuário: {}", usuarioId);
        return true;
    }

    private Usuario buscarUsuarioPorId(Integer usuarioId) {
        LOGGER.debug("Consultando repositório para o usuário com ID: {}", usuarioId);
        return usuarioRepository.findById(usuarioId).orElse(null);
    }

    private UsuarioOutput converterParaUsuarioOutput(UsuarioDadosPessoaisOutput dadosPessoais) {
        if (dadosPessoais == null) {
            LOGGER.error("Erro: Dados pessoais não podem ser nulos.");
            throw new IllegalArgumentException("Dados pessoais não podem ser nulos.");
        }
        return new UsuarioOutput(
                dadosPessoais.getNome(),
                dadosPessoais.getCpf(),
                dadosPessoais.getEmail(),
                dadosPessoais.getDataNascimento(),
                TipoUsuario.valueOf(dadosPessoais.getTipo())
        );
    }

    private void atualizarEspecialidadesVoluntario(Voluntario voluntario, String especialidadePrincipal, List<String> especialidadesAdicionais) {
        // Buscar ou criar a especialidade principal
        Especialidade especialidadePrincipalEntity = especialidadeRepository.findByNome(especialidadePrincipal)
                .orElseGet(() -> {
                    Especialidade novaEspecialidade = new Especialidade();
                    novaEspecialidade.setNome(especialidadePrincipal);
                    return especialidadeRepository.save(novaEspecialidade);
                });

        // Remover todas as especialidades antigas
        voluntarioEspecialidadeRepository.deleteByVoluntario(voluntario);

        // Adicionar a especialidade principal
        VoluntarioEspecialidade principalVE = new VoluntarioEspecialidade();
        principalVE.setVoluntario(voluntario);
        principalVE.setEspecialidade(especialidadePrincipalEntity);
        principalVE.setPrincipal(true);
        voluntarioEspecialidadeRepository.save(principalVE);

        // Adicionar especialidades adicionais
        if (especialidadesAdicionais != null && !especialidadesAdicionais.isEmpty()) {
            for (String nomeEspecialidade : especialidadesAdicionais) {
                if (!nomeEspecialidade.equals(especialidadePrincipal)) {
                    Especialidade especialidade = especialidadeRepository.findByNome(nomeEspecialidade)
                            .orElseGet(() -> {
                                Especialidade novaEspecialidade = new Especialidade();
                                novaEspecialidade.setNome(nomeEspecialidade);
                                return especialidadeRepository.save(novaEspecialidade);
                            });

                    VoluntarioEspecialidade ve = new VoluntarioEspecialidade();
                    ve.setVoluntario(voluntario);
                    ve.setEspecialidade(especialidade);
                    ve.setPrincipal(false);
                    voluntarioEspecialidadeRepository.save(ve);
                }
            }
        }
    }

//    private UsuarioOutput converterParaUsuarioOutput(Usuario usuario) {
//        UsuarioOutput usuarioOutput = new UsuarioOutput(usuario);
//        usuarioOutput.setNome(usuario.getNome());
//        usuarioOutput.setCpf(usuario.getCpf());
//        usuarioOutput.setEmail(usuario.getEmail());
//        usuarioOutput.setDataNascimento(usuario.getDataNascimento());
//        usuarioOutput.setTipo(usuario.getTipo());
//        if (usuario.getEndereco() != null) {
//            EnderecoOutput enderecoOutput = new EnderecoOutput();
//            enderecoOutput.setCep(usuario.getEndereco().getCep());
//            enderecoOutput.setNumero(usuario.getEndereco().getNumero());
//            enderecoOutput.setComplemento(usuario.getEndereco().getComplemento());
//            enderecoOutput.setLogradouro(usuario.getEndereco().getLogradouro());
//            enderecoOutput.setBairro(usuario.getEndereco().getBairro());
//            enderecoOutput.setLocalidade(usuario.getEndereco().getLocalidade());
//            enderecoOutput.setUf(usuario.getEndereco().getUf());
//            usuarioOutput.setEndereco(enderecoOutput);
//        }
//        return usuarioOutput;
//    }
}