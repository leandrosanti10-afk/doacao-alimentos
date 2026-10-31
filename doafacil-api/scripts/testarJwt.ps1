<#
 Etapa 6 - Testes reais de autenticacao e protecao JWT.
 Executar no Windows PowerShell 5.1+:
 powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\testarJwt.ps1
 Solicita usuario e senha da conta de integracao API (nao imprime as credenciais).
 Nao altera nenhum registro no MySQL.
#>
param(
    [string]$BaseApi = 'http://localhost:8081/doafacil-api/api'
)
$ErrorActionPreference = 'Stop'
$quantidade = 0

function Enviar-Requisicao {
    param(
        [Parameter(Mandatory=$true)][string]$Metodo,
        [Parameter(Mandatory=$true)][string]$Url,
        [object]$Dados = $null,
        [string]$Token = ''
    )
    $parametros = @{
        Method = $Metodo
        Uri = $Url
        UseBasicParsing = $true
        ErrorAction = 'Stop'
    }
    if (-not [string]::IsNullOrEmpty($Token)) {
        $parametros.Headers = @{Authorization = "Bearer $Token"}
    }
    if ($null -ne $Dados) {
        $parametros.ContentType = 'application/json; charset=utf-8'
        if ($Dados -is [string]) {
            $texto = $Dados
        } else {
            $texto = $Dados | ConvertTo-Json -Depth 8 -Compress
        }
        $parametros.Body = [Text.Encoding]::UTF8.GetBytes($texto)
    }
    try {
        $r = Invoke-WebRequest @parametros
        return [pscustomobject]@{Codigo=[int]$r.StatusCode; Corpo=[string]$r.Content}
    } catch {
        $resposta = $_.Exception.Response
        if ($null -eq $resposta) { throw }
        $corpo = ''
        if ($resposta -is [System.Net.HttpWebResponse]) {
            $fluxo = $resposta.GetResponseStream()
            if ($null -ne $fluxo) {
                $leitor = New-Object System.IO.StreamReader($fluxo)
                try { $corpo = $leitor.ReadToEnd() } finally { $leitor.Dispose() }
            }
        } elseif ($null -ne $_.ErrorDetails) {
            $corpo = [string]$_.ErrorDetails.Message
        }
        return [pscustomobject]@{Codigo=[int]$resposta.StatusCode; Corpo=$corpo}
    }
}

function Exigir-Status {
    param([string]$Descricao, [object]$Resposta, [int]$Esperado)
    if ($Resposta.Codigo -ne $Esperado) {
        throw "FALHOU: $Descricao. Esperado $Esperado, recebido $($Resposta.Codigo). Corpo: $($Resposta.Corpo)"
    }
    $script:quantidade++
    Write-Host "APROVADO: $Descricao (HTTP $Esperado)" -ForegroundColor Green
}

try {
    Write-Host "Validando seguranca da API: $BaseApi"
    Exigir-Status 'Status publico' (Enviar-Requisicao GET "$BaseApi/status") 200
    Exigir-Status 'Login sem corpo valido' (Enviar-Requisicao POST "$BaseApi/auth/login" -Dados '{}') 400
    Exigir-Status 'Login JSON malformado' (Enviar-Requisicao POST "$BaseApi/auth/login" -Dados '{"usuario":') 400
    Exigir-Status 'Login credenciais erradas' (Enviar-Requisicao POST "$BaseApi/auth/login" -Dados @{usuario='usuario-inexistente';senha='senha-incorreta'}) 401

    foreach ($entidade in @('doadores', 'instituicoes', 'doacoes')) {
        Exigir-Status "GET $entidade sem token" (Enviar-Requisicao GET "$BaseApi/$entidade") 401
    }
    Exigir-Status 'Token malformado' (Enviar-Requisicao GET "$BaseApi/doadores" -Token 'abc.def.ghi') 401

    $credencial = Get-Credential -Message 'Informe API_AUTH_USER e API_AUTH_PASSWORD configurados no Tomcat 8081'
    if ($null -eq $credencial) { throw 'Login cancelado.' }
    $senha = $credencial.GetNetworkCredential().Password
    try {
        $login = Enviar-Requisicao POST "$BaseApi/auth/login" -Dados @{usuario=$credencial.UserName;senha=$senha}
    } finally {
        $senha = $null
    }
    Exigir-Status 'Login valido' $login 200
    $resultado = $login.Corpo | ConvertFrom-Json
    $token = [string]$resultado.access_token
    if (
        [string]::IsNullOrWhiteSpace($token) -or
        ($resultado.token_type -ne 'Bearer') -or
        (([long]$resultado.expires_in) -le 0)
    ) {
        throw 'FALHOU: corpo da resposta de login incompleto.'
    }
    $script:quantidade++
    Write-Host 'APROVADO: resposta de login tem token Bearer e expiracao.' -ForegroundColor Green

    foreach ($entidade in @('doadores', 'instituicoes', 'doacoes')) {
        Exigir-Status "GET $entidade com token valido" (Enviar-Requisicao GET "$BaseApi/$entidade" -Token $token) 200
    }
    $assinaturaAlterada = $token.Substring(0, $token.Length - 4) + 'xxxx'
    Exigir-Status 'Token com assinatura adulterada' (Enviar-Requisicao GET "$BaseApi/doadores" -Token $assinaturaAlterada) 401
    Write-Host "TODOS OS $quantidade TESTES JWT PASSARAM." -ForegroundColor Green
} catch {
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}
