package org.com.imaapi.service;

import lombok.RequiredArgsConstructor;
import org.com.imaapi.model.enums.Genero;
import org.com.imaapi.model.usuario.*;
import org.com.imaapi.model.usuario.input.AssistenteSocialInput;
import org.com.imaapi.model.usuario.input.EnderecoInput;
import org.com.imaapi.model.usuario.output.AssistenteSocialOutput;
import org.com.imaapi.repository.FichaRepository;
import org.com.imaapi.repository.TelefoneRepository;
import org.com.imaapi.repository.UsuarioRepository;
import org.com.imaapi.repository.VoluntarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssistenteSocialService {
    private final UsuarioRepository usuarioRepository;
    private final FichaRepository fichaRepository;
    private final TelefoneRepository telefoneRepository;
    private final VoluntarioRepository voluntarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EnderecoService enderecoService;

    @Transactional
    public AssistenteSocialOutput cadastrarAssistenteSocial(AssistenteSocialInput input) {
        // Create and set up Endereco
        EnderecoInput enderecoInput = new EnderecoInput();
        enderecoInput.setCep(input.getCep());
        enderecoInput.setComplemento(input.getComplemento());

        Endereco endereco = enderecoService.criarOuAtualizarEndereco(enderecoInput);
        // Create and set up Ficha with all user data
        Ficha ficha = new Ficha();        
        ficha.setNome(input.getNome());
        ficha.setSobrenome(input.getSobrenome());
        ficha.setCpf(input.getCpf());
        ficha.setDtNascim(input.getDataNascimento() != null ? LocalDate.parse(input.getDataNascimento()) : null);
        ficha.setGenero(Genero.fromString(input.getGenero()));
        ficha.setProfissao(input.getProfissao());
        ficha.setEndereco(endereco);

        // Save ficha first to get the ID
        fichaRepository.save(ficha);

        // Create and set up Telefone with proper structure
        Telefone telefone = new Telefone();
        telefone.setFicha(ficha);
        telefone.setDdd(input.getDdd());

        // Split the phone number into prefixo and sufixo
        String numeroCompleto = input.getNumero();
        if (numeroCompleto != null && numeroCompleto.length() >= 8) {
            // For Brazilian phone numbers: format NNNNN-NNNN or NNNN-NNNN
            if (numeroCompleto.length() == 9) {
                telefone.setPrefixo(numeroCompleto.substring(0, 5));
                telefone.setSufixo(numeroCompleto.substring(5));
            } else if (numeroCompleto.length() == 8) {
                telefone.setPrefixo(numeroCompleto.substring(0, 4));
                telefone.setSufixo(numeroCompleto.substring(4));
            } else {
                // Default split for other lengths
                int splitPoint = numeroCompleto.length() - 4;
                telefone.setPrefixo(numeroCompleto.substring(0, splitPoint));
                telefone.setSufixo(numeroCompleto.substring(splitPoint));
            }
        }
        telefone.setWhatsapp(true); // Default assumption for social workers

        telefoneRepository.save(telefone);        // Create and set up Usuario

        Usuario usuario = new Usuario();
        usuario.setFicha(ficha);
        usuario.setEmail(input.getEmail());
        usuario.setSenha(passwordEncoder.encode(input.getSenha()));
        usuario.setTipo(input.getTipo());

        usuarioRepository.save(usuario);        // Create and set up Voluntario for professional data
        // Verificar se já existe um voluntário para este usuário
        Voluntario voluntarioExistente = voluntarioRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());
        if (voluntarioExistente != null) {
            throw new IllegalArgumentException("Já existe um voluntário cadastrado para este usuário");
        }

        Voluntario voluntario = new Voluntario();
        voluntario.setFkUsuario(usuario.getIdUsuario());
        voluntario.setUsuario(usuario);
        voluntario.setFuncao(input.getFuncao());
        voluntario.setDataCadastro(LocalDate.now());
        voluntario.setRegistroProfissional(input.getCrp());
        voluntario.setBiografiaProfissional(input.getBio());

        voluntarioRepository.save(voluntario);

        return converterParaOutput(usuario);
    }

    @Transactional
    public AssistenteSocialOutput atualizarAssistenteSocial(Integer idUsuario, AssistenteSocialInput input) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Assistente Social não encontrado"));
        Ficha ficha = usuario.getFicha();
        ficha.setNome(input.getNome());
        ficha.setSobrenome(input.getSobrenome());
        ficha.setCpf(input.getCpf());
        ficha.setDtNascim(input.getDataNascimento() != null ? LocalDate.parse(input.getDataNascimento()) : null);
        ficha.setGenero(Genero.fromString(input.getGenero()));
        ficha.setProfissao(input.getProfissao());

        // Update endereco
        EnderecoInput enderecoInput = new EnderecoInput();
        enderecoInput.setCep(input.getCep());
        enderecoInput.setComplemento(input.getComplemento());

        Endereco endereco = enderecoService.criarOuAtualizarEndereco(enderecoInput);
        ficha.setEndereco(endereco);

        // Update telefone
        List<Telefone> telefones = telefoneRepository.findByFichaIdFicha(ficha.getIdFicha());
        Telefone telefone;
        if (telefones.isEmpty()) {
            telefone = new Telefone();
            telefone.setFicha(ficha);
        } else {
            telefone = telefones.get(0); // Get the first phone number
        }

        telefone.setDdd(input.getDdd());

        // Split the phone number into prefixo and sufixo
        String numeroCompleto = input.getNumero();
        if (numeroCompleto != null && numeroCompleto.length() >= 8) {
            if (numeroCompleto.length() == 9) {
                telefone.setPrefixo(numeroCompleto.substring(0, 5));
                telefone.setSufixo(numeroCompleto.substring(5));
            } else if (numeroCompleto.length() == 8) {
                telefone.setPrefixo(numeroCompleto.substring(0, 4));
                telefone.setSufixo(numeroCompleto.substring(4));
            } else {
                int splitPoint = numeroCompleto.length() - 4;
                telefone.setPrefixo(numeroCompleto.substring(0, splitPoint));
                telefone.setSufixo(numeroCompleto.substring(splitPoint));
            }
        }

        telefoneRepository.save(telefone);

        // Save ficha changes
        fichaRepository.save(ficha);

        // Update usuario

        usuario.setEmail(input.getEmail());
        if (input.getSenha() != null && !input.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(input.getSenha()));
        }

        usuarioRepository.save(usuario);

        // Update or create Voluntario for professional data
        Voluntario voluntario = voluntarioRepository.findByUsuario_IdUsuario(idUsuario);
        if (voluntario == null) {
            voluntario = new Voluntario();
            voluntario.setFkUsuario(usuario.getIdUsuario());
            voluntario.setUsuario(usuario);
            voluntario.setDataCadastro(LocalDate.now());
        }
        voluntario.setFuncao(input.getFuncao());
        voluntario.setRegistroProfissional(input.getCrp());
        voluntario.setBiografiaProfissional(input.getBio());

        voluntarioRepository.save(voluntario);

        return converterParaOutput(usuario);
    }

    public AssistenteSocialOutput buscarAssistenteSocial(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Assistente Social não encontrado"));

        return converterParaOutput(usuario);
    }

    private AssistenteSocialOutput converterParaOutput(Usuario usuario) {

        AssistenteSocialOutput output = new AssistenteSocialOutput();
        output.setIdUsuario(usuario.getIdUsuario());
        output.setNome(usuario.getFicha().getNome());
        output.setSobrenome(usuario.getFicha().getSobrenome());
        output.setEmail(usuario.getEmail());
        output.setFotoUrl(usuario.getFotoUrl());
        output.setEndereco(usuario.getFicha().getEndereco());

        // Get professional details from Voluntario entity
        Voluntario voluntario = voluntarioRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());
        if (voluntario != null) {
            output.setCrp(voluntario.getRegistroProfissional());
            output.setBio(voluntario.getBiografiaProfissional());
            if (voluntario.getFuncao() != null) {
                output.setEspecialidade(voluntario.getFuncao().getValue());
            }
        }

        // Get formatted telefone from telefone table
        List<Telefone> telefones = telefoneRepository.findByFichaIdFicha(usuario.getFicha().getIdFicha());
        if (!telefones.isEmpty()) {
            Telefone telefone = telefones.get(0);
            if (telefone.getDdd() != null && telefone.getPrefixo() != null && telefone.getSufixo() != null) {
                output.setTelefone(String.format("(%s) %s-%s",
                        telefone.getDdd(),
                        telefone.getPrefixo(),
                        telefone.getSufixo()));
            }
        }

        return output;
    }
}
