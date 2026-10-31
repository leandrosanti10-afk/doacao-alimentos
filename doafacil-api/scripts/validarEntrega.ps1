<#
 DoaFacil API - Etapa 7: execucao consolidada dos testes existentes.
 Usa os quatro scripts ja aprovados, sem alterar Java ou o banco fora dos
 cadastros temporarios que os scripts CRUD criam e apagam.

 Uso:
 powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\validarEntrega.ps1
 Os scripts solicitam credenciais de integracao; nao sao gravadas aqui.
#>
param([switch]$SomenteJwt)
$ErrorActionPreference = 'Stop'
$arquivos = @('testarJwt.ps1')
if (-not $SomenteJwt) {
    $arquivos += @('testarCrudDoadores.ps1','testarCrudInstituicoes.ps1','testarCrudDoacoes.ps1')
}
Write-Host "Validacao DoaFacil API - scripts: $($arquivos.Count)" -ForegroundColor Cyan
foreach ($nome in $arquivos) {
    $arquivo = Join-Path $PSScriptRoot $nome
    if (-not (Test-Path -LiteralPath $arquivo)) {
        Write-Host "ARQUIVO AUSENTE: $arquivo" -ForegroundColor Red
        exit 1
    }
    Write-Host "`n=== Executando $nome ===" -ForegroundColor Cyan
    & powershell.exe -NoProfile -ExecutionPolicy Bypass -File $arquivo
    if ($LASTEXITCODE -ne 0) {
        Write-Host "FALHOU: $nome (codigo $LASTEXITCODE)." -ForegroundColor Red
        exit 1
    }
}
Write-Host "`nTODOS OS SCRIPTS SELECIONADOS FORAM APROVADOS." -ForegroundColor Green
