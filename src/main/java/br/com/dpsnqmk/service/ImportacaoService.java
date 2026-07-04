package br.com.dpsnqmk.service;

import br.com.dpsnqmk.dto.ConcursoDTO;
import br.com.dpsnqmk.dto.ConcursoMongoDTO;
import br.com.dpsnqmk.dto.FeaturesDTO;
import br.com.dpsnqmk.enums.Loteria;
import br.com.dpsnqmk.records.AltosBaixos;
import br.com.dpsnqmk.records.ParesImparesFeature;
import br.com.dpsnqmk.repository.ConcursoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static br.com.dpsnqmk.utility.MyMath.*;

@Service
public class ImportacaoService {

    public static final String URL_BASE = "https://servicebus2.caixa.gov.br/portaldeloterias/api/";

    private static final Logger LOG = LoggerFactory.getLogger(ImportacaoService.class);

    private final HttpService httpService;
    private final ConcursoRepository repository;
    private static final long SSE_TIMEOUT_MS = 30L * 60 * 1000;

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<Loteria, StatusImportacao> statusPorLoteria = new ConcurrentHashMap<>();
    private final Map<Loteria, CopyOnWriteArrayList<SseEmitter>> emittersPorLoteria = new ConcurrentHashMap<>();

    public ImportacaoService(HttpService httpService, ConcursoRepository repository) {
        this.httpService = httpService;
        this.repository = repository;
    }

    public StatusImportacao status(Loteria loteria) {
        return statusPorLoteria.computeIfAbsent(loteria, l -> new StatusImportacao());
    }

    /**
     * Assina os eventos SSE de progresso da loteria. Envia imediatamente o
     * status atual, para quem conecta com uma importação já em andamento.
     */
    public SseEmitter assinar(Loteria loteria) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        CopyOnWriteArrayList<SseEmitter> emitters =
                emittersPorLoteria.computeIfAbsent(loteria, l -> new CopyOnWriteArrayList<>());
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        try {
            emitter.send(SseEmitter.event().name("status").data(status(loteria)));
        } catch (IOException e) {
            emitters.remove(emitter);
        }
        return emitter;
    }

    private void publicar(Loteria loteria, StatusImportacao status) {
        List<SseEmitter> emitters = emittersPorLoteria.get(loteria);
        if (emitters == null) {
            return;
        }
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("status").data(status));
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }

    private void finalizarAssinaturas(Loteria loteria, StatusImportacao status) {
        List<SseEmitter> emitters = emittersPorLoteria.get(loteria);
        if (emitters == null) {
            return;
        }
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("status").data(status));
                emitter.complete();
            } catch (Exception e) {
                // emitter já desconectado; só remove
            }
        }
        emitters.clear();
    }

    /**
     * Dispara a importação em background. Retorna false se já houver uma
     * importação em andamento para a loteria.
     */
    public synchronized boolean iniciar(Loteria loteria, boolean completo) {
        StatusImportacao status = status(loteria);
        if (status.getEstado() == StatusImportacao.Estado.EM_EXECUCAO) {
            return false;
        }
        status.iniciar();
        executor.submit(() -> executar(loteria, completo, status));
        return true;
    }

    private void executar(Loteria loteria, boolean completo, StatusImportacao status) {
        String nome = loteria.nome();
        try {
            if (completo) {
                long removidos = repository.deleteByLoteria(nome);
                LOG.info("reimportação completa da {}: {} documentos removidos", nome, removidos);
            }

            int ultimoImportado = completo ? 0 : repository.findTopByLoteriaOrderByConcursoDesc(nome)
                    .map(ConcursoMongoDTO::getConcurso)
                    .orElse(0);

            int ultimoConcurso = httpService.recuperarConcurso(URL_BASE + nome + "/").getNumero();
            status.progresso(ultimoImportado, ultimoConcurso);
            publicar(loteria, status);
            LOG.info("importando {}: concursos {} a {}", nome, ultimoImportado + 1, ultimoConcurso);

            SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
            for (int concurso = ultimoImportado + 1; concurso <= ultimoConcurso; concurso++) {
                ConcursoDTO concursoDTO = httpService.recuperarConcurso(URL_BASE + nome + "/" + concurso);
                repository.save(converter(concursoDTO, loteria, formatoData));
                status.progresso(concurso, ultimoConcurso);
                publicar(loteria, status);
                if (concurso % 100 == 0) {
                    LOG.info("progresso: {} de {} para a loteria {}", concurso, ultimoConcurso, nome);
                }
            }

            status.concluir();
            finalizarAssinaturas(loteria, status);
            LOG.info("✅ importação da {} concluída: {} concursos na base", nome, ultimoConcurso);
        } catch (Exception e) {
            LOG.error("erro ao importar a loteria {}", nome, e);
            status.erro(e.getMessage());
            finalizarAssinaturas(loteria, status);
        }
    }

    private ConcursoMongoDTO converter(ConcursoDTO concursoDTO, Loteria loteria, SimpleDateFormat formatoData)
            throws ParseException {

        List<Integer> numerosSorteados = concursoDTO.getListaDezenas().stream()
                .map(Integer::parseInt)
                .toList();

        Date data = formatoData.parse(concursoDTO.getDataApuracao());
        long soma = numerosSorteados.stream().mapToLong(Integer::longValue).sum();
        AltosBaixos altosBaixos = contarAltosBaixos(numerosSorteados, loteria);
        ParesImparesFeature paresImpares = calcularParesImpares(numerosSorteados);

        return new ConcursoMongoDTO(
                concursoDTO.getNumero(),
                loteria.nome(),
                numerosSorteados,
                data,
                "processado",
                new FeaturesDTO(
                        soma,
                        getMedia(soma, numerosSorteados),
                        getLogProduto(numerosSorteados),
                        paresImpares.pares(),
                        paresImpares.impares(),
                        altosBaixos.baixos(),
                        altosBaixos.altos(),
                        calcularDesvioPadrao(numerosSorteados)
                )
        );
    }
}
