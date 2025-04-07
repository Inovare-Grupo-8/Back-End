package org.com.imaapi.service;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import lombok.Value;
import org.springframework.stereotype.Service;


@Service
public class PagamentoService {

    public Payment realizarPagamento(PagamentoRequest request) throws MPException {

        MercadoPago.SDK.setAccessToken("TEST-6152032977303285-040620-8db50ebf6be58147ca3aeb005eb25294-2367206344");


        Payment payment = new Payment()
                .setTransactionAmount(request.getValor())
                .setToken(request.getToken())
                .setDescription(request.getDescricao())
                .setInstallments(request.getParcelas())
                .setPaymentMethodId(request.getMetodoPagamento())
                .setPayer(new Payer()
                        .setEmail(request.getEmailComprador()));


        payment.save();
        return payment;
    }
}


