package br.com.dpsnqmk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ConferenciaJogo {

    private final JogoMongoDTO jogo;
    private final ResumoJogo resumo;
    private final List<ConferenciaConcurso> concursos;
}
