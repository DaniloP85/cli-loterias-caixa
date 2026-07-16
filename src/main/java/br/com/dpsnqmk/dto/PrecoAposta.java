package br.com.dpsnqmk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/** Linha da tabela de referência de preços por loteria (US3). Getters porque EL do JSP não resolve records. */
@Getter
@AllArgsConstructor
public class PrecoAposta {

    private final int quantidadeDezenas;
    private final BigDecimal valor;
}
