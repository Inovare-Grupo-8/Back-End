package org.com.imaapi.dto;

public class CartaoPaymentResponse {



        private String nomeTitular;
        private String numeroCartao;
        private String validade; // MM/YY
        private String cvv;
        private Double valor;
        private String descricao;

        public CartaoPaymentResponse() {}

        public CartaoPaymentResponse(String nomeTitular, String numeroCartao, String validade, String cvv, Double valor, String descricao) {
            this.nomeTitular = nomeTitular;
            this.numeroCartao = numeroCartao;
            this.validade = validade;
            this.cvv = cvv;
            this.valor = valor;
            this.descricao = descricao;
        }

        public String getNomeTitular() {
            return nomeTitular;
        }

        public void setNomeTitular(String nomeTitular) {
            this.nomeTitular = nomeTitular;
        }

        public String getNumeroCartao() {
            return numeroCartao;
        }

        public void setNumeroCartao(String numeroCartao) {
            this.numeroCartao = numeroCartao;
        }

        public String getValidade() {
            return validade;
        }

        public void setValidade(String validade) {
            this.validade = validade;
        }

        public String getCvv() {
            return cvv;
        }

        public void setCvv(String cvv) {
            this.cvv = cvv;
        }

        public Double getValor() {
            return valor;
        }

        public void setValor(Double valor) {
            this.valor = valor;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }


