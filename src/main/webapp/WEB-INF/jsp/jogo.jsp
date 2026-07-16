<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titulo" value="Conferência — ${conferencia.jogo.loteria}"/>
<%@ include file="comum/cabecalho.jspf" %>
<fmt:setLocale value="pt_BR"/>

<h1>${conferencia.jogo.loteria} — conferência do jogo</h1>
<c:if test="${not empty conferencia.jogo.descricao}">
    <p><c:out value="${conferencia.jogo.descricao}"/></p>
</c:if>

<div class="dezenas-grandes">
    <c:forEach var="numero" items="${conferencia.jogo.numeros}">
        <span class="dezena grande">${numero}</span>
    </c:forEach>
</div>
<p>Teimosinha do concurso ${conferencia.jogo.concursoInicial} ao ${conferencia.jogo.concursoFinal}</p>

<div class="cards">
    <div class="card">
        <p class="numero-grande">${conferencia.resumo.premiados}</p>
        <p>acertei (premiado)</p>
    </div>
    <div class="card">
        <p class="numero-grande">${conferencia.resumo.naoPremiados}</p>
        <p>errei (sem prêmio)</p>
    </div>
    <div class="card">
        <p class="numero-grande">${conferencia.resumo.pendentes}</p>
        <p>pendentes</p>
    </div>
</div>

<h2>Concurso a concurso</h2>
<table>
    <thead>
    <tr>
        <th>Concurso</th>
        <th>Data</th>
        <th>Dezenas sorteadas (acertos destacados)</th>
        <th>Acertos</th>
        <th>Situação</th>
        <th>Prêmio</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="item" items="${conferencia.concursos}">
        <tr>
            <td>${item.concurso}</td>
            <td>
                <c:if test="${item.dataSorteio != null}">
                    <fmt:formatDate value="${item.dataSorteio}" pattern="dd/MM/yyyy"/>
                </c:if>
            </td>
            <td>
                <c:forEach var="numero" items="${item.dezenasSorteadas}">
                    <span class="dezena ${item.dezenasAcertadas.contains(numero) ? 'acertada' : ''}">${numero}</span>
                </c:forEach>
            </td>
            <td>${item.acertos}</td>
            <td>
                <c:choose>
                    <c:when test="${item.situacao == 'PREMIADO'}">
                        <span class="badge premiado">&#10004; premiado</span>
                    </c:when>
                    <c:when test="${item.situacao == 'NAO_PREMIADO'}">
                        <span class="badge nao-premiado">&#10008; sem prêmio</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge pendente">&#8987; pendente</span>
                    </c:otherwise>
                </c:choose>
            </td>
            <td>
                <c:choose>
                    <c:when test="${item.premio.status == 'VALOR'}">R$ <fmt:formatNumber value="${item.premio.valor}" minFractionDigits="2" maxFractionDigits="2"/></c:when>
                    <c:when test="${item.premio.status == 'SEM_GANHADOR'}">não houve ganhador nesta faixa</c:when>
                    <c:when test="${item.premio.status == 'INDISPONIVEL'}">indisponível no momento</c:when>
                </c:choose>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<p><a class="botao" href="/jogos">&laquo; voltar aos meus jogos</a></p>

<%@ include file="comum/rodape.jspf" %>
