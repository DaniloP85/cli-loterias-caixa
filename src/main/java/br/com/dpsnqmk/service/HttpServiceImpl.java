package br.com.dpsnqmk.service;

import br.com.dpsnqmk.dto.ConcursoDTO;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpServiceImpl implements HttpService{

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @Retryable(
            value = { HttpServerErrorException.class }, // Rechama para erros do servidor (5xx)
            maxAttempts = 3, // Tenta até 3 vezes
            backoff = @Backoff(delay = 1000) // Aguarda 2 segundos entre as tentativas
    )
    public ConcursoDTO recuperarConcurso(String url) {
        return restTemplate.getForObject(url, ConcursoDTO.class);
    }

    @Recover
    public String recover(HttpServerErrorException e, String url) {
        System.out.println("Recuperando após falhas ao acessar: " + url);
        return "Falha ao acessar o serviço. Tente novamente mais tarde.";
    }
}
