# Handoff — Integração Web (8080) → API (8081)

## Situação dos projetos

```text
C:\doacaoAlimentos\
├── doacao-alimentos\     # Web MVC + JSP, Tomcat 11 porta 8080
└── doafacil-api\         # API REST + JDBC + MySQL, Tomcat 11 porta 8081
```

Ambos no **mesmo repositório**; a Pessoa 1 desenvolve a API na branch `apiCompleta`. Depois de revisão e merge em `main`, a Pessoa 2 desenvolve a integração na branch `integracaoWeb`.

## Arquitetura-alvo

```text
NAVEGADOR → Web MVC/JSP (8080)
                ↓ HTTP + JSON + JWT
              API REST (8081)
                ↓ Service → DAO/JDBC/HikariCP
              MySQL (3306)
```

A aplicação **Web não deve acessar o MySQL diretamente**. A API **não deve renderizar JSP**. Os dois Tomcats precisam permanecer independentes, assim como os dois projetos Maven.

## Tarefas da Pessoa 2

1. Preservar JSPs, Controllers MVC e Services do Web, adaptando apenas o necessário para a comunicação HTTP.
2. Remover o uso de `MysqlSingleton`, `BaseDAO` JDBC, conexões SQL e credenciais de banco **do projeto Web**, após migrar todos os pontos que dependem deles.
3. Criar um cliente de integração HTTP com `java.net.http.HttpClient` (Java 23). Preferir o cliente centralizado, em vez de espalhar chamadas pelos Controllers.
4. Criar um cliente de autenticação: fazer `POST /api/auth/login`, extrair `access_token` e incluir `Authorization: Bearer ...` nas chamadas REST.
5. Centralizar `baseUrl` e credenciais da conta de integração nas variáveis de ambiente do **Tomcat Web**, sem registrar segredos no repositório.
6. Atualizar os DAOs do Web (ou seus adaptadores) para usar GET/POST/PUT/DELETE e Gson com adaptadores de `LocalDate`/`LocalDateTime` compatíveis.
7. Adequar tratamentos: 400 → erro de formulário; 401 → renovar token/autenticar; 404 → registro não encontrado; 409 → bloqueio de exclusão por vínculo; 500/503 → erro temporário da API.
8. Validar o fluxo completo pelos formulários JSP, sem abrir conexões JDBC no Web.

## Recomendações de contrato

- URL-base do Web para a API: `http://localhost:8081/doafacil-api/api`.
- JSON de login: `{"usuario":"...","senha":"..."}`.
- Resposta de login: `access_token`, `token_type` e `expires_in` (segundos).
- Requisições de CRUD: `Authorization: Bearer <access_token>` e JSON em UTF-8.
- GET retorna listas ou objetos; POST geralmente HTTP 201; PUT HTTP 200; DELETE HTTP 204, **sem corpo**.
- `dataDoacao`: `yyyy-MM-dd`; `dataCadastro`: ISO 8601.
- Doação recebe objetos `doador` e `instituicao` com `id`, não campos soltos como `idDoador` e `idInstituicao`.

## Tratamento de token no Web

O token deve ficar **no servidor Web**, não em formulários JSP, campos ocultos ou JavaScript do navegador. Como é uma credencial compartilhada entre os sistemas, considere cache em memória no cliente HTTP e controle de expiração; sincronize a renovação quando várias requisições ocorrerem simultaneamente. Evite imprimir tokens e senhas em logs.

HTTP `401` deve disparar nova autenticação somente quando apropriado. Ao lidar com POST após falha de rede, não repita cegamente: a API não implementa chave de idempotência e o cadastro pode já ter sido efetivado.

## Testes de integração sugeridos

| Fluxo Web | Esperado |
|---|---|
| Abrir listagem de doadores | JSP exibe dados recebidos da API |
| Criar doador | Web envia POST autenticado; cadastro aparece na listagem |
| Editar doador | Web envia PUT pelo ID e atualiza a tela |
| Excluir doador sem vínculo | API retorna 204; Web remove da listagem |
| Excluir doador com doações | API retorna 409; Web exibe a justificativa |
| Criar/editar/excluir instituição | Igual ao CRUD correspondente |
| Registrar doação | IDs de doador e instituição existentes; gravação pela API |
| API parada | Web exibe erro amigável, sem stack trace |
| JWT expirado | Web obtém novo token e recupera a navegação |

## Aceite da Pessoa 2

- [ ] Acesso Web na porta 8080 e API na porta 8081 simultaneamente.
- [ ] Credenciais de banco somente na API.
- [ ] Nenhuma conexão JDBC/SQL ativa no Web.
- [ ] Todos os fluxos MVC/JSP funcionais através de HTTP + JSON + JWT.
- [ ] Erros HTTP tratados sem quebrar a navegação.
- [ ] Repositório sem senhas, JWTs ou arquivos locais com segredos.
- [ ] Commits em `integracaoWeb`; PR para `main` após testes.

**Observação:** este documento é o contrato de handoff, não significa que a integração Web já foi implementada.
