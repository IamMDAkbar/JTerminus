# Build script for creating Windows .exe installer for JTerminus

Write-Host "Building JTerminus installer..." -ForegroundColor Green

# Step 1: Build the fat JAR with Maven
Write-Host "`n[1/3] Building JAR with Maven..." -ForegroundColor Yellow
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Error "Maven build failed!"
    exit 1
}

Write-Host "[OK] JAR build completed" -ForegroundColor Green

# Step 2: Create minimal Java runtime with jlink
Write-Host "`n[2/3] Creating minimal Java runtime..." -ForegroundColor Yellow

$JAVA_HOME = $env:JAVA_HOME
$JMODS = "$JAVA_HOME\jmods"
$RUNTIME_OUTPUT = ".\target\java-runtime"

& "$JAVA_HOME\bin\jlink.exe" `
    --module-path "$JMODS" `
    --add-modules java.base,java.desktop,java.prefs,java.logging `
    --output "$RUNTIME_OUTPUT" `
    --strip-debug `
    --compress=2

if ($LASTEXITCODE -ne 0) {
    Write-Error "jlink failed!"
    exit 1
}

Write-Host "[OK] Java runtime created" -ForegroundColor Green

# Step 3: Create Windows installer with jpackage
Write-Host "`n[3/3] Creating Windows installer..." -ForegroundColor Yellow

New-Item -ItemType Directory -Force -Path ".\target\installer" | Out-Null

& "$JAVA_HOME\bin\jpackage.exe" `
    --input ".\target" `
    --name "JTerminus" `
    --main-jar "jterminus-1.0.0-shaded.jar" `
    --main-class "com.jterminus.App" `
    --type "msi" `
    --runtime-image "$RUNTIME_OUTPUT" `
    --dest ".\target\installer" `
    --vendor "JTerminus" `
    --description "A multi-shell CLI desktop application built with Java Swing and FlatLaf" `
    --win-menu `
    --win-shortcut

if ($LASTEXITCODE -ne 0) {
    Write-Error "jpackage failed!"
    exit 1
}

Write-Host "`n[OK] Installer created successfully!" -ForegroundColor Green
Write-Host "Location: .\target\installer\JTerminus-1.0.0.msi" -ForegroundColor Cyan
