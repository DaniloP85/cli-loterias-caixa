<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Manutenção — Loterias Caixa"/>
<%@ include file="comum/cabecalho.jspf" %>

<h1>Manutenção da base</h1>
<div class="cards">
    <c:forEach var="card" items="${cards}">
        <div class="card" data-loteria="${card.nome}" data-estado="${card.estadoImportacao}">
            <h2>${card.nome}</h2>
            <p class="numero-grande" id="total-${card.nome}">${card.totalConcursos}</p>
            <p>concursos na base</p>
            <p class="estado" id="estado-${card.nome}">
                <c:choose>
                    <c:when test="${card.totalConcursos > 0}">
                        base atualizada até o concurso ${card.ultimoConcurso}
                        — sorteio de <fmt:formatDate value="${card.dataUltimoConcurso}" pattern="dd/MM/yyyy"/>
                    </c:when>
                    <c:otherwise>nenhum concurso importado ainda</c:otherwise>
                </c:choose>
            </p>
            <div class="progresso" id="progresso-${card.nome}" hidden>
                <div class="progresso-barra">
                    <div class="progresso-preenchido" id="barra-${card.nome}"></div>
                </div>
                <p class="progresso-texto">
                    <span id="pct-${card.nome}">0%</span> —
                    concurso <span id="atual-${card.nome}">0</span> de <span id="fim-${card.nome}">0</span>
                </p>
            </div>
            <div class="acoes">
                <a class="botao" href="/loterias/${card.nome}">Concursos</a>
                <a class="botao" href="/loterias/${card.nome}/dashboard">Dashboard</a>
                <button class="botao destaque" onclick="atualizar('${card.nome}')">Atualizar</button>
                <button class="botao perigo" onclick="reconstruir('${card.nome}')">Reconstruir</button>
            </div>
        </div>
    </c:forEach>
</div>

<script>
    var fontes = {};

    function conectar(loteria) {
        if (fontes[loteria]) {
            return;
        }
        var fonte = new EventSource('/api/loterias/' + loteria + '/importacao/eventos');
        fontes[loteria] = fonte;
        fonte.addEventListener('status', function (evento) {
            var status = JSON.parse(evento.data);
            renderizar(loteria, status);
            if (status.estado === 'CONCLUIDO' || status.estado === 'ERRO') {
                fonte.close();
                delete fontes[loteria];
            }
        });
        fonte.onerror = function () {
            fonte.close();
            delete fontes[loteria];
        };
    }

    function renderizar(loteria, status) {
        var progresso = document.getElementById('progresso-' + loteria);
        var estado = document.getElementById('estado-' + loteria);
        estado.classList.remove('erro');

        if (status.estado === 'EM_EXECUCAO') {
            progresso.hidden = false;
            estado.textContent = 'Importando...';
            var pct = Math.floor(status.percentual);
            document.getElementById('barra-' + loteria).style.width = pct + '%';
            document.getElementById('pct-' + loteria).textContent = pct + '%';
            document.getElementById('atual-' + loteria).textContent = status.processados;
            document.getElementById('fim-' + loteria).textContent = status.total;
            document.getElementById('total-' + loteria).textContent = status.processados;
        } else if (status.estado === 'CONCLUIDO') {
            progresso.hidden = true;
            estado.textContent = 'base atualizada até o concurso ' + status.total;
            document.getElementById('total-' + loteria).textContent = status.total;
        } else if (status.estado === 'ERRO') {
            progresso.hidden = true;
            estado.textContent = 'importação: ERRO — ' + (status.mensagem || 'falha desconhecida');
            estado.classList.add('erro');
        }
    }

    function atualizar(loteria) {
        iniciar(loteria, false);
    }

    function reconstruir(loteria) {
        if (confirm('Apagar todos os concursos da ' + loteria + ' e reimportar do zero?')) {
            iniciar(loteria, true);
        }
    }

    function iniciar(loteria, completo) {
        fetch('/api/loterias/' + loteria + '/importacao' + (completo ? '?completo=true' : ''), {method: 'POST'})
            .then(function (resposta) {
                if (resposta.status !== 202 && resposta.status !== 409) {
                    return resposta.json().then(function (json) {
                        alert(json.erro || json.mensagem || 'Falha ao iniciar importação');
                    });
                }
                conectar(loteria);
            })
            .catch(function (erro) {
                alert('Falha ao iniciar importação: ' + erro);
            });
    }

    // reconecta aos streams de importações que já estavam rodando
    document.querySelectorAll('[data-loteria]').forEach(function (card) {
        if (card.dataset.estado === 'EM_EXECUCAO') {
            conectar(card.dataset.loteria);
        }
    });
</script>

<%@ include file="comum/rodape.jspf" %>
