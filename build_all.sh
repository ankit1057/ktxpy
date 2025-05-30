#!/bin/bash

# Python Pocket IDE - One-Command Submission Builder
# For: Ankit (O23MCA110241) - MCA Jul 2023 Batch
# Subject: Major Project (23ONMCR-753)
# University: Chandigarh University

echo "🎓 Python Pocket IDE - Complete Submission Builder"
echo "=================================================="
echo "Student: Ankit (O23MCA110241)"
echo "University: Chandigarh University"
echo "Subject: Major Project (23ONMCR-753)"
echo ""
echo "This will build ALL architecture APKs, universal APK, and complete documentation!"
echo ""

# Check if we're in the right directory
if [ ! -f "app/build.gradle" ]; then
    echo "❌ Error: Please run this script from the project root directory"
    echo "   Make sure you're in the PPIDE_Source directory"
    exit 1
fi

# Make sure gradlew is executable
chmod +x gradlew

# Check Java/Android environment
echo "🔍 Checking build environment..."

if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Please install JDK 11 or later"
    exit 1
fi

echo "✅ Java found: $(java -version 2>&1 | head -n1)"

# Set up Android SDK environment if needed
if [ -z "$ANDROID_SDK_ROOT" ] && [ -z "$ANDROID_HOME" ]; then
    echo "⚠️ Warning: ANDROID_SDK_ROOT not set. Using default Android Studio location..."
    if [ -d "$HOME/Android/Sdk" ]; then
        export ANDROID_SDK_ROOT="$HOME/Android/Sdk"
        export ANDROID_HOME="$HOME/Android/Sdk"
        export PATH="$PATH:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/tools"
        echo "✅ Using Android SDK: $ANDROID_SDK_ROOT"
    else
        echo "❌ Android SDK not found. Please install Android Studio or set ANDROID_SDK_ROOT"
        exit 1
    fi
else
    echo "✅ Android SDK found: ${ANDROID_SDK_ROOT:-$ANDROID_HOME}"
fi

# Check Python environment
echo "🐍 Checking Python environment..."

if ! command -v python3 &> /dev/null; then
    echo "❌ Python 3 not found. Please install Python 3.7+"
    exit 1
fi

echo "✅ Python found: $(python3 --version)"

# Install Python dependencies if needed
echo "📦 Checking Python dependencies..."
python3 -c "import markdown, pdfkit" 2>/dev/null || {
    echo "⚠️ Installing Python dependencies..."
    python3 -m pip install --user markdown pdfkit
}

# Check wkhtmltopdf for PDF generation
if ! command -v wkhtmltopdf &> /dev/null; then
    echo "⚠️ wkhtmltopdf not found. Installing..."
    
    if command -v apt-get &> /dev/null; then
        sudo apt-get update && sudo apt-get install -y wkhtmltopdf
    elif command -v yum &> /dev/null; then
        sudo yum install -y wkhtmltopdf
    else
        echo "❌ Please install wkhtmltopdf manually for PDF generation"
        echo "   Visit: https://wkhtmltopdf.org/downloads.html"
    fi
fi

echo ""
echo "🚀 Starting complete submission package build..."
echo "This will take several minutes depending on your hardware."
echo ""

# Run the complete submission builder
python3 build_submission_package.py

# Check if build was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "🎉 SUBMISSION PACKAGE BUILD COMPLETED!"
    echo ""
    echo "📁 Your complete submission package is ready in:"
    echo "   📂 01_FINAL_SUBMISSION_PACKAGE/"
    echo ""
    echo "📦 Package contains:"
    echo "   📱 9 APK files (4 arch-specific debug + 4 arch-specific release + 1 universal)"
    echo "   📚 Complete 60+ page PDF documentation"
    echo "   💻 Full source code archives"
    echo "   📋 Installation instructions"
    echo "   🎯 Ready for university submission!"
    echo ""
    echo "🎓 Next steps:"
    echo "   1. Test APK installation on your Android device"
    echo "   2. Review the generated PDF documentation"
    echo "   3. Get supervisor signatures on certificate"
    echo "   4. Submit to Chandigarh University before deadline"
    echo ""
    
    # Show directory structure
    if [ -d "01_FINAL_SUBMISSION_PACKAGE" ]; then
        echo "📋 Generated package structure:"
        tree 01_FINAL_SUBMISSION_PACKAGE/ -L 2 2>/dev/null || ls -la 01_FINAL_SUBMISSION_PACKAGE/
    fi
    
else
    echo ""
    echo "❌ BUILD FAILED!"
    echo "   Please check the error messages above and try again."
    echo "   Common issues:"
    echo "   - Missing Android SDK"
    echo "   - Insufficient disk space"
    echo "   - Network connectivity for dependencies"
    exit 1
fi 