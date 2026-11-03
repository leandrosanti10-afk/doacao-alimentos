# DoaFácil — Plataforma de Doações de Alimentos

Projeto acadêmico de **Aplicações para Internet**, desenvolvido em Java 23, Maven, Jakarta Servlets, JSP, Gson, MySQL e Apache Tomcat 11.

O DoaFácil gerencia **doadores, instituições e doações**, com CRUD completo, validação de campos, prevenção de duplicidades e proteção de vínculos entre entidades.

## Arquitetura atual

Há **dois projetos Maven independentes no mesmo repositório Git**:

```text
Navegador
    |
    v
DoaFácil Web — Tomcat 8080
JSP -> Controllers MVC -> Services -> DAOs HTTP
    |
    | HTTP + JSON + Authorization: Bearer <JWT>
    v
DoaFácil API — Tomcat 8081
Filtro JWT -> Controllers REST -> Services -> DAOs JDBC
    |
    v
MySQL — localhost:3306 / doacao_alimentos
```

**Apenas a API REST acessa o MySQL.** O Web não contém driver JDBC, pool HikariCP ou endpoints REST próprios após a migração da Pessoa 2. A conta JWT é uma **conta de integração entre sistemas**, e não um login individual para os usuários das JSPs.

## Organização do repositório

```text
doacaoAlimentos/
├── README.md
├── doacao-alimentos/          # Web MVC/JSP — porta 8080
│   ├── pom.xml
│   ├── src/
│   ├── scripts/
│   └── docs/
└── doafacil-api/             # REST API + JWT + MySQL — porta 8081
    ├── pom.xml
    ├── src/
    ├── scripts/
    ├── docs/
    └── postman/
```

As pastas `target/` são saídas de compilação e não devem ser versionadas.

## Requisitos

- JDK 23 (`java -version`)
- Maven (`mvn -version`)
- Duas instâncias separadas de Tomcat 11: Web em **8080** e API em **8081**
- MySQL com o banco `doacao_alimentos`, acessível à **API**
- Git e, opcionalmente, Postman

## Configuração por servidor

**Tomcat API (8081):** variáveis de ambiente `DB_URL`, `DB_USER`, `DB_PASSWORD`, `API_AUTH_USER`, `API_AUTH_PASSWORD`, `JWT_SECRET_BASE64` e `JWT_TTL_MINUTES`.

**Tomcat Web (8080):** variáveis de ambiente `DOAFACIL_API_USER`, `DOAFACIL_API_PASSWORD` e, opcionalmente, `DOAFACIL_API_URL` (padrão: `http://localhost:8081/doafacil-api/api`).

As credenciais de integração configuradas no Web devem corresponder a `API_AUTH_USER` e `API_AUTH_PASSWORD` configuradas na API. **Não use `DB_USER` como usuário de login JWT.** Não inclua senhas, chaves ou tokens no Git ou em capturas de tela.

As variáveis podem ser cadastradas na configuração do servidor do Community Server Connector ou no ambiente do processo que inicia o Tomcat. Após mudá-las, reinicie o processo do servidor. Se configuradas como variáveis de usuário do Windows, reinicie também o VS Code para que os novos processos as herdem.

## Compilação

A partir da raiz do repositório:

```powershell
cd .\doafacil-api
mvn clean package

cd ..\doacao-alimentos
mvn clean package
```

O Maven gera `doafacil-api/target/doafacil-api.war` e `doacao-alimentos/target/doacao-alimentos.war`. Publique cada WAR no Tomcat correspondente. Inicie a API antes do Web.

## URLs locais

| Destino | URL |
|---|---|
| Web — início | `http://localhost:8080/doacao-alimentos/` |
| Web — Doadores | `http://localhost:8080/doacao-alimentos/doador/listar` |
| Web — Instituições | `http://localhost:8080/doacao-alimentos/instituicao/listar` |
| Web — Doações | `http://localhost:8080/doacao-alimentos/doacao/listar` |
| API — status público | `http://localhost:8081/doafacil-api/api/status` |
| API — login | `POST http://localhost:8081/doafacil-api/api/auth/login` |
| API — recursos autenticados | `/api/doadores`, `/api/instituicoes`, `/api/doacoes` |

Os Controllers MVC do Web atendem às rotas `/doador/*`, `/instituicao/*` e `/doacao/*`. O CRUD REST pertence apenas à API.

## Testes e entrega

No PowerShell, com ambas as instâncias iniciadas:

```powershell
cd .\doacao-alimentos
powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\scripts\validarIntegracaoFinal.ps1"
```

Esse comando reutiliza os scripts dos passos anteriores, verifica a ausência de JDBC no Web, testa a comunicação JWT e faz GETs das três telas. Os testes de DAO criam e tentam remover **registros temporários**. Para conferir apenas as consultas, use `-SomenteLeitura`; nesse modo os testes de escrita são omitidos.

A operação completa dos formulários JSP (cadastrar, editar, excluir) também deve ser validada manualmente com dados temporários. Os resultados devem ser anotados em [`doacao-alimentos/docs/RELATORIO_TESTES_FINAIS.md`](doacao-alimentos/docs/RELATORIO_TESTES_FINAIS.md).

## Documentação

- [Arquitetura e responsabilidades](doacao-alimentos/docs/ARQUITETURA_WEB_API.md)
- [Relatório e checklist de validação](doacao-alimentos/docs/RELATORIO_TESTES_FINAIS.md)
- [Entrega da Pessoa 2 e Git](doacao-alimentos/docs/PESSOA2_ETAPA6_ENTREGA_FINAL.md)
- [Roteiro de apresentação](doacao-alimentos/docs/ROTEIRO_APRESENTACAO.md)
- [Contrato dos endpoints da API](doafacil-api/docs/CONTRATO_API.md)

## Observações

Este é um projeto acadêmico. A autenticação JWT atual usa credenciais de integração entre os servidores; para um sistema publicado, seriam necessários controles adicionais como HTTPS, gerenciamento seguro de segredos, usuários individuais conforme o requisito do produto e testes de segurança mais amplos.
