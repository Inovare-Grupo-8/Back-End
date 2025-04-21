package org.com.imaapi.controller;

import org.com.imaapi.model.usuario.output.EnderecoOutput;
import org.com.imaapi.service.EnderecoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cep")
public class EnderecoController {
     private final EnderecoService enderecoService;

     public EnderecoController(EnderecoService enderecoService) {
            this.enderecoService = enderecoService;
        }

        
        @GetMapping("/{cep}")
        public ResponseEntity<EnderecoOutput> buscaEndereco(@PathVariable String cep, @RequestParam(required = false) String numero) {
            return enderecoService.buscaEndereco(cep, numero);
        }

        @GetMapping
        public List<EnderecoOutput> listarEnderecos() {
            return enderecoService.listarEnderecos();
        }
    }
