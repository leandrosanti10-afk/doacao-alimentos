# DoaFacil — Pessoa 2, Etapa 4 — InstituicaoDAO e DoacaoDAO via API REST

## Objetivo

Substituir a persistencia JDBC dos dois DAOs do projeto Web (Tomcat 8080) por chamadas HTTP/JSON com JWT ao projeto `doafacil-api` (Tomcat 8081), usando `ApiClient` e `ApiAuthService` da Etapa 2.

**Nao modifica** telas JSP, Controllers, Services, models, `pom.xml` nem o projeto da API. A remocao definitiva de `BaseDAO`, `MysqlSingleton` e dependencia JDBC do Web sera feita somente na etapa seguinte.

## Arquivos

- `src/main/java/br/com/mvc/dao/InstituicaoDAO.java` (substituir)
- `src/main/java/br/com/mvc/dao/DoacaoDAO.java` (substituir)
- `src/main/java/br/com/mvc/api/InstituicaoDoacaoHttpSmokeTest.java` (novo)
- `scripts/testarInstituicaoDoacaoHttp.ps1` (novo)
- `docs/PESSOA2_ETAPA4_INSTITUICAO_DOACAO_HTTP.md` (novo)

Extraia o ZIP na pasta `C:\doacaoAlimentos\` e mescle o diretorio `doacao-alimentos`.

## Metodos preservados

**InstituicaoDAO:** `void inserir(Instituicao)`, `List<Instituicao> listar()`, `Instituicao buscarPorId(Long)`, `void alterar(Instituicao)`, `void deletar(Long)`, `boolean possuiDoacoes(Long)`, `boolean existeEmail(String, Long)`, `boolean existeTelefone(String, Long)`.

**DoacaoDAO:** `void inserir(Doacao)`, `List<Doacao> listar()`, `Doacao buscarPorId(Long)`, `void alterar(Doacao)`, `void deletar(Long)`.

O ID retornado no HTTP 201 e preenchido no objeto passado ao metodo `inserir`, sem mudar a assinatura `void`. Uma consulta inexistente (404) volta `null`, como esperado pelos Services/Controllers Web. Erros 400 da API sao repassados como `IllegalArgumentException` para exibir mensagem nos formularios; 409 de exclusao de Instituicao vira `IllegalStateException`.

`possuiDoacoes` e as checagens de duplicidade fazem consultas HTTP de listagem por nao existir endpoint especifico de contagem na API atual. Isso e adequado ao trabalho academico, mas nao e ideal para conjuntos de dados extensos; a API mantem a validacao definitiva do DELETE.

## Variaveis de ambiente no TOMCAT WEB (8080)

- `DOAFACIL_API_URL` (opcional): `http://localhost:8081/doafacil-api/api`
- `DOAFACIL_API_USER`: usuario da integracao JWT (`doafacil_web` na instalacao atual)
- `DOAFACIL_API_PASSWORD`: senha correspondente a `API_AUTH_PASSWORD` no Tomcat da API

Configure no `mapProperty.launch.env` do **servidor Web**, nao no servidor API. Nunca salve valores reais em Git. Reinicie o servidor Web apos alterar as variaveis.

## Teste automatizado real

Mantenha a API no ar e execute no PowerShell:

```powershell
cd C:\doacaoAlimentos\doacao-alimentos
powershell.exe -NoProfile -ExecutionPolicy Bypass -File '.\scripts\testarInstituicaoDoacaoHttp.ps1'
```

O script pede usuario/senha quando nao estiverem no ambiente, executa `mvn clean package` e inicia a classe Java de teste. O teste:

1. consulta listas por HTTP com JWT;
2. escolhe um doador **preexistente como referencia somente de leitura** (se nao existir, interrompe sem modificar nada);
3. cadastra, consulta e atualiza uma **instituicao temporaria**;
4. cadastra, consulta e atualiza uma **doacao temporaria** vinculada ao doador e a instituicao temporaria;
5. verifica o bloqueio de exclusao de instituicao com doacao vinculada;
6. exclui a doacao e depois a instituicao, confirmando que nao aparecem mais no GET;
7. tenta limpar os registros temporarios no `finally` mesmo em caso de falha.

Confirme os IDs criados na saida. Se a limpeza falhar, remova **apenas esses IDs temporarios** apos verificar os relacionamentos. Nenhum dado do doador preexistente e alterado.

## Teste das telas no navegador

Depois do teste automatizado, compile/republique o WAR do Web 8080 e confira:

- `http://localhost:8080/doacao-alimentos/instituicao/listar`
- `http://localhost:8080/doacao-alimentos/doacao/listar`
- `http://localhost:8080/doacao-alimentos/doador/listar`

Teste cadastro/edicao/exclusao de registros **ficticios** usando os formularios. Mantenha API 8081 e Web 8080 ativos. A etapa seguinte remove JDBC do Web e revisa a segregacao das responsabilidades.

## Git

```bash
cd /c/doacaoAlimentos
git switch integracaoWeb
git add doacao-alimentos/src/main/java/br/com/mvc/dao/InstituicaoDAO.java
git add doacao-alimentos/src/main/java/br/com/mvc/dao/DoacaoDAO.java
git add doacao-alimentos/src/main/java/br/com/mvc/api/InstituicaoDoacaoHttpSmokeTest.java
git add doacao-alimentos/scripts/testarInstituicaoDoacaoHttp.ps1
git add doacao-alimentos/docs/PESSOA2_ETAPA4_INSTITUICAO_DOACAO_HTTP.md
git diff --cached --stat
git commit -m "feat: migra instituicoes e doacoes do Web para API REST"
git push origin integracaoWeb
```

Nao realize merge para `main` ate finalizar a integracao Web.
