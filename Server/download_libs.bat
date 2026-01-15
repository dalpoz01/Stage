# Script PowerShell per scaricare le librerie BIRT
# Esegui con: powershell -ExecutionPolicy Bypass -File download-birt-libs.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Download Librerie BIRT" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Repository Maven
$mavenRepo = "https://repo1.maven.org/maven2"

# Crea directory lib se non esiste
if (-not (Test-Path "lib")) {
    New-Item -ItemType Directory -Path "lib" | Out-Null
}

# Funzione per scaricare un file
function Download-Maven-Jar {
    param (
        [string]$groupId,
        [string]$artifactId,
        [string]$version
    )
    
    $groupPath = $groupId -replace '\.','\/'
    $jarName = "$artifactId-$version.jar"
    $url = "$mavenRepo/$groupPath/$artifactId/$version/$jarName"
    $output = "lib\$jarName"
    
    if (Test-Path $output) {
        Write-Host "[SKIP] $jarName (già presente)" -ForegroundColor Yellow
        return
    }
    
    Write-Host "[DOWN] $jarName ..." -ForegroundColor Green
    
    try {
        Invoke-WebRequest -Uri $url -OutFile $output -ErrorAction Stop
        Write-Host "[OK]   $jarName" -ForegroundColor Green
    } catch {
        Write-Host "[FAIL] $jarName - $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "Download in corso..." -ForegroundColor Yellow
Write-Host ""

# BIRT Runtime principale
Write-Host "BIRT Runtime:" -ForegroundColor Cyan
Download-Maven-Jar "org.eclipse.birt.runtime" "org.eclipse.birt.runtime" "4.13.0"

# Logging (necessario per BIRT)
Write-Host ""
Write-Host "Logging:" -ForegroundColor Cyan
Download-Maven-Jar "org.slf4j" "slf4j-api" "1.7.36"
Download-Maven-Jar "org.slf4j" "slf4j-simple" "1.7.36"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Download Completato!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "NOTA: Potrebbero essere necessarie altre dipendenze BIRT" -ForegroundColor Yellow
Write-Host "specifiche per il tuo progetto. Controlla la documentazione" -ForegroundColor Yellow
Write-Host "BIRT per le librerie aggiuntive." -ForegroundColor Yellow
Write-Host ""
Write-Host "Directory lib contiene ora:" -ForegroundColor Green
Get-ChildItem "lib\*.jar" | ForEach-Object { Write-Host "  - $($_.Name)" -ForegroundColor Gray }
Write-Host ""

Read-Host "Premi INVIO per uscire"