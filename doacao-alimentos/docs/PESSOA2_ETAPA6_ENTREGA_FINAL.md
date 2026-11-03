# Pessoa 2 — Etapa 6: entrega final da integração Web + API

## Objetivo

Concluir e registrar a arquitetura em que o sistema `doacao-alimentos` (8080) se comunica exclusivamente pela API `doafacil-api` (8081) para acesso a dados. **Esta etapa não altera Java, JSP ou os projetos Maven.**

## Conteúdo do pacote

```text
README.md                                  # README da raiz atualizado

doacao-alimentos/
├── docs/
│   ├── ARQUITETURA_WEB_API.md
│   ├── RELATORIO_TESTES_FINAIS.md
│   ├── ROTEIRO_APRESENTACAO.md
│   └── PESSOA2_ETAPA6_ENTREGA_FINAL.md
└── scripts/
    └── validarIntegracaoFinal.ps1
```

O README na raiz será **substituído**. Revise-o antes da extração se você o personalizou após o último ZIP enviado.

## Instalação

1. Confira a branch com `git branch --show-current`: deve ser `integracaoWeb`.
2. Extraia o ZIP em `C:\doacaoAlimentos\`, mesclando as pastas.
3. Inicie Tomcat API (8081), MySQL e Tomcat Web (8080).
4. Confirme as variáveis `DOAFACIL_API_USER` e `DOAFACIL_API_PASSWORD` no processo Web. Não reutilize o usuário `DB_USER` como conta JWT.

## Validação automatizada

No PowerShell:

```powershell
cd C:\doacaoAlimentos\doacao-alimentos
powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\scripts\validarIntegracaoFinal.ps1"
```

O script executa os testes existentes e faz consultas HTTP às telas. Ele pede a senha de integração **uma única vez** se a variável não estiver configurada. Usa uma senha somente no processo PowerShell, não a grava em arquivos. A API precisa permanecer ligada durante a execução.

Para validação **sem operações de cadastro/alteração/exclusão**, use:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\scripts\validarIntegracaoFinal.ps1" -SomenteLeitura
```

A verificação da Etapa 5 (`verificarWebSemJdbc.ps1`) deve ser a **versão corrigida**. Se a versão antiga apresentar erro de parser, substitua-a pela correção que você já aplicou, sem repetir a limpeza.

O navegador deve ser usado também para testar os formulários JSP com dados temporários. Registre os resultados no relatório e evite alterar registros reais.

## Revisão do Git e segurança

No Git Bash, na raiz do repositório:

```bash
cd /c/doacaoAlimentos
git switch integracaoWeb
git status --short

git add README.md
git add doacao-alimentos/docs/ARQUITETURA_WEB_API.md
git add doacao-alimentos/docs/RELATORIO_TESTES_FINAIS.md
git add doacao-alimentos/docs/ROTEIRO_APRESENTACAO.md
git add doacao-alimentos/docs/PESSOA2_ETAPA6_ENTREGA_FINAL.md
git add doacao-alimentos/scripts/validarIntegracaoFinal.ps1

git diff --cached --stat
git diff --cached --check
git diff --cached
```

Antes de commitar, confira se **nenhum segredo real**, saída `target/`, WAR ou arquivo de ambiente local preenchido aparece no staging. Caso existam alterações de fim de linha não relacionadas, evite `git add .`.

Em seguida:

```bash
git commit -m "docs: finaliza integracao Web API REST DoaFacil"
git push origin integracaoWeb
```

Se já houver mudanças pendentes das etapas anteriores, confira se foram commitadas na mesma branch. Não faça merge sem revisar os arquivos alterados.

## Pull Request

1. No GitHub, crie um Pull Request com **base `main`** e **compare `integracaoWeb`**.
2. Título sugerido: `Integração Web MVC com API REST e JWT`.
3. Revise as diferenças de DAOs, dependências, remoções de classes legadas e documentação.
4. Após aprovação e ausência de conflitos, efetue `Merge pull request`.
5. No computador:

```bash
cd /c/doacaoAlimentos
git switch main
git pull --ff-only origin main
```

A `main` terá **os dois projetos Maven**; não mova nem exclua as pastas. A branch `integracaoWeb` pode continuar no histórico até a entrega ser aceita.

## Critério de encerramento

- API 8081 e Web 8080 respondem nas rotas corretas.
- JWT é usado no acesso do Web à API.
- Os três DAOs do Web usam HTTP e não acessam MySQL diretamente.
- Testes automatizados e telas estão validados conforme o relatório.
- Repositório não expõe credenciais e PR foi revisado.

**Nota:** a entrega é acadêmica e não representa homologação para produção.
