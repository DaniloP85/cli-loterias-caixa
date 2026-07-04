package br.com.dpsnqmk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JogoComResumo {

    private final JogoMongoDTO jogo;
    private final ResumoJogo resumo;
}
