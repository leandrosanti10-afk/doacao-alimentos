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
<html lang="pt-br">

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0"
    >

    <title>Doadores — DoaFácil</title>

    <link rel="stylesheet" href="<%= ctx %>/assets/css/style.css?v=3">

</head>

<body>

    <header class="topo">

        <div class="container topo-conteudo">

            <div class="logo">

                <img
                    src="<%= ctx %>/assets/img/DoaFacil.png"
                    alt="Logo DoaFácil"
                    class="logo-img"
                >

                <div class="logo-texto">

                    <h2>
                        DoaFácil
                    </h2>

                    <span>
                        Plataforma de Doações de Alimentos
                    </span>

                </div>

            </div>

            <nav class="menu">

                <a href="<%= ctx %>/">
                    Início
                </a>

                <a href="<%= ctx %>/doador/listar">
                    Doadores
                </a>

                <a href="<%= ctx %>/instituicao/listar">
                    Instituições
                </a>

                <a href="<%= ctx %>/doacao/listar">
                    Doações
                </a>

            </nav>

        </div>

    </header>

    <main>

        <section class="container">

            <div class="pagina-cabecalho">

                <h1>
                    Lista de Doadores
                </h1>

                <a
                    class="botao-principal"
                    href="<%= ctx %>/doador/inserir"
                >
                    Novo Doador
                </a>

            </div>

            <%
                if (erro != null) {
            %>

            <p style="color: #b3261e; margin-bottom: 20px;">
                <%= erro %>
            </p>

            <%
                }
            %>

            <div class="tabela-wrap">

                <table class="tabela-dados">

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

                                <td class="acoes">

                                    <a
                                        class="link-editar"
                                        href="<%= ctx %>/doador/alterar?id=<%= doador.getId() %>"
                                    >
                                        Editar
                                    </a>

                                    <span class="separador">|</span>

                                    <a
                                        class="link-excluir"
                                        href="<%= ctx %>/doador/deletar?id=<%= doador.getId() %>"
                                        onclick="return confirm('Deseja excluir este doador?')"
                                    >
                                        Excluir
                                    </a>

                                </td>

                            </tr>

                        <%
                                }

                            } else {
                        %>

                            <tr>

                                <td class="tabela-vazia" colspan="8">
                                    Nenhum doador cadastrado.
                                </td>

                            </tr>

                        <%
                            }
                        %>

                    </tbody>

                </table>

            </div>

        </section>

    </main>

    <footer>

        <div class="container">

            DoaFácil — Plataforma de Doações de Alimentos

        </div>

    </footer>

    <script src="<%= ctx %>/assets/js/script.js?v=2"></script>

</body>

</html>
