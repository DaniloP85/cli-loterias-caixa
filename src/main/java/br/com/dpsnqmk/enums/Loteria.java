package br.com.dpsnqmk.enums;

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
}

