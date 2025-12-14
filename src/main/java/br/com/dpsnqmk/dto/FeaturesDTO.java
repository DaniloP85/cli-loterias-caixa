package br.com.dpsnqmk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class FeaturesDTO implements Serializable {

    @Field(name = "soma")
    private Long soma;

    @Field(name = "media")
    private double media;

    @Field(name = "log_produto")
    private double logProduto;

    @Field(name = "pares")
    private Long pares;

    @Field(name = "impares")
    private Long impares;

    @Field(name = "baixos")
    private Long baixos;

    @Field(name = "altos")
    private Long altos;

    @Field(name = "desvio_padrao")
    private double desvioPadrao;
}