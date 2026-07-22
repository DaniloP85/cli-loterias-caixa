package br.com.dpsnqmk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.List;

/** Resultado da conferência de um jogo contra um concurso. Classe com getters porque EL do JSP não resolve records. */
@Getter
@AllArgsConstructor
public class ConferenciaConcurso {

    public static final String PREMIADO = "PREMIADO";
    public static final String NAO_PREMIADO = "NAO_PREMIADO";
    public static final String PENDENTE = "PENDENTE";

    private final int concurso;
    private final Date dataSorteio;
    private final List<Integer> dezenasSorteadas;
    private final List<Integer> dezenasAcertadas;
    private final int acertos;
    private final String situacao;
    /** Só preenchido quando situacao == PREMIADO (FR-005). */
    private final PremioFaixa premio;

    public static ConferenciaConcurso pendente(int concurso) {
        return new ConferenciaConcurso(concurso, null, List.of(), List.of(), 0, PENDENTE, null);
    }
}
