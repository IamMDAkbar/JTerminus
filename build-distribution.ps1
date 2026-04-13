# Simple distribution script WITHOUT installer (uses folder + batch launcher)

Write-Host "Building JTerminus distribution..." -ForegroundColor Green

# Clean up old distribution
if (Test-Path ".\dist") {
    Write-Host "Cleaning previous build..." -ForegroundColor Yellow
    Remove-Item ".\dist" -Recurse -Force
}

# Step 1: Build the fat JAR with Maven
Write-Host "`n[1/2] Building JAR with Maven..." -ForegroundColor Yellow
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Error "Maven build failed!"
    exit 1
}

Write-Host "[OK] JAR build completed" -ForegroundColor Green

# Step 2: Create distribution folder
Write-Host "`n[2/2] Creating distribution package..." -ForegroundColor Yellow

$DIST_DIR = ".\dist\JTerminus"
$JAVA_HOME = $env:JAVA_HOME

# Create directory structure
New-Item -ItemType Directory -Force -Path "$DIST_DIR\lib" | Out-Null
New-Item -ItemType Directory -Force -Path "$DIST_DIR\runtime" | Out-Null

# Copy the shaded JAR (the main JAR is the shaded one after shade plugin runs)
Copy-Item ".\target\jterminus-1.0.0.jar" "$DIST_DIR\lib\"

# Create minimal Java runtime
Write-Host "Creating embedded Java runtime..." -ForegroundColor Cyan
& "$JAVA_HOME\bin\jlink.exe" `
    --module-path "$JAVA_HOME\jmods" `
    --add-modules java.base,java.desktop,java.prefs,java.logging `
    --output "$DIST_DIR\runtime" `
    --strip-debug `
    --compress=2

# Create launcher batch file
$BATCH_CONTENT = @'
@echo off
REM JTerminus Launcher
set JAVA_HOME=%~dp0runtime
set JAR_PATH=%~dp0lib\jterminus-1.0.0.jar
"%JAVA_HOME%\bin\java.exe" -jar "%JAR_PATH%" %*
'@

$BATCH_CONTENT | Out-File "$DIST_DIR\JTerminus.bat" -Encoding ASCII

# Create README
$README = @'
# JTerminus - Portable Application

## Installation & Running

Double-click **JTerminus.bat** to launch the application.

Everything you need is included in this folder:
- `JTerminus.bat` - Launcher script
- `lib/` - Application files
- `runtime/` - Private Java Runtime (no external Java needed)

## System Requirements

Windows 7 or later
''~50 MB disk space

## Troubleshooting

If it doesn't start:
1. Make sure all files are in the same folder structure
2. Try running from Command Prompt for error details:
   Command Prompt > cd to this folder > type: JTerminus.bat
'@

$README | Out-File "$DIST_DIR\README.txt" -Encoding UTF8

Write-Host "[OK] Distribution created!" -ForegroundColor Green
Write-Host "Location: .\dist\JTerminus\" -ForegroundColor Cyan
Write-Host "`nTo share this app, just ZIP the folding and distribute!" -ForegroundColor Yellow
Write-Host "Users simply extract and double-click JTerminus.bat" -ForegroundColor Yellow
