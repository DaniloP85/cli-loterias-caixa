package br.com.dpsnqmk.controller.web;

import br.com.dpsnqmk.controller.api.ConcursoNaoEncontradoException;
import br.com.dpsnqmk.dto.ConcursoMongoDTO;
import br.com.dpsnqmk.dto.ConferenciaJogo;
import br.com.dpsnqmk.dto.EstatisticasDTO;
import br.com.dpsnqmk.enums.Loteria;
import br.com.dpsnqmk.repository.ConcursoRepository;
import br.com.dpsnqmk.service.EstatisticaService;
import br.com.dpsnqmk.service.ImportacaoService;
import br.com.dpsnqmk.service.JogoService;
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
    private final JogoService jogoService;

    public PaginasController(ConcursoRepository repository,
                             EstatisticaService estatisticaService,
                             ImportacaoService importacaoService,
                             JogoService jogoService) {
        this.repository = repository;
        this.estatisticaService = estatisticaService;
        this.importacaoService = importacaoService;
        this.jogoService = jogoService;
    }

    /** View model dos cards da home (classe com getters porque EL do JSP não resolve records). */
    @Getter
    @AllArgsConstructor
    public static class CardLoteria {
        private final String nome;
        private final long totalConcursos;
        private final String estadoImportacao;
    }

    /** Configuração de cada loteria para o volante de cadastro de jogos. */
    @Getter
    @AllArgsConstructor
    public static class ConfigLoteria {
        private final String nome;
        private final int min;
        private final int max;
        private final int minDezenas;
        private final int maxDezenas;
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
        model.addAttribute("abaAtiva", "manutencao");
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
        model.addAttribute("abaAtiva", "manutencao");
        return "concursos";
    }

    @GetMapping("/loterias/{loteria}/concursos/{numero}")
    public String concurso(@PathVariable Loteria loteria, @PathVariable int numero, Model model) {
        ConcursoMongoDTO concurso = repository.findByLoteriaAndConcurso(loteria.nome(), numero)
                .orElseThrow(() -> new ConcursoNaoEncontradoException(loteria.nome(), numero));
        model.addAttribute("loteria", loteria.nome());
        model.addAttribute("concurso", concurso);
        model.addAttribute("abaAtiva", "manutencao");
        return "concurso";
    }

    @GetMapping("/loterias/{loteria}/dashboard")
    public String dashboard(@PathVariable Loteria loteria, Model model) {
        EstatisticasDTO estatisticas = estatisticaService.estatisticas(loteria);
        model.addAttribute("loteria", loteria.nome());
        model.addAttribute("totalConcursos", estatisticas.totalConcursos());
        model.addAttribute("medias", estatisticas.medias());
        model.addAttribute("dezenaMin", loteria.getMin());
        model.addAttribute("dezenaMax", loteria.getMax());
        model.addAttribute("abaAtiva", "manutencao");
        return "dashboard";
    }

    @GetMapping("/jogos")
    public String jogos(Model model) {
        List<ConfigLoteria> loterias = Arrays.stream(Loteria.values())
                .map(loteria -> new ConfigLoteria(loteria.nome(), loteria.getMin(), loteria.getMax(),
                        loteria.getMinDezenas(), loteria.getMaxDezenas()))
                .toList();
        model.addAttribute("jogos", jogoService.listarComResumo());
        model.addAttribute("loterias", loterias);
        model.addAttribute("abaAtiva", "jogos");
        return "jogos";
    }

    @GetMapping("/jogos/{id}")
    public String jogo(@PathVariable String id, Model model) {
        ConferenciaJogo conferencia = jogoService.conferir(id);
        model.addAttribute("conferencia", conferencia);
        model.addAttribute("abaAtiva", "jogos");
        return "jogo";
    }

    @GetMapping("/ml")
    public String machineLearning(Model model) {
        model.addAttribute("abaAtiva", "ml");
        return "ml";
    }
}
