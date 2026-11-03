# Pessoa 2 - Etapa 5: auditoria sem JDBC + compilacao Maven + inspecao do WAR.
# Execute apos aplicarEtapa5.ps1. Nao modifica dados do MySQL.

$ErrorActionPreference = 'Stop'
$web = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$java = Join-Path $web 'src\main\java\br\com\mvc'
$pom = Join-Path $web 'pom.xml'

$arquivosQueNaoDevemExistir = @(
    'config/MysqlSingleton.java',
    'config/MysqlPoolShutdownListener.java',
    'dao/BaseDAO.java',
    'controller/api/ApiServlet.java',
    'controller/api/StatusApiController.java',
    'controller/api/DoadorApiController.java',
    'util/ApiJson.java'
)
foreach ($relativo in $arquivosQueNaoDevemExistir) {
    if (Test-Path -LiteralPath (Join-Path $java ($relativo.Replace('/', '\')))) {
        throw "Ainda existe classe legada: $relativo"
    }
}

[xml]$documento = Get-Content -LiteralPath $pom -Raw -Encoding UTF8
$dependencias = @($documento.SelectNodes('/*[local-name()="project"]/*[local-name()="dependencies"]/*[local-name()="dependency"]'))
foreach ($dep in $dependencias) {
    $artefato = $dep.SelectSingleNode('*[local-name()="artifactId"]').InnerText
    if ($artefato -in @('HikariCP', 'mysql-connector-j')) {
        throw "O pom.xml ainda inclui: $artefato"
    }
}

$padraoJdbc = 'java\.sql|javax\.sql|jdbc:mysql|\bMysqlSingleton\b|\bBaseDAO\b|\bHikariDataSource\b|\bDriverManager\b|\bPreparedStatement\b|\bResultSet\b'
foreach ($arquivo in @(Get-ChildItem -LiteralPath $java -Filter '*.java' -Recurse -File)) {
    if ((Get-Content -LiteralPath $arquivo.FullName -Raw) -match $padraoJdbc) {
        throw "Referencia JDBC no Web: $($arquivo.FullName)"
    }
}
foreach ($nome in @('DoadorDAO.java', 'InstituicaoDAO.java', 'DoacaoDAO.java')) {
    $arquivo = Join-Path $java (Join-Path 'dao' $nome)
    if (-not (Test-Path -LiteralPath $arquivo)) { throw "DAO ausente: $nome" }
    if ((Get-Content -LiteralPath $arquivo -Raw) -notmatch '\bApiClient\b') {
        throw "DAO nao usa ApiClient: $nome"
    }
}
Write-Host 'APROVADO: sem referencias JDBC, pool MySQL ou Controllers REST legados no Web.' -ForegroundColor Green

Push-Location $web
try {
    Write-Host 'Executando mvn clean package no projeto Web...'
    & mvn clean package
    if ($LASTEXITCODE -ne 0) { throw 'BUILD FAILED no Maven.' }

    $war = Join-Path $web 'target\doacao-alimentos.war'
    if (-not (Test-Path -LiteralPath $war -PathType Leaf)) {
        throw 'WAR do Web nao encontrado em target\doacao-alimentos.war.'
    }
    $itens = @(& jar tf $war)
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao inspecionar o WAR com jar tf.' }

    $padroesProibidos = @(
        '^WEB-INF/lib/(HikariCP|mysql-connector-j)[^/]*\.jar$',
        '^WEB-INF/classes/br/com/mvc/config/Mysql',
        '^WEB-INF/classes/br/com/mvc/dao/BaseDAO',
        '^WEB-INF/classes/br/com/mvc/controller/api/',
        '^WEB-INF/classes/br/com/mvc/util/ApiJson'
    )
    foreach ($item in $itens) {
        foreach ($padrao in $padroesProibidos) {
            if ($item -match $padrao) { throw "Artefato indevido no WAR: $item" }
        }
    }
    foreach ($nome in @('DoadorDAO', 'InstituicaoDAO', 'DoacaoDAO')) {
        $classe = "WEB-INF/classes/br/com/mvc/dao/$nome.class"
        if (-not ($itens -contains $classe)) { throw "Falta classe $nome no WAR." }
    }
    Write-Host 'APROVADO: BUILD SUCCESS e WAR sem JDBC/MySQL/Controllers REST legados.' -ForegroundColor Green
    Write-Host 'Proximo: publicar o WAR Web (8080) e testar as telas com a API (8081) iniciada.'
} finally {
    Pop-Location
}
