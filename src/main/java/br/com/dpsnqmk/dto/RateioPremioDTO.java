package br.com.dpsnqmk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/** Um item de "listaRateioPremio" na resposta da API da Caixa: valor pago por faixa de acerto. */
@Setter
@Getter
public class RateioPremioDTO implements Serializable {

    @JsonProperty("faixa")
    private int faixa;

    @JsonProperty("descricaoFaixa")
    private String descricaoFaixa;

    @JsonProperty("numeroDeGanhadores")
    private int numeroDeGanhadores;

    @JsonProperty("valorPremio")
    private double valorPremio;
}
