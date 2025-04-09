package org.com.imaapi.controller;

import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.service.EnderecoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cep")
public class EnderecoController {
     private final EnderecoService enderecoService;

     public EnderecoController(EnderecoService enderecoService) {
            this.enderecoService = enderecoService;
        }

        
        @GetMapping("/{cep}")
        public ResponseEntity<EnderecoOutput> buscaEndereco(@PathVariable String cep) {
            return enderecoService.buscaEndereco(cep);
        }
    }
