package org.com.imaapi.controller;

import org.com.imaapi.dto.PagamentoRequest;
import org.com.imaapi.dto.PagamentoResponse;
import org.com.imaapi.service.impl.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamento")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping
    public ResponseEntity<PagamentoResponse> criarPagamento(@RequestBody PagamentoRequest request) {
        PagamentoResponse response = pagamentoService.criarLinkPagamento(request);
        return ResponseEntity.ok(response);
    }
}
