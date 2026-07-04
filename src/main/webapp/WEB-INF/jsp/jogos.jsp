<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Meus jogos — Loterias Caixa"/>
<%@ include file="comum/cabecalho.jspf" %>

<h1>Meus jogos</h1>

<div class="card form-jogo">
    <h2>Cadastrar jogo (teimosinha)</h2>
    <form onsubmit="return cadastrar(event)">
        <label>Loteria
            <select id="campo-loteria">
                <c:forEach var="nome" items="${loterias}">
                    <option value="${nome}">${nome}</option>
                </c:forEach>
            </select>
        </label>
        <label>Dezenas (separadas por espaço)
            <input id="campo-numeros" placeholder="04 08 15 16 23 42" required>
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
        <button class="botao destaque" type="submit">Salvar</button>
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
                <td>
                    <span class="badge premiado" title="acertei">&#10004; ${item.resumo.premiados}</span>
                    <span class="badge nao-premiado" title="errei">&#10008; ${item.resumo.naoPremiados}</span>
                    <span class="badge pendente" title="pendentes">&#8987; ${item.resumo.pendentes}</span>
                </td>
                <td>
                    <a class="botao" href="/jogos/${item.jogo.id}">conferir</a>
                    <button class="botao perigo" onclick="excluir('${item.jogo.id}')">excluir</button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>

<script>
    function cadastrar(evento) {
        evento.preventDefault();
        var erro = document.getElementById('erro-jogo');
        erro.hidden = true;

        var numeros = document.getElementById('campo-numeros').value
            .trim().split(/[\s,;]+/).filter(function (parte) { return parte !== ''; })
            .map(Number);
        if (numeros.some(isNaN)) {
            erro.textContent = 'Dezenas inválidas: use apenas números separados por espaço';
            erro.hidden = false;
            return false;
        }

        fetch('/api/jogos', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                loteria: document.getElementById('campo-loteria').value,
                numeros: numeros,
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
</script>

<%@ include file="comum/rodape.jspf" %>
