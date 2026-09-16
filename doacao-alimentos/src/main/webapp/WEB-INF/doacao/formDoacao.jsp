<%@ page import="java.util.List" %>
<%@ page import="br.com.mvc.model.Doacao" %>
<%@ page import="br.com.mvc.model.Doador" %>
<%@ page import="br.com.mvc.model.Instituicao" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Doacao doacao = (Doacao) request.getAttribute("doacao");
    List<Doador> doadores = (List<Doador>) request.getAttribute("doadores");
    List<Instituicao> instituicoes = (List<Instituicao>) request.getAttribute("instituicoes");
    Boolean ehEdicao = (Boolean) request.getAttribute("ehEdicao");
    String erro = (String) request.getAttribute("erro");
    String ctx = request.getContextPath();
    if (ehEdicao == null) ehEdicao = false;
%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= ehEdicao ? "Editar Doação" : "Nova Doação" %> — DoaFácil</title>
    <link rel="stylesheet" href="<%= ctx %>/assets/css/style.css?v=5">
</head>
<body>

<header class="topo">
    <div class="container topo-conteudo">
        <div class="logo">
            <img src="<%= ctx %>/assets/img/DoaFacil.png" alt="Logo DoaFácil" class="logo-img">
            <div class="logo-texto">
                <h2>DoaFácil</h2>
                <span>Plataforma de Doações de Alimentos</span>
            </div>
        </div>
        <nav class="menu">
            <a href="<%= ctx %>/">Início</a>
            <a href="<%= ctx %>/doador/listar">Doadores</a>
            <a href="<%= ctx %>/instituicao/listar">Instituições</a>
            <a href="<%= ctx %>/doacao/listar">Doações</a>
        </nav>
    </div>
</header>

<main>
    <section class="container">
        <div class="formulario-card">
            <h1><%= ehEdicao ? "Editar Doação" : "Nova Doação" %></h1>

            <% if (erro != null) { %>
                <div class="mensagem-erro"><%= erro %></div>
            <% } %>

            <form class="formulario" method="post"
                  action="<%= ctx %>/doacao/<%= ehEdicao ? "alterar" : "inserir" %>">

                <% if (ehEdicao) { %>
                    <input type="hidden" name="id" value="<%= doacao.getId() %>">
                <% } %>

                <div class="campo">
                    <label for="doadorId">Doador</label>
                    <select id="doadorId" name="doadorId" required>
                        <option value="">Selecione um doador</option>
                        <% if (doadores != null) {
                            for (Doador item : doadores) {
                                boolean sel = doacao != null && doacao.getDoador() != null
                                        && doacao.getDoador().getId().equals(item.getId()); %>
                                <option value="<%= item.getId() %>" <%= sel ? "selected" : "" %>>
                                    <%= item.getNome() %>
                                </option>
                        <%  }
                           } %>
                    </select>
                </div>

                <div class="campo">
                    <label for="instituicaoId">Instituição</label>
                    <select id="instituicaoId" name="instituicaoId" required>
                        <option value="">Selecione uma instituição</option>
                        <% if (instituicoes != null) {
                            for (Instituicao item : instituicoes) {
                                boolean sel = doacao != null && doacao.getInstituicao() != null
                                        && doacao.getInstituicao().getId().equals(item.getId()); %>
                                <option value="<%= item.getId() %>" <%= sel ? "selected" : "" %>>
                                    <%= item.getNome() %>
                                </option>
                        <%  }
                           } %>
                    </select>
                </div>

                <div class="campo">
                    <label for="descricao">Itens da Doação</label>
                    <textarea id="descricao" name="descricao"
                              placeholder="Ex.: 10 kg de feijão, 5 pacotes de arroz, 4 L de óleo"
                              required><%= doacao != null && doacao.getDescricao() != null ? doacao.getDescricao() : "" %></textarea>
                    <span class="ajuda-campo">Separe os itens por vírgula.</span>
                </div>

                <div class="campo">
                    <label for="dataDoacao">Data da Doação</label>
                    <input id="dataDoacao" type="date" name="dataDoacao"
                           placeholder="dd/mm/aaaa"
                           value="<%= doacao != null && doacao.getDataDoacao() != null ? doacao.getDataDoacao() : "" %>"
                           required>
                </div>

                <div class="formulario-acoes">
                    <button class="botao-formulario" type="submit">
                        <%= ehEdicao ? "Salvar Alterações" : "Cadastrar" %>
                    </button>
                    <a class="link-voltar" href="<%= ctx %>/doacao/listar">Voltar</a>
                </div>
            </form>
        </div>
    </section>
</main>

<footer>
    <div class="container">DoaFácil — Plataforma de Doações de Alimentos</div>
</footer>

</body>
</html>
