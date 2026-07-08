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

<h2>Mapa de calor das dezenas</h2>
<p class="legenda-mapa">menos sorteada <span class="gradiente"></span> mais sorteada</p>
<div class="mapa-calor" id="mapa-calor" data-min="${dezenaMin}" data-max="${dezenaMax}"></div>

<script>
    var loteria = '${loteria}';
    var mapa = document.getElementById('mapa-calor');
    var dezenaMin = parseInt(mapa.dataset.min, 10);
    var dezenaMax = parseInt(mapa.dataset.max, 10);

    fetch('/api/loterias/' + loteria + '/estatisticas')
        .then(function (resposta) { return resposta.json(); })
        .then(function (estatisticas) {
            var frequencias = {};
            var maiorFrequencia = 0;
            estatisticas.frequenciaDezenas.forEach(function (item) {
                frequencias[item.dezena] = item.frequencia;
                if (item.frequencia > maiorFrequencia) {
                    maiorFrequencia = item.frequencia;
                }
            });

            for (var numero = dezenaMin; numero <= dezenaMax; numero++) {
                var celula = document.createElement('div');
                celula.className = 'celula-calor';
                var vezes = frequencias[numero] || 0;
                var intensidade = maiorFrequencia === 0 ? 0 : vezes / maiorFrequencia;
                if (intensidade === 0) {
                    celula.style.background = '#e5e7eb';
                } else {
                    celula.style.background =
                        'rgba(220, 38, 38, ' + (0.15 + 0.85 * intensidade).toFixed(3) + ')';
                }
                if (intensidade > 0.55) {
                    celula.classList.add('intensa');
                }
                celula.textContent = numero < 10 ? '0' + numero : numero;
                celula.title = 'dezena ' + numero + ' — sorteada ' + vezes
                    + (vezes === 1 ? ' vez' : ' vezes');
                mapa.appendChild(celula);
            }
        });
</script>

<%@ include file="comum/rodape.jspf" %>
