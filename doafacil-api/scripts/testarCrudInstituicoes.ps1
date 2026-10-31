<#
 DoaFacil API - Etapa 4: testes de instituicoes
 Uso no Windows PowerShell:
 powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\testarCrudInstituicoes.ps1
 Testa a API local na porta 8081. Cria UMA instituicao temporaria e tenta exclui-la ao final.
 Nao testa a exclusao de instituicoes existentes nem doacoes vinculadas.
 JWT: solicita credenciais de desenvolvimento para obter token via /api/auth/login.
#>
param(
    [string]$BaseUrl = 'http://localhost:8081/doafacil-api/api/instituicoes'
)

$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot '_JwtTesteHelper.ps1')
$script:TokenJwt = Obter-TokenJwtTeste -BaseApi ($BaseUrl -replace "/(?:doadores|instituicoes)/?$", "")
$idTemporario = $null
$registroRemovido = $false
$falhou = $false
$quantidadeTestes = 0

function Enviar-Requisicao {
    param(
        [Parameter(Mandatory = $true)][string]$Metodo,
        [Parameter(Mandatory = $true)][string]$Url,
        [object]$Dados = $null
    )
    $parametros = @{
        Uri             = $Url
        Method          = $Metodo
        UseBasicParsing = $true
        ErrorAction     = 'Stop'
    }
    $parametros.Headers = @{ Authorization = "Bearer $script:TokenJwt" }
    if ($null -ne $Dados) {
        $parametros.ContentType = 'application/json; charset=utf-8'
        if ($Dados -is [string]) {
            $parametros.Body = [System.Text.Encoding]::UTF8.GetBytes($Dados)
        } else {
            $parametros.Body = [System.Text.Encoding]::UTF8.GetBytes(($Dados | ConvertTo-Json -Compress -Depth 8))
        }
    }
    try {
        $r = Invoke-WebRequest @parametros
        return [pscustomobject]@{ Codigo = [int]$r.StatusCode; Corpo = [string]$r.Content }
    } catch {
        $resp = $_.Exception.Response
        if ($null -eq $resp) { throw }
        $codigo = [int]$resp.StatusCode
        $corpo = ''
        if ($resp -is [System.Net.HttpWebResponse]) {
            $fluxo = $resp.GetResponseStream()
            if ($null -ne $fluxo) {
                $leitor = [System.IO.StreamReader]::new($fluxo)
                try { $corpo = $leitor.ReadToEnd() } finally { $leitor.Dispose() }
            }
        } elseif ($null -ne $_.ErrorDetails -and $null -ne $_.ErrorDetails.Message) {
            $corpo = [string]$_.ErrorDetails.Message
        }
        return [pscustomobject]@{ Codigo = $codigo; Corpo = $corpo }
    }
}

function Exigir-Status {
    param([string]$Teste, [object]$Resposta, [int]$Esperado)
    if ($Resposta.Codigo -ne $Esperado) {
        throw "FALHOU: $Teste. Esperado HTTP $Esperado, recebido $($Resposta.Codigo). Corpo: $($Resposta.Corpo)"
    }
    $script:quantidadeTestes++
    Write-Host "APROVADO: $Teste (HTTP $Esperado)" -ForegroundColor Green
}

try {
    Write-Host "API em teste: $BaseUrl"
    $lista = Enviar-Requisicao -Metodo GET -Url $BaseUrl
    Exigir-Status 'GET lista de instituicoes' $lista 200

    $sufixo = [guid]::NewGuid().ToString('N').Substring(0, 12)
    $fone = '34' + (Get-Random -Minimum 900000000 -Maximum 999999999).ToString('000000000')
    $novo = @{
        nome     = 'Instituicao Temporaria API'
        endereco = 'Rua Teste 100'
        telefone = $fone
        email    = "instituicao.$sufixo@example.com"
        cidade   = 'Uberaba'
    }

    $post = Enviar-Requisicao -Metodo POST -Url $BaseUrl -Dados $novo
    Exigir-Status 'POST instituicao temporaria' $post 201
    $idTemporario = ($post.Corpo | ConvertFrom-Json).id
    if (-not $idTemporario) { throw 'FALHOU: POST nao devolveu o ID gerado pelo MySQL.' }
    Write-Host "ID temporario criado: $idTemporario"

    $get = Enviar-Requisicao -Metodo GET -Url "$BaseUrl/$idTemporario"
    Exigir-Status 'GET por ID criado' $get 200
    if ([long](($get.Corpo | ConvertFrom-Json).id) -ne [long]$idTemporario) {
        throw 'FALHOU: GET nao retornou a instituicao de teste.'
    }

    $duplicado = Enviar-Requisicao -Metodo POST -Url $BaseUrl -Dados $novo
    Exigir-Status 'POST com email e telefone duplicados' $duplicado 400

    $invalido = @{
        nome     = ''
        endereco = 'Rua Teste 100'
        telefone = $fone
        email    = "semnome.$sufixo@example.com"
        cidade   = 'Uberaba'
    }
    $semNome = Enviar-Requisicao -Metodo POST -Url $BaseUrl -Dados $invalido
    Exigir-Status 'POST sem nome' $semNome 400

    $alterado = @{
        id       = 999999999  # A API deve ignorar o ID do JSON.
        nome     = 'Instituicao Atualizada API'
        endereco = 'Avenida Teste 250'
        telefone = $fone
        email    = "alterado.$sufixo@example.com"
        cidade   = 'Ribeirao Preto'
    }
    $put = Enviar-Requisicao -Metodo PUT -Url "$BaseUrl/$idTemporario" -Dados $alterado
    Exigir-Status 'PUT atualizacao de instituicao' $put 200
    $dadosEditados = $put.Corpo | ConvertFrom-Json
    if ([long]$dadosEditados.id -ne [long]$idTemporario) {
        throw 'FALHOU: PUT permitiu modificar o ID pelo JSON.'
    }
    if ($dadosEditados.endereco -ne 'Avenida Teste 250') {
        throw 'FALHOU: PUT nao retornou o endereco atualizado.'
    }

    $persistiu = Enviar-Requisicao -Metodo GET -Url "$BaseUrl/$idTemporario"
    Exigir-Status 'GET confirma alteracao no MySQL' $persistiu 200
    if (($persistiu.Corpo | ConvertFrom-Json).endereco -ne 'Avenida Teste 250') {
        throw 'FALHOU: alteracao nao persistiu no banco.'
    }

    Exigir-Status 'GET com ID invalido' (Enviar-Requisicao -Metodo GET -Url "$BaseUrl/abc") 400
    Exigir-Status 'GET com ID inexistente' (Enviar-Requisicao -Metodo GET -Url "$BaseUrl/999999999") 404
    Exigir-Status 'PUT com ID invalido' (Enviar-Requisicao -Metodo PUT -Url "$BaseUrl/abc" -Dados $alterado) 400
    Exigir-Status 'PUT com instituicao inexistente' (Enviar-Requisicao -Metodo PUT -Url "$BaseUrl/999999999" -Dados $alterado) 404
    Exigir-Status 'PUT com JSON malformado' (Enviar-Requisicao -Metodo PUT -Url "$BaseUrl/$idTemporario" -Dados '{"nome":') 400
    Exigir-Status 'POST sem JSON valido' (Enviar-Requisicao -Metodo POST -Url $BaseUrl -Dados '{"nome":') 400
    Exigir-Status 'POST na rota individual' (Enviar-Requisicao -Metodo POST -Url "$BaseUrl/$idTemporario" -Dados $novo) 405
    Exigir-Status 'PUT sem ID' (Enviar-Requisicao -Metodo PUT -Url $BaseUrl -Dados $alterado) 405
    Exigir-Status 'DELETE sem ID' (Enviar-Requisicao -Metodo DELETE -Url $BaseUrl) 405
    Exigir-Status 'DELETE com ID inexistente' (Enviar-Requisicao -Metodo DELETE -Url "$BaseUrl/999999999") 404

    $del = Enviar-Requisicao -Metodo DELETE -Url "$BaseUrl/$idTemporario"
    Exigir-Status 'DELETE instituicao temporaria' $del 204
    if (-not [string]::IsNullOrEmpty($del.Corpo)) { throw 'FALHOU: DELETE 204 nao deveria retornar corpo.' }
    $registroRemovido = $true

    $apos = Enviar-Requisicao -Metodo GET -Url "$BaseUrl/$idTemporario"
    Exigir-Status 'GET apos DELETE' $apos 404
    Write-Host "TODOS OS $quantidadeTestes TESTES PASSARAM. Instituicao temporaria $idTemporario excluida." -ForegroundColor Green
} catch {
    $falhou = $true
    Write-Host $_.Exception.Message -ForegroundColor Red
} finally {
    if ($null -ne $idTemporario -and -not $registroRemovido) {
        Write-Host "Tentando limpar instituicao temporaria ID $idTemporario..." -ForegroundColor Yellow
        try {
            $limpeza = Enviar-Requisicao -Metodo DELETE -Url "$BaseUrl/$idTemporario"
            if ($limpeza.Codigo -eq 204 -or $limpeza.Codigo -eq 404) {
                Write-Host 'Limpeza realizada.' -ForegroundColor Green
            } else {
                Write-Host "ATENCAO: remova manualmente o ID temporario $idTemporario (HTTP $($limpeza.Codigo))." -ForegroundColor Yellow
            }
        } catch {
            Write-Host "ATENCAO: nao foi possivel limpar o ID $idTemporario. Confira o banco." -ForegroundColor Yellow
        }
    }
}
if ($falhou) { exit 1 }
