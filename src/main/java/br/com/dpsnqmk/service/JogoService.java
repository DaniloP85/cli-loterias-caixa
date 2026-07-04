package br.com.dpsnqmk.service;

import br.com.dpsnqmk.controller.api.JogoNaoEncontradoException;
import br.com.dpsnqmk.dto.ConcursoMongoDTO;
import br.com.dpsnqmk.dto.ConferenciaConcurso;
import br.com.dpsnqmk.dto.ConferenciaJogo;
import br.com.dpsnqmk.dto.JogoComResumo;
import br.com.dpsnqmk.dto.JogoMongoDTO;
import br.com.dpsnqmk.dto.ResumoJogo;
import br.com.dpsnqmk.enums.Loteria;
import br.com.dpsnqmk.repository.ConcursoRepository;
import br.com.dpsnqmk.repository.JogoRepository;
import org.springframework.stereotype.Service;

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

    public JogoService(JogoRepository jogoRepository, ConcursoRepository concursoRepository) {
        this.jogoRepository = jogoRepository;
        this.concursoRepository = concursoRepository;
    }

    public JogoMongoDTO criar(String loteriaNome, List<Integer> numeros,
                              Integer concursoInicial, Integer quantidadeConcursos, String descricao) {
        Loteria loteria = Loteria.from(loteriaNome);

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

    public List<JogoComResumo> listarComResumo() {
        return jogoRepository.findAllByOrderByCriadoEmDesc().stream()
                .map(jogo -> new JogoComResumo(jogo, resumo(conferirConcursos(jogo))))
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
                .findByLoteriaAndConcursoGreaterThanEqualAndConcursoLessThanEqualOrderByConcursoAsc(
                        jogo.getLoteria(), jogo.getConcursoInicial(), jogo.getConcursoFinal())
                .stream()
                .collect(Collectors.toMap(ConcursoMongoDTO::getConcurso, Function.identity(), (a, b) -> a));

        Loteria loteria = Loteria.from(jogo.getLoteria());
        List<ConferenciaConcurso> conferencia = new ArrayList<>();
        for (int numero = jogo.getConcursoInicial(); numero <= jogo.getConcursoFinal(); numero++) {
            ConcursoMongoDTO resultado = resultadosPorConcurso.get(numero);
            if (resultado == null) {
                conferencia.add(ConferenciaConcurso.pendente(numero));
                continue;
            }
            List<Integer> acertadas = resultado.getNumerosSorteados().stream()
                    .filter(jogo.getNumeros()::contains)
                    .toList();
            String situacao = loteria.premiado(acertadas.size())
                    ? ConferenciaConcurso.PREMIADO
                    : ConferenciaConcurso.NAO_PREMIADO;
            conferencia.add(new ConferenciaConcurso(numero, resultado.getDataSorteio(),
                    resultado.getNumerosSorteados(), acertadas, acertadas.size(), situacao));
        }
        return conferencia;
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
}
