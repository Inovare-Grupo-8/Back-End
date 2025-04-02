package org.com.imaapi.controller;

import org.com.imaapi.model.Usuario.output.EnderecoOutput;
import org.com.imaapi.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cep")
public class EnderecoController {
    @Autowired
    EnderecoService enderecoService;

    @GetMapping
    public EnderecoOutput gerarEndereco(String cep) {
        return enderecoService.buscaEndereco(cep);
    }
}
