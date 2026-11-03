# Pessoa 2 — Etapa 3: DoadorDAO por HTTP + JWT

## Objetivo

Substituir somente o `DoadorDAO` da aplicacao Web pelo consumo da API REST.
A API (Tomcat 8081) continua a ser a unica responsavel por SQL/JDBC/MySQL.
O Web continua no Tomcat 8080 e utiliza as classes `ApiClient` / `ApiAuthService` da Etapa 2.

## Instalar

Extraia o ZIP na pasta raiz `C:\doacaoAlimentos\`, mesclando `doacao-alimentos/`.
A classe `src/main/java/br/com/mvc/dao/DoadorDAO.java` sera substituida.
Nao substitua arquivos Java de `doafacil-api`, Controllers ou JSPs.

### Arquivos

- `src/main/java/br/com/mvc/dao/DoadorDAO.java`: DAO HTTP, sem herdar `BaseDAO`.
- `src/main/java/br/com/mvc/api/DoadorDaoHttpSmokeTest.java`: teste de integracao, cria e exclui apenas doador temporario.
- `scripts/testarDoadorHttp.ps1`: executa Maven e teste Java, solicitando credenciais JWT.
- `docs/PESSOA2_ETAPA3_DOADOR_DAO_HTTP.md`: este documento.

## Mapeamento de metodos

| Metodo Web | Endpoint API |
|---|---|
| `listar()` | `GET /api/doadores` |
| `buscarPorId(id)` | `GET /api/doadores/{id}`; 404 vira `null` |
| `inserir(doador)` | `POST /api/doadores`; retorna `id` |
| `alterar(doador)` | `PUT /api/doadores/{id}` |
| `deletar(id)` | `DELETE /api/doadores/{id}`; 409 vira `IllegalStateException` |
| `possuiDoacoes(id)` | `GET /api/doacoes` e filtra `doador.id` |
| `existeCpfCnpj`, `existeEmail`, `existeTelefone` | `GET /api/doadores`, filtrando em memoria |

As tres verificacoes de duplicidade continuam presentes para manter o Service do Web intacto. Esse mecanismo e apropriado para o escopo didatico, mas carrega listagens completas em vez de consultar indices. A API valida os dados novamente e continua sendo a autoridade final. A exclusao com doacao vinculada tambem e barrada pela API.

## Testar

1. Inicie o Tomcat da API (porta 8081).
2. Verifique que `DOAFACIL_API_URL` aponta para `http://localhost:8081/doafacil-api/api` ou deixe sem definir para usar o padrao.
3. Execute em PowerShell:

```powershell
cd C:\doacaoAlimentos\doacao-alimentos
powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\scripts\testarDoadorHttp.ps1"
```

Use o usuario e senha da conta **JWT do Web**, nao do MySQL (ex.: usuario `doafacil_web`). O script solicita as credenciais se nao estiverem definidas no ambiente. Nao grave senhas no Git.

O teste usa `DoadorService` e `DoadorDAO`, percorre listagem, POST, GET individual, duplicidade, PUT, verificacao de doacoes, DELETE e GET apos exclusao. O registro temporario e removido no `finally` em caso de erro, quando possivel.

## Teste visual opcional

Com o Tomcat Web (porta 8080) publicado com o WAR atualizado, abra `http://localhost:8080/doacao-alimentos/doador/listar` no navegador e confira a listagem. Teste depois o formulario com um doador ficticio e o exclua no final. No Tomcat Web, configure as variaveis `DOAFACIL_API_USER`, `DOAFACIL_API_PASSWORD` e, se nao usar o padrao, `DOAFACIL_API_URL`. Elas precisam existir no **processo do Tomcat Web**; as variaveis do terminal PowerShell do teste nao sao automaticamente compartilhadas com o servidor iniciado pelo VS Code.

Os DAOs de Instituicoes e Doacoes, bem como `BaseDAO` e `MysqlSingleton` do Web, ainda permanecem ate as proximas etapas. Nao os exclua agora.

## Git

Na branch `integracaoWeb`:

```bash
git add doacao-alimentos/src/main/java/br/com/mvc/dao/DoadorDAO.java
git add doacao-alimentos/src/main/java/br/com/mvc/api/DoadorDaoHttpSmokeTest.java
git add doacao-alimentos/scripts/testarDoadorHttp.ps1
git add doacao-alimentos/docs/PESSOA2_ETAPA3_DOADOR_DAO_HTTP.md
git commit -m "feat: migra DoadorDAO do Web para API REST"
git push origin integracaoWeb
```

**Nao faca merge para main ainda**: faltam InstituicaoDAO, DoacaoDAO e remover o JDBC do Web.
