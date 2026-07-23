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
@Document(collection = "jogos")
public class JogoMongoDTO implements Serializable {

    @Id
    private String id;

    @Field(name = "loteria")
    private String loteria;

    @Field(name = "numeros")
    private List<Integer> numeros;

    @Field(name = "concurso_inicial")
    private int concursoInicial;

    @Field(name = "quantidade_concursos")
    private int quantidadeConcursos;

    @Field(name = "descricao")
    private String descricao;

    @Field(name = "criado_em")
    private Date criadoEm;

    public JogoMongoDTO(String loteria,
                        List<Integer> numeros,
                        int concursoInicial,
                        int quantidadeConcursos,
                        String descricao,
                        Date criadoEm) {
        this.loteria = loteria;
        this.numeros = numeros;
        this.concursoInicial = concursoInicial;
        this.quantidadeConcursos = quantidadeConcursos;
        this.descricao = descricao;
        this.criadoEm = criadoEm;
    }

    public int getConcursoFinal() {
        return concursoInicial + quantidadeConcursos - 1;
    }

    public boolean isTeimosinha() {
        return quantidadeConcursos > 1;
    }
}
