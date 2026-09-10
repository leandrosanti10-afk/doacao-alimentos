<%@ page import="java.util.List" %>
<%@ page import="br.com.mvc.model.Doacao" %>
<%@ page import="br.com.mvc.model.Doador" %>
<%@ page import="br.com.mvc.model.Instituicao" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    Doacao doacao =
            (Doacao)
                    request.getAttribute("doacao");

    List<Doador> doadores =
            (List<Doador>)
                    request.getAttribute("doadores");

    List<Instituicao> instituicoes =
            (List<Instituicao>)
                    request.getAttribute("instituicoes");

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
                ? "Editar Doação"
                : "Nova Doação" %>
    </title>

</head>

<body>

<h1>
    <%= ehEdicao
            ? "Editar Doação"
            : "Nova Doação" %>
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
      action="<%= ctx %>/doacao/<%= ehEdicao ? "alterar" : "inserir" %>">

    <%
        if (ehEdicao) {
    %>

    <input type="hidden"
           name="id"
           value="<%= doacao.getId() %>">

    <%
        }
    %>

    <label>Doador:</label>

    <br>

    <select name="doadorId"
            required>

        <option value="">
            Selecione um doador
        </option>

        <%
            if (doadores != null) {

                for (Doador doador : doadores) {

                    boolean selecionado =
                            doacao != null
                            && doacao.getDoador() != null
                            && doacao.getDoador()
                                     .getId()
                                     .equals(
                                             doador.getId()
                                     );
        %>

        <option
                value="<%= doador.getId() %>"
                <%= selecionado
                        ? "selected"
                        : "" %>>

            <%= doador.getNome() %>

        </option>

        <%
                }
            }
        %>

    </select>

    <br><br>

    <label>Instituição:</label>

    <br>

    <select name="instituicaoId"
            required>

        <option value="">
            Selecione uma instituição
        </option>

        <%
            if (instituicoes != null) {

                for (Instituicao instituicao
                        : instituicoes) {

                    boolean selecionada =
                            doacao != null
                            && doacao.getInstituicao() != null
                            && doacao.getInstituicao()
                                     .getId()
                                     .equals(
                                             instituicao.getId()
                                     );
        %>

        <option
                value="<%= instituicao.getId() %>"
                <%= selecionada
                        ? "selected"
                        : "" %>>

            <%= instituicao.getNome() %>

        </option>

        <%
                }
            }
        %>

    </select>

    <br><br>

    <label>
        Itens da Doação:
    </label>

    <br>

    <textarea
            name="descricao"
            rows="8"
            cols="60"
            placeholder="Ex.: 10 kg de feijão, 5 pacotes de arroz, 4 L de óleo"
            required><%= doacao != null
                    && doacao.getDescricao() != null
                    ? doacao.getDescricao()
                    : "" %></textarea>

    <br><br>

    <small>
        Separe os itens por vírgula.
    </small>

    <br><br>

    <label>
        Data da Doação:
    </label>

    <br>

    <input type="date"
           name="dataDoacao"
           value="<%= doacao != null
                    && doacao.getDataDoacao() != null
                    ? doacao.getDataDoacao()
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

<a href="<%= ctx %>/doacao/listar">
    Voltar
</a>

</body>
</html>