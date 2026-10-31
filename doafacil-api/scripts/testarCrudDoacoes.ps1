<#
 DoaFacil API - Etapa 5: CRUD de doacoes (Windows PowerShell 5.1+)
 Executar:
 powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\testarCrudDoacoes.ps1

 Utiliza doador e instituicao EXISTENTES SOMENTE como referencias.
 Cria e apaga UMA doacao temporaria; nunca altera doadores,
 instituicoes ou doacoes anteriores ao teste.
 API ainda sem JWT: execute somente em ambiente local.
#>
param(
    [string]$BaseApi = 'http://localhost:8081/doafacil-api/api'
)

$ErrorActionPreference = 'Stop'
$baseUrl = "$BaseApi/doacoes"
$idTemporario = $null
$removido = $false
$testes = 0
$falhou = $false

function Enviar-Requisicao {
    param(
        [Parameter(Mandatory=$true)][string]$Metodo,
        [Parameter(Mandatory=$true)][string]$Url,
        [object]$Dados = $null
    )
    $parametros = @{
        Uri = $Url; Method = $Metodo; UseBasicParsing = $true; ErrorAction = 'Stop'
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
        return [pscustomobject]@{ Codigo = [int]$r.StatusCode; Corpo = [string]$r.Content; Headers = $r.Headers }
    } catch {
        if ($null -eq $_.Exception.Response) { throw }
        $resposta = $_.Exception.Response
        $corpo = ''
        if ($resposta -is [System.Net.HttpWebResponse]) {
            $fluxo = $resposta.GetResponseStream()
            if ($null -ne $fluxo) {
                $leitor = New-Object System.IO.StreamReader($fluxo)
                try { $corpo = $leitor.ReadToEnd() } finally { $leitor.Dispose() }
            }
        } elseif ($null -ne $_.ErrorDetails -and $null -ne $_.ErrorDetails.Message) {
            $corpo = [string]$_.ErrorDetails.Message
        }
        return [pscustomobject]@{ Codigo = [int]$resposta.StatusCode; Corpo = $corpo; Headers = $resposta.Headers }
    }
}

function Exigir-Status {
    param([string]$Nome, [object]$Resposta, [int]$Esperado)
    if ($Resposta.Codigo -ne $Esperado) {
        throw "FALHOU: $Nome. Esperado HTTP $Esperado, recebido HTTP $($Resposta.Codigo). Corpo: $($Resposta.Corpo)"
    }
    $script:testes++
    Write-Host "APROVADO: $Nome (HTTP $Esperado)" -ForegroundColor Green
}

try {
    Write-Host "API em teste: $baseUrl"
    $doadores = Enviar-Requisicao -Metodo GET -Url "$BaseApi/doadores"
    Exigir-Status 'GET doadores disponiveis' $doadores 200
    $instituicoes = Enviar-Requisicao -Metodo GET -Url "$BaseApi/instituicoes"
    Exigir-Status 'GET instituicoes disponiveis' $instituicoes 200
    # ConvertFrom-Json ja transforma os arrays JSON em objetos PowerShell.
    # Evite @(...), que pode empacotar a lista inteira em um unico elemento
    # no Windows PowerShell 5.1 e causar erro ao converter o ID em Int64.
    $listaDoadores = ConvertFrom-Json -InputObject $doadores.Corpo
    $listaInstituicoes = ConvertFrom-Json -InputObject $instituicoes.Corpo
    if ($null -eq $listaDoadores -or $null -eq $listaInstituicoes -or
        @($listaDoadores).Count -eq 0 -or @($listaInstituicoes).Count -eq 0) {
        throw 'Cadastre ao menos um doador e uma instituicao de teste antes de executar o roteiro de doacoes.'
    }
    $primeiroDoador = $listaDoadores | Select-Object -First 1
    $primeiraInstituicao = $listaInstituicoes | Select-Object -First 1
    $idDoador = [long]$primeiroDoador.id
    $idInstituicao = [long]$primeiraInstituicao.id
    if ($idDoador -le 0 -or $idInstituicao -le 0) { throw 'IDs de referencia invalidos.' }
    Write-Host "Referencias (somente leitura): doador $idDoador; instituicao $idInstituicao"

    Exigir-Status 'GET listagem de doacoes' (Enviar-Requisicao -Metodo GET -Url $baseUrl) 200
    Exigir-Status 'GET ID inexistente' (Enviar-Requisicao -Metodo GET -Url "$baseUrl/999999999") 404
    Exigir-Status 'GET ID invalido' (Enviar-Requisicao -Metodo GET -Url "$baseUrl/abc") 400
    Exigir-Status 'GET rota adicional' (Enviar-Requisicao -Metodo GET -Url "$baseUrl/1/extra") 404

    $hoje = (Get-Date).ToString('yyyy-MM-dd')
    $ontem = (Get-Date).AddDays(-1).ToString('yyyy-MM-dd')
    $novo = @{
        doador = @{ id = $idDoador }
        instituicao = @{ id = $idInstituicao }
        descricao = 'Doacao temporaria de verificacao da API'
        dataDoacao = $hoje
    }
    $semDoador = @{
        instituicao = @{ id = $idInstituicao }
        descricao = 'Nao deve ser inserida'
        dataDoacao = $hoje
    }
    Exigir-Status 'POST sem doador' (Enviar-Requisicao -Metodo POST -Url $baseUrl -Dados $semDoador) 400
    $idFalso = 999999999
    $doadorInexistente = @{
        doador = @{ id = $idFalso }
        instituicao = @{ id = $idInstituicao }
        descricao = 'Nao deve ser inserida'
        dataDoacao = $hoje
    }
    Exigir-Status 'POST com doador inexistente' (Enviar-Requisicao -Metodo POST -Url $baseUrl -Dados $doadorInexistente) 404
    $instituicaoInexistente = @{
        doador = @{ id = $idDoador }
        instituicao = @{ id = $idFalso }
        descricao = 'Nao deve ser inserida'
        dataDoacao = $hoje
    }
    Exigir-Status 'POST com instituicao inexistente' (Enviar-Requisicao -Metodo POST -Url $baseUrl -Dados $instituicaoInexistente) 404
    Exigir-Status 'POST com data invalida' (Enviar-Requisicao -Metodo POST -Url $baseUrl -Dados '{"doador":{"id":1},"instituicao":{"id":1},"descricao":"X","dataDoacao":"31/10/2026"}') 400
    Exigir-Status 'POST com JSON malformado' (Enviar-Requisicao -Metodo POST -Url $baseUrl -Dados '{"doador":') 400

    $post = Enviar-Requisicao -Metodo POST -Url $baseUrl -Dados $novo
    Exigir-Status 'POST doacao temporaria' $post 201
    $criada = $post.Corpo | ConvertFrom-Json
    $idTemporario = [long]$criada.id
    if ($idTemporario -le 0) { throw 'FALHOU: POST nao retornou ID valido.' }
    if ([long]$criada.doador.id -ne $idDoador -or [long]$criada.instituicao.id -ne $idInstituicao) {
        throw 'FALHOU: POST retornou relacoes diferentes das informadas.'
    }
    Write-Host "ID da doacao temporaria: $idTemporario"

    $get = Enviar-Requisicao -Metodo GET -Url "$baseUrl/$idTemporario"
    Exigir-Status 'GET doacao temporaria' $get 200
    if ([long](($get.Corpo | ConvertFrom-Json).id) -ne $idTemporario) {
        throw 'FALHOU: GET retornou ID diferente do cadastrado.'
    }
    Exigir-Status 'POST em rota individual' (Enviar-Requisicao -Metodo POST -Url "$baseUrl/$idTemporario" -Dados $novo) 405

    $atualizada = @{
        id = 999999999 # A API deve usar o ID da URL.
        doador = @{ id = $idDoador }
        instituicao = @{ id = $idInstituicao }
        descricao = 'Doacao temporaria atualizada pela API'
        dataDoacao = $ontem
    }
    $put = Enviar-Requisicao -Metodo PUT -Url "$baseUrl/$idTemporario" -Dados $atualizada
    Exigir-Status 'PUT atualiza doacao' $put 200
    $retornoPut = $put.Corpo | ConvertFrom-Json
    if ([long]$retornoPut.id -ne $idTemporario) { throw 'FALHOU: PUT aceitou ID do corpo.' }
    if ($retornoPut.descricao -ne 'Doacao temporaria atualizada pela API') { throw 'FALHOU: descricao nao foi atualizada.' }

    $persistiu = Enviar-Requisicao -Metodo GET -Url "$baseUrl/$idTemporario"
    Exigir-Status 'GET confirma persistencia no MySQL' $persistiu 200
    $persistido = $persistiu.Corpo | ConvertFrom-Json
    if ($persistido.descricao -ne 'Doacao temporaria atualizada pela API' -or $persistido.dataDoacao -ne $ontem) {
        throw 'FALHOU: atualizacao nao persistiu no MySQL.'
    }
    Exigir-Status 'PUT com doacao inexistente' (Enviar-Requisicao -Metodo PUT -Url "$baseUrl/999999999" -Dados $atualizada) 404
    Exigir-Status 'PUT com ID invalido' (Enviar-Requisicao -Metodo PUT -Url "$baseUrl/abc" -Dados $atualizada) 400
    Exigir-Status 'PUT sem ID' (Enviar-Requisicao -Metodo PUT -Url $baseUrl -Dados $atualizada) 405
    Exigir-Status 'PUT com JSON malformado' (Enviar-Requisicao -Metodo PUT -Url "$baseUrl/$idTemporario" -Dados '{"doador":') 400
    Exigir-Status 'DELETE sem ID' (Enviar-Requisicao -Metodo DELETE -Url $baseUrl) 405
    Exigir-Status 'DELETE com ID inexistente' (Enviar-Requisicao -Metodo DELETE -Url "$baseUrl/999999999") 404

    $delete = Enviar-Requisicao -Metodo DELETE -Url "$baseUrl/$idTemporario"
    Exigir-Status 'DELETE doacao temporaria' $delete 204
    if (-not [string]::IsNullOrEmpty($delete.Corpo)) { throw 'FALHOU: 204 deve ter corpo vazio.' }
    $removido = $true
    Exigir-Status 'GET apos exclusao' (Enviar-Requisicao -Metodo GET -Url "$baseUrl/$idTemporario") 404
    Write-Host "TODOS OS $testes TESTES PASSARAM. Doacao temporaria $idTemporario excluida." -ForegroundColor Green
} catch {
    $falhou = $true
    Write-Host $_.Exception.Message -ForegroundColor Red
} finally {
    if ($null -ne $idTemporario -and -not $removido) {
        Write-Host "Tentando excluir a doacao temporaria $idTemporario..." -ForegroundColor Yellow
        try {
            $limpeza = Enviar-Requisicao -Metodo DELETE -Url "$baseUrl/$idTemporario"
            if ($limpeza.Codigo -eq 204 -or $limpeza.Codigo -eq 404) {
                Write-Host 'Registro temporario removido.' -ForegroundColor Green
            } else {
                Write-Host "ATENCAO: confira manualmente a doacao temporaria ID $idTemporario (HTTP $($limpeza.Codigo))." -ForegroundColor Yellow
            }
        } catch {
            Write-Host "ATENCAO: nao foi possivel remover ID $idTemporario. Verifique o MySQL." -ForegroundColor Yellow
        }
    }
}
if ($falhou) { exit 1 }
