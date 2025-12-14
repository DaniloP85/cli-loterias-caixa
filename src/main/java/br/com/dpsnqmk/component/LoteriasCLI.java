package br.com.dpsnqmk.component;

import br.com.dpsnqmk.dto.ConcursoDTO;
import br.com.dpsnqmk.dto.ConcursoMongoDTO;
import br.com.dpsnqmk.dto.FeaturesDTO;
import br.com.dpsnqmk.enums.Loteria;
import br.com.dpsnqmk.records.AltosBaixos;
import br.com.dpsnqmk.records.ParesImparesFeature;
import br.com.dpsnqmk.service.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static br.com.dpsnqmk.utility.MyMath.*;

@Component
@CommandLine.Command(
        name = "loterias-caixa",
        description = "Busca resultados de loterias da Caixa com barra de progresso"
)
public class LoteriasCLI implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-l", "--loteria"},
            description = "Loteria desejada: megasena, quina, lotofacil, etc.",
            required = true
    )
    private String loteria;

    private static final Logger LOG = LoggerFactory.getLogger(LoteriasCLI.class);

    @Autowired
    private HttpService httpService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Integer call() throws Exception {
        // 1. Busca o total de concursos
        int totalConcursos = buscarTotalConcursos(loteria);
        LOG.info("Total de concursos da {}: {}", loteria, totalConcursos);

        for (int concurso = 1; concurso <= totalConcursos; concurso++) {
            ConcursoDTO concursoDTO = buscarConcurso(loteria, concurso);

            List<Integer> numerosSorteados = concursoDTO.getListaDezenas().stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            Date data = new SimpleDateFormat("dd/MM/yyyy").parse(concursoDTO.getDataApuracao());
            Long soma = numerosSorteados.stream().mapToLong(Integer::longValue).sum();
            AltosBaixos AltosBaixos = contarAltosBaixos(numerosSorteados, Loteria.valueOf(loteria.toUpperCase()));
            ParesImparesFeature paresImparesFeature = calcularParesImpares(numerosSorteados);

            ConcursoMongoDTO concursoMongoDTO = new ConcursoMongoDTO(
                    concursoDTO.getNumero(),
                    concursoDTO.getTipoJogo().toLowerCase(),
                    numerosSorteados,
                    data,
                    "processado",
                    new FeaturesDTO(
                            soma,
                            getMedia((double) soma, numerosSorteados),
                            getLogProduto(numerosSorteados),
                            paresImparesFeature.pares(),
                            paresImparesFeature.impares(),
                            AltosBaixos.baixos(),
                            AltosBaixos.altos(),
                            calcularDesvioPadrao(numerosSorteados)
                    )
            );
            LOG.info("progresso: {} de {} para a loteria {}", concurso, totalConcursos, loteria);
            mongoTemplate.insert(concursoMongoDTO, "resultados");
        }

        LOG.info("✅ Concluído!");
        return 0;
    }

    private int buscarTotalConcursos(String loteria) throws Exception {
        String url = String.format("https://servicebus2.caixa.gov.br/portaldeloterias/api/%s/", loteria);
        ConcursoDTO concursos = httpService.recuperarConcurso(url);
        return concursos.getNumero(); // Retorna o número do último concurso
    }

    private ConcursoDTO buscarConcurso(String loteria, int numeroConcurso) throws Exception {
        String url = String.format("https://servicebus2.caixa.gov.br/portaldeloterias/api/%s/%d", loteria, numeroConcurso);
        return httpService.recuperarConcurso(url);
    }
}