package br.com.dpsnqmk.controller.api;

import br.com.dpsnqmk.dto.ConferenciaJogo;
import br.com.dpsnqmk.dto.JogoComResumo;
import br.com.dpsnqmk.dto.JogoMongoDTO;
import br.com.dpsnqmk.service.JogoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jogos")
public class JogoRestController {

    /** Corpo do POST; record é ok aqui (só Jackson, não passa por JSP EL). */
    public record NovoJogoRequest(String loteria, List<Integer> numeros,
                                  Integer concursoInicial, Integer concursoFinal,
                                  String descricao) {}

    private final JogoService jogoService;

    public JogoRestController(JogoService jogoService) {
        this.jogoService = jogoService;
    }

    @PostMapping
    public ResponseEntity<JogoMongoDTO> criar(@RequestBody NovoJogoRequest request) {
        JogoMongoDTO jogo = jogoService.criar(request.loteria(), request.numeros(),
                request.concursoInicial(), request.concursoFinal(), request.descricao());
        return ResponseEntity.status(HttpStatus.CREATED).body(jogo);
    }

    @GetMapping
    public List<JogoComResumo> listar() {
        return jogoService.listarComResumo();
    }

    @GetMapping("/{id}/conferencia")
    public ConferenciaJogo conferencia(@PathVariable String id) {
        return jogoService.conferir(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        jogoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
