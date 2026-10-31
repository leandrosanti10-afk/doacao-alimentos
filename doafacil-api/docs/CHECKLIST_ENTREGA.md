# Checklist — Entrega da Pessoa 1 (`apiCompleta`)

## Antes da entrega

- [x] Duas aplicações Maven independentes no mesmo repositório.
- [x] API Tomcat 8081 com status público.
- [x] MySQL acessado pela API (DAO/JDBC/HikariCP).
- [x] CRUD REST de doadores funcional.
- [x] CRUD REST de instituições funcional.
- [x] CRUD REST de doações funcional.
- [x] Filtro JWT instalado e validado no ambiente local.
- [x] **69/69** testes automatizados aprovados em 31/10/2026.
- [x] Documentação e coleção Postman preparadas na Etapa 7.
- [ ] Compilar `mvn clean package` novamente após extrair esta etapa (se necessário).
- [ ] Importar Postman e confirmar status 200, bloqueio 401 e login 200 com seu ambiente.
- [ ] Realizar verificação adicional de **409** ao excluir entidade com doações relacionadas, sem excluir dados reais.
- [ ] Revisar `git diff --cached` para garantir ausência de credenciais e arquivos locais.
- [ ] Registrar commits finais e fazer push da branch `apiCompleta`.
- [ ] Abrir PR `apiCompleta` → `main` e revisar o diff.
- [ ] Após merge, informar a Pessoa 2 para iniciar `integracaoWeb` a partir da `main` atualizada.

## Verificações Git sugeridas (Git Bash)

```bash
cd /c/doacaoAlimentos
git switch apiCompleta
git status
git add doafacil-api/README.md doafacil-api/docs/ doafacil-api/postman/
git diff --cached --stat
git diff --cached --check
# Conferir manualmente o diff antes de confirmar:
git diff --cached
# Somente apos validar que nao ha segredos:
git commit -m "docs: finaliza API REST e adiciona colecao Postman"
git push origin apiCompleta
```

**Cuidado com `git grep` de segredos:** comandos que imprimem linhas inteiras podem revelar valores no terminal ou em capturas. Faça revisão de segredos sem divulgar valores. Exclua `.env`, configs temporárias locais do Tomcat, tokens e senhas reais do staging. Se alguma credencial foi comitada anteriormente, remova-a do histórico/rotacione conforme o cenário.

## Merge (apenas após revisão)

Opção recomendada: abrir um Pull Request no GitHub com **base `main` / compare `apiCompleta`**, revisar e fazer merge. Depois, localmente:

```bash
git switch main
git pull --ff-only origin main
```

A Pessoa 2 deverá criar `integracaoWeb` a partir dessa `main` atualizada.

## Critérios para a Pessoa 2

- API em `localhost:8081/doafacil-api/api`, status 200.
- Login JWT retorna `access_token` válido.
- Rotas CRUD exigem Bearer JWT.
- Modelos JSON e códigos HTTP documentados em `CONTRATO_API.md`.
- Web continua com Tomcat 8080 e ainda precisará trocar DAOs JDBC por clientes HTTP.

## Observações de segurança

A aplicação é acadêmica/local. A conta única de integração não substitui identidade/autorização individual em um sistema real; o acesso em HTTP sem HTTPS não deve ser exposto a redes externas. Segredos não devem ser incluídos nos arquivos de entrega.
