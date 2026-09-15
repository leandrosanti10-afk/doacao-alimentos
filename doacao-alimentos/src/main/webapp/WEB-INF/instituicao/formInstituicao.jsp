<%@ page import="br.com.mvc.model.Instituicao" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    Instituicao instituicao =
            (Instituicao)
                    request.getAttribute("instituicao");

    Boolean ehEdicao =
            (Boolean)
                    request.getAttribute("ehEdicao");

    String erro =
            (String)
                    request.getAttribute("erro");

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
        <%= ehEdicao
                ? "Editar Instituição"
                : "Nova Instituição" %>
    </title>

</head>

<body>

<h1>
    <%= ehEdicao
            ? "Editar Instituição"
            : "Nova Instituição" %>
</h1>

<%
    if (erro != null) {
%>

<p style="color: red;">
    <%= erro %>
</p>

<%
    }
%>

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

    <label>
        Nome:
    </label>

    <br>

    <input type="text"
           name="nome"
           value="<%= instituicao.getNome() != null
                    ? instituicao.getNome()
                    : "" %>"
           required>

    <br><br>

    <label>
        Endereço:
    </label>

    <br>

    <input type="text"
           name="endereco"
           value="<%= instituicao.getEndereco() != null
                    ? instituicao.getEndereco()
                    : "" %>"
           required>

    <br><br>

    <label>
        Telefone:
    </label>

    <br>

    <input type="text"
           name="telefone"
           value="<%= instituicao.getTelefone() != null
                    ? instituicao.getTelefone()
                    : "" %>"
           required>

    <br><br>

    <label>
        Email:
    </label>

    <br>

    <input type="email"
           name="email"
           value="<%= instituicao.getEmail() != null
                    ? instituicao.getEmail()
                    : "" %>"
           required>

    <br><br>

    <label>
        Cidade:
    </label>

    <br>

    <input type="text"
           name="cidade"
           value="<%= instituicao.getCidade() != null
                    ? instituicao.getCidade()
                    : "" %>"
           required>

    <br><br>

    <button type="submit">

        <%= ehEdicao
                ? "Salvar Alterações"
                : "Cadastrar" %>

    </button>

</form>

<br>

<a href="<%= ctx %>/instituicao/listar">
    Voltar
</a>

</body>
</html>