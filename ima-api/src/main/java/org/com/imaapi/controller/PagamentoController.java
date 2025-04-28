package org.com.imaapi.controller;

import org.com.imaapi.service.MercadoPagoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class PagamentoController {

    @RestController
    @RequestMapping("/pagamento")

        private final MercadoPagoService mercadoPagoService;

        // Injeção de dependência do MercadoPagoService
        public PagamentoController(MercadoPagoService mercadoPagoService) {
            this.mercadoPagoService = mercadoPagoService;
        }

        // Endpoint para realizar o pagamento
        @PostMapping("/realizar")
        public ResponseEntity<String> realizarPagamento(@RequestBody String dadosPagamento) {
            // Chama o serviço para processar o pagamento
            String resultado = mercadoPagoService.realizarPagamento(dadosPagamento);

            // Retorna a resposta para o cliente
            if (resultado.contains("Erro")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao processar o pagamento");
            } else {
                return ResponseEntity.ok("Pagamento realizado com sucesso! Detalhes: " + resultado);
            }
        }
    }

}
