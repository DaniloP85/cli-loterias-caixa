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
    /** Dezenas jogadas que coincidiram com o concurso mais recente já apurado do intervalo. */
    private final List<Integer> dezenasAcertadasUltimoConcurso;
    /** Número do concurso a que dezenasAcertadasUltimoConcurso se refere; null quando nenhum concurso do intervalo foi apurado ainda. */
    private final Integer concursoComparado;
}
