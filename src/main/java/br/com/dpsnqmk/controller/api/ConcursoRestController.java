package br.com.dpsnqmk.controller.api;

import br.com.dpsnqmk.dto.ConcursoMongoDTO;
import br.com.dpsnqmk.dto.ConferenciaConcurso;
import br.com.dpsnqmk.dto.EstatisticasDTO;
import br.com.dpsnqmk.dto.LinhaDataset;
import br.com.dpsnqmk.enums.Loteria;
import br.com.dpsnqmk.repository.ConcursoRepository;
import br.com.dpsnqmk.service.DatasetService;
import br.com.dpsnqmk.service.EstatisticaService;
import br.com.dpsnqmk.service.ImportacaoService;
import br.com.dpsnqmk.service.JogoService;
import br.com.dpsnqmk.service.StatusImportacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loterias/{loteria}")
public class ConcursoRestController {

    private final ImportacaoService importacaoService;
    private final EstatisticaService estatisticaService;
    private final DatasetService datasetService;
    private final JogoService jogoService;
    private final ConcursoRepository repository;

    public ConcursoRestController(ImportacaoService importacaoService,
                                  EstatisticaService estatisticaService,
                                  DatasetService datasetService,
                                  JogoService jogoService,
                                  ConcursoRepository repository) {
        this.importacaoService = importacaoService;
        this.estatisticaService = estatisticaService;
        this.datasetService = datasetService;
        this.jogoService = jogoService;
        this.repository = repository;
    }

    @PostMapping("/importacao")
    public ResponseEntity<Map<String, String>> importar(@PathVariable Loteria loteria,
                                                        @RequestParam(defaultValue = "false") boolean completo) {
        boolean iniciada = importacaoService.iniciar(loteria, completo);
        String statusUrl = "/api/loterias/" + loteria.nome() + "/importacao/status";
        if (!iniciada) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("mensagem", "Importação já em andamento", "status", statusUrl));
        }
        return ResponseEntity.accepted()
                .body(Map.of("mensagem", "Importação iniciada", "status", statusUrl));
    }

    @GetMapping("/importacao/status")
    public StatusImportacao status(@PathVariable Loteria loteria) {
        return importacaoService.status(loteria);
    }

    @GetMapping("/importacao/eventos")
    public SseEmitter eventos(@PathVariable Loteria loteria) {
        return importacaoService.assinar(loteria);
    }

    @GetMapping("/concursos")
    public Page<ConcursoMongoDTO> concursos(@PathVariable Loteria loteria,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return repository.findByLoteria(loteria.nome(),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "concurso")));
    }

    @GetMapping("/concursos/{numero}")
    public ConcursoMongoDTO concurso(@PathVariable Loteria loteria, @PathVariable int numero) {
        return repository.findByLoteriaAndConcurso(loteria.nome(), numero)
                .orElseThrow(() -> new ConcursoNaoEncontradoException(loteria.nome(), numero));
    }

    @GetMapping("/concursos/{numero}/conferencia")
    public ConferenciaConcurso conferencia(@PathVariable Loteria loteria,
                                           @PathVariable int numero,
                                           @RequestParam List<Integer> dezenas) {
        return jogoService.conferirAvulso(loteria, numero, dezenas);
    }

    @GetMapping("/estatisticas")
    public EstatisticasDTO estatisticas(@PathVariable Loteria loteria) {
        return estatisticaService.estatisticas(loteria);
    }

    @GetMapping(value = "/export", params = "formato=json")
    public List<LinhaDataset> exportJson(@PathVariable Loteria loteria) {
        return datasetService.dataset(loteria);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv(@PathVariable Loteria loteria) throws IOException {
        List<LinhaDataset> linhas = datasetService.dataset(loteria);

        StringWriter writer = new StringWriter();
        datasetService.escreverCsv(linhas, writer);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + loteria.nome() + "-dataset.csv\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(writer.toString().getBytes(StandardCharsets.UTF_8));
    }
}
