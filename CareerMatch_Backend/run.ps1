# Auto-refresh PATH so newly installed JDK is detected in the current terminal
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

# If java or javac still not found in PATH, look in standard install locations
$javaCmd = "java"
$javacCmd = "javac"

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    $foundJava = Get-ChildItem "C:\Program Files\Microsoft\jdk*\bin\java.exe" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($foundJava) { $javaCmd = $foundJava.FullName }
}

if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    $foundJavac = Get-ChildItem "C:\Program Files\Microsoft\jdk*\bin\javac.exe" -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($foundJavac) { $javacCmd = $foundJavac.FullName }
}

$m2 = "$env:USERPROFILE\.m2\repository"
$sqlite = "$m2\org\xerial\sqlite-jdbc\3.46.1.0\sqlite-jdbc-3.46.1.0.jar"
$slf4j = "$m2\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar"
$cp = "target\classes;$sqlite;$slf4j"

if (-not (Test-Path "target\classes")) {
    New-Item -ItemType Directory -Path "target\classes" -Force | Out-Null
}

Write-Host "Compiling CareerMatch Java source files..." -ForegroundColor Cyan
$sources = (Get-ChildItem -Recurse -Filter *.java src\main\java).FullName
& $javacCmd -encoding UTF-8 -cp $cp -d target\classes $sources

Write-Host "Launching CareerMatch Desktop Platform..." -ForegroundColor Green
& $javaCmd -cp $cp com.careermatch.Main
