package org.com.imaapi.service;

import org.com.imaapi.model.usuario.Endereco;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.repository.EnderecoRepository;
import org.hibernate.annotations.DialectOverride;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public interface EnderecoService {
    public ResponseEntity<EnderecoOutput> buscaEndereco(String cep, String numero, String complemento);

    public List<EnderecoOutput> listarEnderecos();

    Endereco cadastrarEndereco(EnderecoOutput enderecoOutput, String complemento);
}
