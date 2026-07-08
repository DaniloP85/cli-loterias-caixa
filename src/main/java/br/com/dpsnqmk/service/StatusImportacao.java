package br.com.dpsnqmk.service;

import lombok.Getter;

import java.util.Date;

@Getter
public class StatusImportacao {

    public enum Estado {NUNCA_EXECUTADO, EM_EXECUCAO, CONCLUIDO, ERRO}

    private volatile Estado estado = Estado.NUNCA_EXECUTADO;
    private volatile int processados;
    private volatile int total;
    private volatile String mensagem;
    private volatile Date inicio;
    private volatile Date fim;

    public void iniciar() {
        this.estado = Estado.EM_EXECUCAO;
        this.processados = 0;
        this.total = 0;
        this.mensagem = null;
        this.inicio = new Date();
        this.fim = null;
    }

    public void progresso(int processados, int total) {
        this.processados = processados;
        this.total = total;
    }

    public void concluir() {
        this.estado = Estado.CONCLUIDO;
        this.fim = new Date();
    }

    public void erro(String mensagem) {
        this.estado = Estado.ERRO;
        this.mensagem = mensagem;
        this.fim = new Date();
    }

    public double getPercentual() {
        return total == 0 ? 0.0 : (100.0 * processados) / total;
    }
}
