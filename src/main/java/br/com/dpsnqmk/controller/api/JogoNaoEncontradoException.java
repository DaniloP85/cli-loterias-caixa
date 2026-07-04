package br.com.dpsnqmk.controller.api;

public class JogoNaoEncontradoException extends RuntimeException {

    public JogoNaoEncontradoException(String id) {
        super("Jogo não encontrado: " + id);
    }
}
