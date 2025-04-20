package org.com.imaapi.controller;

import org.com.imaapi.model.usuario.Endereco;
import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.service.EnderecoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/cep")
public class EnderecoController {
    private final EnderecoService enderecoService;
    private static final Logger LOGGER = LoggerFactory.getLogger(EnderecoService.class);


    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<EnderecoOutput> buscaESalvarEndereco(@PathVariable String cep) {
        return enderecoService.buscaEndereco(cep);
    }

    @GetMapping
    public List<EnderecoOutput> listarEnderecos() {
        return enderecoService.listarEnderecos();
    }
}