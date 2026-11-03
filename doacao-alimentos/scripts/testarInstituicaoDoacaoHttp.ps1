# Pessoa 2 / Etapa 4: teste real Web -> API com JWT para Instituicoes e Doacoes.
# Cria apenas uma instituicao e uma doacao temporarias, com limpeza no finally Java.
$ErrorActionPreference = 'Stop'
$projeto = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$criouUsuario = [string]::IsNullOrWhiteSpace($env:DOAFACIL_API_USER)
$criouSenha = [string]::IsNullOrWhiteSpace($env:DOAFACIL_API_PASSWORD)
$criouUrl = [string]::IsNullOrWhiteSpace($env:DOAFACIL_API_URL)

try {
    if ($criouUsuario) {
        $env:DOAFACIL_API_USER = Read-Host 'Usuario de integracao da API (doafacil_web; nao e o usuario MySQL)'
    }
    if ($criouSenha) {
        $segura = Read-Host 'Senha de integracao da API (nao sera exibida)' -AsSecureString
        $ponteiro = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($segura)
        try {
            $env:DOAFACIL_API_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ponteiro)
        } finally {
            [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ponteiro)
        }
    }
    if ([string]::IsNullOrWhiteSpace($env:DOAFACIL_API_USER) -or
        [string]::IsNullOrWhiteSpace($env:DOAFACIL_API_PASSWORD)) {
        throw 'Usuario e senha JWT sao obrigatorios.'
    }
    if ($criouUrl) {
        $env:DOAFACIL_API_URL = 'http://localhost:8081/doafacil-api/api'
    }

    Push-Location $projeto
    try {
        Write-Host "Testando InstituicaoDAO/DoacaoDAO por HTTP + JWT: $env:DOAFACIL_API_URL"
        Write-Host 'Compilando o projeto Web com Maven...'
        & mvn clean package
        if ($LASTEXITCODE -ne 0) {
            throw "Maven falhou (codigo $LASTEXITCODE)."
        }
        $gson = Join-Path $env:USERPROFILE '.m2\repository\com\google\code\gson\gson\2.14.0\gson-2.14.0.jar'
        if (-not (Test-Path $gson)) {
            throw "Gson nao encontrado: $gson"
        }
        $cp = "$(Join-Path $projeto 'target\classes');$gson"
        & java -cp $cp br.com.mvc.api.InstituicaoDoacaoHttpSmokeTest
        if ($LASTEXITCODE -ne 0) {
            throw "Teste de Instituicoes/Doacoes falhou (codigo $LASTEXITCODE)."
        }
    } finally {
        Pop-Location
    }
} catch {
    Write-Host "FALHOU: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
} finally {
    if ($criouSenha) { Remove-Item Env:DOAFACIL_API_PASSWORD -ErrorAction SilentlyContinue }
    if ($criouUsuario) { Remove-Item Env:DOAFACIL_API_USER -ErrorAction SilentlyContinue }
    if ($criouUrl) { Remove-Item Env:DOAFACIL_API_URL -ErrorAction SilentlyContinue }
}
