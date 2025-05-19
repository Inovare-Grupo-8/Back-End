package org.com.imaapi.dto;

public class TEDPaymentResponse {



        private String nomeFavorecido;
        private String cpfCnpj;
        private String banco; // código do banco, ex: "001" para Banco do Brasil
        private String agencia;
        private String numeroConta;
        private String tipoConta; // "corrente" ou "poupanca"
        private Double valor;
        private String descricao;

        public TEDPaymentResponse() {}

        public TEDPaymentResponse(String nomeFavorecido, String cpfCnpj, String banco, String agencia, String numeroConta, String tipoConta, Double valor, String descricao) {
            this.nomeFavorecido = nomeFavorecido;
            this.cpfCnpj = cpfCnpj;
            this.banco = banco;
            this.agencia = agencia;
            this.numeroConta = numeroConta;
            this.tipoConta = tipoConta;
            this.valor = valor;
            this.descricao = descricao;
        }

        public String getNomeFavorecido() {
            return nomeFavorecido;
        }

        public void setNomeFavorecido(String nomeFavorecido) {
            this.nomeFavorecido = nomeFavorecido;
        }

        public String getCpfCnpj() {
            return cpfCnpj;
        }

        public void setCpfCnpj(String cpfCnpj) {
            this.cpfCnpj = cpfCnpj;
        }

        public String getBanco() {
            return banco;
        }

        public void setBanco(String banco) {
            this.banco = banco;
        }

        public String getAgencia() {
            return agencia;
        }

        public void setAgencia(String agencia) {
            this.agencia = agencia;
        }

        public String getNumeroConta() {
            return numeroConta;
        }

        public void setNumeroConta(String numeroConta) {
            this.numeroConta = numeroConta;
        }

        public String getTipoConta() {
            return tipoConta;
        }

        public void setTipoConta(String tipoConta) {
            this.tipoConta = tipoConta;
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


