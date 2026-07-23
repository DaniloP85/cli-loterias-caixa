package br.com.dpsnqmk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class JogoComResumo {

    private final JogoMongoDTO jogo;
    private final ResumoJogo resumo;
    private final BigDecimal custoTotal;
    private final BigDecimal ganhoTotal;
    /** Dezenas jogadas que coincidiram com o concurso mais recente já apurado, só enquanto houver concurso pendente. */
    private final List<Integer> dezenasAcertadasUltimoConcurso;
}
