package br.com.dpsnqmk.service;

import br.com.dpsnqmk.dto.ConcursoMongoDTO;
import br.com.dpsnqmk.dto.FeaturesDTO;
import br.com.dpsnqmk.dto.LinhaDataset;
import br.com.dpsnqmk.enums.Loteria;
import br.com.dpsnqmk.repository.ConcursoRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class DatasetService {

    private final ConcursoRepository repository;

    public DatasetService(ConcursoRepository repository) {
        this.repository = repository;
    }

    public List<LinhaDataset> dataset(Loteria loteria) {
        SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");
        return repository.findByLoteriaOrderByConcursoAsc(loteria.nome()).stream()
                .filter(concurso -> concurso.getHistorial() != null)
                .map(concurso -> converter(concurso, formatoData))
                .toList();
    }

    private LinhaDataset converter(ConcursoMongoDTO concurso, SimpleDateFormat formatoData) {
        FeaturesDTO f = concurso.getHistorial();
        return new LinhaDataset(
                concurso.getConcurso(),
                formatoData.format(concurso.getDataSorteio()),
                concurso.getNumerosSorteados(),
                f.getSoma(),
                f.getMedia(),
                f.getDesvioPadrao(),
                f.getLogProduto(),
                f.getPares(),
                f.getImpares(),
                f.getBaixos(),
                f.getAltos()
        );
    }

    public void escreverCsv(List<LinhaDataset> linhas, Writer writer) throws IOException {
        int qtdNumeros = linhas.stream()
                .mapToInt(linha -> linha.numeros().size())
                .max()
                .orElse(0);

        StringBuilder cabecalho = new StringBuilder("concurso,data");
        for (int i = 1; i <= qtdNumeros; i++) {
            cabecalho.append(",n").append(i);
        }
        cabecalho.append(",soma,media,desvio_padrao,log_produto,pares,impares,baixos,altos\n");
        writer.write(cabecalho.toString());

        for (LinhaDataset linha : linhas) {
            StringBuilder sb = new StringBuilder();
            sb.append(linha.concurso()).append(',').append(linha.data());
            for (int i = 0; i < qtdNumeros; i++) {
                sb.append(',');
                if (i < linha.numeros().size()) {
                    sb.append(linha.numeros().get(i));
                }
            }
            sb.append(',').append(linha.soma())
                    .append(',').append(linha.media())
                    .append(',').append(linha.desvioPadrao())
                    .append(',').append(linha.logProduto())
                    .append(',').append(linha.pares())
                    .append(',').append(linha.impares())
                    .append(',').append(linha.baixos())
                    .append(',').append(linha.altos())
                    .append('\n');
            writer.write(sb.toString());
        }
    }
}
