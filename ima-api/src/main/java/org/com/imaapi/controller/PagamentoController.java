package org.com.imaapi.controller;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.com.imaapi.model.pagamento.dto.Charge;
import org.com.imaapi.model.pagamento.dto.TEDPaymentResponse;
import org.com.imaapi.service.pagamento.PixService;
import org.com.imaapi.service.pagamento.TEDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Data
@Getter
@Setter
@RestController
@RequestMapping("/pagamento")
public class PagamentoController {

    @Autowired
    private PixService pixService;

    @Autowired
    private TEDService tedService;

    @PostMapping("/pix")
    public ResponseEntity<?> realizarPagamentoPix(@RequestBody Charge charge) {
        try {
            String pixUrl = pixService.gerarCobrancaPix(charge);
            return ResponseEntity.ok(pixUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao gerar cobrança PIX: " + e.getMessage());
        }
    }

    @PostMapping("/ted")
    public ResponseEntity<?> realizarPagamentoTed(@RequestBody TEDPaymentResponse tedRequest, @RequestHeader("Authorization") String token) {
        try {
            String resultado = tedService.realizarTed(tedRequest, token);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao realizar TED: " + e.getMessage());
        }
    }

}
