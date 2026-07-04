package br.com.dpsnqmk.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "resultados")
public class ConcursoMongoDTO implements Serializable {

    @Id
    private String id;

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

    public ConcursoMongoDTO(int concurso,
                            String loteria,
                            List<Integer> numerosSorteados,
                            Date dataSorteio,
                            String status,
                            FeaturesDTO historial) {
        this.concurso = concurso;
        this.loteria = loteria;
        this.numerosSorteados = numerosSorteados;
        this.dataSorteio = dataSorteio;
        this.status = status;
        this.historial = historial;
    }
}
