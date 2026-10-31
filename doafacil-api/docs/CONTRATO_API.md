# Contrato HTTP/JSON — DoaFácil API

**Base URL local:** `http://localhost:8081/doafacil-api/api`  
**Codificação:** UTF-8 · **Corpo:** `application/json` · **Versão examinada:** Etapa 6 (31/10/2026)

Este documento foi escrito a partir dos Controllers, Models e Services do pacote da Etapa 6. Não descreve rotas futuras ou funcionalidades do Web ainda não integradas.

## 1. Autenticação

`GET /status` e `POST /auth/login` são públicos. Qualquer outra rota sob `/api/*` passa pelo `JwtAuthenticationFilter` e exige `Authorization: Bearer <JWT>`.

### POST `/auth/login`

```json
{"usuario":"doafacil_web","senha":"SENHA_CONFIGURADA_LOCALMENTE"}
```

**200 OK** (exemplo estrutural, não é token real):

```json
{"access_token":"<JWT_ASSINADO>","token_type":"Bearer","expires_in":1800}
```

O token é HS256 e contém emissor, sujeito e expiração. `expires_in` é fornecido em segundos (por padrão, 1800). Quando expirado, o cliente deve autenticar novamente. O login da etapa atual é **uma conta de integração**, não um login individual de doadores.

**401**: credenciais incorretas ou token ausente/inválido/expirado.  
**400**: corpo JSON inválido ou campos de credenciais ausentes.  
**503**: configuração de autenticação indisponível no processo do servidor.

### GET `/status`

**200 OK** — exemplo:

```json
{"status":"ok","aplicacao":"DoaFacil API","mensagem":"API independente funcionando"}
```

## 2. Doadores

| Método | Rota | Corpo | Sucesso |
|---|---|---|---|
| GET | `/doadores` | — | 200 (array) |
| GET | `/doadores/{id}` | — | 200 (objeto) |
| POST | `/doadores` | Doador sem `id` | 201 (objeto com `id`) |
| PUT | `/doadores/{id}` | Dados editáveis completos | 200 (objeto atualizado) |
| DELETE | `/doadores/{id}` | — | 204 (sem corpo) |

**POST ou PUT** (exemplo com dados fictícios; CPF/CNPJ não é exemplo de documento verdadeiro):

```json
{
  "nome": "Doador Temporario",
  "cpfCnpj": "77123456789",
  "email": "doador.temporario@example.com",
  "telefone": "34987654321",
  "cidade": "Uberaba"
}
```

**GET** inclui, conforme valor persistido, `id`, `nome`, `cpfCnpj`, `email`, `telefone`, `cidade` e `dataCadastro`, que é uma data/hora ISO 8601 (ex.: `2026-10-31T09:30:00`). O MySQL define `id` e `dataCadastro`; o POST ignora esses campos recebidos no JSON. O PUT respeita **o ID da URL**, não o ID do corpo.

O `DoadorService` exige campos não vazios e verifica duplicidade de CPF/CNPJ, e-mail e telefone; formata nome, cidade, CPF/CNPJ e telefone. **Não** é correto afirmar que o serviço valida matematicamente o dígito verificador de CPF/CNPJ: essa checagem não consta no Service analisado.

DELETE retorna **409 Conflict** se o doador possuir doações cadastradas; 404 para ID inexistente.

## 3. Instituições

| Método | Rota | Corpo | Sucesso |
|---|---|---|---|
| GET | `/instituicoes` | — | 200 (array) |
| GET | `/instituicoes/{id}` | — | 200 (objeto) |
| POST | `/instituicoes` | Instituição sem `id` | 201 (objeto com `id`) |
| PUT | `/instituicoes/{id}` | Dados editáveis completos | 200 (objeto atualizado) |
| DELETE | `/instituicoes/{id}` | — | 204 (sem corpo) |

**POST ou PUT:**

```json
{
  "nome": "Instituicao Temporaria",
  "endereco": "Rua de Teste, 100",
  "telefone": "34991234567",
  "email": "instituicao.temporaria@example.com",
  "cidade": "Uberaba"
}
```

O GET pode conter `id`, `nome`, `endereco`, `telefone`, `email`, `cidade` e `dataCadastro` em formato ISO 8601. Os campos `id` e `dataCadastro` são controlados pelo banco. Campos obrigatórios: nome, endereço, telefone, e-mail e cidade. E-mail e telefone são verificados quanto à duplicidade.

DELETE retorna **409 Conflict** se há doações vinculadas; 404 para ID inexistente.

## 4. Doações

| Método | Rota | Corpo | Sucesso |
|---|---|---|---|
| GET | `/doacoes` | — | 200 (array) |
| GET | `/doacoes/{id}` | — | 200 (objeto) |
| POST | `/doacoes` | Doação sem `id` | 201 (objeto persistido) |
| PUT | `/doacoes/{id}` | Dados editáveis completos | 200 (objeto persistido) |
| DELETE | `/doacoes/{id}` | — | 204 (sem corpo) |

**POST ou PUT** — substitua os IDs pelos valores reais de cadastros existentes:

```json
{
  "doador": {"id": 1},
  "instituicao": {"id": 1},
  "descricao": "Doacao temporaria de cestas basicas",
  "dataDoacao": "2026-10-31"
}
```

**GET** ou retorno persistido do POST/PUT incluem o `id`, os objetos relacionados `doador` e `instituicao` (com seus dados conforme os Models), `descricao` e `dataDoacao` no formato **`yyyy-MM-dd`**. Os IDs em `doador.id` e `instituicao.id` precisam existir no MySQL.

Campos obrigatórios: doador com ID positivo, instituição com ID positivo, descrição não vazia, data válida. ID relacionado inexistente retorna **404**. O ID de `/doacoes/{id}` no PUT prevalece sobre qualquer ID no corpo.

## 5. Códigos HTTP e erros

| Código | Uso no projeto |
|---|---|
| 200 | GET, PUT e login bem-sucedidos |
| 201 | POST de criação, com cabeçalho `Location` e objeto retornado |
| 204 | DELETE bem-sucedido; corpo vazio |
| 400 | ID inválido, campo obrigatório ausente, JSON inválido, data inválida ou duplicidade tratada |
| 401 | JWT ausente, inválido, expirado, adulterado, ou credenciais de login incorretas |
| 404 | Registro/rota inexistente ou doador/instituição de referência ausente |
| 405 | Método em rota incompatível (por exemplo, PUT sem ID) |
| 409 | Tentativa de excluir doador/instituição com doações vinculadas |
| 500 | Falha inesperada interna/DAO |
| 503 | Configuração de autenticação ausente/inválida |

Erros gerados pelos Controllers usam o padrão:

```json
{"erro":"Mensagem explicativa"}
```

**Detalhes práticos:** (1) Nem toda chamada 404 ou 405 necessariamente vem do Controller — o Tomcat também pode devolver respostas padrão. (2) Os serviços doadores/instituições não implementam validação completa de formato para todos os campos; evite anunciar isso como implementado. (3) O bloqueio de 409 consta nos Services/Controllers, mas o roteiro automatizado de 69 testes relatado não cobre essa hipótese específica.

## 6. Regras de integração

- Enviar `Content-Type: application/json; charset=UTF-8` quando houver corpo.
- Para rotas protegidas, enviar `Authorization: Bearer <token>`.
- Para criar, **não** depender de IDs enviados no corpo; o servidor define o ID persistido.
- Para atualizar, usar sempre o ID da URL; o PUT recebe campos editáveis completos, não é PATCH.
- Usar `yyyy-MM-dd` em `dataDoacao` e aceitar `dataCadastro` em ISO 8601 nas respostas.
- Nunca assumir que 204 retorna JSON; não tentar analisá-lo como JSON.
- Ao obter 401 por token expirado, repetir login e reenviar **no máximo uma vez** a operação originalmente tentada, evitando duplicação indevida de POSTs sem estratégia de idempotência.
