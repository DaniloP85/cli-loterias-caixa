package br.com.dpsnqmk.service;

import br.com.dpsnqmk.dto.EstatisticasDTO;
import br.com.dpsnqmk.enums.Loteria;
import br.com.dpsnqmk.repository.ConcursoRepository;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EstatisticaService {

    private static final String COLECAO = "resultados";

    private final MongoTemplate mongoTemplate;
    private final ConcursoRepository repository;

    public EstatisticaService(MongoTemplate mongoTemplate, ConcursoRepository repository) {
        this.mongoTemplate = mongoTemplate;
        this.repository = repository;
    }

    public EstatisticasDTO estatisticas(Loteria loteria) {
        String nome = loteria.nome();
        return new EstatisticasDTO(
                nome,
                repository.countByLoteria(nome),
                frequenciaDezenas(nome),
                mediasFeatures(nome)
        );
    }

    private List<EstatisticasDTO.FrequenciaDezena> frequenciaDezenas(String loteria) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("loteria").is(loteria)),
                Aggregation.unwind("numeros_sorteados"),
                Aggregation.group("numeros_sorteados").count().as("frequencia"),
                Aggregation.sort(Sort.Direction.ASC, "_id")
        );

        return mongoTemplate.aggregate(agg, COLECAO, Document.class).getMappedResults().stream()
                .map(doc -> new EstatisticasDTO.FrequenciaDezena(
                        ((Number) doc.get("_id")).intValue(),
                        ((Number) doc.get("frequencia")).longValue()))
                .toList();
    }

    private Map<String, Double> mediasFeatures(String loteria) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("loteria").is(loteria)),
                Aggregation.group()
                        .avg("historial.soma").as("soma")
                        .avg("historial.media").as("media")
                        .avg("historial.desvio_padrao").as("desvioPadrao")
                        .avg("historial.log_produto").as("logProduto")
                        .avg("historial.pares").as("pares")
                        .avg("historial.impares").as("impares")
                        .avg("historial.baixos").as("baixos")
                        .avg("historial.altos").as("altos")
        );

        Document doc = mongoTemplate.aggregate(agg, COLECAO, Document.class).getUniqueMappedResult();
        Map<String, Double> medias = new LinkedHashMap<>();
        if (doc != null) {
            for (String campo : List.of("soma", "media", "desvioPadrao", "logProduto",
                    "pares", "impares", "baixos", "altos")) {
                Number valor = (Number) doc.get(campo);
                medias.put(campo, valor == null ? 0.0 : valor.doubleValue());
            }
        }
        return medias;
    }
}
