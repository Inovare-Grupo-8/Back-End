package org.com.imaapi.service.impl;

import org.com.imaapi.model.usuario.Endereco;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.repository.EnderecoRepository;
import org.com.imaapi.service.EnderecoService;
import org.hibernate.annotations.DialectOverride;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EnderecoServiceImpl implements EnderecoService {
    private static final String ViaCepApi = "https://viacep.com.br/ws/%s/json/";
    private static final Logger LOGGER = LoggerFactory.getLogger(EnderecoService.class);

    private final EnderecoRepository enderecoRepository;

    public EnderecoServiceImpl(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    @Override
    public ResponseEntity<EnderecoOutput> buscaEndereco(String cep) {
        if (cep == null || cep.trim().isEmpty()) {
            throw new IllegalArgumentException("O CEP não pode ser nulo ou vazio.");
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(ViaCepApi, cep);
        ResponseEntity<EnderecoOutput> response = restTemplate.getForEntity(url, EnderecoOutput.class);
        EnderecoOutput enderecoOutput = response.getBody();

        if (enderecoOutput == null || enderecoOutput.getCep() == null) {
            throw new RuntimeException("Não consegui obter o endereço com esse CEP: " + cep);
        }

        Endereco endereco = new Endereco();
        endereco.setCep(enderecoOutput.getCep());
        endereco.setLogradouro(enderecoOutput.getLogradouro());
        endereco.setBairro(enderecoOutput.getBairro());
        endereco.setNumero(enderecoOutput.getNumero());

        enderecoRepository.save(endereco);

        LOGGER.info("CEP consultado: {}", cep);
        LOGGER.info("Endereço salvo no banco de dados: {}", endereco);
        return ResponseEntity.ok(enderecoOutput);
    }
}
