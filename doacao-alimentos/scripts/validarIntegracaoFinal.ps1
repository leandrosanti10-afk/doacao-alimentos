# DoaFacil - Pessoa 2 / Etapa 6: validacao consolidada da integracao Web + API.
# Requer PowerShell 5.1+, Maven, JDK 23 e os dois Tomcats iniciados (8080/8081).
# Por padrao, executa testes que criam e tentam excluir dados TEMPORARIOS.
# Use -SomenteLeitura para nao executar os smoke tests de escrita.
param(
    [switch]$SomenteLeitura
)

$ErrorActionPreference = 'Stop'
$web = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$apiBase = 'http://localhost:8081/doafacil-api/api'
$webBase = 'http://localhost:8080/doacao-alimentos'
$criouUsuario = [string]::IsNullOrWhiteSpace($env:DOAFACIL_API_USER)
$criouSenha = [string]::IsNullOrWhiteSpace($env:DOAFACIL_API_PASSWORD)
$criouUrl = [string]::IsNullOrWhiteSpace($env:DOAFACIL_API_URL)

function Executar-Script([string]$nome) {
    $caminho = Join-Path $PSScriptRoot $nome
    if (-not (Test-Path -LiteralPath $caminho -PathType Leaf)) {
        throw "Script necessario nao encontrado: $caminho"
    }
    Write-Host "`n=== Executando $nome ===" -ForegroundColor Cyan
    & powershell.exe -NoProfile -ExecutionPolicy Bypass -File $caminho
    if ($LASTEXITCODE -ne 0) {
        throw "Falha ao executar $nome (codigo $LASTEXITCODE)."
    }
    Write-Host "APROVADO: $nome" -ForegroundColor Green
}

function Obter-StatusHttp([string]$url) {
    try {
        $resposta = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 20 -ErrorAction Stop
        return [int]$resposta.StatusCode
    } catch [System.Net.WebException] {
        if ($null -ne $_.Exception.Response) {
            return [int]$_.Exception.Response.StatusCode
        }
        throw "Nao foi possivel acessar ${url}: $($_.Exception.Message)"
    }
}

function Conferir-Http([string]$descricao, [string]$url, [int]$esperado) {
    $recebido = Obter-StatusHttp $url
    if ($recebido -ne $esperado) {
        throw "${descricao}: esperado HTTP $esperado, recebido HTTP $recebido. URL: $url"
    }
    Write-Host "APROVADO: $descricao (HTTP $recebido)" -ForegroundColor Green
}

try {
    Write-Host 'DoaFacil - Validacao final da Pessoa 2' -ForegroundColor Cyan
    Write-Host "Projeto Web: $web"
    if ($SomenteLeitura) {
        Write-Host 'Modo somente leitura: testes de escrita dos DAOs serao ignorados.' -ForegroundColor Yellow
    } else {
        Write-Host 'Modo completo: os testes de DAO criam e removem registros temporarios.' -ForegroundColor Yellow
    }

    # Primeiro confirma build e ausencia de JDBC no projeto Web.
    Executar-Script 'verificarWebSemJdbc.ps1'

    # Usuario e senha sao solicitados apenas neste processo PowerShell.
    if ($criouUsuario) {
        $usuario = Read-Host 'Usuario JWT de integracao da API [doafacil_web]'
        if ([string]::IsNullOrWhiteSpace($usuario)) {
            $usuario = 'doafacil_web'
        }
        $env:DOAFACIL_API_USER = $usuario
    }
    if ($criouSenha) {
        $segura = Read-Host 'Senha JWT de integracao (nao sera exibida)' -AsSecureString
        $ponteiro = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($segura)
        try {
            $env:DOAFACIL_API_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ponteiro)
        } finally {
            [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ponteiro)
        }
    }
    if ([string]::IsNullOrWhiteSpace($env:DOAFACIL_API_USER) -or
        [string]::IsNullOrWhiteSpace($env:DOAFACIL_API_PASSWORD)) {
        throw 'Usuario e senha JWT de integracao sao obrigatorios.'
    }
    if ($criouUrl) {
        $env:DOAFACIL_API_URL = $apiBase
    }
    $apiBase = $env:DOAFACIL_API_URL.TrimEnd('/')

    # Confirma a API publica e a exigencia de token antes dos testes Java.
    Conferir-Http 'API publica /api/status' "$apiBase/status" 200
    Conferir-Http 'API /api/doadores sem JWT' "$apiBase/doadores" 401

    # Testes implementados nas Etapas 2, 3 e 4: reutiliza arquivos existentes.
    Executar-Script 'testarClienteHttp.ps1'
    if (-not $SomenteLeitura) {
        Executar-Script 'testarDoadorHttp.ps1'
        Executar-Script 'testarInstituicaoDoacaoHttp.ps1'
    }

    # Verificacao de publicacao: as telas dependem da API e nao do JDBC.
    Conferir-Http 'Pagina inicial Web' "$webBase/" 200
    Conferir-Http 'Web - listagem de Doadores' "$webBase/doador/listar" 200
    Conferir-Http 'Web - listagem de Instituicoes' "$webBase/instituicao/listar" 200
    Conferir-Http 'Web - listagem de Doacoes' "$webBase/doacao/listar" 200
    Conferir-Http 'Web nao expoe o REST antigo /api/status' "$webBase/api/status" 404

    Write-Host "`nTODAS AS VERIFICACOES AUTOMATIZADAS DA INTEGRACAO PASSARAM." -ForegroundColor Green
    Write-Host 'Ainda e necessario registrar no relatorio o teste manual dos formularios JSP.'
} catch {
    Write-Host "`nFALHOU: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
} finally {
    if ($criouSenha) {
        Remove-Item Env:DOAFACIL_API_PASSWORD -ErrorAction SilentlyContinue
    }
    if ($criouUsuario) {
        Remove-Item Env:DOAFACIL_API_USER -ErrorAction SilentlyContinue
    }
    if ($criouUrl) {
        Remove-Item Env:DOAFACIL_API_URL -ErrorAction SilentlyContinue
    }
}
