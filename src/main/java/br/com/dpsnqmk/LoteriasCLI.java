package br.com.dpsnqmk;

import br.com.dpsnqmk.dto.ConcursoDTO;
import br.com.dpsnqmk.service.HttpService;
import me.tongfei.progressbar.ProgressBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.concurrent.Callable;

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

    @Autowired
    private HttpService httpService;

    @Override
    public Integer call() throws Exception {
        // 1. Busca o total de concursos
        int totalConcursos = buscarTotalConcursos(loteria);
        System.out.printf("Total de concursos da %s: %d%n", loteria, totalConcursos);

        // 2. Barra de progresso
        try (ProgressBar pb = new ProgressBar("Buscando concursos...", totalConcursos)) {
            for (int concurso = 1; concurso <= totalConcursos; concurso++) {
                buscarConcurso(loteria, concurso);
                pb.step(); // Atualiza a barra
            }
        }

        System.out.println("✅ Concluído!");
        return 0;
    }

    private int buscarTotalConcursos(String loteria) throws Exception {
        String url = String.format("https://servicebus2.caixa.gov.br/portaldeloterias/api/%s/", loteria);
        ConcursoDTO concursos = httpService.recuperarConcurso(url);
        return concursos.getNumero(); // Retorna o número do último concurso
    }

    private void buscarConcurso(String loteria, int numeroConcurso) throws Exception {
        String url = String.format("https://servicebus2.caixa.gov.br/portaldeloterias/api/%s/%d", loteria, numeroConcurso);
        ConcursoDTO concursos = httpService.recuperarConcurso(url);

    }
}
