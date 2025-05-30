# Python Pocket IDE (PPIDE)

**Final Year Project - MCA Jul 2023 Batch**  
**Developer:** Ankit - Chandigarh University  
**Based on:** KtxPy by PsiCodes

## 🚀 Overview

Python Pocket IDE is an advanced mobile development environment for Python programming on Android. It provides a desktop-quality development experience with enhanced features including AI-powered code completion, improved terminal with arrow key support, better pip installation, project management, and modern Material 3 UI.

## ✨ Key Features

### 🔧 Enhanced Development Environment
- **Advanced Code Editor** with syntax highlighting and auto-completion
- **Enhanced Terminal** with arrow key navigation and improved pip support
- **Project Management** with organized file structure
- **Modern Material 3 UI** with dark/light theme support
- **Multi-file Editing** with tabbed interface

### 🐍 Python Runtime
- **Python 3.12** cross-compiled for Android
- **Built-in Package Manager** with enhanced pip installation
- **Interactive Python Shell** with improved REPL experience
- **Virtual Environment Support** for project isolation

### 📱 Mobile-Optimized Features
- **Touch-friendly Interface** designed for mobile development
- **Navigation Keyboard** with arrow keys and special characters
- **Gesture Support** for common editing operations
- **Responsive Layout** that adapts to different screen sizes

### 🎯 Educational Features
- **Code Samples** and tutorials for learning Python
- **Error Highlighting** with helpful suggestions
- **Documentation Integration** for quick reference
- **Learning Mode** with guided tutorials

## 🛠️ Technical Architecture

### Core Technologies
- **Kotlin** with Jetpack Compose for modern Android UI
- **Sora Editor** by Rosemoe for advanced code editing
- **Termux Libraries** for terminal emulation
- **Material Design 3** for consistent UI/UX
- **Cross-compiled Python 3.12** runtime

### Architecture Pattern
- **MVVM (Model-View-ViewModel)** for clean separation of concerns
- **Repository Pattern** for data management
- **Dependency Injection** with manual DI
- **Modular Structure** for maintainable code

## 📋 System Requirements

### Minimum Requirements
- Android 8.0 (API level 26) or higher
- 3GB RAM
- 2GB free storage space
- ARMv7 or ARM64 processor

### Recommended Requirements
- Android 12.0 (API level 31) or higher
- 4GB RAM or more
- 4GB free storage space
- ARM64 processor for optimal performance

## 🚀 Installation & Setup

### For Users
1. Download the APK from the releases section
2. Enable "Install from Unknown Sources" in Android settings
3. Install the APK
4. Launch Python Pocket IDE
5. Wait for Python environment initialization (first launch only)

### For Developers

#### Prerequisites
- Android Studio Arctic Fox or newer
- Android SDK with API level 26-34
- Android NDK 25.1.8937393
- JDK 17 or newer
- Git

#### Setup Instructions
1. **Clone the Repository**
   ```bash
   git clone https://github.com/ankit1057/ktxpy.git
   cd ktxpy/PythonPocketIDE_FYP
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the `PythonPocketIDE_FYP` directory
   - Wait for Gradle sync to complete

3. **Configure NDK**
   - Ensure NDK 25.1.8937393 is installed
   - Update `local.properties` if needed:
     ```
     ndk.dir=/path/to/android/ndk/25.1.8937393
     ```

4. **Build the Project**
   ```bash
   ./gradlew assembleDebug
   ```

5. **Run on Device/Emulator**
   - Connect Android device or start emulator
   - Click "Run" in Android Studio or use:
     ```bash
     ./gradlew installDebug
     ```

## 📁 Project Structure

```
PythonPocketIDE_FYP/
├── PPIDE_Source/                 # Main source code
│   ├── app/
│   │   ├── src/main/java/com/ankit/pythonpocketide/
│   │   │   ├── activities/       # Activity classes
│   │   │   ├── ui/              # UI components and screens
│   │   │   ├── utils/           # Utility classes
│   │   │   ├── viewModels/      # ViewModel classes
│   │   │   └── Application.kt   # Application class
│   │   ├── src/main/res/        # Resources (layouts, drawables, etc.)
│   │   └── build.gradle         # App-level build configuration
│   ├── libp7zip/               # Compression library
│   └── build.gradle            # Project-level build configuration
├── Documentation/              # Project documentation
└── README.md                  # This file
```

## 🎨 UI/UX Enhancements

### Material Design 3
- **Dynamic Color** support with system theme integration
- **Adaptive Layouts** for different screen sizes
- **Consistent Typography** with custom font families
- **Smooth Animations** and transitions

### Enhanced Editor Features
- **Syntax Highlighting** for Python and other languages
- **Code Folding** for better code organization
- **Line Numbers** with customizable display
- **Search and Replace** with regex support
- **Multiple Cursors** for efficient editing

### Terminal Improvements
- **Arrow Key Navigation** with on-screen keyboard
- **Command History** with up/down arrow support
- **Copy/Paste** functionality
- **Customizable Font Size** and colors
- **Split View** for editor and terminal

## 🔧 Development Features

### Enhanced Commands
- **Improved Pip Installation** with better error handling
- **Virtual Environment** creation and management
- **Package Management** with dependency resolution
- **Project Templates** for quick setup

### Code Quality
- **Linting Integration** with real-time error checking
- **Code Formatting** with automatic indentation
- **Import Organization** and optimization
- **Documentation Generation** support

## 🤝 Contributing

We welcome contributions to Python Pocket IDE! Here's how you can help:

### Getting Started
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Test thoroughly on different devices
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

### Development Guidelines
- Follow Kotlin coding conventions
- Use meaningful commit messages
- Add tests for new features
- Update documentation as needed
- Ensure compatibility with minimum Android version

### Code Style
- Use 4 spaces for indentation
- Follow Android Architecture Guidelines
- Use descriptive variable and function names
- Add comments for complex logic
- Keep functions small and focused

## 📄 License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE.md](LICENSE.md) file for details.

## 🙏 Acknowledgments

### Original Project
- **KtxPy** by PsiCodes (@PsiCodes) - Base project foundation
- **GitHub:** https://github.com/PsiCodes/KtxPy

### Third-Party Libraries
- **Sora Editor** by Rosemoe - Advanced code editor component
- **Termux Libraries** - Terminal emulation and shell functionality
- **p7zip Library** by hzy3774 - Compression and extraction support
- **Material Design Components** - UI components and theming

### Educational Support
- **Chandigarh University** - Academic guidance and project supervision
- **MCA Department** - Technical mentorship and resources

## 📞 Support & Contact

### Developer Contact
- **GitHub:** [@ankit1057](https://github.com/ankit1057)
- **University:** Chandigarh University
- **Program:** MCA Jul 2023 Batch

### Project Links
- **Source Code:** https://github.com/ankit1057/ktxpy
- **Issues:** https://github.com/ankit1057/ktxpy/issues
- **Discussions:** https://github.com/ankit1057/ktxpy/discussions

### Academic Information
- **Institution:** Chandigarh University
- **Department:** Computer Applications
- **Project Type:** Final Year Project (FYP)
- **Academic Year:** 2023-2024

---

**Made with ❤️ for the Python community on Android**

*This project represents the culmination of academic learning and practical application in mobile app development, bringing desktop-quality Python development to Android devices.* 