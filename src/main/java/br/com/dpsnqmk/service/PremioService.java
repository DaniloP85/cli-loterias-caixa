package br.com.dpsnqmk.service;

import br.com.dpsnqmk.dto.ConcursoDTO;
import br.com.dpsnqmk.dto.ConcursoMongoDTO;
import br.com.dpsnqmk.dto.PrecoAposta;
import br.com.dpsnqmk.dto.PremioFaixa;
import br.com.dpsnqmk.dto.RateioPremioMongoDTO;
import br.com.dpsnqmk.enums.Loteria;
import br.com.dpsnqmk.repository.ConcursoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PremioService {

    private static final Logger LOG = LoggerFactory.getLogger(PremioService.class);

    // "6 acertos", "0 acertos" etc. — casar por texto, não pelo índice de `faixa`
    // (a ordem de `faixa` muda por loteria, ver research.md secao 2)
    private static final Pattern ACERTOS_NA_DESCRICAO = Pattern.compile("(\\d+)\\s*acertos?");

    private final String urlBase;
    private final HttpService httpService;
    private final ConcursoRepository concursoRepository;

    public PremioService(@Value("${caixa.api.base-url}") String urlBase,
                         HttpService httpService,
                         ConcursoRepository concursoRepository) {
        this.urlBase = urlBase;
        this.httpService = httpService;
        this.concursoRepository = concursoRepository;
    }

    /** Custo oficial da aposta, dado pela tabela de preços da loteria (ideas/*.md secao 2). */
    public BigDecimal custoAposta(Loteria loteria, int quantidadeDezenas) {
        return loteria.getPrecos().get(quantidadeDezenas);
    }

    /** Tabela de referência de preços por quantidade de dezenas válida da loteria (US3). */
    public List<PrecoAposta> tabelaReferencia(Loteria loteria) {
        List<PrecoAposta> tabela = new ArrayList<>();
        for (int quantidade = loteria.getMinDezenas(); quantidade <= loteria.getMaxDezenas(); quantidade++) {
            BigDecimal valor = custoAposta(loteria, quantidade);
            if (valor != null) {
                tabela.add(new PrecoAposta(quantidade, valor));
            }
        }
        return tabela;
    }

    /**
     * Valor do prêmio da faixa batida por um jogo PREMIADO. Busca sob demanda e cacheia
     * (persistindo de volta no concurso) quando o rateio ainda não está persistido
     * (concurso legado, importado antes desta funcionalidade existir) — FR-007a/FR-007b.
     */
    public PremioFaixa valorPremio(ConcursoMongoDTO concurso, Loteria loteria, int acertos) {
        List<RateioPremioMongoDTO> rateioPremios = concurso.getRateioPremios();
        if (rateioPremios == null || rateioPremios.isEmpty()) {
            rateioPremios = buscarRateioSobDemanda(concurso, loteria);
            if (rateioPremios == null) {
                return PremioFaixa.indisponivel();
            }
        }
        return resolver(rateioPremios, acertos);
    }

    private PremioFaixa resolver(List<RateioPremioMongoDTO> rateioPremios, int acertos) {
        for (RateioPremioMongoDTO item : rateioPremios) {
            if (acertosDaDescricao(item.getDescricaoFaixa()) == acertos) {
                return item.getNumeroDeGanhadores() > 0
                        ? PremioFaixa.valor(item.getValorPremio())
                        : PremioFaixa.semGanhador();
            }
        }
        return PremioFaixa.indisponivel();
    }

    private int acertosDaDescricao(String descricaoFaixa) {
        if (descricaoFaixa == null) {
            return -1;
        }
        Matcher matcher = ACERTOS_NA_DESCRICAO.matcher(descricaoFaixa);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
    }

    /** @return null se a busca falhar (falha de rede/API) — nunca lança, para não quebrar a exibição. */
    private List<RateioPremioMongoDTO> buscarRateioSobDemanda(ConcursoMongoDTO concurso, Loteria loteria) {
        try {
            ConcursoDTO concursoDTO = httpService.recuperarConcurso(
                    urlBase + loteria.nome() + "/" + concurso.getConcurso());
            List<RateioPremioMongoDTO> rateioPremios =
                    ImportacaoService.converterRateioPremios(concursoDTO.getListaRateioPremio());
            if (rateioPremios == null) {
                return null;
            }
            concurso.setRateioPremios(rateioPremios);
            concursoRepository.save(concurso);
            return rateioPremios;
        } catch (Exception e) {
            LOG.warn("falha ao buscar rateio de prêmio sob demanda para {} concurso {}: {}",
                    loteria.nome(), concurso.getConcurso(), e.getMessage());
            return null;
        }
    }
}
