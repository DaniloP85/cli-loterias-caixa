package br.com.dpsnqmk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

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

    public boolean isAcumulado() {
        return acumulado;
    }

    public void setAcumulado(boolean acumulado) {
        this.acumulado = acumulado;
    }

    public String getDataApuracao() {
        return dataApuracao;
    }

    public void setDataApuracao(String dataApuracao) {
        this.dataApuracao = dataApuracao;
    }

    public String getDataProximoConcurso() {
        return dataProximoConcurso;
    }

    public void setDataProximoConcurso(String dataProximoConcurso) {
        this.dataProximoConcurso = dataProximoConcurso;
    }

    public List<String> getDezenasSorteadasOrdemSorteio() {
        return dezenasSorteadasOrdemSorteio;
    }

    public void setDezenasSorteadasOrdemSorteio(List<String> dezenasSorteadasOrdemSorteio) {
        this.dezenasSorteadasOrdemSorteio = dezenasSorteadasOrdemSorteio;
    }

    public boolean isExibirDetalhamentoPorCidade() {
        return exibirDetalhamentoPorCidade;
    }

    public void setExibirDetalhamentoPorCidade(boolean exibirDetalhamentoPorCidade) {
        this.exibirDetalhamentoPorCidade = exibirDetalhamentoPorCidade;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIndicadorConcursoEspecial() {
        return indicadorConcursoEspecial;
    }

    public void setIndicadorConcursoEspecial(Integer indicadorConcursoEspecial) {
        this.indicadorConcursoEspecial = indicadorConcursoEspecial;
    }

    public List<String> getListaDezenas() {
        return listaDezenas;
    }

    public void setListaDezenas(List<String> listaDezenas) {
        this.listaDezenas = listaDezenas;
    }

    public List<String> getListaDezenasSegundoSorteio() {
        return listaDezenasSegundoSorteio;
    }

    public void setListaDezenasSegundoSorteio(List<String> listaDezenasSegundoSorteio) {
        this.listaDezenasSegundoSorteio = listaDezenasSegundoSorteio;
    }

    public List<Object> getListaMunicipioUFGanhadores() {
        return listaMunicipioUFGanhadores;
    }

    public void setListaMunicipioUFGanhadores(List<Object> listaMunicipioUFGanhadores) {
        this.listaMunicipioUFGanhadores = listaMunicipioUFGanhadores;
    }

    public String getLocalSorteio() {
        return localSorteio;
    }

    public void setLocalSorteio(String localSorteio) {
        this.localSorteio = localSorteio;
    }

    public String getNomeMunicipioUFSorteio() {
        return nomeMunicipioUFSorteio;
    }

    public void setNomeMunicipioUFSorteio(String nomeMunicipioUFSorteio) {
        this.nomeMunicipioUFSorteio = nomeMunicipioUFSorteio;
    }

    public String getNomeTimeCoracaoMesSorte() {
        return nomeTimeCoracaoMesSorte;
    }

    public void setNomeTimeCoracaoMesSorte(String nomeTimeCoracaoMesSorte) {
        this.nomeTimeCoracaoMesSorte = nomeTimeCoracaoMesSorte;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getNumeroConcursoAnterior() {
        return numeroConcursoAnterior;
    }

    public void setNumeroConcursoAnterior(Integer numeroConcursoAnterior) {
        this.numeroConcursoAnterior = numeroConcursoAnterior;
    }

    public Integer getNumeroConcursoFinal() {
        return numeroConcursoFinal;
    }

    public void setNumeroConcursoFinal(Integer numeroConcursoFinal) {
        this.numeroConcursoFinal = numeroConcursoFinal;
    }

    public Integer getNumeroConcursoProximo() {
        return numeroConcursoProximo;
    }

    public void setNumeroConcursoProximo(Integer numeroConcursoProximo) {
        this.numeroConcursoProximo = numeroConcursoProximo;
    }

    public Integer getNumeroJogo() {
        return numeroJogo;
    }

    public void setNumeroJogo(Integer numeroJogo) {
        this.numeroJogo = numeroJogo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Object getPremiacaoContingencia() {
        return premiacaoContingencia;
    }

    public void setPremiacaoContingencia(Object premiacaoContingencia) {
        this.premiacaoContingencia = premiacaoContingencia;
    }

    public String getTipoJogo() {
        return tipoJogo;
    }

    public void setTipoJogo(String tipoJogo) {
        this.tipoJogo = tipoJogo;
    }

    public Integer getTipoPublicacao() {
        return tipoPublicacao;
    }

    public void setTipoPublicacao(Integer tipoPublicacao) {
        this.tipoPublicacao = tipoPublicacao;
    }

    public boolean isUltimoConcurso() {
        return ultimoConcurso;
    }

    public void setUltimoConcurso(boolean ultimoConcurso) {
        this.ultimoConcurso = ultimoConcurso;
    }

    public Double getValorArrecadado() {
        return valorArrecadado;
    }

    public void setValorArrecadado(Double valorArrecadado) {
        this.valorArrecadado = valorArrecadado;
    }

    public Double getValorAcumuladoConcurso_() {
        return valorAcumuladoConcurso_;
    }

    public void setValorAcumuladoConcurso_(Double valorAcumuladoConcurso_) {
        this.valorAcumuladoConcurso_ = valorAcumuladoConcurso_;
    }

    public Double getValorAcumuladoConcursoEspecial() {
        return valorAcumuladoConcursoEspecial;
    }

    public void setValorAcumuladoConcursoEspecial(Double valorAcumuladoConcursoEspecial) {
        this.valorAcumuladoConcursoEspecial = valorAcumuladoConcursoEspecial;
    }

    public Double getValorAcumuladoProximoConcurso() {
        return valorAcumuladoProximoConcurso;
    }

    public void setValorAcumuladoProximoConcurso(Double valorAcumuladoProximoConcurso) {
        this.valorAcumuladoProximoConcurso = valorAcumuladoProximoConcurso;
    }

    public Double getValorEstimadoProximoConcurso() {
        return valorEstimadoProximoConcurso;
    }

    public void setValorEstimadoProximoConcurso(Double valorEstimadoProximoConcurso) {
        this.valorEstimadoProximoConcurso = valorEstimadoProximoConcurso;
    }

    public Double getValorSaldoReservaGarantidora() {
        return valorSaldoReservaGarantidora;
    }

    public void setValorSaldoReservaGarantidora(Double valorSaldoReservaGarantidora) {
        this.valorSaldoReservaGarantidora = valorSaldoReservaGarantidora;
    }

    public Double getValorTotalPremioFaixaUm() {
        return valorTotalPremioFaixaUm;
    }

    public void setValorTotalPremioFaixaUm(Double valorTotalPremioFaixaUm) {
        this.valorTotalPremioFaixaUm = valorTotalPremioFaixaUm;
    }
}
