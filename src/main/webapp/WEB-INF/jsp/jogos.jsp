<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Meus jogos — Loterias Caixa"/>
<%@ include file="comum/cabecalho.jspf" %>
<fmt:setLocale value="pt_BR"/>

<h1>Meus jogos</h1>

<c:if test="${empty jogos}">
    <p>Nenhum jogo cadastrado ainda. <a href="/jogos/cadastro">Cadastre seu primeiro jogo</a>.</p>
</c:if>

<c:if test="${not empty jogos}">
    <div class="grade-jogos">
        <c:forEach var="item" items="${jogos}">
            <div class="card">
                <div class="card-cabecalho">
                    <h2>${item.jogo.loteria} de ${item.jogo.concursoInicial} até ${item.jogo.concursoFinal}</h2>
                    <button class="botao-fechar" onclick="excluir('${item.jogo.id}')" title="excluir" aria-label="excluir">&times;</button>
                </div>
                <div class="dezenas-grandes">
                    <c:forEach var="numero" items="${item.jogo.numeros}">
                        <span class="dezena grande ${item.dezenasAcertadasUltimoConcurso.contains(numero) ? 'acertada' : ''}">${numero}</span>
                    </c:forEach>
                </div>
                <c:if test="${not empty item.jogo.descricao}">
                    <p><c:out value="${item.jogo.descricao}"/></p>
                </c:if>
                <p>Custo: R$ <fmt:formatNumber value="${item.custoTotal}" minFractionDigits="2" maxFractionDigits="2"/></p>
                <p>Ganhos: R$ <fmt:formatNumber value="${item.ganhoTotal}" minFractionDigits="2" maxFractionDigits="2"/></p>
                <p>
                    <span class="badge premiado clicavel" title="acertei" onclick="alternarPremiados('${item.jogo.id}')">&#10004; ${item.resumo.premiados}</span>
                    <span class="badge nao-premiado" title="errei">&#10008; ${item.resumo.naoPremiados}</span>
                    <span class="badge pendente" title="pendentes">&#8987; ${item.resumo.pendentes}</span>
                </p>
            </div>
        </c:forEach>
    </div>
</c:if>

<div id="painel-premiados" class="card" hidden></div>

<script>
    var jogoAbertoId = null;

    function alternarPremiados(id) {
        var painel = document.getElementById('painel-premiados');
        if (jogoAbertoId === id) {
            painel.hidden = true;
            painel.innerHTML = '';
            jogoAbertoId = null;
            return;
        }
        fetch('/api/jogos/' + id + '/conferencia')
            .then(function (resposta) { return resposta.json(); })
            .then(function (conferencia) {
                montarPainelPremiados(conferencia);
                jogoAbertoId = id;
            });
    }

    function montarPainelPremiados(conferencia) {
        var painel = document.getElementById('painel-premiados');
        var premiados = conferencia.concursos.filter(function (item) {
            return item.situacao === 'PREMIADO';
        }).sort(function (a, b) {
            return b.concurso - a.concurso;
        });

        if (premiados.length === 0) {
            painel.innerHTML = '<h2>Sorteios premiados</h2><p>Nenhum prêmio ainda nesta teimosinha.</p>';
            painel.hidden = false;
            return;
        }

        var linhas = premiados.map(function (item) {
            var dezenas = conferencia.jogo.numeros.map(function (numero) {
                var acertou = item.dezenasAcertadas.indexOf(numero) >= 0;
                return '<span class="dezena' + (acertou ? ' acertada' : '') + '">' + numero + '</span>';
            }).join('');
            var data = item.dataSorteio ? new Date(item.dataSorteio).toLocaleDateString('pt-BR') : '';
            var premio;
            if (item.premio.status === 'VALOR') {
                premio = 'R$ ' + item.premio.valor.toFixed(2).replace('.', ',');
            } else if (item.premio.status === 'SEM_GANHADOR') {
                premio = 'não houve ganhador nesta faixa';
            } else {
                premio = 'indisponível no momento';
            }
            return '<tr>'
                + '<td>' + conferencia.jogo.loteria + '</td>'
                + '<td>' + item.concurso + '</td>'
                + '<td>' + data + '</td>'
                + '<td>' + dezenas + '</td>'
                + '<td><span class="badge premiado">' + item.acertos + (item.acertos === 1 ? ' acerto' : ' acertos') + '</span></td>'
                + '<td>' + premio + '</td>'
                + '</tr>';
        }).join('');

        painel.innerHTML = '<h2>Sorteios premiados</h2>'
            + '<p class="legenda-cores">'
            + '<span><span class="dezena acertada">07</span> dezena jogada e acertada</span>'
            + '<span><span class="dezena">02</span> dezena jogada, não saiu no sorteio</span>'
            + '</p>'
            + '<table><thead><tr><th>Loteria</th><th>Concurso</th><th>Apuração</th>'
            + '<th>Dezenas jogadas (acertos destacados)</th><th>Acertos</th><th>Prêmio</th></tr></thead>'
            + '<tbody>' + linhas + '</tbody></table>';
        painel.hidden = false;
    }

    function excluir(id) {
        if (!confirm('Excluir este jogo?')) {
            return;
        }
        fetch('/api/jogos/' + id, {method: 'DELETE'}).then(function () {
            location.reload();
        });
    }
</script>

<%@ include file="comum/rodape.jspf" %>
