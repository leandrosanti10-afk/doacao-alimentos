# DoaFácil — Roteiro de apresentação da arquitetura final

## 1. Objetivo do sistema

Gerenciar doadores, instituições e doações de alimentos, com operações de CRUD, relacionamentos e validação de dados em uma aplicação Web Java.

## 2. Evolução da arquitetura

**Antes da divisão:** o Web MVC/JSP executava operações JDBC diretamente no banco.

**Após o trabalho em dupla:** o Web apresenta a interface e consome a API REST via HTTP, JSON e JWT; a API gerencia autenticação dos endpoints, regras e acesso ao MySQL.

## 3. Divisão de trabalho

- **Pessoa 1:** API Maven independente, Tomcat 8081, Controllers REST, Service, JDBC/MySQL, JWT e testes.
- **Pessoa 2:** cliente `HttpClient` e autenticação JWT no Web, DAOs HTTP das três entidades, remoção do JDBC e dos REST legados, testes do Web e documentação.

Os dois projetos permanecem no **mesmo repositório Git**, mas usam dois WARs e dois servidores Tomcat distintos.

## 4. Demonstração recomendada (sem expor credenciais)

1. Mostrar as duas pastas Maven e as instâncias de Tomcat nas portas 8080/8081.
2. Demonstrar `GET /api/status` na API (200) e `GET /api/doadores` sem token (401).
3. Apresentar o login JWT com credenciais não visíveis e uma consulta autenticada (200).
4. Abrir as três listagens JSP no Web (8080).
5. Demonstrar cadastro e edição com **dados temporários**, se as telas estiverem validadas.
6. Abrir `DoadorDAO`, `InstituicaoDAO` e `DoacaoDAO` do Web e mostrar o uso de `ApiClient`, sem JDBC.
7. Mostrar que o MySQL Connector e o HikariCP aparecem apenas no WAR da API, não no Web.
8. Exibir o resultado do `validarIntegracaoFinal.ps1` e os commits da branch `integracaoWeb`.

## 5. Respostas rápidas ao professor

**Por que dois Tomcats?** Porque o front MVC/JSP e a API são aplicações independentes, com contextos e responsabilidades diferentes.

**O Web usa MySQL?** Não diretamente. O Web chama a API autenticada; somente a API usa JDBC para persistir.

**Onde o JWT é validado?** No filtro da API. O Web obtém o token pela rota de login e o envia no cabeçalho `Authorization: Bearer`.

**O JWT é um login individual?** Não. Nesta implementação acadêmica é a autenticação de uma conta de integração entre o servidor Web e a API.

**Como são tratados os erros?** HTTP 400 para entrada inválida, 401 para falta de autenticação, 404 para registro ausente, 409 para alguns conflitos por vínculo, e 200/201/204 para sucessos conforme a operação.

**Como garantiram que a divisão não quebrou o Web?** Por meio de compilação, testes automatizados dos DAOs HTTP/JWT e validação das páginas JSP.
