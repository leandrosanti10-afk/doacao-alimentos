<%@ page import="java.util.List" %>
<%@ page import="br.com.mvc.model.Doacao" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    List<Doacao> doacoes =
            (List<Doacao>)
                    request.getAttribute("doacoes");

    String ctx =
            request.getContextPath();
%>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>Doações</title>
</head>

<body>

<h1>Lista de Doações</h1>

<a href="<%= ctx %>/doacao/inserir">
    Nova Doação
</a>

<br><br>

<table border="1"
       cellpadding="8">

    <thead>

    <tr>
        <th>ID</th>
        <th>Doador</th>
        <th>Instituição</th>
        <th>Descrição</th>
        <th>Data</th>
        <th>Ações</th>
    </tr>

    </thead>

    <tbody>

    <%
        if (doacoes != null
                && !doacoes.isEmpty()) {

            for (Doacao doacao : doacoes) {
    %>

    <tr>

        <td>
            <%= doacao.getId() %>
        </td>

        <td>
            <%= doacao.getDoador().getNome() %>
        </td>

        <td>
            <%= doacao.getInstituicao().getNome() %>
        </td>

        <td>
            <%= doacao.getDescricao() != null
                    ? doacao.getDescricao()
                        .replace(", ", "<br>")
                    : "" %>
        </td>

        <td>
            <%= doacao.getDataDoacao() %>
        </td>

        <td>

            <a href="<%= ctx %>/doacao/alterar?id=<%= doacao.getId() %>">
                Editar
            </a>

            |

            <a href="<%= ctx %>/doacao/deletar?id=<%= doacao.getId() %>"
               onclick="return confirm('Deseja excluir esta doação?')">
                Excluir
            </a>

        </td>

    </tr>

    <%
            }

        } else {
    %>

    <tr>

        <td colspan="6">
            Nenhuma doação cadastrada.
        </td>

    </tr>

    <%
        }
    %>

    </tbody>

</table>

</body>
</html>