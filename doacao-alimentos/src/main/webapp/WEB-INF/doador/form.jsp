<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="br.com.mvc.model.Doador" %>

<%
    Doador doador =
            (Doador) request.getAttribute("doador");

    Boolean ehEdicao =
            (Boolean) request.getAttribute("ehEdicao");

    if (doador == null) {
        doador = new Doador();
    }

    if (ehEdicao == null) {
        ehEdicao = false;
    }

    String ctx = request.getContextPath();

    String action;

    if (ehEdicao) {
        action = ctx + "/doador/alterar";
    } else {
        action = ctx + "/doador/inserir";
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

    <form method="post" action="<%= action %>">

        <% if (ehEdicao) { %>

            <input
                type="hidden"
                name="id"
                value="<%= doador.getId() %>"
            >

        <% } %>

        <label>Nome:</label>

        <input
            type="text"
            name="nome"
            value="<%= doador.getNome() != null ? doador.getNome() : "" %>"
            required
        >

        <br><br>

        <label>Email:</label>

        <input
            type="email"
            name="email"
            value="<%= doador.getEmail() != null ? doador.getEmail() : "" %>"
            required
        >

        <br><br>

        <button type="submit">
            Salvar
        </button>

    </form>

    <br>

    <a href="<%= ctx %>/doador/listar">
        Voltar
    </a>

</body>
</html>