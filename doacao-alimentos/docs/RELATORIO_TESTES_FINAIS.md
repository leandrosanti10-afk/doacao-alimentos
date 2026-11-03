# DoaFácil — Relatório de validação final (Pessoa 2)

## Evidência já compartilhada

| Conjunto | Evidência na execução do projeto | Situação |
|---|---|---|
| API — Doadores | 12 verificações automáticas aprovadas | Aprovado |
| API — Instituições | 19 verificações automáticas aprovadas | Aprovado |
| API — Doações | 24 verificações automáticas aprovadas | Aprovado |
| API — JWT | 14 verificações automáticas aprovadas | Aprovado |
| **Total dos testes da Pessoa 1** | **69/69** | **Aprovado** |
| Web — Etapa 2, cliente HTTP e JWT | `mvn clean package` e os quatro GETs retornaram sucesso após utilizar a conta de integração correta | Aprovado pelo usuário |
| Web — Etapas 3/4, DAOs HTTP | Scripts de smoke test distribuídos nas etapas anteriores | Conferir saída na validação final |
| Web — Etapa 5, sem JDBC | Script de verificação corrigido distribuído na etapa anterior | Conferir saída na validação final |
| Web — telas JSP | Validação de navegador prevista na Etapa 5 | Registrar resultados abaixo |

Não preencher automaticamente os campos de evidência em branco: anote a saída real após rodar a Etapa 6.

## Execução automatizada consolidada

Com o Tomcat Web em 8080 e o Tomcat API em 8081, no PowerShell:

```powershell
cd C:\doacaoAlimentos\doacao-alimentos
powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\scripts\validarIntegracaoFinal.ps1"
```

O script executa `verificarWebSemJdbc.ps1`, `testarClienteHttp.ps1`, `testarDoadorHttp.ps1` e `testarInstituicaoDoacaoHttp.ps1`, além de consultar as URLs principais. Os testes de DAO realizam operações temporárias de escrita e tentam limpar os registros ao terminar. Não interrompa a API durante esses testes.

Para testar sem gravar no banco:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\scripts\validarIntegracaoFinal.ps1" -SomenteLeitura
```

### Resultado da execução final

- Data da execução: __________________
- Branch e commit: __________________
- Verificador estático/JDBC: __________________
- Smoke test de autenticação e consultas: __________________
- Smoke test de DoadorDAO: __________________
- Smoke test de Instituição/Doação: __________________
- HTTP 200 para a API pública: __________________
- HTTP 401 para API protegida sem token: __________________
- HTTP 200 para as três listagens JSP: __________________
- HTTP 404 para `/doacao-alimentos/api/status`: __________________
- Resultado final: __________________

## Checklist manual de telas

Use somente cadastros temporários. Crie uma doação vinculando um doador e uma instituição temporários e, para excluir, remova **a doação primeiro**.

- [ ] Página inicial do Web abre na porta 8080.
- [ ] Doador — listar.
- [ ] Doador — cadastrar temporário.
- [ ] Doador — editar temporário e confirmar alteração.
- [ ] Instituição — listar.
- [ ] Instituição — cadastrar temporária.
- [ ] Instituição — editar temporária e confirmar alteração.
- [ ] Doação — listar.
- [ ] Doação — cadastrar vinculando registros temporários.
- [ ] Doação — editar e confirmar alteração.
- [ ] Tentativa de excluir doador/instituição vinculados é bloqueada.
- [ ] Doação temporária é excluída.
- [ ] Doador temporário é excluído.
- [ ] Instituição temporária é excluída.
- [ ] O Web não possui acesso direto ao MySQL.

## Segurança e entrega

- [ ] Senhas MySQL e JWT não aparecem no commit.
- [ ] Ambiente local preenchido do Postman não foi versionado.
- [ ] `git diff --cached --check` sem problemas.
- [ ] `target/` e WARs gerados não foram enviados ao Git.
- [ ] Pull Request revisado (`integracaoWeb` -> `main`).

## Observações da demonstração

Não confundir os 69 testes da API com os testes da Pessoa 2: eles são conjuntos distintos. A validação final do Web comprova a integração, mas não equivale a uma análise completa de segurança de produção.
