package br.com.dpsnqmk.service;

import br.com.dpsnqmk.dto.ConcursoDTO;

public interface HttpService {

    public ConcursoDTO recuperarConcurso(String url);
}
