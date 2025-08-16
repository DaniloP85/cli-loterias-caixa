package br.com.dpsnqmk.service;

import br.com.dpsnqmk.dto.ConcursoDTO;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
            value = { HttpServerErrorException.BadGateway.class }, // Rechama para erros do servidor (5xx)
            maxAttempts = 5, // Tenta até 3 vezes
            backoff = @Backoff(delay = 1000, multiplier = 2) // 1s, depois 2s, depois 4s...
    )
    public ConcursoDTO recuperarConcurso(String url) {
        try {
            ResponseEntity<ConcursoDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    ConcursoDTO.class
            );

            return response.getBody();
        } catch (HttpServerErrorException e) {
            int statusCode = e.getStatusCode().value();

            if (statusCode == 502) {
                System.err.println("Erro 502 - Bad Gateway");
                // Faça algo específico para 502
            }
            else if (statusCode == 503) {
                System.err.println("Erro 503 - Service Unavailable");
                // Faça algo específico para 503
            }

            throw e; // Repropaga a exceção para o @Retryable ou @Recover
        }
    }

    @Recover
    public String recover(HttpServerErrorException e, String url) {
        System.out.println("Recuperando após falhas ao acessar: " + url);
        return "Falha ao acessar o serviço. Tente novamente mais tarde.";
    }
}