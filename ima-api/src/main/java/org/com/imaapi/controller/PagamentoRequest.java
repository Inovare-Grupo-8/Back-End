package org.com.imaapi.controller;
public class PagamentoRequest {
    private String token;
    private String descricao;
    private Float valor;
    private Integer parcelas;
    private String metodoPagamento;
    private String emailComprador;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Float getValor() { return valor; }
    public void setValor(Float valor) { this.valor = valor; }

    public Integer getParcelas() { return parcelas; }
    public void setParcelas(Integer parcelas) { this.parcelas = parcelas; }

    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }

    public String getEmailComprador() { return emailComprador; }
    public void setEmailComprador(String emailComprador) { this.emailComprador = emailComprador; }
}
