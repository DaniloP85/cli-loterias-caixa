package br.com.dpsnqmk.controller.api;

import br.com.dpsnqmk.enums.Loteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> parametroInvalido(MethodArgumentTypeMismatchException e) {
        String mensagem = Loteria.class.equals(e.getRequiredType())
                ? "Loteria inválida: '" + e.getValue() + "'. Válidas: " + Loteria.nomesValidos()
                : "Parâmetro inválido: " + e.getName();
        return ResponseEntity.badRequest().body(Map.of("erro", mensagem));
    }

    @ExceptionHandler(ConcursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> concursoNaoEncontrado(ConcursoNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
    }
}
