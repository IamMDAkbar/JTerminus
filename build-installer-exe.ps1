# Build Windows .exe Installer for JTerminus
# Can optionally generate icon from the app's built-in design

param(
    [Switch]$UseAppIcon = $false
)

Write-Host "Building JTerminus Windows Installer..." -ForegroundColor Green

# Clean up old distribution
if (Test-Path ".\dist") {
    Write-Host "Cleaning previous build..." -ForegroundColor Yellow
    Remove-Item ".\dist" -Recurse -Force -ErrorAction SilentlyContinue
    Start-Sleep -Milliseconds 500  # Wait for filesystem to sync
}

# Also clean installer output
if (Test-Path ".\installer-output") {
    Remove-Item ".\installer-output" -Recurse -Force -ErrorAction SilentlyContinue
    Start-Sleep -Milliseconds 500
}

# Step 1: Build the fat JAR with Maven
Write-Host "`n[1/3] Building JAR with Maven..." -ForegroundColor Yellow
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Error "Maven build failed!"
    exit 1
}

Write-Host "[OK] JAR build completed" -ForegroundColor Green

# Step 2: Create distribution folder with runtime
Write-Host "`n[2/3] Creating distribution package with embedded Java runtime..." -ForegroundColor Yellow

$DIST_DIR = ".\dist\JTerminus"
$JAVA_HOME = $env:JAVA_HOME

# Create directory structure
New-Item -ItemType Directory -Force -Path "$DIST_DIR\lib" | Out-Null

# Create minimal Java runtime
Write-Host "Creating embedded Java runtime..." -ForegroundColor Cyan
& "$JAVA_HOME\bin\jlink.exe" `
    --module-path "$JAVA_HOME\jmods" `
    --add-modules java.base,java.desktop,java.prefs,java.logging `
    --output "$DIST_DIR\runtime" `
    --strip-debug `
    --compress=2 2>&1 | Where-Object { $_ -notmatch "deprecated" }

# Copy the JAR (shaded JAR is renamed to the original name by Maven)
Copy-Item ".\target\jterminus-1.0.0.jar" "$DIST_DIR\lib\jterminus-1.0.0.jar"

# Handle icon selection
if ($UseAppIcon) {
    Write-Host "Generating icon from JTerminus app design..." -ForegroundColor Cyan
    
    # Create a simple PowerShell-based icon generator
    # This creates a 256x256 PNG that can be converted to ICO
    $IconGenScript = @'
Add-Type -AssemblyName System.Drawing

$size = 256
$bitmap = New-Object System.Drawing.Bitmap($size, $size)
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)

# Smooth rendering
$graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
$graphics.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAlias

# Gradient background (Cyan to Blue - matching the app)
$brush = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    (New-Object System.Drawing.Point(0, 0)),
    (New-Object System.Drawing.Point($size, $size)),
    [System.Drawing.Color]::FromArgb(0x94, 0xE2, 0xD5),   # Cyan
    [System.Drawing.Color]::FromArgb(0x89, 0xB4, 0xFA)    # Blue
)

# Draw rounded rectangle
$margin = 16
$graphics.FillRectangle($brush, $margin, $margin, $size - 2*$margin, $size - 2*$margin)

# Draw terminal prompt ">_"
$font = New-Object System.Drawing.Font("Courier New", 120, [System.Drawing.FontStyle]::Bold)
$textBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(0x1E, 0x1E, 0x2E))
$format = New-Object System.Drawing.StringFormat
$format.Alignment = [System.Drawing.StringAlignment]::Center
$format.LineAlignment = [System.Drawing.StringAlignment]::Center

$rect = New-Object System.Drawing.RectangleF(0, 0, $size, $size)
$graphics.DrawString(">_", $font, $textBrush, $rect, $format)

# Save as PNG
[System.Drawing.Image]$image = $bitmap
$image.Save("jterminus-app.png", [System.Drawing.Imaging.ImageFormat]::Png)
$graphics.Dispose()
$bitmap.Dispose()

Write-Host "[OK] App icon saved as: jterminus-app.png" -ForegroundColor Green
'@

    $IconGenScript | Out-File "$DIST_DIR\generate-icon.ps1" -Encoding UTF8
    Invoke-Expression -Command $IconGenScript
    
    # Check if ImageMagick is available for direct ICO conversion
    $magick = Get-Command magick -ErrorAction SilentlyContinue
    if ($magick) {
        Write-Host "Converting PNG to ICO using ImageMagick..." -ForegroundColor Cyan
        & magick jterminus-app.png -define icon:auto-resize=256,128,96,64,48,32,16 jterminus-app.ico
        if (Test-Path "jterminus-app.ico") {
            Copy-Item "jterminus-app.ico" "$DIST_DIR\jterminus.ico"
            Remove-Item "jterminus-app.png" -Force
            Write-Host "[OK] App icon (ICO) created successfully" -ForegroundColor Green
        }
    }
    else {
        # No ImageMagick, keep PNG and provide conversion info
        Copy-Item "jterminus-app.png" "$DIST_DIR\jterminus.png"
        Write-Host "[WARNING] ImageMagick not found - saved as PNG instead" -ForegroundColor Yellow
        Write-Host "To convert PNG to ICO:" -ForegroundColor Cyan
        Write-Host "  Option 1: Install ImageMagick (https://imagemagick.org)" -ForegroundColor Gray
        Write-Host "  Option 2: Use online converter (https://convertio.co/png-ico/)" -ForegroundColor Gray
        Write-Host "  Option 3: Use fallback icon" -ForegroundColor Gray
        
        # Fall back to existing icon if available
        if (Test-Path ".\jterminus.ico") {
            Copy-Item ".\jterminus.ico" "$DIST_DIR\jterminus.ico"
            Write-Host "[INFO] Using existing jterminus.ico instead" -ForegroundColor Cyan
        }
    }
}
else {
    # Use existing logo/icon
    if (Test-Path ".\jterminus.ico") {
        Copy-Item ".\jterminus.ico" "$DIST_DIR\jterminus.ico"
        Write-Host "[OK] Using existing custom icon from: jterminus.ico" -ForegroundColor Green
    }
    elseif (Test-Path ".\src\main\resources\icons\app-icon.ico") {
        Copy-Item ".\src\main\resources\icons\app-icon.ico" "$DIST_DIR\jterminus.ico"
        Write-Host "[OK] Using app icon from resources: app-icon.ico" -ForegroundColor Green
    }
    elseif (Test-Path ".\logo.ico") {
        Copy-Item ".\logo.ico" "$DIST_DIR\jterminus.ico"
        Write-Host "[OK] Using existing logo.ico" -ForegroundColor Green
    }
    else {
        Write-Host "[WARNING] No icon file found - installer will use defaults" -ForegroundColor Yellow
    }
}

# Create VBScript launcher (runs without console window)
$LauncherVBS = @'
Set objShell = CreateObject("WScript.Shell")
Set objFSO = CreateObject("Scripting.FileSystemObject")

' Get the script directory
strScriptPath = WScript.ScriptFullName
strScriptDir = objFSO.GetParentFolderName(strScriptPath)

' Build command with full paths
strJavaPath = strScriptDir & "\runtime\bin\java.exe"
strJarPath = strScriptDir & "\lib\jterminus-1.0.0.jar"

' Run Java silently (0 = hidden window, False = don't wait)
strCommand = """" & strJavaPath & """ -jar """ & strJarPath & """"
objShell.Run strCommand, 0, False
'@

$LauncherVBS | Out-File "$DIST_DIR\launch.vbs" -Encoding ASCII

# Create README
$README = @'
# JTerminus v1.0.0 - Installation Complete!

Thank you for installing JTerminus, a multi-shell CLI desktop application!

## Running the Application

- **Start Menu**: Look for "JTerminus" in your Start Menu
- **Desktop Shortcut**: Click the JTerminus icon on your desktop
- **Command Line**: You can also run: java -jar "C:\Program Files\JTerminus\lib\jterminus-1.0.0.jar"

## Features

- Multi-shell CLI support
- Modern FlatLaf UI with IntelliJ themes
- Lightweight and fast
- Built with Java Swing

## Uninstalling

1. Go to Control Panel
2. Click "Programs and Features"
3. Find "JTerminus 1.0.0" in the list
4. Click "Uninstall"

## Support

For issues and updates, visit: https://example.com

Enjoy using JTerminus!
'@

$README | Out-File "$DIST_DIR\README.txt" -Encoding UTF8

Write-Host "[OK] Distribution package created" -ForegroundColor Green

# Step 3: Build NSIS installer
Write-Host "`n[3/3] Building Windows installer (.exe)..." -ForegroundColor Yellow

# Check if NSIS is installed
$NSIS_PATH = "C:\Program Files (x86)\NSIS\makensis.exe"
$NSIS_PATH_ALT = "C:\Program Files\NSIS\makensis.exe"

if (Test-Path $NSIS_PATH) {
    $NSIS_EXE = $NSIS_PATH
} elseif (Test-Path $NSIS_PATH_ALT) {
    $NSIS_EXE = $NSIS_PATH_ALT
} else {
    Write-Error "NSIS not found! Please install from: https://nsis.sourceforge.io/Download"
    Write-Host "After installation, run this script again." -ForegroundColor Yellow
    exit 1
}

# Create output directory
New-Item -ItemType Directory -Force -Path ".\installer-output" | Out-Null

# Check if custom icon exists
$iconPath = ".\jterminus.ico"
if (Test-Path $iconPath) {
    Write-Host "Using custom icon: $iconPath" -ForegroundColor Cyan
    & "$NSIS_EXE" /DOUTDIR=".\installer-output" /DICON_FILE="jterminus.ico" ".\installer.nsi"
} else {
    Write-Host "No custom icon found (using defaults)" -ForegroundColor Gray
    & "$NSIS_EXE" /DOUTDIR=".\installer-output" ".\installer.nsi"
}

if ($LASTEXITCODE -eq 0) {
    Write-Host "[OK] Installer created successfully!" -ForegroundColor Green
    Write-Host "Location: .\installer-output\JTerminus-1.0.0-installer.exe" -ForegroundColor Cyan
    Write-Host "`nYou can now distribute this single .exe file to users!" -ForegroundColor Yellow
    Write-Host "Users simply need to double-click it to install the app." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Icon being used:" -ForegroundColor Cyan
    if ($UseAppIcon) {
        Write-Host "  - Installer: Generated from JTerminus app design (gradient cyan-to-blue with >_ symbol)" -ForegroundColor Green
    } else {
        Write-Host "  - Installer: Custom icon from jterminus.ico" -ForegroundColor Green
    }
    Write-Host "  - App Window: Loaded from src/main/resources/icons/app-icon.ico" -ForegroundColor Green
}
else {
    Write-Error "NSIS build failed!"
    exit 1
}
