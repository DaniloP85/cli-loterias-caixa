package br.com.dpsnqmk.service.impl;

import br.com.dpsnqmk.dto.ConcursoDTO;
import br.com.dpsnqmk.service.HttpService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpServiceImpl implements HttpService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @Retryable(
            retryFor = HttpServerErrorException.class, // erros do servidor (5xx)
            maxAttempts = 5,
            backoff = @Backoff(delay = 500, multiplier = 2)
    )
    public ConcursoDTO recuperarConcurso(String url) {
        return restTemplate.getForObject(url, ConcursoDTO.class);
    }
}
