# Pessoa 2 - Etapa 5: remocao do JDBC e dos REST Controllers legados do Web.
# Execute no PowerShell 5.1+ dentro de C:\doacaoAlimentos\doacao-alimentos:
# powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\scripts\aplicarEtapa5.ps1"
# Faz backup no TEMP antes de alterar arquivos. Nao altera doafacil-api nem JSPs.

$ErrorActionPreference = 'Stop'
$web = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$java = Join-Path $web 'src\main\java\br\com\mvc'
$pom = Join-Path $web 'pom.xml'

if (-not (Test-Path -LiteralPath $pom -PathType Leaf)) {
    throw 'pom.xml do projeto Web nao encontrado.'
}

$daos = @('DoadorDAO.java', 'InstituicaoDAO.java', 'DoacaoDAO.java')
foreach ($nome in $daos) {
    $arquivo = Join-Path $java (Join-Path 'dao' $nome)
    if (-not (Test-Path -LiteralPath $arquivo -PathType Leaf)) {
        throw "DAO ausente: $nome. Instale primeiro as Etapas 3 e 4."
    }
    $codigo = Get-Content -LiteralPath $arquivo -Raw
    if (($codigo -notmatch '\bApiClient\b') -or ($codigo -match '\bextends\s+BaseDAO\b')) {
        throw "O $nome ainda nao usa exclusivamente HTTP. Instale as Etapas 3 e 4 antes de limpar o JDBC."
    }
}

# Estas classes pertencem ao projeto API, nao ao Web.
$remover = @(
    'config/MysqlSingleton.java',
    'config/MysqlPoolShutdownListener.java',
    'dao/BaseDAO.java',
    'controller/api/ApiServlet.java',
    'controller/api/StatusApiController.java',
    'controller/api/DoadorApiController.java',
    'util/ApiJson.java'
)

# Verifica dependencias externas aos arquivos que serao removidos antes de excluir.
$referenciasLegadas = '\b(MysqlSingleton|MysqlPoolShutdownListener|BaseDAO|ApiJson|ApiServlet|StatusApiController|DoadorApiController)\b'
$arquivosJava = @(Get-ChildItem -LiteralPath $java -Filter '*.java' -File -Recurse)
foreach ($arquivo in $arquivosJava) {
    $relativo = $arquivo.FullName.Substring($java.Length + 1).Replace('\', '/')
    if ($remover -contains $relativo) { continue }
    $codigo = Get-Content -LiteralPath $arquivo.FullName -Raw
    if ($codigo -match $referenciasLegadas) {
        throw "Referencia legada em $relativo. Revise antes de remover as classes JDBC/REST."
    }
}

$webXml = Join-Path $web 'src\main\webapp\WEB-INF\web.xml'
if ((Test-Path -LiteralPath $webXml) -and
    ((Get-Content -LiteralPath $webXml -Raw) -match $referenciasLegadas)) {
    throw 'O web.xml referencia uma classe legada. Corrija antes de prosseguir.'
}

$backup = Join-Path $env:TEMP ('DoaFacil_Pessoa2_Etapa5_Backup_' + (Get-Date -Format 'yyyyMMdd_HHmmss'))
New-Item -ItemType Directory -Path $backup -Force | Out-Null
Copy-Item -LiteralPath $pom -Destination (Join-Path $backup 'pom.xml')
foreach ($relativo in $remover) {
    $origem = Join-Path $java ($relativo.Replace('/', '\'))
    if (Test-Path -LiteralPath $origem -PathType Leaf) {
        $destino = Join-Path $backup $relativo.Replace('/', '\')
        New-Item -ItemType Directory -Path (Split-Path $destino) -Force | Out-Null
        Copy-Item -LiteralPath $origem -Destination $destino
    }
}
Write-Host "Backup criado em: $backup" -ForegroundColor Cyan

# Remove do pom.xml exclusivamente as dependencias do driver e pool MySQL.
[xml]$documento = Get-Content -LiteralPath $pom -Raw -Encoding UTF8
$dependencias = @($documento.SelectNodes('/*[local-name()="project"]/*[local-name()="dependencies"]/*[local-name()="dependency"]'))
$removidas = 0
foreach ($dep in $dependencias) {
    $grupo = $dep.SelectSingleNode('*[local-name()="groupId"]').InnerText
    $artefato = $dep.SelectSingleNode('*[local-name()="artifactId"]').InnerText
    if (($grupo -eq 'com.zaxxer' -and $artefato -eq 'HikariCP') -or
        ($grupo -eq 'com.mysql' -and $artefato -eq 'mysql-connector-j')) {
        [void]$dep.ParentNode.RemoveChild($dep)
        $removidas++
        Write-Host "Dependencia removida: $grupo`:$artefato"
    }
}
$comentarios = @($documento.SelectNodes('/*[local-name()="project"]/*[local-name()="dependencies"]/comment()'))
foreach ($comentario in $comentarios) {
    if ($comentario.Value -match 'Pool de conexoes|Driver para conex') {
        [void]$comentario.ParentNode.RemoveChild($comentario)
    }
}
$settings = New-Object System.Xml.XmlWriterSettings
$settings.Indent = $true
$settings.Encoding = New-Object System.Text.UTF8Encoding($false)
$writer = [System.Xml.XmlWriter]::Create($pom, $settings)
try { $documento.Save($writer) } finally { $writer.Dispose() }

foreach ($relativo in $remover) {
    $arquivo = Join-Path $java ($relativo.Replace('/', '\'))
    if (Test-Path -LiteralPath $arquivo -PathType Leaf) {
        Remove-Item -LiteralPath $arquivo -Force
        Write-Host "Classe removida: $relativo"
    }
}
foreach ($diretorio in @('config', 'controller/api')) {
    $pasta = Join-Path $java ($diretorio.Replace('/', '\'))
    if ((Test-Path -LiteralPath $pasta -PathType Container) -and
        @((Get-ChildItem -LiteralPath $pasta -Force)).Count -eq 0) {
        Remove-Item -LiteralPath $pasta
    }
}

Write-Host 'LIMPEZA CONCLUIDA. Nenhuma JSP ou DAO HTTP foi alterada.' -ForegroundColor Green
Write-Host 'Agora execute: powershell.exe -NoProfile -ExecutionPolicy Bypass -File ".\scripts\verificarWebSemJdbc.ps1"'
Write-Host 'Depois publique o WAR Web na porta 8080 e teste as tres listagens no navegador.'
