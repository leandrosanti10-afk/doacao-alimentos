# DoaFácil API REST

API independente do projeto **DoaFácil** (trabalho acadêmico). Implementada em Java 23, Jakarta Servlet 6.1, Tomcat 11, Maven, MySQL, Gson, HikariCP e autenticação JWT (JJWT).

> **Escopo:** a aplicação `doafacil-api` opera na porta **8081** e é a única responsável pelo acesso JDBC ao banco `doacao_alimentos`. A aplicação `doacao-alimentos` (Web/JSP) opera separadamente na porta **8080**; a integração HTTP + JSON + JWT entre as duas aplicações é uma etapa posterior. Ambas pertencem ao mesmo repositório Git.

## Requisitos

- JDK **23** (`java -version`)
- Maven com Java 23 (`mvn -version`)
- MySQL ativo em `localhost:3306`, com o banco `doacao_alimentos` já criado e populado
- Tomcat 11 separado para a API, configurado na porta **8081** e context path `/doafacil-api`
- VS Code + Community Server Connector (alternativa: implantação manual do WAR)
- PowerShell 5.1+ para scripts de validação; Postman opcional

## Configurar localmente

A API lê as configurações pelo ambiente do **processo Tomcat**, não por um arquivo Java. Informe estes valores em `mapProperty.launch.env` do servidor **Tomcat 11.x API** ou nas variáveis de ambiente herdadas pelo processo Java:

| Nome | Exemplo / finalidade |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/doacao_alimentos` |
| `DB_USER` | Usuário MySQL com acesso restrito ao banco |
| `DB_PASSWORD` | Senha do usuário MySQL |
| `API_AUTH_USER` | `doafacil_web` (conta de integração) |
| `API_AUTH_PASSWORD` | Senha exclusiva da conta de integração |
| `JWT_SECRET_BASE64` | Chave aleatória de **32 bytes ou mais**, codificada em Base64 |
| `JWT_TTL_MINUTES` | `30` (padrão 30, aceito de 1 a 60) |

Para gerar uma nova chave (PowerShell):

```powershell
$bytes = New-Object byte[] 32
$rng = [Security.Cryptography.RandomNumberGenerator]::Create()
try { $rng.GetBytes($bytes) } finally { $rng.Dispose() }
[Convert]::ToBase64String($bytes)
```

**Nunca versione credenciais reais, tokens JWT ou um arquivo preenchido com segredos.** Reinicie o Tomcat depois de alterar variáveis; salvar o JSON sem reiniciar não altera o ambiente do processo já iniciado. Em algumas instalações, é necessário configurar as variáveis de usuário do Windows e fechar/reabrir todas as janelas do VS Code para o processo novo herdá-las.

## Compilar e executar

No PowerShell:

```powershell
cd C:\doacaoAlimentos\doafacil-api
mvn clean package
jar tf .\target\doafacil-api.war | Select-String 'Controller|JwtAuthenticationFilter'
```

Resultado esperado: `BUILD SUCCESS` e WAR em `target/doafacil-api.war`. Publique/atualize esse WAR no **Tomcat 11.x API**, usando a publicação completa do deployment existente, se necessário. **Não** registre o mesmo WAR duas vezes nem execute dois Tomcats na mesma porta.

### Testes rápidos

```powershell
curl.exe -i http://localhost:8081/doafacil-api/api/status
curl.exe -i http://localhost:8081/doafacil-api/api/doadores
```

Esperado: `/api/status` retorna **200**; `/api/doadores` **sem** JWT retorna **401**. Para obter um token, faça `POST /api/auth/login` com `usuario` e `senha` configurados. Envie-o nos próximos pedidos em `Authorization: Bearer <token>`.

## Endpoints

A URL-base é `http://localhost:8081/doafacil-api/api`.

| Método | Rota | JWT | Sucesso |
|---|---|---|---|
| GET | `/status` | Não | 200 |
| POST | `/auth/login` | Não | 200 |
| GET | `/doadores` | Sim | 200 |
| GET | `/doadores/{id}` | Sim | 200 |
| POST | `/doadores` | Sim | 201 |
| PUT | `/doadores/{id}` | Sim | 200 |
| DELETE | `/doadores/{id}` | Sim | 204 |
| GET | `/instituicoes` | Sim | 200 |
| GET | `/instituicoes/{id}` | Sim | 200 |
| POST | `/instituicoes` | Sim | 201 |
| PUT | `/instituicoes/{id}` | Sim | 200 |
| DELETE | `/instituicoes/{id}` | Sim | 204 |
| GET | `/doacoes` | Sim | 200 |
| GET | `/doacoes/{id}` | Sim | 200 |
| POST | `/doacoes` | Sim | 201 |
| PUT | `/doacoes/{id}` | Sim | 200 |
| DELETE | `/doacoes/{id}` | Sim | 204 |

A documentação completa do formato JSON, métodos e erros está em [docs/CONTRATO_API.md](docs/CONTRATO_API.md).

## Testes automatizados

Executar na pasta `doafacil-api`, com Tomcat 8081 e MySQL operantes:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\testarJwt.ps1
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\testarCrudDoadores.ps1
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\testarCrudInstituicoes.ps1
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\testarCrudDoacoes.ps1
```

**Evidência já observada em 31/10/2026:** 14 testes JWT, 12 de doadores, 19 de instituições e 24 de doações, total **69/69 aprovados**, com exclusão dos cadastros temporários. Consulte [docs/RELATORIO_VALIDACAO.md](docs/RELATORIO_VALIDACAO.md). Esse resultado não substitui os testes após mudanças futuras.

## Postman e entrega

- Importar `postman/DoaFacil_API.postman_collection.json`.
- Importar `postman/DoaFacil_Local.postman_environment.json`.
- Selecionar o ambiente e preencher `apiUsuario` e `apiSenha` **somente na sua instalação local**; o login salva `jwt` nesse ambiente. Não exporte/publicize o ambiente preenchido.
- Executar primeiro a pasta `01 - Diagnostico e JWT` e depois os cadastros **somente de teste**.
- Não clicar em **Run entire collection**: as pastas CRUD contêm ações de criação, alteração e exclusão, que requerem IDs temporários válidos.

[Guia de integração do Web](docs/GUIA_INTEGRACAO_WEB.md) · [Checklist de entrega](docs/CHECKLIST_ENTREGA.md)

## Limites atuais

Projeto local para faculdade, **não pronto para exposição pública**. O login usa uma única conta técnica compartilhada, não contas individuais; não há papéis/permissões, refresh/revogação de token, proteção contra força bruta ou TLS na configuração local. Não exponha a porta 8081 diretamente à internet. Doadores e instituições com doações vinculadas possuem regra de bloqueio de exclusão (409), que merece um teste de ponta a ponta específico antes da entrega definitiva.
