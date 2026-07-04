<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Machine Learning — Loterias Caixa"/>
<%@ include file="comum/cabecalho.jspf" %>

<h1>Machine Learning</h1>

<div class="card">
    <h2>Em breve</h2>
    <p>
        Esta aba vai conectar em um serviço na AWS que, a partir das features estatísticas
        já calculadas por concurso (soma, média, desvio padrão, log-produto, pares/ímpares,
        altos/baixos), retorna sugestões de números mais refinadas.
    </p>
    <p>
        Enquanto isso, o dataset para treinar modelos já está disponível no export de cada
        loteria — por exemplo:
        <a href="/api/loterias/megasena/export?formato=csv">megasena-dataset.csv</a>.
    </p>
    <button class="botao destaque" disabled title="integração AWS em outra atividade">Sugerir números</button>
</div>

<%@ include file="comum/rodape.jspf" %>
