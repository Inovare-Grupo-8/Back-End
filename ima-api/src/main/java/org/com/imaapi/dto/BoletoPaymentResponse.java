package org.com.imaapi.dto;

public class BoletoPaymentResponse {

    private String nome;
    private String cpfCnpj;
    private Double valor;
    private String vencimento; // Formato ISO: yyyy-MM-dd
    private String descricao;

    public void BoletoChargeRequest() {}

    public void BoletoChargeRequest(String nome, String cpfCnpj, Double valor, String vencimento, String descricao) {
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.valor = valor;
        this.vencimento = vencimento;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getVencimento() {
        return vencimento;
    }

    public void setVencimento(String vencimento) {
        this.vencimento = vencimento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
