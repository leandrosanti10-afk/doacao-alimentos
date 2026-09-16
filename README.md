DoaFácil

Plataforma web para gerenciamento de doações de alimentos, desenvolvida em Java Web utilizando o padrão arquitetural MVC (Model-View-Controller).

O sistema permite cadastrar doadores, instituições e doações, mantendo o relacionamento entre essas entidades e oferecendo operações completas de CRUD.

Funcionalidades

Cadastro de doadores

Listagem de doadores

Edição de doadores

Exclusão de doadores

Cadastro de instituições

Listagem de instituições

Edição de instituições

Exclusão de instituições

Cadastro de doações

Listagem de doações

Edição de doações

Exclusão de doações

Validação de dados antes da gravação

Verificação de CPF/CNPJ, e-mail e telefone duplicados

Formatação automática de CPF/CNPJ

Formatação automática de telefone

Padronização de nomes e cidades

Bloqueio da exclusão de doadores ou instituições vinculados a doações

Ordenação das listagens por data e identificador

Tecnologias utilizadas

Java 23

Jakarta Servlet API 6.1

JSP

HTML

CSS

MySQL

MySQL Connector/J 9.4

Maven

Apache Tomcat 11

JUnit 4.13.2

Arquitetura

O projeto foi organizado utilizando o padrão MVC e camadas auxiliares para separar responsabilidades.

Requisição HTTP
      ↓
Controller
      ↓
Service
      ↓
DAO
      ↓
MySQL

Controller
      ↓
JSP / View

Model

Representa as entidades e os dados utilizados pelo sistema.

Principais classes:

Doador

Instituicao

Doacao

Controller

Recebe as requisições HTTP, identifica a rota solicitada, chama a camada de serviço e encaminha os dados para as páginas JSP.

Principais Controllers:

DoadorController

InstituicaoController

DoacaoController

Service

Responsável pelas regras de negócio e validações antes da comunicação com o DAO.

Entre as regras implementadas estão:

validação de campos obrigatórios;

verificação de dados duplicados;

formatação de CPF/CNPJ;

formatação de telefone;

padronização de nomes e cidades;

impedimento de exclusão de registros relacionados a doações.

DAO

Responsável pelo acesso ao banco de dados e execução dos comandos SQL.

Principais DAOs:

DoadorDAO

InstituicaoDAO

DoacaoDAO

View

As telas do sistema foram desenvolvidas em JSP e ficam principalmente em src/main/webapp/WEB-INF.

Estrutura do projeto

doacao-alimentos/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── br/com/mvc/
│       │       ├── config/
│       │       │   └── MysqlSingleton.java
│       │       ├── controller/
│       │       │   ├── DoadorController.java
│       │       │   ├── InstituicaoController.java
│       │       │   └── DoacaoController.java
│       │       ├── dao/
│       │       │   ├── DoadorDAO.java
│       │       │   ├── InstituicaoDAO.java
│       │       │   └── DoacaoDAO.java
│       │       ├── model/
│       │       │   ├── Doador.java
│       │       │   ├── Instituicao.java
│       │       │   └── Doacao.java
│       │       ├── service/
│       │       │   ├── DoadorService.java
│       │       │   ├── InstituicaoService.java
│       │       │   └── DoacaoService.java
│       │       └── util/
│       │           ├── FormatadorCpfCnpj.java
│       │           ├── FormatadorTelefone.java
│       │           └── FormatadorTexto.java
│       └── webapp/
│           ├── index.jsp
│           ├── assets/
│           │   ├── css/
│           │   ├── img/
│           └── WEB-INF/
│               ├── doador/
│               ├── instituicao/
│               ├── doacao/
│               └── web.xml
└── .gitignore

Pré-requisitos

Antes de executar o projeto, é necessário possuir:

JDK 23

Apache Maven

Apache Tomcat 11

MySQL Server

Git, caso o projeto seja clonado pelo GitHub

Para verificar Java e Maven:

java -version
mvn -version

Configuração do banco de dados

O projeto utiliza o banco:

doacao_alimentos

A conexão está configurada para:

Host: localhost
Porta: 3306
Usuário: root

A senha do MySQL não fica salva no código-fonte. Ela deve ser informada através da variável de ambiente:

DB_PASSWORD

Windows PowerShell

$env:DB_PASSWORD="SUA_SENHA"

Essa configuração vale para a sessão atual do terminal.

Para salvar a variável de forma permanente no Windows:

setx DB_PASSWORD "SUA_SENHA"

Após usar setx, abra um novo terminal antes de executar o projeto.

Compilação

Na pasta raiz do projeto, execute:

mvn clean package

O Maven irá gerar o arquivo:

target/doacao-alimentos.war

Execução no Tomcat

Faça o deploy de:

target/doacao-alimentos.war

em um servidor Apache Tomcat 11.

Após iniciar o servidor, acesse:

http://localhost:8080/doacao-alimentos/

Principais rotas

Doadores

/doador/listar
/doador/inserir
/doador/alterar
/doador/deletar

Instituições

/instituicao/listar
/instituicao/inserir
/instituicao/alterar
/instituicao/deletar

Doações

/doacao/listar
/doacao/inserir
/doacao/alterar
/doacao/deletar

Fluxo de uma operação

Exemplo de cadastro de um doador:

formDoador.jsp
      ↓
DoadorController
      ↓
DoadorService
      ↓
Validações e formatadores
      ↓
DoadorDAO
      ↓
MySQL
      ↓
listaDoador.jsp

Esse fluxo demonstra a separação de responsabilidades utilizada no projeto.

Regras de negócio implementadas

Doadores

nome obrigatório;

CPF/CNPJ obrigatório;

e-mail obrigatório;

telefone obrigatório;

cidade obrigatória;

CPF/CNPJ não pode ser duplicado;

e-mail não pode ser duplicado;

telefone não pode ser duplicado;

telefone é salvo em formato padronizado;

CPF/CNPJ é salvo em formato padronizado;

nome e cidade são salvos com capitalização padronizada;

um doador vinculado a uma doação não pode ser excluído.

Instituições

nome obrigatório;

endereço obrigatório;

telefone obrigatório;

e-mail obrigatório;

cidade obrigatória;

e-mail não pode ser duplicado;

telefone não pode ser duplicado;

telefone é salvo em formato padronizado;

nome e cidade são salvos com capitalização padronizada;

uma instituição vinculada a uma doação não pode ser excluída.

Doações

um doador deve ser selecionado;

uma instituição deve ser selecionada;

descrição dos itens é obrigatória;

data da doação é obrigatória.

Trabalho acadêmico

Projeto desenvolvido para a disciplina Aplicações para Internet.

O objetivo do trabalho é desenvolver uma aplicação web em Java utilizando MVC, DAO e Service, banco de dados MySQL e um CRUD completo, aplicando organização estrutural, modularidade e separação de responsabilidades.

Repositório

GitHub:

https://github.com/leandrosanti10-afk/doacao-alimentos
