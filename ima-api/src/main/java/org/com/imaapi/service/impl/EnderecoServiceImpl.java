package org.com.imaapi.service.impl;

import org.com.imaapi.model.usuario.Endereco;
import org.com.imaapi.model.usuario.input.EnderecoInput;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.repository.EnderecoRepository;
import org.com.imaapi.service.EnderecoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class EnderecoServiceImpl implements EnderecoService {
    private static final String ViaCepApi = "https://viacep.com.br/ws/%s/json/";
    private static final Logger LOGGER = LoggerFactory.getLogger(EnderecoService.class);

    private final EnderecoRepository enderecoRepository;

    public EnderecoServiceImpl(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    @Override
    public ResponseEntity<EnderecoOutput> buscaEndereco(String cep, String numero, String complemento) {
        if (cep == null || cep.trim().isEmpty()) {
            throw new IllegalArgumentException("O CEP não pode ser nulo ou vazio.");
        }

        cep = formatarCep(cep);

        if (!isValidCep(cep)) {
            throw new IllegalArgumentException("CEP inválido. Deve conter 8 dígitos.");
        }

        Optional<Endereco> enderecoExistenteOpt = enderecoRepository.findByCepAndNumero(cep, numero);

        if (enderecoExistenteOpt.isPresent()) {
            Endereco enderecoExistente = enderecoExistenteOpt.get();

            if (complemento != null && !complemento.trim().isEmpty()
                    && !complemento.equals(enderecoExistente.getComplemento())) {
                enderecoExistente.setComplemento(complemento);
                enderecoExistente = enderecoRepository.save(enderecoExistente);
            }

            return ResponseEntity.ok(converterParaEnderecoOutput(enderecoExistente));
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(ViaCepApi, cep);
        ResponseEntity<EnderecoOutput> response = restTemplate.getForEntity(url, EnderecoOutput.class);
        EnderecoOutput enderecoOutput = response.getBody();

        if (enderecoOutput == null || enderecoOutput.getCep() == null) {
            throw new RuntimeException("Não consegui obter o endereço com esse CEP: " + cep);
        }

        enderecoOutput.setCep(formatarCep(enderecoOutput.getCep()));
        enderecoOutput.setNumero(numero);
        enderecoOutput.setComplemento(complemento);
        Endereco novoEndereco = cadastrarEndereco(enderecoOutput, complemento);
        return ResponseEntity.ok(converterParaEnderecoOutput(novoEndereco));
    }

    @Override
    public List<EnderecoOutput> listarEnderecos() {
        List<Endereco> enderecos = enderecoRepository.findAll();
        return enderecos.stream()
                .map(this::converterParaEnderecoOutput)
                .toList();
    }

    @Override
    public Endereco cadastrarEndereco(EnderecoOutput enderecoOutput, String complemento) {
        if (enderecoOutput == null || enderecoOutput.getCep() == null) {
            throw new IllegalArgumentException("EndereçoOutput inválido para cadastro");
        }

        String cep = formatarCep(enderecoOutput.getCep());
        if (!isValidCep(cep)) {
            throw new IllegalArgumentException("CEP inválido. Deve conter 8 dígitos.");
        }

        Endereco endereco = new Endereco();
        endereco.setCep(cep);
        endereco.setLogradouro(enderecoOutput.getLogradouro());
        endereco.setBairro(enderecoOutput.getBairro());
        endereco.setNumero(enderecoOutput.getNumero());
        endereco.setUf(enderecoOutput.getUf());
        endereco.setCidade(enderecoOutput.getLocalidade());
        endereco.setComplemento(complemento);
        return enderecoRepository.save(endereco);
    }    @Override
    public Endereco criarOuAtualizarEndereco(EnderecoInput enderecoInput) {
        if (enderecoInput == null) {
            throw new IllegalArgumentException("O objeto EnderecoInput não pode ser nulo.");
        }
        
        LOGGER.info("Iniciando criação/atualização de endereço com CEP: {}", enderecoInput.getCep());
        
        String cep = formatarCep(enderecoInput.getCep());
        String numero = enderecoInput.getNumero();
        String complemento = enderecoInput.getComplemento();

        if (!isValidCep(cep)) {
            throw new IllegalArgumentException("CEP inválido. Deve conter 8 dígitos.");
        }
        
        // Primeiro verifica se já existe um endereço com esse CEP e número
        Optional<Endereco> enderecoExistente = enderecoRepository.findByCepAndNumero(cep, numero);
        
        if (enderecoExistente.isPresent()) {
            Endereco endereco = enderecoExistente.get();
            if (complemento != null && !complemento.equals(endereco.getComplemento())) {
                endereco.setComplemento(complemento);
                endereco = enderecoRepository.save(endereco);
                LOGGER.info("Endereço atualizado com sucesso: {}", endereco);
            }
            return endereco;
        }

        // Se não existe, busca o endereço na API do ViaCEP e cria um novo
        EnderecoOutput enderecoDetalhes = null;
        try {
            ResponseEntity<EnderecoOutput> response = buscaEndereco(cep, numero, complemento);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                enderecoDetalhes = response.getBody();
            }
        } catch (Exception e) {
            LOGGER.error("Erro ao buscar detalhes do CEP: {}", e.getMessage());
        }
        
        if (enderecoDetalhes == null) {
            LOGGER.error("Não foi possível obter os dados do endereço para o CEP: {}", cep);
            throw new RuntimeException("Não foi possível obter os dados do endereço");
        }
        
        return cadastrarEndereco(enderecoDetalhes, complemento);
    }

    private EnderecoOutput converterParaEnderecoOutput(Endereco endereco) {
        EnderecoOutput output = new EnderecoOutput();
        output.setCep(endereco.getCep());
        output.setLogradouro(endereco.getLogradouro());
        output.setBairro(endereco.getBairro());
        output.setNumero(endereco.getNumero());
        output.setUf(endereco.getUf());
        output.setLocalidade(endereco.getCidade());
        output.setComplemento(endereco.getComplemento());
        return output;
    }

    private String formatarCep(String cep) {
        if (cep == null) return null;
        return cep.replaceAll("\\D", "");
    }    private boolean isValidCep(String cep) {
        return cep != null && cep.matches("\\d{8}");
    }
}
