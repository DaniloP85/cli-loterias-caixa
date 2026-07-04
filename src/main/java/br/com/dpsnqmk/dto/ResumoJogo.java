package br.com.dpsnqmk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Resumo rápido do jogo: quantos concursos premiaram, quantos não, quantos ainda faltam. */
@Getter
@AllArgsConstructor
public class ResumoJogo {

    private final int conferidos;
    private final int premiados;
    private final int naoPremiados;
    private final int pendentes;
}
