<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String ctx = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="pt-br">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>DoaFácil</title>

    <link rel="stylesheet"
          href="<%= ctx %>/assets/css/style.css">
</head>

<body>

    <header class="topo">

        <div class="container topo-conteudo">

            <div class="logo">
                <h2>DoaFácil</h2>
                <span>Plataforma de Doações de Alimentos</span>
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

        <section class="hero">

            <div class="container">
                
                <h1>
                    Conectando quem pode doar
                    com quem precisa receber.
                </h1>

                <p>
                    Uma plataforma simples para conectar doadores
                    a instituições e facilitar a doação de alimentos.
                </p>

                <a class="botao-principal"
                   href="<%= ctx %>/doacao/inserir">

                    Nova Doação

                </a>

            </div>

        </section>

        <section class="container">

            <h2 class="titulo-secao">
                Gerenciamento
            </h2>

            <div class="cards">

                <a class="card"
                   href="<%= ctx %>/doador/listar">

                    <div class="card-icone">
                        👤
                    </div>

                    <h3>
                        Doadores
                    </h3>

                    <p>
                        Cadastre e gerencie pessoas
                        e empresas doadoras.
                    </p>

                    <span>
                        Gerenciar doadores →
                    </span>

                </a>

                <a class="card"
                   href="<%= ctx %>/instituicao/listar">

                    <div class="card-icone">
                        🏠
                    </div>

                    <h3>
                        Instituições
                    </h3>

                    <p>
                        Gerencie instituições que
                        recebem as doações.
                    </p>

                    <span>
                        Gerenciar instituições →
                    </span>

                </a>

                <a class="card"
                   href="<%= ctx %>/doacao/listar">

                    <div class="card-icone">
                        📦
                    </div>

                    <h3>
                        Doações
                    </h3>

                    <p>
                        Registre e acompanhe
                        as doações realizadas.
                    </p>

                    <span>
                        Gerenciar doações →
                    </span>

                </a>

            </div>

        </section>

    </main>

    <footer>
        <div class="container">
            DoaFácil — Plataforma de Doações de Alimentos
        </div>
    </footer>

    <script src="<%= ctx %>/assets/js/script.js"></script>

</body>

</html>