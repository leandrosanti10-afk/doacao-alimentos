# Relatório de testes — API DoaFácil

**Data dos resultados fornecidos pelo executor:** 31/10/2026  
**Branch:** `apiCompleta`  
**Ambiente:** Tomcat 11, porta 8081, API `/doafacil-api`, MySQL local.  
**Estado:** resultados relatados no terminal; compilação e execução não foram refeitas neste pacote de documentação.

| Script | Testes aprovados | Falhas |
|---|---:|---:|
| `testarJwt.ps1` | 14 | 0 |
| `testarCrudDoadores.ps1` | 12 | 0 |
| `testarCrudInstituicoes.ps1` | 19 | 0 |
| `testarCrudDoacoes.ps1` | 24 | 0 |
| **Total** | **69** | **0** |

### Cobertura observada

- Status público e login JWT válido/inválido.
- Acesso negado a todos os CRUDs sem token e com token malformado/adulterado.
- Acesso autorizado aos três CRUDs com JWT válido.
- GET lista e detalhe; POST; PUT; DELETE dos três cadastros.
- IDs inválidos e inexistentes; JSON malformado; duplicidade de telefone/e-mail em instituições; referências ausentes em doações; datas inválidas em doações; métodos em rotas inadequadas.
- Persistência de atualização confirmada por GET posterior; GET após DELETE retorna 404.

### Limpeza de dados temporários registrada

- Doador temporário ID 19 removido.
- Instituição temporária ID 10 removida.
- Doação temporária ID 8 removida.

### Limitações da evidência

- Não há teste automatizado específico do HTTP 409 ao tentar excluir **doador ou instituição com doação vinculada**. O comportamento está implementado, mas permanece como verificação adicional recomendada.
- O teste JWT não demonstra por si só HTTPS, políticas de senha, prevenção de brute force ou controle de permissões individuais.
- Após qualquer mudança de código, configuração, esquema SQL ou WAR publicado, os testes devem ser executados novamente.
- Não há auditoria de segurança independente nem testes de carga documentados nesta etapa.

### Reproduzir

```powershell
cd C:\doacaoAlimentos\doafacil-api
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\testarJwt.ps1
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\testarCrudDoadores.ps1
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\testarCrudInstituicoes.ps1
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\testarCrudDoacoes.ps1
```

Os scripts solicitam credenciais de integração; não as registre em logs, screenshots ou no Git.
