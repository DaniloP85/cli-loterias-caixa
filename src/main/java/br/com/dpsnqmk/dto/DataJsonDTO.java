package br.com.dpsnqmk.dto;

import java.io.Serializable;
import java.util.List;

public class DataJsonDTO implements Serializable {

    private int concuso;

    private String loteria;

    private DataDoSorteio sorteio;

    private List<String> dezenasSorteadas;

    public DataJsonDTO(int concuso, String loteria, DataDoSorteio dataDoSorteio, List<String> dezenasSorteadas) {
        this.concuso = concuso;
        this.loteria = loteria;
        this.sorteio = dataDoSorteio;
        this.dezenasSorteadas = dezenasSorteadas;
    }

    public int getConcuso() {
        return concuso;
    }

    public void setConcuso(int concuso) {
        this.concuso = concuso;
    }

    public String getLoteria() {
        return loteria;
    }

    public void setLoteria(String loteria) {
        this.loteria = loteria;
    }

    public DataDoSorteio getSorteio() {
        return sorteio;
    }

    public void setSorteio(DataDoSorteio sorteio) {
        this.sorteio = sorteio;
    }

    public List<String> getDezenasSorteadas() {
        return dezenasSorteadas;
    }

    public void setDezenasSorteadas(List<String> dezenasSorteadas) {
        this.dezenasSorteadas = dezenasSorteadas;
    }
}
