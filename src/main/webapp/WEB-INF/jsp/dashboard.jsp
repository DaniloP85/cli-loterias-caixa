<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titulo" value="Dashboard — ${loteria}"/>
<%@ include file="comum/cabecalho.jspf" %>

<h1>Dashboard da ${loteria}</h1>

<div class="cards">
    <div class="card">
        <p class="numero-grande">${totalConcursos}</p>
        <p>concursos na base</p>
    </div>
    <c:forEach var="media" items="${medias}">
        <div class="card">
            <p class="numero-grande"><fmt:formatNumber value="${media.value}" maxFractionDigits="2"/></p>
            <p>média de ${media.key}</p>
        </div>
    </c:forEach>
</div>

<h2>Frequência das dezenas</h2>
<canvas id="graficoFrequencia" height="110"></canvas>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4"></script>
<script>
    var loteria = '${loteria}';

    fetch('/api/loterias/' + loteria + '/estatisticas')
        .then(function (resposta) { return resposta.json(); })
        .then(function (estatisticas) {
            var dezenas = estatisticas.frequenciaDezenas.map(function (f) { return f.dezena; });
            var frequencias = estatisticas.frequenciaDezenas.map(function (f) { return f.frequencia; });

            new Chart(document.getElementById('graficoFrequencia'), {
                type: 'bar',
                data: {
                    labels: dezenas,
                    datasets: [{
                        label: 'vezes sorteada',
                        data: frequencias,
                        backgroundColor: '#2563eb'
                    }]
                },
                options: {
                    plugins: {legend: {display: false}},
                    scales: {y: {beginAtZero: true}}
                }
            });
        });
</script>

<%@ include file="comum/rodape.jspf" %>
