package br.com.dpsnqmk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ConcursoMongoDTO implements Serializable {

    @Field(name = "concurso")
    private int concurso;

    @Field(name = "loteria")
    private String loteria;

    @Field(name = "numeros_sorteados")
    private List<Integer> numerosSorteados;

    @Field(name = "data_sorteio")
    private Date dataSorteio;

    @Field(name = "status")
    private String status;

    @Field(name = "historial")
    private FeaturesDTO historial;
}
