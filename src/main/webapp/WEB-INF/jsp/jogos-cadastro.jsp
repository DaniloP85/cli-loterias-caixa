<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="titulo" value="Cadastrar jogo — Loterias Caixa"/>
<%@ include file="comum/cabecalho.jspf" %>
<fmt:setLocale value="pt_BR"/>

<h1>Cadastrar jogo</h1>

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
        <p id="sucesso-jogo" class="sucesso" hidden>Jogo cadastrado! <a href="/jogos">ver na conferência</a></p>
    </form>
</div>

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

    function limparFormulario() {
        selecionadas = [];
        document.querySelectorAll('.dezena-volante.selecionada').forEach(function (botao) {
            botao.classList.remove('selecionada');
        });
        document.getElementById('campo-concurso').value = '';
        document.getElementById('campo-qtd').value = '1';
        document.getElementById('campo-descricao').value = '';
        atualizarContador();
    }

    function cadastrar(evento) {
        evento.preventDefault();
        var erro = document.getElementById('erro-jogo');
        var sucesso = document.getElementById('sucesso-jogo');
        erro.hidden = true;
        sucesso.hidden = true;

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
                limparFormulario();
                sucesso.hidden = false;
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

    configurar();
</script>

<%@ include file="comum/rodape.jspf" %>
