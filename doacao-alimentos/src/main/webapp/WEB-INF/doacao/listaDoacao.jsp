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
<html lang="pt-br">

<head>

    <meta charset="UTF-8">

    <meta
        name="viewport"
        content="width=device-width, initial-scale=1.0"
    >

    <title>Doações — DoaFácil</title>

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
                    Lista de Doações
                </h1>

                <a
                    class="botao-principal"
                    href="<%= ctx %>/doacao/inserir"
                >
                    Nova Doação
                </a>

            </div>

            <div class="tabela-wrap">

                <table class="tabela-dados">

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

                        <td class="acoes">

                            <a
                                class="link-editar"
                                href="<%= ctx %>/doacao/alterar?id=<%= doacao.getId() %>"
                            >
                                Editar
                            </a>

                            <span class="separador">|</span>

                            <a
                                class="link-excluir"
                                href="<%= ctx %>/doacao/deletar?id=<%= doacao.getId() %>"
                                onclick="return confirm('Deseja excluir esta doação?')"
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

                        <td class="tabela-vazia" colspan="6">
                            Nenhuma doação cadastrada.
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
