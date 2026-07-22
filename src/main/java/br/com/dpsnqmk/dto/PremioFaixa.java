package br.com.dpsnqmk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/** Prêmio de uma faixa de acerto (data-model.md: PremioFaixa). Getters porque EL do JSP não resolve records. */
@Getter
@AllArgsConstructor
public class PremioFaixa {

    public static final String VALOR = "VALOR";
    public static final String SEM_GANHADOR = "SEM_GANHADOR";
    public static final String INDISPONIVEL = "INDISPONIVEL";

    private final BigDecimal valor;
    private final String status;

    public static PremioFaixa valor(BigDecimal valor) {
        return new PremioFaixa(valor, VALOR);
    }

    public static PremioFaixa semGanhador() {
        return new PremioFaixa(null, SEM_GANHADOR);
    }

    public static PremioFaixa indisponivel() {
        return new PremioFaixa(null, INDISPONIVEL);
    }
}
