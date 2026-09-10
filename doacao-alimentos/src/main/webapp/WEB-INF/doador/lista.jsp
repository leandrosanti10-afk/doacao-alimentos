<%@ page import="java.util.List" %>
<%@ page import="br.com.mvc.model.Doador" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    List<Doador> doadores =
            (List<Doador>) request.getAttribute("doadores");

    String ctx =
            request.getContextPath();

    String erro =
            (String) request.getAttribute("erro");
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
        if (erro != null) {
    %>

        <p style="color: red;">
            <%= erro %>
        </p>

    <%
        }
    %>

    <a href="<%= ctx %>/doador/inserir">
        Novo Doador
    </a>

    <br><br>

    <table border="1" cellpadding="8">

        <thead>

            <tr>
                <th>ID</th>
                <th>Nome</th>
                <th>CPF/CNPJ</th>
                <th>Email</th>
                <th>Telefone</th>
                <th>Cidade</th>
                <th>Data de Cadastro</th>
                <th>Ações</th>
            </tr>

        </thead>

        <tbody>

            <%
                if (doadores != null && !doadores.isEmpty()) {

                    for (Doador doador : doadores) {
            %>

                <tr>

                    <td>
                        <%= doador.getId() %>
                    </td>

                    <td>
                        <%= doador.getNome() %>
                    </td>

                    <td>
                        <%= doador.getCpfCnpj() %>
                    </td>

                    <td>
                        <%= doador.getEmail() %>
                    </td>

                    <td>
                        <%= doador.getTelefone() %>
                    </td>

                    <td>
                        <%= doador.getCidade() %>
                    </td>

                    <td>
                        <%= doador.getDataCadastro() %>
                    </td>

                    <td>

                        <a href="<%= ctx %>/doador/alterar?id=<%= doador.getId() %>">
                            Editar
                        </a>

                        |

                        <a href="<%= ctx %>/doador/deletar?id=<%= doador.getId() %>"
                           onclick="return confirm('Deseja excluir este doador?')">
                            Excluir
                        </a>

                    </td>

                </tr>

            <%
                    }

                } else {
            %>

                <tr>

                    <td colspan="8">
                        Nenhum doador cadastrado.
                    </td>

                </tr>

            <%
                }
            %>

        </tbody>

    </table>

</body>

</html>