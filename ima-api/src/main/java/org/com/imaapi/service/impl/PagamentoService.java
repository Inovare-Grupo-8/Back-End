package org.com.imaapi.service.impl;


import org.com.imaapi.dto.PagamentoRequest;
import org.com.imaapi.dto.PagamentoResponse;
import org.springframework.stereotype.Service;

    @Service
    public class PagamentoService {
        public PagamentoResponse criarLinkPagamento(PagamentoRequest request) {
            PagamentoResponse response = new PagamentoResponse();
            response.setLinkPagamento("COLOCA A URL AQUI JHENY" + request.getDescricao());
            return response;
        }
    }

