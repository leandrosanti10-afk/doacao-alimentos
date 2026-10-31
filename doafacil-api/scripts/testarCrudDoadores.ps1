<#
 Testes integrados da API DoaFacil - CRUD de Doadores
 Executar no PowerShell: .\scripts\testarCrudDoadores.ps1
 Cria um cadastro FICTICIO temporario, executa testes GET/POST/PUT/DELETE e o exclui.
 JWT: solicita credenciais de desenvolvimento para obter token via /api/auth/login.
#>
param(
    [string]$BaseUrl = 'http://localhost:8081/doafacil-api/api/doadores'
)

$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot '_JwtTesteHelper.ps1')
$script:TokenJwt = Obter-TokenJwtTeste -BaseApi ($BaseUrl -replace "/(?:doadores|instituicoes)/?$", "")
$registroCriado = $null
$registroRemovido = $false
$falha = $false

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
    Write-Host "APROVADO: $Teste (HTTP $Esperado)" -ForegroundColor Green
}

try {
    Write-Host "API em teste: $BaseUrl"
    $listagem = Enviar-Requisicao -Metodo 'GET' -Url $BaseUrl
    Exigir-Status 'GET listagem' $listagem 200

    # Dados aleatorios de teste; os numeros nao representam documentos reais.
    $cpf = '77' + (Get-Random -Minimum 100000000 -Maximum 999999999).ToString('000000000')
    $fone = '34' + (Get-Random -Minimum 900000000 -Maximum 999999999).ToString('000000000')
    $sufixo = [guid]::NewGuid().ToString('N').Substring(0, 12)
    $novo = @{
        nome     = 'Teste Temporario API'
        cpfCnpj  = $cpf
        email    = "teste.$sufixo@example.com"
        telefone = $fone
        cidade   = 'Uberaba'
    }

    $cadastro = Enviar-Requisicao -Metodo 'POST' -Url $BaseUrl -Dados $novo
    Exigir-Status 'POST cadastro temporario' $cadastro 201
    $registroCriado = ($cadastro.Corpo | ConvertFrom-Json).id
    if (-not $registroCriado) { throw 'POST nao retornou ID de cadastro.' }
    Write-Host "ID temporario criado: $registroCriado"

    $detalhe = Enviar-Requisicao -Metodo 'GET' -Url "$BaseUrl/$registroCriado"
    Exigir-Status 'GET cadastro temporario' $detalhe 200

    $modificado = @{
        id       = 999999999 # Deve ser ignorado: ID da URL prevalece.
        nome     = 'Teste Atualizado API'
        cpfCnpj  = $cpf
        email    = "alterado.$sufixo@example.com"
        telefone = $fone
        cidade   = 'Ribeirao Preto'
    }
    $edicao = Enviar-Requisicao -Metodo 'PUT' -Url "$BaseUrl/$registroCriado" -Dados $modificado
    Exigir-Status 'PUT atualizacao' $edicao 200
    $dadosEditados = $edicao.Corpo | ConvertFrom-Json
    if ([long]$dadosEditados.id -ne [long]$registroCriado) {
        throw 'PUT alterou o ID usando o valor enviado no JSON.'
    }
    if ($dadosEditados.cidade -ne 'Ribeirao Preto') {
        throw 'PUT nao persistiu a cidade atualizada.'
    }

    $confirmacao = Enviar-Requisicao -Metodo 'GET' -Url "$BaseUrl/$registroCriado"
    Exigir-Status 'GET confirma atualizacao no MySQL' $confirmacao 200
    if (($confirmacao.Corpo | ConvertFrom-Json).cidade -ne 'Ribeirao Preto') {
        throw 'GET nao retornou os dados atualizados.'
    }

    $invalido = Enviar-Requisicao -Metodo 'PUT' -Url "$BaseUrl/abc" -Dados $modificado
    Exigir-Status 'PUT com ID invalido' $invalido 400

    $ausente = Enviar-Requisicao -Metodo 'PUT' -Url "$BaseUrl/999999999" -Dados $modificado
    Exigir-Status 'PUT com doador inexistente' $ausente 404

    $malformado = Enviar-Requisicao -Metodo 'PUT' -Url "$BaseUrl/$registroCriado" -Dados '{"nome":'
    Exigir-Status 'PUT com JSON malformado' $malformado 400

    $deleteInexistente = Enviar-Requisicao -Metodo 'DELETE' -Url "$BaseUrl/999999999"
    Exigir-Status 'DELETE com ID inexistente' $deleteInexistente 404

    $exclusao = Enviar-Requisicao -Metodo 'DELETE' -Url "$BaseUrl/$registroCriado"
    Exigir-Status 'DELETE doador temporario' $exclusao 204
    if (-not [string]::IsNullOrEmpty($exclusao.Corpo)) {
        throw 'DELETE 204 retornou um corpo de resposta; deveria estar vazio.'
    }
    $registroRemovido = $true

    $depois = Enviar-Requisicao -Metodo 'GET' -Url "$BaseUrl/$registroCriado"
    Exigir-Status 'GET apos exclusao' $depois 404

    Write-Host "TODOS OS TESTES PASSARAM. Registro temporario $registroCriado excluido." -ForegroundColor Green
} catch {
    $falha = $true
    Write-Host $_.Exception.Message -ForegroundColor Red
} finally {
    if ($null -ne $registroCriado -and -not $registroRemovido) {
        Write-Host "Tentando limpar cadastro temporario ID $registroCriado..."
        try {
            $limpeza = Enviar-Requisicao -Metodo 'DELETE' -Url "$BaseUrl/$registroCriado"
            if ($limpeza.Codigo -eq 204 -or $limpeza.Codigo -eq 404) {
                Write-Host 'Limpeza realizada.'
            } else {
                Write-Host "ATENCAO: exclua manualmente o registro de teste ID $registroCriado (HTTP $($limpeza.Codigo))." -ForegroundColor Yellow
            }
        } catch {
            Write-Host "ATENCAO: nao foi possivel limpar o ID $registroCriado. Verifique manualmente." -ForegroundColor Yellow
        }
    }
}
if ($falha) { exit 1 }
