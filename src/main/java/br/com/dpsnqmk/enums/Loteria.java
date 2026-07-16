package br.com.dpsnqmk.enums;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum Loteria {

    // min/max = intervalo das dezenas; minDezenas/maxDezenas = quantas o volante
    // da Caixa permite marcar por aposta; precos = valor oficial da aposta por
    // quantidade de dezenas marcadas (ideas/*.md secao 2)
    MEGASENA(1, 60, 4, 6, 20, precos(
            preco(6, "6.00"), preco(7, "42.00"), preco(8, "168.00"), preco(9, "504.00"),
            preco(10, "1260.00"), preco(11, "2772.00"), preco(12, "5544.00"), preco(13, "10296.00"),
            preco(14, "18018.00"), preco(15, "30030.00"), preco(16, "48048.00"), preco(17, "74256.00"),
            preco(18, "111384.00"), preco(19, "162792.00"), preco(20, "232560.00"))),
    LOTOFACIL(1, 25, 11, 15, 20, precos(
            preco(15, "3.50"), preco(16, "56.00"), preco(17, "476.00"),
            preco(18, "2856.00"), preco(19, "13566.00"), preco(20, "54264.00"))),
    QUINA(1, 80, 2, 5, 15, precos(
            preco(5, "3.00"), preco(6, "18.00"), preco(7, "63.00"), preco(8, "168.00"),
            preco(9, "378.00"), preco(10, "756.00"), preco(11, "1386.00"), preco(12, "2376.00"),
            preco(13, "3861.00"), preco(14, "6006.00"), preco(15, "9009.00"))),
    LOTOMANIA(0, 99, 15, 50, 50, precos(preco(50, "3.00")));

    private final int min;
    private final int max;
    private final int minAcertosPremio;
    private final int minDezenas;
    private final int maxDezenas;
    private final Map<Integer, BigDecimal> precos;

    Loteria(int min, int max, int minAcertosPremio, int minDezenas, int maxDezenas,
            Map<Integer, BigDecimal> precos) {
        this.min = min;
        this.max = max;
        this.minAcertosPremio = minAcertosPremio;
        this.minDezenas = minDezenas;
        this.maxDezenas = maxDezenas;
        this.precos = precos;
    }

    @SafeVarargs
    private static Map<Integer, BigDecimal> precos(Map.Entry<Integer, BigDecimal>... entradas) {
        return Map.ofEntries(entradas);
    }

    private static Map.Entry<Integer, BigDecimal> preco(int quantidadeDezenas, String valor) {
        return Map.entry(quantidadeDezenas, new BigDecimal(valor));
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
