package br.com.dpsnqmk.controller.web;

import br.com.dpsnqmk.controller.api.ConcursoNaoEncontradoException;
import br.com.dpsnqmk.dto.ConcursoMongoDTO;
import br.com.dpsnqmk.dto.EstatisticasDTO;
import br.com.dpsnqmk.enums.Loteria;
import br.com.dpsnqmk.repository.ConcursoRepository;
import br.com.dpsnqmk.service.EstatisticaService;
import br.com.dpsnqmk.service.ImportacaoService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class PaginasController {

    private final ConcursoRepository repository;
    private final EstatisticaService estatisticaService;
    private final ImportacaoService importacaoService;

    public PaginasController(ConcursoRepository repository,
                             EstatisticaService estatisticaService,
                             ImportacaoService importacaoService) {
        this.repository = repository;
        this.estatisticaService = estatisticaService;
        this.importacaoService = importacaoService;
    }

    /** View model dos cards da home (classe com getters porque EL do JSP não resolve records). */
    @Getter
    @AllArgsConstructor
    public static class CardLoteria {
        private final String nome;
        private final long totalConcursos;
        private final String estadoImportacao;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<CardLoteria> cards = Arrays.stream(Loteria.values())
                .map(loteria -> new CardLoteria(
                        loteria.nome(),
                        repository.countByLoteria(loteria.nome()),
                        importacaoService.status(loteria).getEstado().name()))
                .toList();
        model.addAttribute("cards", cards);
        return "home";
    }

    @GetMapping("/loterias/{loteria}")
    public String concursos(@PathVariable Loteria loteria,
                            @RequestParam(defaultValue = "0") int page,
                            Model model) {
        Page<ConcursoMongoDTO> pagina = repository.findByLoteria(loteria.nome(),
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "concurso")));
        model.addAttribute("loteria", loteria.nome());
        model.addAttribute("pagina", pagina);
        return "concursos";
    }

    @GetMapping("/loterias/{loteria}/concursos/{numero}")
    public String concurso(@PathVariable Loteria loteria, @PathVariable int numero, Model model) {
        ConcursoMongoDTO concurso = repository.findByLoteriaAndConcurso(loteria.nome(), numero)
                .orElseThrow(() -> new ConcursoNaoEncontradoException(loteria.nome(), numero));
        model.addAttribute("loteria", loteria.nome());
        model.addAttribute("concurso", concurso);
        return "concurso";
    }

    @GetMapping("/loterias/{loteria}/dashboard")
    public String dashboard(@PathVariable Loteria loteria, Model model) {
        EstatisticasDTO estatisticas = estatisticaService.estatisticas(loteria);
        model.addAttribute("loteria", loteria.nome());
        model.addAttribute("totalConcursos", estatisticas.totalConcursos());
        model.addAttribute("medias", estatisticas.medias());
        return "dashboard";
    }
}
