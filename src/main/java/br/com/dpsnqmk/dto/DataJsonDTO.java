package br.com.dpsnqmk.dto;

import java.io.Serializable;
import java.util.List;

public class DataJsonDTO implements Serializable {

    private int concusso;

    private String loteria;

    private String dataDoSorteio;

    private List<String> dezenasSorteadas;

    public DataJsonDTO(int concusso, String loteria, String dataDoSorteio, List<String> dezenasSorteadas) {
        this.concusso = concusso;
        this.loteria = loteria;
        this.dataDoSorteio = dataDoSorteio;
        this.dezenasSorteadas = dezenasSorteadas;
    }

    public int getConcusso() {
        return concusso;
    }

    public void setConcusso(int concusso) {
        this.concusso = concusso;
    }

    public String getLoteria() {
        return loteria;
    }

    public void setLoteria(String loteria) {
        this.loteria = loteria;
    }

    public String getDataDoSorteio() {
        return dataDoSorteio;
    }

    public void setDataDoSorteio(String dataDoSorteio) {
        this.dataDoSorteio = dataDoSorteio;
    }

    public List<String> getDezenasSorteadas() {
        return dezenasSorteadas;
    }

    public void setDezenasSorteadas(List<String> dezenasSorteadas) {
        this.dezenasSorteadas = dezenasSorteadas;
    }
}
