# JTerminus - Multi-Shell CLI Terminal

![JTerminus Logo](https://img.shields.io/badge/JTerminus-v1.0.0-blue?style=for-the-badge&logo=java)
![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=openjdk)
![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-green?style=for-the-badge&logo=windows)
![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge&logo=maven)

![JTerminus Screenshot]([https://via.placeholder.com/800x500/1E1E2E/CDD6F4?text=JTerminus+Multi-Shell+Terminal+Interface](https://res.cloudinary.com/dstx1zcsk/image/upload/v1776098409/t0ez3xazilb1hrrlncfk.png))

A powerful multi-shell CLI desktop application built with Java Swing and FlatLaf, providing a modern terminal experience with support for multiple shell environments.

## Overview

JTerminus is a cross-platform terminal emulator that brings together the best of multiple shell environments in a single, elegant interface. Whether you prefer Linux commands, Windows CMD, PowerShell, or RedHat shell syntax, JTerminus has you covered.

## Why Choose JTerminus?

### 🎓 **Perfect for Learning & Education**
- **Students**: Practice Linux, Unix, and shell commands without needing multiple operating systems
- **Educational Institutions**: Ideal for computer science labs and programming courses
- **Safe Learning Environment**: Experiment with commands in a controlled, risk-free setting
- **Command Mastery**: Build proficiency in multiple shell environments simultaneously

### 💻 **Lightweight & Accessible**
- **Low Resource Usage**: Runs smoothly on older hardware and low-spec computers
- **No Virtualization Required**: Skip the overhead of virtual machines or dual-boot setups
- **Instant Access**: Launch immediately without complex setup procedures
- **Portable**: Run from USB drives or any storage device

### 🏫 **Educational Benefits**
- **Cost-Effective**: Free alternative to expensive virtualization software
- **Unified Interface**: Learn multiple command-line interfaces in one application
- **Cross-Platform Skills**: Prepare students for diverse IT environments
- **Classroom Friendly**: Easy deployment across school computer labs

### 🚀 **Professional Development**
- **Multi-Shell Proficiency**: Master Linux, PowerShell, CMD, and RedHat commands
- **Career Readiness**: Gain practical skills for DevOps, system administration, and development
- **Flexible Learning**: Switch between shells to match different job requirements
- **Command Reference**: Built-in help system for quick command lookup

### 🌟 **Key Advantages**
- **Resource Efficient**: Perfect for schools with limited IT budgets
- **No Installation Hassles**: Portable version works without admin rights
- **Cross-Platform**: Same experience on Windows, macOS, and Linux
- **Modern Interface**: Clean, intuitive design with dark theme for reduced eye strain
- **Extensible**: Easy to add new commands and shell adapters

## Features

- **Multi-Shell Support**: Switch between Linux, CMD, PowerShell, and RedHat shells seamlessly
- **Modern UI**: Built with Java Swing and FlatLaf for a beautiful dark theme interface
- **Rich Command Set**: 30+ built-in commands for file operations, text processing, and system utilities
- **Command History**: Navigate through your command history with arrow keys
- **Font Customization**: Adjust font size dynamically for better readability
- **Cross-Platform**: Runs on Windows, macOS, and Linux (requires Java 17+)
- **Portable Distribution**: Self-contained with embedded Java runtime

## System Requirements

- **Java**: Java 17 or higher (for development)
- **Operating System**: Windows 7+, macOS 10.14+, or Linux
- **Memory**: Minimum 512MB RAM
- **Disk Space**: 50MB for installation

## Installation

### Option 1: Download Pre-built Distribution

1. Download the latest release from the releases page
2. Extract the ZIP file to your desired location
3. Double-click `JTerminus.bat` (Windows) or `JTerminus.sh` (Linux/macOS) to launch

### Option 2: Build from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/IamMDAkbar/JTerminus.git
   cd JTerminus
   ```

2. Build the application:
   ```bash
   mvn clean package -DskipTests
   ```

3. Run the application:
   ```bash
   java -jar target/jterminus-1.0.0.jar
   ```

### Option 3: Create Distribution Package

Run the PowerShell script to create a portable distribution:
```powershell
.\build-distribution.ps1
```

This creates a self-contained package in the `dist/` folder with an embedded Java runtime.

## Available Commands

### File Operations
- **ls** - List directory contents
- **cd** - Change directory
- **pwd** - Print working directory
- **mkdir** - Create directory
- **rmdir** - Remove empty directory
- **touch** - Create empty file
- **rm** - Remove files or directories
- **cp** - Copy files or directories
- **mv** - Move or rename files
- **cat** - Display file contents
- **ln** - Create symbolic links

### Text Processing
- **grep** - Search text patterns
- **head** - Display first lines of file
- **tail** - Display last lines of file
- **cut** - Extract columns from text
- **tr** - Translate or delete characters
- **sort** - Sort text lines
- **uniq** - Remove duplicate lines
- **wc** - Count lines, words, and characters
- **diff** - Compare files

### System Information
- **date** - Display current date and time
- **whoami** - Display current user
- **hostname** - Display system hostname
- **which** - Locate command in PATH
- **echo** - Display text messages

### Utilities
- **clear** - Clear terminal screen
- **history** - Display command history
- **find** - Search for files
- **exit** - Exit application
- **help** - Show available commands and usage

## Usage

### Basic Usage

1. Launch JTerminus
2. Select your preferred shell from the dropdown menu
3. Type commands in the terminal input area
4. Press Enter to execute commands
5. Use arrow keys to navigate command history

### Keyboard Shortcuts

- **Enter** - Execute command
- **Up/Down** - Navigate command history
- **Ctrl+C** - Cancel current input
- **Ctrl+L** - Clear terminal
- **Ctrl+Q** - Exit application
- **Ctrl+=** - Increase font size
- **Ctrl+-** - Decrease font size
- **Ctrl+0** - Reset font size
- **Home** - Move cursor to start of input

### Shell Switching

JTerminus supports multiple shell environments:

- **Linux**: Standard Linux/Unix command syntax
- **CMD**: Windows Command Prompt syntax
- **PowerShell**: Windows PowerShell syntax
- **RedHat**: RedHat Enterprise Linux specific commands

Switch between shells using the dropdown menu or the Shell menu.

## Development

### Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── jterminus/
│               ├── App.java                 # Main entry point
│               ├── commands/                # Built-in command implementations
│               ├── core/                    # Core functionality
│               ├── shell/                   # Shell adapters
│               └── ui/                      # User interface components
```

### Building

The project uses Maven for build management:

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Create JAR
mvn package

# Create distribution
mvn clean package -DskipTests
```

### Dependencies

- **FlatLaf**: Modern Look and Feel for Swing
- **FlatLaf IntelliJ Themes**: Additional theme support
- **Java 17**: Minimum Java version

## Creating Installer

To create a Windows installer:

1. Build the distribution first:
   ```powershell
   .\build-distribution.ps1
   ```

2. Create the installer:
   ```powershell
   .\build-installer.ps1
   ```

This will create `JTerminus-1.0.0-installer.exe` in the `installer-output/` directory.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Copyright

Copyright © 2025 JTerminus. All rights reserved.

## Support

For issues, questions, or feature requests:

1. Check the [Issues](https://github.com/IamMDAkbar/JTerminus/issues) page
2. Create a new issue with detailed information
3. Join our community discussions

## Changelog

### Version 1.0.0
- Initial release
- Multi-shell support (Linux, CMD, PowerShell, RedHat)
- 30+ built-in commands
- Modern FlatLaf dark theme UI
- Command history and navigation
- Font size customization
- Cross-platform support
- Portable distribution with embedded Java runtime

---

## 🏷️ Repository Tags

![Java Swing](https://img.shields.io/badge/Java%20Swing-UI%20Framework-red?style=flat-square&logo=java)
![FlatLaf](https://img.shields.io/badge/FlatLaf-Modern%20UI-9cf?style=flat-square)
![Terminal](https://img.shields.io/badge/Terminal-CLI%20Tool-ff69b4?style=flat-square&logo=terminal)
![Multi-Shell](https://img.shields.io/badge/Multi--Shell-Linux%20%7C%20CMD%20%7C%20PowerShell-00add8?style=flat-square)
![Educational](https://img.shields.io/badge/Educational-Student%20Friendly-yellow?style=flat-square&logo=education)
![Open Source](https://img.shields.io/badge/Open%20Source-Community%20Driven-success?style=flat-square&logo=open-source-initiative)
![Maven](https://img.shields.io/badge/Maven-Build%20Tool-red?style=flat-square&logo=apache-maven)
![Cross-Platform](https://img.shields.io/badge/Cross--Platform-Windows%20%7C%20macOS%20%7C%20Linux-blue?style=flat-square)

---

**JTerminus** - Your terminal, your way. 🚀
