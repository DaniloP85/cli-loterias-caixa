package br.com.dpsnqmk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class JogoComResumo {

    private final JogoMongoDTO jogo;
    private final ResumoJogo resumo;
    private final BigDecimal custoTotal;
    private final BigDecimal ganhoTotal;
}
