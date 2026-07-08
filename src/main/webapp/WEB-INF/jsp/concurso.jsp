<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titulo" value="Concurso ${concurso.concurso} — ${loteria}"/>
<%@ include file="comum/cabecalho.jspf" %>

<h1>${loteria} — concurso ${concurso.concurso}</h1>
<p>Sorteado em <fmt:formatDate value="${concurso.dataSorteio}" pattern="dd/MM/yyyy"/></p>

<div class="dezenas-grandes">
    <c:forEach var="numero" items="${concurso.numerosSorteados}">
        <span class="dezena grande">${numero}</span>
    </c:forEach>
</div>

<h2>Features estatísticas</h2>
<table class="features">
    <tr><th>Soma</th><td>${concurso.historial.soma}</td></tr>
    <tr><th>Média</th><td><fmt:formatNumber value="${concurso.historial.media}" maxFractionDigits="4"/></td></tr>
    <tr><th>Desvio padrão</th><td><fmt:formatNumber value="${concurso.historial.desvioPadrao}" maxFractionDigits="4"/></td></tr>
    <tr><th>Log-produto</th><td><fmt:formatNumber value="${concurso.historial.logProduto}" maxFractionDigits="4"/></td></tr>
    <tr><th>Pares</th><td>${concurso.historial.pares}</td></tr>
    <tr><th>Ímpares</th><td>${concurso.historial.impares}</td></tr>
    <tr><th>Baixos</th><td>${concurso.historial.baixos}</td></tr>
    <tr><th>Altos</th><td>${concurso.historial.altos}</td></tr>
</table>

<p>
    <a class="botao" href="/loterias/${loteria}">&laquo; voltar aos concursos</a>
    <a class="botao" href="/api/loterias/${loteria}/concursos/${concurso.concurso}">ver JSON</a>
</p>

<%@ include file="comum/rodape.jspf" %>
