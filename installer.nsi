; JTerminus Installer Script (NSIS)

!include "MUI2.nsh"

; Constants
!define APP_NAME "JTerminus"
!define APP_VERSION "1.0.0"
!define APP_PUBLISHER "JTerminus"
!define APP_URL "https://example.com"
!define APP_DESCRIPTION "A multi-shell CLI desktop application built with Java Swing and FlatLaf"
!define INSTALL_DIR "$PROGRAMFILES\JTerminus"
!define UNINSTALL_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\JTerminus"

; Output file
OutFile "installer-output\JTerminus-1.0.0-installer.exe"
InstallDir "${INSTALL_DIR}"

; Icon support - for window title bar and installer
!ifdef ICON_FILE
!define MUI_ICON "${ICON_FILE}"
!else
; Default behavior without icon
!endif

; Installer settings (must be after icon definition)
Name "${APP_NAME} ${APP_VERSION}"
Caption "${APP_NAME} ${APP_VERSION} - Installation Wizard"
BrandingText "${APP_NAME} ${APP_VERSION} by ${APP_PUBLISHER}"

; MUI Settings - MUST be after MUI_ICON definition
!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_RIGHT
!define MUI_WELCOMEFINISHPAGE_BITMAP_NOSTRETCH
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_LANGUAGE "English"

; Installer sections
Section "Install ${APP_NAME}"
    SetOutPath "${INSTALL_DIR}"
    
    ; Copy JAR file (hidden in lib subfolder)
    SetOutPath "${INSTALL_DIR}\lib"
    File "dist\JTerminus\lib\jterminus-1.0.0.jar"
    
    ; Copy entire runtime directory (hidden)
    SetOutPath "${INSTALL_DIR}\runtime"
    File /r "dist\JTerminus\runtime\*.*"
    
    ; Copy README and launcher
    SetOutPath "${INSTALL_DIR}"
    File "dist\JTerminus\README.txt"
    File "dist\JTerminus\launch.vbs"
    File "dist\JTerminus\jterminus.ico"
    
    ; Create Start Menu shortcuts (with icon, no parameters needed)
    CreateDirectory "$SMPROGRAMS\${APP_NAME}"
    CreateShortCut "$SMPROGRAMS\${APP_NAME}\${APP_NAME}.lnk" "${INSTALL_DIR}\launch.vbs" "" "${INSTALL_DIR}\jterminus.ico"
    CreateShortCut "$SMPROGRAMS\${APP_NAME}\Uninstall ${APP_NAME}.lnk" "$INSTDIR\uninstall.exe"
    CreateShortCut "$SMPROGRAMS\${APP_NAME}\README.lnk" "$INSTDIR\README.txt"
    
    ; Create Desktop shortcut (with icon, no parameters needed)
    CreateShortCut "$DESKTOP\${APP_NAME}.lnk" "${INSTALL_DIR}\launch.vbs" "" "${INSTALL_DIR}\jterminus.ico"
    
    ; Create uninstaller
    WriteUninstaller "$INSTDIR\uninstall.exe"
    
    ; Write registry entries for Add/Remove Programs
    WriteRegStr HKLM "${UNINSTALL_KEY}" "DisplayName" "${APP_NAME} ${APP_VERSION}"
    WriteRegStr HKLM "${UNINSTALL_KEY}" "UninstallString" "$INSTDIR\uninstall.exe"
    WriteRegStr HKLM "${UNINSTALL_KEY}" "DisplayVersion" "${APP_VERSION}"
    WriteRegStr HKLM "${UNINSTALL_KEY}" "Publisher" "${APP_PUBLISHER}"
    WriteRegStr HKLM "${UNINSTALL_KEY}" "URLInfoAbout" "${APP_URL}"
    WriteRegStr HKLM "${UNINSTALL_KEY}" "Comments" "${APP_DESCRIPTION}"
    WriteRegStr HKLM "${UNINSTALL_KEY}" "InstallLocation" "${INSTALL_DIR}"
    WriteRegStr HKLM "${UNINSTALL_KEY}" "DisplayIcon" "${INSTALL_DIR}\jterminus.ico"
    
    ; Show success message
    MessageBox MB_OK "${APP_NAME} has been installed successfully!$\r$\nYou can launch it from your Start Menu or Desktop shortcut."
    
SectionEnd

; Uninstaller section
Section "Uninstall"
    ; Remove Start Menu shortcuts
    RMDir /r "$SMPROGRAMS\${APP_NAME}"
    
    ; Remove Desktop shortcut
    Delete "$DESKTOP\${APP_NAME}.lnk"
    
    ; Remove installation files
    RMDir /r "${INSTALL_DIR}"
    
    ; Remove registry entries
    DeleteRegKey HKLM "${UNINSTALL_KEY}"
    
    MessageBox MB_OK "${APP_NAME} has been uninstalled successfully."
    
SectionEnd
