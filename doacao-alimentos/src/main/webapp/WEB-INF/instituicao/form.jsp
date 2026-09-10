<%@ page import="br.com.mvc.model.Instituicao" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    Instituicao instituicao =
            (Instituicao) request.getAttribute("instituicao");

    Boolean ehEdicao =
            (Boolean) request.getAttribute("ehEdicao");

    String ctx = request.getContextPath();

    if (ehEdicao == null) {
        ehEdicao = false;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <title>
        <%= ehEdicao ? "Editar Instituição" : "Nova Instituição" %>
    </title>
</head>

<body>

    <h1>
        <%= ehEdicao ? "Editar Instituição" : "Nova Instituição" %>
    </h1>

    <form method="post"
          action="<%= ctx %>/instituicao/<%= ehEdicao ? "alterar" : "inserir" %>">

        <%
            if (ehEdicao) {
        %>

            <input type="hidden"
                   name="id"
                   value="<%= instituicao.getId() %>">

        <%
            }
        %>

        <label>Nome:</label>

        <br>

        <input type="text"
               name="nome"
               value="<%= instituicao.getNome() != null ? instituicao.getNome() : "" %>"
               required>

        <br><br>

        <label>Endereço:</label>

        <br>

        <input type="text"
               name="endereco"
               value="<%= instituicao.getEndereco() != null ? instituicao.getEndereco() : "" %>"
               required>

        <br><br>

        <button type="submit">
            <%= ehEdicao ? "Salvar Alterações" : "Cadastrar" %>
        </button>

    </form>

    <br>

    <a href="<%= ctx %>/instituicao/listar">
        Voltar
    </a>

</body>
</html>