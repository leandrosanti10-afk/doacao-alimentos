<%@ page import="java.util.List" %>
<%@ page import="br.com.mvc.model.Instituicao" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    List<Instituicao> instituicoes =
            (List<Instituicao>) request.getAttribute("instituicoes");

    String ctx = request.getContextPath();
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Instituições</title>
</head>

<body>

    <h1>Lista de Instituições</h1>

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

    <a href="<%= ctx %>/instituicao/inserir">
        Nova Instituição
    </a>

    <br><br>

    <table border="1" cellpadding="8">

        <thead>
            <tr>
                <th>ID</th>
                <th>Nome</th>
                <th>Endereço</th>
                <th>Ações</th>
            </tr>
        </thead>

        <tbody>

        <%
            if (instituicoes != null && !instituicoes.isEmpty()) {

                for (Instituicao instituicao : instituicoes) {
        %>

            <tr>

                <td>
                    <%= instituicao.getId() %>
                </td>

                <td>
                    <%= instituicao.getNome() %>
                </td>

                <td>
                    <%= instituicao.getEndereco() %>
                </td>

                <td>

                    <a href="<%= ctx %>/instituicao/alterar?id=<%= instituicao.getId() %>">
                        Editar
                    </a>

                    |

                    <a href="<%= ctx %>/instituicao/deletar?id=<%= instituicao.getId() %>"
                       onclick="return confirm('Deseja excluir esta instituição?')">
                        Excluir
                    </a>

                </td>

            </tr>

        <%
                }

            } else {
        %>

            <tr>
                <td colspan="4">
                    Nenhuma instituição cadastrada.
                </td>
            </tr>

        <%
            }
        %>

        </tbody>

    </table>

</body>
</html>