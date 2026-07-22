package br.com.dpsnqmk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.math.BigDecimal;

/** Rateio de prêmio de uma faixa, persistido junto do concurso (research.md secao 4). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateioPremioMongoDTO implements Serializable {

    @Field(name = "faixa")
    private int faixa;

    @Field(name = "descricao_faixa")
    private String descricaoFaixa;

    @Field(name = "numero_de_ganhadores")
    private int numeroDeGanhadores;

    @Field(name = "valor_premio")
    private BigDecimal valorPremio;
}
