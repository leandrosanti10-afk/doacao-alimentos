<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="br.com.mvc.model.Doador" %>

<%
    List<Doador> doadores =
            (List<Doador>) request.getAttribute("doadores");

    String ctx = request.getContextPath();
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Doadores</title>
</head>

<body>

    <h1>Lista de Doadores</h1>

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

    <a href="<%= ctx %>/doador/inserir">
        Novo doador
    </a>

    <br><br>

    <table border="1">

        <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Email</th>
            <th>Ações</th>
        </tr>

        <%
            if (doadores != null) {

                for (Doador d : doadores) {
        %>

        <tr>

            <td><%= d.getId() %></td>

            <td><%= d.getNome() %></td>

            <td><%= d.getEmail() %></td>

            <td>

                <a href="<%= ctx %>/doador/alterar?id=<%= d.getId() %>">
                    Editar
                </a>

                |

                <a href="<%= ctx %>/doador/deletar?id=<%= d.getId() %>">
                    Excluir
                </a>

            </td>

        </tr>

        <%
                }
            }
        %>

    </table>

</body>
</html>