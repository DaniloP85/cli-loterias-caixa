package br.com.dpsnqmk.dto;

import java.util.List;
import java.util.Map;

public record EstatisticasDTO(
        String loteria,
        long totalConcursos,
        List<FrequenciaDezena> frequenciaDezenas,
        Map<String, Double> medias
) {
    public record FrequenciaDezena(int dezena, long frequencia) {}
}
