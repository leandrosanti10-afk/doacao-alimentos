<%@ page import="br.com.mvc.model.Doador" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    Doador doador =
            (Doador) request.getAttribute("doador");

    Boolean ehEdicao =
            (Boolean) request.getAttribute("ehEdicao");

    String ctx =
            request.getContextPath();

    if (ehEdicao == null) {
        ehEdicao = false;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <title>
        <%= ehEdicao ? "Editar Doador" : "Novo Doador" %>
    </title>
</head>

<body>

    <h1>
        <%= ehEdicao ? "Editar Doador" : "Novo Doador" %>
    </h1>

    <%
    String erro =
            (String) request.getAttribute("erro");

    if (erro != null) {
%>

    <p style="color: red;">
        <%= erro %>
    </p>

<%
    }
%>

    <form method="post"
          action="<%= ctx %>/doador/<%= ehEdicao ? "alterar" : "inserir" %>">

        <% if (ehEdicao) { %>

            <input type="hidden"
                   name="id"
                   value="<%= doador.getId() %>">

        <% } %>

        <label>Nome:</label>
        <br>

        <input type="text"
               name="nome"
               value="<%= doador.getNome() != null ? doador.getNome() : "" %>"
               required>

        <br><br>

        <label>CPF/CNPJ:</label>
        <br>

        <input type="text"
               name="cpfCnpj"
               value="<%= doador.getCpfCnpj() != null ? doador.getCpfCnpj() : "" %>"
               required>

        <br><br>

        <label>Email:</label>
        <br>

        <input type="email"
               name="email"
               value="<%= doador.getEmail() != null ? doador.getEmail() : "" %>"
               required>

        <br><br>

        <label>Telefone:</label>
        <br>

        <input type="text"
               name="telefone"
               value="<%= doador.getTelefone() != null ? doador.getTelefone() : "" %>"
               required>

        <br><br>

        <label>Cidade:</label>
        <br>

        <input type="text"
               name="cidade"
               value="<%= doador.getCidade() != null ? doador.getCidade() : "" %>"
               required>

        <br><br>

        <button type="submit">
            <%= ehEdicao ? "Salvar Alterações" : "Cadastrar" %>
        </button>

    </form>

    <br>

    <a href="<%= ctx %>/doador/listar">
        Voltar
    </a>

</body>
</html>