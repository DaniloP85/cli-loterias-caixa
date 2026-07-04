package br.com.dpsnqmk.controller.api;

public class ConcursoNaoEncontradoException extends RuntimeException {

    public ConcursoNaoEncontradoException(String loteria, int concurso) {
        super("Concurso " + concurso + " não encontrado para a loteria " + loteria
                + ". Já rodou a importação?");
    }
}
