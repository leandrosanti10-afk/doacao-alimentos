# Pessoa 2 — Etapa 5: Web sem JDBC + validação das telas

## Objetivo

A aplicação **Web** (`doacao-alimentos`, Tomcat 8080) mantém apenas Controllers MVC, Services, DAOs HTTP, modelos, páginas JSP e o `ApiClient`/JWT. **Somente a API REST** (`doafacil-api`, Tomcat 8081) mantém acesso ao MySQL. Não altera lógica, modelos ou interface nesta etapa.

## O que será removido do Web

Arquivos Java legados, somente de `doacao-alimentos/src/main/java/br/com/mvc/`:

- `config/MysqlSingleton.java`
- `config/MysqlPoolShutdownListener.java`
- `dao/BaseDAO.java`
- `controller/api/ApiServlet.java`
- `controller/api/StatusApiController.java`
- `controller/api/DoadorApiController.java`
- `util/ApiJson.java`

Também remove somente as dependências `com.zaxxer:HikariCP` e `com.mysql:mysql-connector-j` do `doacao-alimentos/pom.xml`. Não mexe em `doafacil-api`, JSPs, Controllers MVC, Services ou DAOs HTTP. O arquivo histórico `database/banco.sql` continua como material de referência, **não é executado pelo Web**.

## Pré-requisitos

1. Branch `integracaoWeb` ativa e alterações anteriores salvas no Git.
2. Etapas 2, 3 e 4 da Pessoa 2 instaladas (os três DAOs devem usar `ApiClient`).
3. JDK 23 e Maven instalados, como nas etapas anteriores.
4. O Tomcat Web 8080 deverá continuar com `DOAFACIL_API_USER` e `DOAFACIL_API_PASSWORD`, e opcionalmente `DOAFACIL_API_URL` apontando para `http://localhost:8081/doafacil-api/api`.

## Instalação

1. Pare **somente o Tomcat Web** no painel SERVERS. Não pare a API.
2. Extraia o ZIP em `C:\doacaoAlimentos\` mesclando `doacao-alimentos/scripts` e `doacao-alimentos/docs`.
3. Execute no PowerShell:

```powershell
cd C:\doacaoAlimentos\doacao-alimentos
powershell.exe -NoProfile -ExecutionPolicy Bypass -File '.\scripts\aplicarEtapa5.ps1'
powershell.exe -NoProfile -ExecutionPolicy Bypass -File '.\scripts\verificarWebSemJdbc.ps1'
```

O primeiro script **faz backup no diretório TEMP** antes de editar `pom.xml` e apagar as classes legadas. Se algum DAO ainda usar `BaseDAO` ou não usar `ApiClient`, ele interrompe a remoção. O segundo script confere o código, executa Maven e inspeciona o WAR, rejeitando bibliotecas MySQL/Hikari e classes REST antigas.

Não é necessário nem recomendado remover as classes JDBC equivalentes dentro do projeto **`doafacil-api`**: elas continuam responsáveis pela persistência.

## Testes no navegador (Web 8080)

Publique o `target/doacao-alimentos.war` atualizado no **Tomcat Web 8080** e inicie-o. Deixe **Tomcat API 8081** ligado.

Confirme:

1. `http://localhost:8080/doacao-alimentos/` — a página inicial abre normalmente.
2. `http://localhost:8080/doacao-alimentos/doador/listar` — listagem, cadastro, edição e exclusão de um doador temporário.
3. `http://localhost:8080/doacao-alimentos/instituicao/listar` — listagem, cadastro, edição e exclusão de uma instituição temporária.
4. `http://localhost:8080/doacao-alimentos/doacao/listar` — listagem, cadastro, edição e exclusão de uma doação temporária vinculada a um doador e instituição existentes.
5. Se criar registros vinculados durante o teste, **exclua a doação primeiro**, depois os cadastros temporários.

**Verificações de separação de responsabilidades:**

- `GET http://localhost:8081/doafacil-api/api/status` deve retornar 200.
- `GET http://localhost:8080/doacao-alimentos/api/status` deve retornar 404 (nenhuma API REST publicada pelo Web).
- Ao desligar a API 8081, as listagens que dependem dela devem acusar indisponibilidade, e não buscar dados diretamente no MySQL. Reative a API em seguida. É apenas um teste de arquitetura; não faça durante POST/PUT/DELETE.
- Os registros dos três módulos só são lidos/gravadados no MySQL por meio da API 8081.

## Registro no Git

O `git status` deverá mostrar **exclusões** dos sete Java legados, **modificação** no `pom.xml` e **adições** dos dois scripts e desta documentação.

```bash
cd /c/doacaoAlimentos
git switch integracaoWeb
git status --short
git add -u doacao-alimentos/src/main/java/ doacao-alimentos/pom.xml
git add doacao-alimentos/scripts/aplicarEtapa5.ps1
git add doacao-alimentos/scripts/verificarWebSemJdbc.ps1
git add doacao-alimentos/docs/PESSOA2_ETAPA5_WEB_SEM_JDBC.md
git diff --cached --stat
git diff --cached --check
git commit -m "refactor: remove JDBC e endpoints REST legados do Web"
git push origin integracaoWeb
```

Não faça merge ainda: a próxima etapa é documentação, regressão e entrega final da Pessoa 2.
