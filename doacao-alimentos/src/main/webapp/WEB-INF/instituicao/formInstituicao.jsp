<%@ page import="br.com.mvc.model.Instituicao" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Instituicao instituicao = (Instituicao) request.getAttribute("instituicao");
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
    <title><%= ehEdicao ? "Editar Instituição" : "Nova Instituição" %> — DoaFácil</title>
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
            <h1><%= ehEdicao ? "Editar Instituição" : "Nova Instituição" %></h1>

            <% if (erro != null) { %>
                <div class="mensagem-erro"><%= erro %></div>
            <% } %>

            <form class="formulario" method="post"
                  action="<%= ctx %>/instituicao/<%= ehEdicao ? "alterar" : "inserir" %>">

                <% if (ehEdicao) { %>
                    <input type="hidden" name="id" value="<%= instituicao.getId() %>">
                <% } %>

                <div class="campo">
                    <label for="nome">Nome</label>
                    <input id="nome" type="text" name="nome"
                           placeholder="Ex.: Casa Esperança"
                           value="<%= instituicao.getNome() != null ? instituicao.getNome() : "" %>" required>
                </div>

                <div class="campo">
                    <label for="endereco">Endereço</label>
                    <input id="endereco" type="text" name="endereco"
                           placeholder="Ex.: Rua das Flores, 102"
                           value="<%= instituicao.getEndereco() != null ? instituicao.getEndereco() : "" %>" required>
                </div>

                <div class="campo">
                    <label for="telefone">Telefone</label>
                    <input id="telefone" type="text" name="telefone"
                           placeholder="Ex.: (34) 99999-9999"
                           value="<%= instituicao.getTelefone() != null ? instituicao.getTelefone() : "" %>" required>
                </div>

                <div class="campo">
                    <label for="email">Email</label>
                    <input id="email" type="email" name="email"
                           placeholder="Ex.: contato@instituicao.org"
                           value="<%= instituicao.getEmail() != null ? instituicao.getEmail() : "" %>" required>
                </div>

                <div class="campo">
                    <label for="cidade">Cidade</label>
                    <input id="cidade" type="text" name="cidade"
                           placeholder="Ex.: Uberaba"
                           value="<%= instituicao.getCidade() != null ? instituicao.getCidade() : "" %>" required>
                </div>

                <div class="formulario-acoes">
                    <button class="botao-formulario" type="submit">
                        <%= ehEdicao ? "Salvar Alterações" : "Cadastrar" %>
                    </button>
                    <a class="link-voltar" href="<%= ctx %>/instituicao/listar">Voltar</a>
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
