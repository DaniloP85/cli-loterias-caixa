package br.com.dpsnqmk.utility;

import br.com.dpsnqmk.enums.Loteria;
import br.com.dpsnqmk.records.AltosBaixos;
import br.com.dpsnqmk.records.ParesImparesFeature;

import java.util.List;

public class MyMath {

    private MyMath(){
        throw new IllegalStateException("Utility class");
    }

    public static ParesImparesFeature calcularParesImpares(
            List<Integer> numeros) {

        long pares = 0;
        long impares = 0;

        for (int n : numeros) {
            if ((n & 1) == 0) pares++;
            else impares++;
        }

        double proporcao = (double) pares / numeros.size();
        return new ParesImparesFeature(pares, impares, proporcao);
    }

    public static double getMedia(double soma, List<Integer> numerosSorteados) {
        return soma / numerosSorteados.size();
    }

    public static double getLogProduto(List<Integer> numerosSorteados) {
        double logProduto = 0.0;

        for (int n : numerosSorteados) {
            logProduto += Math.log(n);
        }

        return logProduto;
    }

    public static AltosBaixos contarAltosBaixos(List<Integer> numeros, Loteria loteria) {

        double limite = loteria.limiteBaixoAlto();

        long baixos = 0;
        long altos = 0;

        for (int n : numeros) {
            if (n <= limite) baixos++;
            else altos++;
        }

        return new AltosBaixos(baixos, altos);
    }

    public static double calcularDesvioPadrao(List<Integer> numeros) {

        int n = numeros.size();

        // 1. média
        double media = numeros.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        // 2. soma dos quadrados
        double somaQuadrados = numeros.stream()
                .mapToDouble(nr -> Math.pow(nr - media, 2))
                .sum();

        // 3. variância
        double variancia = somaQuadrados / n;

        // 4. desvio padrão
        return Math.sqrt(variancia);
    }
}