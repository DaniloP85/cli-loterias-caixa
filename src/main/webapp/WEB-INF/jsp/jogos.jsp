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
