package br.com.dpsnqmk.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@JsonIgnoreProperties
public class ConcursoDTO implements Serializable {

    @JsonProperty("acumulado")
    private boolean acumulado;

    @JsonProperty("dataApuracao")
    private String dataApuracao;

    @JsonProperty("dataProximoConcurso")
    private String dataProximoConcurso;

    @JsonProperty("dezenasSorteadasOrdemSorteio")
    private List<String> dezenasSorteadasOrdemSorteio;

    @JsonProperty("exibirDetalhamentoPorCidade")
    private boolean exibirDetalhamentoPorCidade;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("indicadorConcursoEspecial")
    private Integer indicadorConcursoEspecial;

    @JsonProperty("listaDezenas")
    private List<String> listaDezenas;

    @JsonProperty("listaDezenasSegundoSorteio")
    private List<String> listaDezenasSegundoSorteio;

    @JsonProperty("listaMunicipioUFGanhadores")
    private List<Object> listaMunicipioUFGanhadores;

    @JsonProperty("localSorteio")
    private String localSorteio;

    @JsonProperty("nomeMunicipioUFSorteio")
    private String nomeMunicipioUFSorteio;

    @JsonProperty("nomeTimeCoracaoMesSorte")
    private String nomeTimeCoracaoMesSorte;

    @JsonProperty("numero")
    private Integer numero;

    @JsonProperty("numeroConcursoAnterior")
    private Integer numeroConcursoAnterior;

    @JsonProperty("numeroConcursoFinal_0_5")
    private Integer numeroConcursoFinal;

    @JsonProperty("numeroConcursoProximo")
    private Integer numeroConcursoProximo;

    @JsonProperty("numeroJogo")
    private Integer numeroJogo;

    @JsonProperty("observacao")
    private String observacao;

    @JsonProperty("premiacaoContingencia")
    private Object premiacaoContingencia;

    @JsonProperty("tipoJogo")
    private String tipoJogo;

    @JsonProperty("tipoPublicacao")
    private Integer tipoPublicacao;

    @JsonProperty("ultimoConcurso")
    private boolean ultimoConcurso;

    @JsonProperty("valorArrecadado")
    private Double valorArrecadado;

    @JsonProperty("valorAcumuladoConcurso_0_5")
    private Double valorAcumuladoConcurso_;

    @JsonProperty("valorAcumuladoConcursoEspecial")
    private Double valorAcumuladoConcursoEspecial;

    @JsonProperty("valorAcumuladoProximoConcurso")
    private Double valorAcumuladoProximoConcurso;

    @JsonProperty("valorEstimadoProximoConcurso")
    private Double valorEstimadoProximoConcurso;

    @JsonProperty("valorSaldoReservaGarantidora")
    private Double valorSaldoReservaGarantidora;

    @JsonProperty("valorTotalPremioFaixaUm")
    private Double valorTotalPremioFaixaUm;
}
