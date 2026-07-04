package br.com.dpsnqmk.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum Loteria {

    MEGASENA(1, 60),
    LOTOFACIL(1, 25),
    QUINA(1, 80),
    LOTOMANIA(0, 99);

    private final int min;
    private final int max;

    Loteria(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public double limiteBaixoAlto() {
        return (min + max) / 2.0;
    }

    /** Nome usado na URL da API da Caixa e no campo `loteria` do MongoDB. */
    public String nome() {
        return name().toLowerCase();
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
