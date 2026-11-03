# DoaFácil — Pessoa 2, Etapa 2: cliente HTTP do sistema Web

## Objetivo

Criar a infraestrutura de comunicação **servidor Web → API REST**, preservando o sistema Web em `doacao-alimentos/` e a API em `doafacil-api/`, no mesmo repositório Git. A aplicação Web roda no **Tomcat 8080**, a API no **Tomcat 8081**; só a API acessa MySQL.

Esta etapa **não migra ainda os DAOs**, não remove JDBC nem altera JSPs/Controllers/Services. A troca dos DAOs virá na etapa de integração seguinte, após a infraestrutura ser testada.

## Arquivos adicionados

- `src/main/java/br/com/mvc/api/ApiClient.java`: chamadas GET/POST/PUT/DELETE HTTP, JSON com Gson, timeouts, JWT automático, um novo login após HTTP 401 e tratamento de erros.
- `src/main/java/br/com/mvc/api/ApiAuthService.java`: login `POST /api/auth/login`, cache de JWT em memória e renovação sincronizada.
- `src/main/java/br/com/mvc/api/ApiException.java`: erros da comunicação com código HTTP (`getStatusHttp()`), ou 0 para falhas sem resposta HTTP.
- `src/main/java/br/com/mvc/api/ApiConexaoSmokeTest.java`: aplicativo de diagnóstico Java somente leitura; não cria endpoint no Tomcat.
- `scripts/testarClienteHttp.ps1`: compila e executa o diagnóstico.

**Não é necessário alterar `pom.xml`:** Gson 2.14.0 e Java 23 já estão configurados no projeto Web.

## Instalação

1. Trabalhe na branch `integracaoWeb` após o merge da `apiCompleta` na `main`.
2. Extraia o ZIP em `C:\doacaoAlimentos\`, mesclando a pasta `doacao-alimentos`.
3. Mantenha a API rodando na porta 8081 e o MySQL disponível somente para a API.
4. Execute no PowerShell:

```powershell
cd C:\doacaoAlimentos\doacao-alimentos
powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\scripts\testarClienteHttp.ps1"
```

O script solicita usuário e senha de integração de forma interativa se não estiverem definidos; não os grava em arquivos. Usa `mvn clean package` e executa a classe Java com o Gson instalado no Maven local. O Tomcat Web 8080 **não precisa estar ligado para este primeiro teste**; o teste utiliza o mesmo cliente Java que os DAOs usarão.

Resultados esperados:

```text
APROVADO: GET /api/status (HTTP 200)
APROVADO: GET /api/doadores com JWT (HTTP 200, registros: ...)
APROVADO: GET /api/instituicoes com JWT (HTTP 200, registros: ...)
APROVADO: GET /api/doacoes com JWT (HTTP 200, registros: ...)
TODOS OS TESTES DO CLIENTE HTTP PASSARAM.
```

## Variáveis para o Tomcat Web (8080)

Essas variáveis devem estar disponíveis **ao processo Java do Tomcat Web** (não apenas ao PowerShell atual):

| Variável | Exemplo | Observação |
|---|---|---|
| `DOAFACIL_API_URL` | `http://localhost:8081/doafacil-api/api` | Opcional; é o padrão local |
| `DOAFACIL_API_USER` | `doafacil_web` | Usuário de integração da API |
| `DOAFACIL_API_PASSWORD` | *(senha local)* | Senha de integração; não versionar |

Configure-as em `mapProperty.launch.env` do **Tomcat Web 8080** no Community Server Connector (e confirme que o processo recebe as variáveis). Alternativamente, use variáveis de usuário do Windows e **feche/reabra todas as instâncias do VS Code antes de reiniciar o Tomcat**, como foi necessário na configuração JWT da API. Não é necessário copiar `JWT_SECRET_BASE64` para o Web: a chave de assinatura permanece **exclusivamente na API**. Nunca envie JWT, senha, arquivos locais preenchidos ou logs sensíveis ao GitHub.

## Exemplo de uso futuro nos DAOs

```java
ApiClient cliente = new ApiClient();
Doador[] dados = cliente.get("doadores", Doador[].class);
Doador cadastrado = cliente.post("doadores", novoDoador, Doador.class);
Doador atualizado = cliente.put("doadores/" + id, doadorAlterado, Doador.class);
cliente.delete("doadores/" + id);
```

O Gson configura `LocalDate` e `LocalDateTime` para compatibilidade com o JSON da API. O serviço de login não envia credenciais ou token para o navegador. A renovação após `401` é feita uma vez; chamadas com erro de rede **não são reenviadas automaticamente** para não duplicar gravações.

## Tratamento de erro na etapa de migração dos DAOs

```java
try {
    Doador[] resultado = new ApiClient().get("doadores", Doador[].class);
} catch (ApiException ex) {
    if (ex.getStatusHttp() == 404) {
        // registro nao encontrado
    } else if (ex.getStatusHttp() == 409) {
        // conflito por vinculo, mensagem ao usuario
    } else {
        // mensagem amigavel, sem expor stack trace na JSP
    }
}
```

## Proximas etapas

1. Integrar os DAOs de Doadores (substituir SQL por `ApiClient`).
2. Integrar Instituições e Doações.
3. Remover JDBC, MySQL, HikariCP, REST Controllers duplicados e credenciais de banco do projeto **Web** quando não houver mais referências a essas classes.
4. Verificar os formulários JSP no Tomcat 8080 em conjunto com a API 8081.

Faça commit apenas dos arquivos novos desta etapa, após o teste:

```bash
git add doacao-alimentos/src/main/java/br/com/mvc/api/
git add doacao-alimentos/scripts/testarClienteHttp.ps1
git add doacao-alimentos/docs/PESSOA2_ETAPA2_CLIENTE_HTTP.md
git commit -m "feat: adiciona cliente HTTP e autenticacao JWT no Web"
git push origin integracaoWeb
```
