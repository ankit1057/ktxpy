# Python Pocket IDE - Build Guide

This guide provides detailed instructions for building Python Pocket IDE from source code.

## 🛠️ Prerequisites

### Required Software
1. **Android Studio** (Arctic Fox 2020.3.1 or newer)
2. **Android SDK** with the following components:
   - Android SDK Platform 26 (minimum)
   - Android SDK Platform 34 (target)
   - Android SDK Build-Tools 34.0.0
3. **Android NDK** version 25.1.8937393 (exact version required)
4. **Java Development Kit (JDK)** 17 or newer
5. **Git** for version control

### System Requirements
- **Operating System:** Windows 10/11, macOS 10.14+, or Ubuntu 18.04+
- **RAM:** 8GB minimum, 16GB recommended
- **Storage:** 10GB free space for Android SDK and project
- **Internet Connection:** Required for downloading dependencies

## 📥 Setup Instructions

### 1. Install Android Studio
1. Download Android Studio from [developer.android.com](https://developer.android.com/studio)
2. Install with default settings
3. Launch Android Studio and complete the setup wizard
4. Install additional SDK components through SDK Manager

### 2. Configure Android SDK
1. Open Android Studio
2. Go to **File → Settings → Appearance & Behavior → System Settings → Android SDK**
3. Install the following SDK Platforms:
   - Android 8.0 (API level 26)
   - Android 14 (API level 34)
4. Switch to **SDK Tools** tab and install:
   - Android SDK Build-Tools 34.0.0
   - NDK (Side by side) version 25.1.8937393
   - CMake 3.22.1

### 3. Clone the Repository
```bash
git clone https://github.com/ankit1057/ktxpy.git
cd ktxpy/PythonPocketIDE_FYP
```

### 4. Configure Local Properties
Create or update `local.properties` file in the project root:
```properties
sdk.dir=/path/to/Android/Sdk
ndk.dir=/path/to/Android/Sdk/ndk/25.1.8937393
```

**Platform-specific paths:**
- **Windows:** `C:\\Users\\[username]\\AppData\\Local\\Android\\Sdk`
- **macOS:** `/Users/[username]/Library/Android/sdk`
- **Linux:** `/home/[username]/Android/Sdk`

## 🔨 Building the Project

### Method 1: Using Android Studio (Recommended)
1. Open Android Studio
2. Select **File → Open** and navigate to `PythonPocketIDE_FYP` directory
3. Wait for Gradle sync to complete (may take several minutes on first run)
4. Select build variant: **Build → Select Build Variant**
   - Choose `arch_arm64Debug` for ARM64 devices (recommended)
   - Choose `arch_arm32Debug` for older ARM devices
5. Build the project: **Build → Make Project** (Ctrl+F9)
6. Generate APK: **Build → Build Bundle(s) / APK(s) → Build APK(s)**

### Method 2: Using Command Line
```bash
# Navigate to project directory
cd PythonPocketIDE_FYP

# Make gradlew executable (Linux/macOS)
chmod +x gradlew

# Clean and build debug APK
./gradlew clean assembleDebug

# Build specific architecture (recommended for faster builds)
./gradlew assembleArch_arm64Debug

# Build release APK (requires signing configuration)
./gradlew assembleRelease
```

### Build Variants
The project supports multiple CPU architectures:
- **arch_arm64:** For ARM64 devices (most modern Android devices)
- **arch_arm32:** For ARMv7 devices (older Android devices)
- **arch_x86:** For x86 emulators and Intel-based devices
- **arch_x86_64:** For x86_64 emulators

## 📱 Installation and Testing

### Install on Device
```bash
# Install debug APK
./gradlew installArch_arm64Debug

# Or install manually
adb install app/build/outputs/apk/arch_arm64/debug/app-arch_arm64-debug.apk
```

### Run Tests
```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device)
./gradlew connectedAndroidTest
```

## 🐛 Troubleshooting

### Common Issues and Solutions

#### 1. NDK Version Mismatch
**Error:** `NDK version mismatch`
**Solution:** 
- Ensure NDK 25.1.8937393 is installed
- Update `local.properties` with correct NDK path
- Clean and rebuild project

#### 2. Gradle Sync Failed
**Error:** `Gradle sync failed`
**Solution:**
- Check internet connection
- Clear Gradle cache: `./gradlew clean`
- Invalidate caches: **File → Invalidate Caches and Restart**

#### 3. Out of Memory Error
**Error:** `OutOfMemoryError during build`
**Solution:**
- Increase heap size in `gradle.properties`:
  ```properties
  org.gradle.jvmargs=-Xmx4096M -Dkotlin.daemon.jvm.options="-Xmx4096M"
  ```

#### 4. Missing Dependencies
**Error:** `Could not resolve dependencies`
**Solution:**
- Check internet connection
- Clear Gradle cache: `rm -rf ~/.gradle/caches`
- Sync project again

#### 5. Build Tools Version Error
**Error:** `Build tools version not found`
**Solution:**
- Install required build tools through SDK Manager
- Update `build.gradle` with available version

### Performance Optimization
- **Use Gradle Daemon:** Add `org.gradle.daemon=true` to `gradle.properties`
- **Enable Parallel Builds:** Add `org.gradle.parallel=true`
- **Increase Memory:** Adjust JVM heap size as shown above
- **Use Build Cache:** Add `org.gradle.caching=true`

## 🔧 Development Setup

### IDE Configuration
1. **Code Style:** Import Android code style settings
2. **Plugins:** Install recommended plugins:
   - Kotlin
   - Android APK Support
   - Material Theme UI
3. **Emulator:** Create ARM64 emulator for testing

### Debugging
1. **Enable USB Debugging** on your Android device
2. **Connect Device:** Use `adb devices` to verify connection
3. **Run Debug Build:** Use Android Studio's debug configuration
4. **Logs:** Monitor logs using `adb logcat` or Android Studio's Logcat

### Code Quality Tools
```bash
# Run lint checks
./gradlew lint

# Generate lint report
./gradlew lintDebug

# Check code style
./gradlew ktlintCheck
```

## 📦 Release Build

### Signing Configuration
1. Create keystore file:
   ```bash
   keytool -genkey -v -keystore release-key.keystore -alias release -keyalg RSA -keysize 2048 -validity 10000
   ```

2. Add signing config to `app/build.gradle`:
   ```gradle
   android {
       signingConfigs {
           release {
               storeFile file('release-key.keystore')
               storePassword 'your_store_password'
               keyAlias 'release'
               keyPassword 'your_key_password'
           }
       }
       buildTypes {
           release {
               signingConfig signingConfigs.release
               minifyEnabled true
               proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
           }
       }
   }
   ```

3. Build release APK:
   ```bash
   ./gradlew assembleRelease
   ```

### APK Optimization
- **Enable ProGuard:** For code obfuscation and size reduction
- **Enable R8:** For advanced code shrinking
- **Split APKs:** Generate separate APKs for different architectures
- **Bundle Format:** Consider using Android App Bundle (AAB) for Play Store

## 📊 Build Metrics

### Typical Build Times
- **Clean Build:** 5-10 minutes (first time)
- **Incremental Build:** 30-60 seconds
- **Debug APK Size:** ~50-80 MB (per architecture)
- **Release APK Size:** ~30-50 MB (optimized)

### Resource Usage
- **RAM Usage:** 4-8 GB during build
- **CPU Usage:** High during compilation
- **Storage:** ~2-3 GB for build artifacts

## 🚀 Continuous Integration

### GitHub Actions Example
```yaml
name: Build APK
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Setup Android SDK
      uses: android-actions/setup-android@v2
    - name: Build Debug APK
      run: ./gradlew assembleDebug
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: debug-apk
        path: app/build/outputs/apk/debug/*.apk
```

## 📞 Support

If you encounter issues during the build process:

1. **Check Documentation:** Review this guide and project README
2. **Search Issues:** Look for similar issues on GitHub
3. **Create Issue:** Report new issues with detailed information:
   - Operating system and version
   - Android Studio version
   - Error messages and logs
   - Steps to reproduce

---

**Happy Building! 🎉**

*This build guide is maintained as part of the Python Pocket IDE project. For updates and improvements, please contribute to the project repository.* 