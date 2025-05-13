package org.com.imaapi.controller;

import org.com.imaapi.dto.TokenRequest;
import org.com.imaapi.dto.TokenResponse;
import org.com.imaapi.service.impl.CoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamento")
public class PagamentoController {



        @Autowired
        private CoraService coraApiService;

        @GetMapping("/token")
        public String pegarToken() throws Exception {
            return coraApiService.obterToken();
        }
    }


