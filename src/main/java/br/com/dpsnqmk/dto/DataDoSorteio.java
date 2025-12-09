package br.com.dpsnqmk.dto;

import java.io.Serializable;

public class DataDoSorteio implements Serializable {

    private String dia;

    private String mes;

    private String ano;

    public DataDoSorteio(String dia, String mes, String ano) {
        this.dia = dia;
        this.mes = mes;
        this.ano = ano;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }
}
