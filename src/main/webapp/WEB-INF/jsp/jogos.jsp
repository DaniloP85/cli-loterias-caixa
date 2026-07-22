<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Meus jogos — Loterias Caixa"/>
<%@ include file="comum/cabecalho.jspf" %>
<fmt:setLocale value="pt_BR"/>

<h1>Meus jogos</h1>

<div class="card form-jogo">
    <h2>Cadastrar jogo (teimosinha)</h2>
    <form onsubmit="return cadastrar(event)">
        <div class="linha-campos">
            <label>Loteria
                <select id="campo-loteria" onchange="configurar()">
                    <c:forEach var="config" items="${loterias}">
                        <option value="${config.nome}"
                                data-min="${config.min}"
                                data-max="${config.max}"
                                data-min-dezenas="${config.minDezenas}"
                                data-max-dezenas="${config.maxDezenas}">${config.nome}</option>
                    </c:forEach>
                </select>
            </label>
            <label>Concurso inicial
                <input id="campo-concurso" type="number" min="1" required>
            </label>
            <label>Qtd. de concursos
                <input id="campo-qtd" type="number" min="1" value="1" required>
            </label>
            <label>Descrição
                <input id="campo-descricao" placeholder="teimosinha de julho">
            </label>
        </div>

        <p class="contador-dezenas">Dezenas: <strong id="contador-dezenas"></strong></p>
        <div class="volante" id="volante"></div>

        <button class="botao destaque" type="submit" id="botao-salvar" disabled>Salvar</button>
        <p id="erro-jogo" class="erro" hidden></p>
    </form>
</div>

<c:if test="${empty jogos}">
    <p>Nenhum jogo cadastrado ainda. Cadastre acima a sua primeira teimosinha.</p>
</c:if>

<c:if test="${not empty jogos}">
    <table>
        <thead>
        <tr>
            <th>Loteria</th>
            <th>Dezenas</th>
            <th>Concursos</th>
            <th>Descrição</th>
            <th>Custo</th>
            <th>Resumo</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${jogos}">
            <tr>
                <td>${item.jogo.loteria}</td>
                <td>
                    <c:forEach var="numero" items="${item.jogo.numeros}">
                        <span class="dezena">${numero}</span>
                    </c:forEach>
                </td>
                <td>${item.jogo.concursoInicial} a ${item.jogo.concursoFinal}</td>
                <td><c:out value="${item.jogo.descricao}"/></td>
                <td>R$ <fmt:formatNumber value="${item.custoAposta}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td>
                    <span class="badge premiado" title="acertei">&#10004; ${item.resumo.premiados}</span>
                    <span class="badge nao-premiado" title="errei">&#10008; ${item.resumo.naoPremiados}</span>
                    <span class="badge pendente" title="pendentes">&#8987; ${item.resumo.pendentes}</span>
                </td>
                <td>
                    <button class="botao perigo" onclick="excluir('${item.jogo.id}')">excluir</button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>

<c:if test="${not empty jogos and empty resultados}">
    <h2>Sorteios premiados</h2>
    <p>Nenhum prêmio ainda — os sorteios premiados dos seus jogos aparecerão aqui.</p>
</c:if>

<c:if test="${not empty resultados}">
    <h2>Sorteios premiados</h2>
    <p class="legenda-cores">
        <span><span class="dezena acertada">07</span> dezena jogada e acertada</span>
        <span><span class="dezena">02</span> dezena jogada, não saiu no sorteio</span>
    </p>
    <table>
        <thead>
        <tr>
            <th>Loteria</th>
            <th>Concurso</th>
            <th>Apuração</th>
            <th>Dezenas jogadas (acertos destacados)</th>
            <th>Acertos</th>
            <th>Prêmio</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${resultados}">
            <tr>
                <td>${item.loteria}</td>
                <td>${item.concurso}</td>
                <td><fmt:formatDate value="${item.dataSorteio}" pattern="dd/MM/yyyy"/></td>
                <td>
                    <c:forEach var="numero" items="${item.numerosJogados}">
                        <span class="dezena ${item.dezenasAcertadas.contains(numero) ? 'acertada' : ''}">${numero}</span>
                    </c:forEach>
                </td>
                <td>
                    <span class="badge premiado">${item.acertos}
                        <c:choose><c:when test="${item.acertos == 1}">acerto</c:when><c:otherwise>acertos</c:otherwise></c:choose>
                    </span>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${item.premio.status == 'VALOR'}">R$ <fmt:formatNumber value="${item.premio.valor}" minFractionDigits="2" maxFractionDigits="2"/></c:when>
                        <c:when test="${item.premio.status == 'SEM_GANHADOR'}">não houve ganhador nesta faixa</c:when>
                        <c:otherwise>indisponível no momento</c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>

<script>
    var selecionadas = [];
    var configAtual = null;

    function configurar() {
        var select = document.getElementById('campo-loteria');
        var opcao = select.options[select.selectedIndex];
        configAtual = {
            min: parseInt(opcao.dataset.min, 10),
            max: parseInt(opcao.dataset.max, 10),
            minDezenas: parseInt(opcao.dataset.minDezenas, 10),
            maxDezenas: parseInt(opcao.dataset.maxDezenas, 10)
        };
        selecionadas = [];
        montarVolante();
        atualizarContador();
    }

    function montarVolante() {
        var volante = document.getElementById('volante');
        volante.innerHTML = '';
        for (var numero = configAtual.min; numero <= configAtual.max; numero++) {
            var botao = document.createElement('button');
            botao.type = 'button';
            botao.className = 'dezena-volante';
            botao.textContent = numero < 10 ? '0' + numero : numero;
            botao.dataset.numero = numero;
            botao.onclick = alternar;
            volante.appendChild(botao);
        }
    }

    function alternar(evento) {
        var botao = evento.currentTarget;
        var numero = parseInt(botao.dataset.numero, 10);
        var indice = selecionadas.indexOf(numero);
        if (indice >= 0) {
            selecionadas.splice(indice, 1);
            botao.classList.remove('selecionada');
        } else {
            if (selecionadas.length >= configAtual.maxDezenas) {
                return;
            }
            selecionadas.push(numero);
            botao.classList.add('selecionada');
        }
        atualizarContador();
    }

    function atualizarContador() {
        var faixa = configAtual.minDezenas === configAtual.maxDezenas
            ? String(configAtual.minDezenas)
            : configAtual.minDezenas + ' a ' + configAtual.maxDezenas;
        document.getElementById('contador-dezenas').textContent =
            selecionadas.length + ' selecionadas (a loteria pede ' + faixa + ')';
        document.getElementById('botao-salvar').disabled = selecionadas.length < configAtual.minDezenas;
        document.getElementById('volante').classList.toggle('cheio',
            selecionadas.length >= configAtual.maxDezenas);
    }

    function cadastrar(evento) {
        evento.preventDefault();
        var erro = document.getElementById('erro-jogo');
        erro.hidden = true;

        fetch('/api/jogos', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                loteria: document.getElementById('campo-loteria').value,
                numeros: selecionadas.slice().sort(function (a, b) { return a - b; }),
                concursoInicial: parseInt(document.getElementById('campo-concurso').value, 10),
                quantidadeConcursos: parseInt(document.getElementById('campo-qtd').value, 10),
                descricao: document.getElementById('campo-descricao').value
            })
        }).then(function (resposta) {
            if (resposta.status === 201) {
                location.reload();
                return;
            }
            return resposta.json().then(function (json) {
                erro.textContent = json.erro || 'Falha ao salvar o jogo';
                erro.hidden = false;
            });
        }).catch(function (falha) {
            erro.textContent = 'Falha ao salvar o jogo: ' + falha;
            erro.hidden = false;
        });
        return false;
    }

    function excluir(id) {
        if (!confirm('Excluir este jogo?')) {
            return;
        }
        fetch('/api/jogos/' + id, {method: 'DELETE'}).then(function () {
            location.reload();
        });
    }

    configurar();
</script>

<%@ include file="comum/rodape.jspf" %>
