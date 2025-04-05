package org.com.imaapi.service;

import org.com.imaapi.model.Usuario.output.EnderecoOutput;
import org.com.imaapi.repository.EnderecoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EnderecoService {

    private static final String ViaCepApi = "https://viacep.com.br/ws/%s/json/";
    private static final Logger LOGGER = LoggerFactory.getLogger(EnderecoService.class);

    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    public EnderecoOutput buscaEndereco(String cep) {

        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(ViaCepApi, cep);
        ResponseEntity<EnderecoOutput> response = restTemplate.getForEntity(url, EnderecoOutput.class);

        EnderecoOutput enderecoOutput = response.getBody();

        if (enderecoOutput == null || enderecoOutput.getCep() == null) {
            throw new RuntimeException("Não consegui obter o endereço com esse CEP: " + cep);
        }

        LOGGER.info("CEP consultado: {}", cep);

//        EnderecoOutput EnderecoCompleto = getEndereco(enderecoOutput);

//        enderecoRepository.save(EnderecoCompleto);
        return enderecoOutput;


    }

//    private static EnderecoOutput getEndereco(EnderecoOutput enderecoOutput) {
//        EnderecoOutput EnderecoCompleto = new EnderecoOutput();
//        EnderecoCompleto.setCep(enderecoOutput.getCep());
//        EnderecoCompleto.setLogradouro(enderecoOutput.getLogradouro());
//        EnderecoCompleto.setComplemento(enderecoOutput.getComplemento());
//        EnderecoCompleto.setNumero(enderecoOutput.getNumero());
//        EnderecoCompleto.setBairro(enderecoOutput.getBairro());
//        EnderecoCompleto.setLocalidade(enderecoOutput.getLocalidade());
//        EnderecoCompleto.setUf(enderecoOutput.getUf());
//        EnderecoCompleto.setEstado(enderecoOutput.getEstado());
//        EnderecoCompleto.setRegiao(enderecoOutput.getRegiao());
//        return EnderecoCompleto;

}
