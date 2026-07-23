package br.com.dpsnqmk.service;

import br.com.dpsnqmk.controller.api.ConcursoNaoEncontradoException;
import br.com.dpsnqmk.controller.api.JogoNaoEncontradoException;
import br.com.dpsnqmk.dto.ConcursoMongoDTO;
import br.com.dpsnqmk.dto.ConferenciaConcurso;
import br.com.dpsnqmk.dto.ConferenciaJogo;
import br.com.dpsnqmk.dto.JogoComResumo;
import br.com.dpsnqmk.dto.JogoMongoDTO;
import br.com.dpsnqmk.dto.PremioFaixa;
import br.com.dpsnqmk.dto.ResumoJogo;
import br.com.dpsnqmk.enums.Loteria;
import br.com.dpsnqmk.repository.ConcursoRepository;
import br.com.dpsnqmk.repository.JogoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JogoService {

    private final JogoRepository jogoRepository;
    private final ConcursoRepository concursoRepository;
    private final PremioService premioService;

    public JogoService(JogoRepository jogoRepository, ConcursoRepository concursoRepository,
                        PremioService premioService) {
        this.jogoRepository = jogoRepository;
        this.concursoRepository = concursoRepository;
        this.premioService = premioService;
    }

    public JogoMongoDTO criar(String loteriaNome, List<Integer> numeros,
                              Integer concursoInicial, Integer quantidadeConcursos, String descricao) {
        Loteria loteria = Loteria.from(loteriaNome);

        TreeSet<Integer> dezenas = validarDezenas(loteria, numeros);
        if (dezenas.size() < loteria.getMinDezenas() || dezenas.size() > loteria.getMaxDezenas()) {
            throw new IllegalArgumentException("A " + loteria.nome() + " aceita de "
                    + loteria.getMinDezenas() + " a " + loteria.getMaxDezenas()
                    + " dezenas por jogo (recebi " + dezenas.size() + ")");
        }
        if (concursoInicial == null || concursoInicial < 1) {
            throw new IllegalArgumentException("Concurso inicial deve ser maior ou igual a 1");
        }
        if (quantidadeConcursos == null || quantidadeConcursos < 1) {
            throw new IllegalArgumentException("Quantidade de concursos deve ser maior ou igual a 1");
        }

        JogoMongoDTO jogo = new JogoMongoDTO(loteria.nome(), List.copyOf(dezenas),
                concursoInicial, quantidadeConcursos, descricao, new Date());
        return jogoRepository.save(jogo);
    }

    private TreeSet<Integer> validarDezenas(Loteria loteria, List<Integer> numeros) {
        if (numeros == null || numeros.isEmpty()) {
            throw new IllegalArgumentException("Informe as dezenas do jogo");
        }
        TreeSet<Integer> dezenas = new TreeSet<>(numeros);
        if (dezenas.size() != numeros.size()) {
            throw new IllegalArgumentException("Há dezenas repetidas no jogo");
        }
        for (int dezena : dezenas) {
            if (dezena < loteria.getMin() || dezena > loteria.getMax()) {
                throw new IllegalArgumentException("Dezena " + dezena + " fora do intervalo da "
                        + loteria.nome() + " (" + loteria.getMin() + " a " + loteria.getMax() + ")");
            }
        }
        return dezenas;
    }

    /** Conferência avulsa: interseção das dezenas apostadas com um sorteio já importado. */
    public ConferenciaConcurso conferirAvulso(Loteria loteria, int numeroConcurso, List<Integer> numeros) {
        List<Integer> dezenas = List.copyOf(validarDezenas(loteria, numeros));
        ConcursoMongoDTO resultado = concursoRepository
                .findByLoteriaAndConcurso(loteria.nome(), numeroConcurso)
                .orElseThrow(() -> new ConcursoNaoEncontradoException(loteria.nome(), numeroConcurso));
        return conferirContra(resultado, dezenas, loteria);
    }

    public List<JogoComResumo> listarComResumo() {
        return jogoRepository.findAllByOrderByCriadoEmDesc().stream()
                .map(jogo -> {
                    List<ConferenciaConcurso> concursos = conferirConcursos(jogo);
                    ResumoJogo resumo = resumo(concursos);
                    BigDecimal custoTotal = premioService
                            .custoAposta(Loteria.from(jogo.getLoteria()), jogo.getNumeros().size())
                            .multiply(BigDecimal.valueOf(jogo.getQuantidadeConcursos()));
                    ConferenciaConcurso ultimo = ultimoConcursoApurado(concursos);
                    List<Integer> dezenasAcertadas = ultimo == null ? List.of() : ultimo.getDezenasAcertadas();
                    Integer concursoComparado = ultimo == null ? null : ultimo.getConcurso();
                    return new JogoComResumo(jogo, resumo, custoTotal, ganhoTotal(concursos),
                            dezenasAcertadas, concursoComparado);
                })
                .toList();
    }

    public ConferenciaJogo conferir(String id) {
        JogoMongoDTO jogo = buscar(id);
        List<ConferenciaConcurso> concursos = conferirConcursos(jogo);
        return new ConferenciaJogo(jogo, resumo(concursos), concursos);
    }

    public JogoMongoDTO buscar(String id) {
        return jogoRepository.findById(id)
                .orElseThrow(() -> new JogoNaoEncontradoException(id));
    }

    public void excluir(String id) {
        jogoRepository.delete(buscar(id));
    }

    private List<ConferenciaConcurso> conferirConcursos(JogoMongoDTO jogo) {
        Map<Integer, ConcursoMongoDTO> resultadosPorConcurso = concursoRepository
                .findConcursosNoIntervalo(jogo.getLoteria(), jogo.getConcursoInicial(), jogo.getConcursoFinal())
                .stream()
                .collect(Collectors.toMap(ConcursoMongoDTO::getConcurso, Function.identity(), (a, b) -> a));

        Loteria loteria = Loteria.from(jogo.getLoteria());
        List<ConferenciaConcurso> conferencia = new ArrayList<>();
        for (int numero = jogo.getConcursoInicial(); numero <= jogo.getConcursoFinal(); numero++) {
            ConcursoMongoDTO resultado = resultadosPorConcurso.get(numero);
            conferencia.add(resultado == null
                    ? ConferenciaConcurso.pendente(numero)
                    : conferirContra(resultado, jogo.getNumeros(), loteria));
        }
        return conferencia;
    }

    private ConferenciaConcurso conferirContra(ConcursoMongoDTO resultado, List<Integer> numerosJogados,
                                               Loteria loteria) {
        List<Integer> acertadas = resultado.getNumerosSorteados().stream()
                .filter(numerosJogados::contains)
                .toList();
        boolean premiado = loteria.premiado(acertadas.size());
        String situacao = premiado ? ConferenciaConcurso.PREMIADO : ConferenciaConcurso.NAO_PREMIADO;
        PremioFaixa premio = premiado
                ? premioService.valorPremio(resultado, loteria, acertadas.size())
                : null;
        return new ConferenciaConcurso(resultado.getConcurso(), resultado.getDataSorteio(),
                resultado.getNumerosSorteados(), acertadas, acertadas.size(), situacao, premio);
    }

    private ResumoJogo resumo(List<ConferenciaConcurso> concursos) {
        int premiados = 0;
        int naoPremiados = 0;
        int pendentes = 0;
        for (ConferenciaConcurso concurso : concursos) {
            switch (concurso.getSituacao()) {
                case ConferenciaConcurso.PREMIADO -> premiados++;
                case ConferenciaConcurso.NAO_PREMIADO -> naoPremiados++;
                default -> pendentes++;
            }
        }
        return new ResumoJogo(premiados + naoPremiados, premiados, naoPremiados, pendentes);
    }

    /** Concurso mais recente já apurado do intervalo, ou null se nenhum foi apurado ainda (research.md §1-2). */
    private ConferenciaConcurso ultimoConcursoApurado(List<ConferenciaConcurso> concursos) {
        for (int i = concursos.size() - 1; i >= 0; i--) {
            ConferenciaConcurso concurso = concursos.get(i);
            if (!ConferenciaConcurso.PENDENTE.equals(concurso.getSituacao())) {
                return concurso;
            }
        }
        return null;
    }

    /** Soma dos prêmios já recebidos pela teimosinha (research.md §5). */
    private BigDecimal ganhoTotal(List<ConferenciaConcurso> concursos) {
        BigDecimal total = BigDecimal.ZERO;
        for (ConferenciaConcurso concurso : concursos) {
            if (ConferenciaConcurso.PREMIADO.equals(concurso.getSituacao())
                    && PremioFaixa.VALOR.equals(concurso.getPremio().getStatus())) {
                total = total.add(concurso.getPremio().getValor());
            }
        }
        return total;
    }
}
