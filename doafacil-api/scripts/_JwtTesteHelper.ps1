<#
 Funcoes reutilizaveis de autenticacao para testes locais.
 Nao escreve tokens nem senhas no console ou em arquivos.
 Opcional: definir DOAFACIL_TEST_TOKEN no ambiente antes de executar scripts.
#>
function Obter-TokenJwtTeste {
    param([Parameter(Mandatory=$true)][string]$BaseApi)

    if (-not [string]::IsNullOrWhiteSpace($env:DOAFACIL_TEST_TOKEN)) {
        return $env:DOAFACIL_TEST_TOKEN
    }

    $credencial = Get-Credential -Message 'Credenciais API_AUTH_USER / API_AUTH_PASSWORD (login de desenvolvimento da API)'
    if ($null -eq $credencial) { throw 'Login cancelado.' }
    $senha = $credencial.GetNetworkCredential().Password
    try {
        $json = @{usuario=$credencial.UserName; senha=$senha} | ConvertTo-Json -Compress
        $resp = Invoke-RestMethod -Method Post -Uri "$BaseApi/auth/login" `
            -ContentType 'application/json; charset=utf-8' `
            -Body ([Text.Encoding]::UTF8.GetBytes($json)) -ErrorAction Stop
        if ([string]::IsNullOrWhiteSpace($resp.access_token)) {
            throw 'Login nao retornou access_token.'
        }
        return [string]$resp.access_token
    } finally {
        $senha = $null
        $json = $null
    }
}
