package br.com.dpsnqmk.dto;

import java.util.List;

/** Uma linha "flat" do dataset exportado para estudo de ML. */
public record LinhaDataset(
        int concurso,
        String data,
        List<Integer> numeros,
        long soma,
        double media,
        double desvioPadrao,
        double logProduto,
        long pares,
        long impares,
        long baixos,
        long altos
) {}
