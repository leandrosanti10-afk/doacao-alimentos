<%@ page import="java.util.List" %>
<%@ page import="br.com.mvc.model.Instituicao" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    List<Instituicao> instituicoes =
            (List<Instituicao>)
                    request.getAttribute("instituicoes");

    String ctx =
            request.getContextPath();

    String erro =
            (String)
                    request.getAttribute("erro");
%>

<!DOCTYPE html>
<html lang="pt-br">

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0"
    >

    <title>Instituições — DoaFácil</title>

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
                    Lista de Instituições
                </h1>

                <a
                    class="botao-principal"
                    href="<%= ctx %>/instituicao/inserir"
                >
                    Nova Instituição
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
                        <th>Endereço</th>
                        <th>Telefone</th>
                        <th>Email</th>
                        <th>Cidade</th>
                        <th>Data de Cadastro</th>
                        <th>Ações</th>
                    </tr>

                    </thead>

                    <tbody>

                    <%
                        if (instituicoes != null
                                && !instituicoes.isEmpty()) {

                            for (Instituicao instituicao
                                    : instituicoes) {
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
                            <%= instituicao.getTelefone() %>
                        </td>

                        <td>
                            <%= instituicao.getEmail() %>
                        </td>

                        <td>
                            <%= instituicao.getCidade() %>
                        </td>

                        <td>
                            <%= instituicao.getDataCadastro() %>
                        </td>

                        <td class="acoes">

                            <a
                                class="link-editar"
                                href="<%= ctx %>/instituicao/alterar?id=<%= instituicao.getId() %>"
                            >
                                Editar
                            </a>

                            <span class="separador">|</span>

                            <a
                                class="link-excluir"
                                href="<%= ctx %>/instituicao/deletar?id=<%= instituicao.getId() %>"
                                onclick="return confirm('Deseja excluir esta instituição?')"
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
                            Nenhuma instituição cadastrada.
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
