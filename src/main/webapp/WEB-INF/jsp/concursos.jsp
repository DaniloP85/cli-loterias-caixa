<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="titulo" value="Concursos — ${loteria}"/>
<%@ include file="comum/cabecalho.jspf" %>

<h1>Concursos da ${loteria}</h1>

<c:if test="${pagina.totalElements == 0}">
    <p>Nenhum concurso importado ainda. Volte à <a href="/">home</a> e clique em "Importar".</p>
</c:if>

<c:if test="${pagina.totalElements > 0}">
    <table>
        <thead>
        <tr>
            <th>Concurso</th>
            <th>Data</th>
            <th>Números sorteados</th>
            <th>Soma</th>
            <th>Pares</th>
            <th>Ímpares</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="concurso" items="${pagina.content}">
            <tr>
                <td>${concurso.concurso}</td>
                <td><fmt:formatDate value="${concurso.dataSorteio}" pattern="dd/MM/yyyy"/></td>
                <td>
                    <c:forEach var="numero" items="${concurso.numerosSorteados}">
                        <span class="dezena">${numero}</span>
                    </c:forEach>
                </td>
                <td>${concurso.historial.soma}</td>
                <td>${concurso.historial.pares}</td>
                <td>${concurso.historial.impares}</td>
                <td><a href="/loterias/${loteria}/concursos/${concurso.concurso}">detalhes</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div class="paginacao">
        <c:if test="${pagina.hasPrevious()}">
            <a class="botao" href="/loterias/${loteria}?page=${pagina.number - 1}">&laquo; Anterior</a>
        </c:if>
        <span>página ${pagina.number + 1} de ${pagina.totalPages}</span>
        <c:if test="${pagina.hasNext()}">
            <a class="botao" href="/loterias/${loteria}?page=${pagina.number + 1}">Próxima &raquo;</a>
        </c:if>
    </div>
</c:if>

<%@ include file="comum/rodape.jspf" %>
