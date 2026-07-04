<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Loterias Caixa"/>
<%@ include file="comum/cabecalho.jspf" %>

<h1>Loterias</h1>
<div class="cards">
    <c:forEach var="card" items="${cards}">
        <div class="card">
            <h2>${card.nome}</h2>
            <p class="numero-grande">${card.totalConcursos}</p>
            <p>concursos importados</p>
            <p class="estado">importação: ${card.estadoImportacao}</p>
            <div class="acoes">
                <a class="botao" href="/loterias/${card.nome}">Concursos</a>
                <a class="botao" href="/loterias/${card.nome}/dashboard">Dashboard</a>
                <button class="botao destaque" onclick="importar('${card.nome}')">Importar</button>
            </div>
        </div>
    </c:forEach>
</div>

<script>
    function importar(loteria) {
        fetch('/api/loterias/' + loteria + '/importacao', {method: 'POST'})
            .then(function (resposta) { return resposta.json(); })
            .then(function (json) { alert(json.mensagem + '\nAcompanhe em: ' + json.status); })
            .catch(function (erro) { alert('Falha ao iniciar importação: ' + erro); });
    }
</script>

<%@ include file="comum/rodape.jspf" %>
