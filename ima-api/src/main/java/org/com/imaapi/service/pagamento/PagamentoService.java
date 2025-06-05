package org.com.imaapi.service.pagamento;

import org.com.imaapi.model.pagamento.dto.BoletoPaymentResponse;
import org.com.imaapi.model.pagamento.dto.Charge;
import org.com.imaapi.model.pagamento.dto.PixPaymentResponse;
import org.com.imaapi.model.pagamento.dto.TEDPaymentResponse;

public interface PagamentoService {
    PixPaymentResponse realizarPagamentoPix(Charge charge) throws Exception;

    String realizarTed(TEDPaymentResponse request, String token) throws Exception;

    String gerarBoleto(BoletoPaymentResponse request, String token) throws Exception;

    String obterToken() throws Exception;
}
