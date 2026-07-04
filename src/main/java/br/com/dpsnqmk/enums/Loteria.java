package br.com.dpsnqmk.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public enum Loteria {

    // min/max = intervalo das dezenas; minDezenas/maxDezenas = quantas o volante
    // da Caixa permite marcar por aposta
    MEGASENA(1, 60, 4, 6, 20),
    LOTOFACIL(1, 25, 11, 15, 20),
    QUINA(1, 80, 2, 5, 15),
    LOTOMANIA(0, 99, 15, 50, 50);

    private final int min;
    private final int max;
    private final int minAcertosPremio;
    private final int minDezenas;
    private final int maxDezenas;

    Loteria(int min, int max, int minAcertosPremio, int minDezenas, int maxDezenas) {
        this.min = min;
        this.max = max;
        this.minAcertosPremio = minAcertosPremio;
        this.minDezenas = minDezenas;
        this.maxDezenas = maxDezenas;
    }

    public double limiteBaixoAlto() {
        return (min + max) / 2.0;
    }

    /** Nome usado na URL da API da Caixa e no campo `loteria` do MongoDB. */
    public String nome() {
        return name().toLowerCase();
    }

    /** Atingiu alguma faixa de premiação? Na lotomania, 0 acertos também premia. */
    public boolean premiado(int acertos) {
        if (this == LOTOMANIA && acertos == 0) {
            return true;
        }
        return acertos >= minAcertosPremio;
    }

    public static Loteria from(String nome) {
        try {
            return Loteria.valueOf(nome.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Loteria inválida: '" + nome + "'. Válidas: " + nomesValidos());
        }
    }

    public static String nomesValidos() {
        return Arrays.stream(values())
                .map(Loteria::nome)
                .collect(Collectors.joining(", "));
    }
}
