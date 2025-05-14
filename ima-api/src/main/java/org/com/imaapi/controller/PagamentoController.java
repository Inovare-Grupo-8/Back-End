package org.com.imaapi.controller;

import org.com.imaapi.service.pagamento.CoraService;
import org.springframework.beans.factory.annotation.Autowired;
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


