# Etapa 2 - Pessoa 2: smoke test real do cliente Java Web -> API REST.
# Executa somente GETs; nao cadastra, atualiza nem exclui registros.
$ErrorActionPreference = 'Stop'
$projeto = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$criouUsuario = [string]::IsNullOrWhiteSpace($env:DOAFACIL_API_USER)
$criouSenha = [string]::IsNullOrWhiteSpace($env:DOAFACIL_API_PASSWORD)

try {
    if ($criouUsuario) {
        $env:DOAFACIL_API_USER = Read-Host 'Usuario de integracao da API'
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
        throw 'Usuario e senha de integracao sao obrigatorios.'
    }

    if ([string]::IsNullOrWhiteSpace($env:DOAFACIL_API_URL)) {
        $env:DOAFACIL_API_URL = 'http://localhost:8081/doafacil-api/api'
    }

    Push-Location $projeto
    try {
        Write-Host "Testando cliente Java contra: $env:DOAFACIL_API_URL"
        Write-Host 'Compilando o projeto Web...'
        & mvn clean package
        if ($LASTEXITCODE -ne 0) {
            throw "Maven falhou (codigo $LASTEXITCODE)."
        }

        $gson = Join-Path $env:USERPROFILE '.m2\repository\com\google\code\gson\gson\2.14.0\gson-2.14.0.jar'
        if (-not (Test-Path $gson)) {
            throw "Gson nao encontrado no repositorio Maven local: $gson"
        }
        $cp = "$(Join-Path $projeto 'target\classes');$gson"
        & java -cp $cp br.com.mvc.api.ApiConexaoSmokeTest
        if ($LASTEXITCODE -ne 0) {
            throw "O teste Java retornou falha (codigo $LASTEXITCODE)."
        }
    } finally {
        Pop-Location
    }
} catch {
    Write-Host "FALHOU: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
} finally {
    # A senha informada no prompt fica somente neste processo PowerShell.
    if ($criouSenha) {
        Remove-Item Env:DOAFACIL_API_PASSWORD -ErrorAction SilentlyContinue
    }
    if ($criouUsuario) {
        Remove-Item Env:DOAFACIL_API_USER -ErrorAction SilentlyContinue
    }
}
