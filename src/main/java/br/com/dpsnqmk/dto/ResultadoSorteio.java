package br.com.dpsnqmk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.List;

/** Linha da tabela "Resultados dos sorteios": um jogo conferido contra um sorteio realizado. */
@Getter
@AllArgsConstructor
public class ResultadoSorteio {

    private final String loteria;
    private final int concurso;
    private final Date dataSorteio;
    private final List<Integer> numerosJogados;
    private final List<Integer> dezenasAcertadas;
    private final int acertos;
    private final boolean premiado;
    private final String descricaoJogo;
    private final PremioFaixa premio;
}
