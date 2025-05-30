#!/bin/bash

# Python Pocket IDE - Academic Submission Setup Script
# Created for: Ankit (Enrollment No.: O23MCA110241)
# Course: MCA Jul 2023 Batch, Chandigarh University
# Subject: Major Project (23ONMCR-753)

echo "🎓 Python Pocket IDE - Academic Submission Setup"
echo "================================================"
echo "Student: Ankit (O23MCA110241)"
echo "University: Chandigarh University"
echo "Subject: Major Project (23ONMCR-753)"
echo ""

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to install packages on different systems
install_packages() {
    if command_exists apt-get; then
        echo "📦 Detected Debian/Ubuntu system"
        sudo apt-get update
        sudo apt-get install -y python3 python3-pip wkhtmltopdf
    elif command_exists yum; then
        echo "📦 Detected Red Hat/CentOS system"
        sudo yum install -y python3 python3-pip wkhtmltopdf
    elif command_exists brew; then
        echo "📦 Detected macOS with Homebrew"
        brew install python wkhtmltopdf
    else
        echo "❌ Could not detect package manager. Please install manually:"
        echo "   - Python 3.7+"
        echo "   - pip"
        echo "   - wkhtmltopdf"
        exit 1
    fi
}

# Check and install system dependencies
echo "🔍 Checking system dependencies..."

if ! command_exists python3; then
    echo "❌ Python 3 not found. Installing..."
    install_packages
else
    echo "✅ Python 3 found: $(python3 --version)"
fi

if ! command_exists pip3; then
    echo "❌ pip not found. Installing..."
    install_packages
else
    echo "✅ pip found: $(pip3 --version)"
fi

if ! command_exists wkhtmltopdf; then
    echo "❌ wkhtmltopdf not found. Installing..."
    install_packages
else
    echo "✅ wkhtmltopdf found: $(wkhtmltopdf --version | head -n1)"
fi

# Install Python dependencies
echo ""
echo "🐍 Installing Python dependencies..."

# Create virtual environment if it doesn't exist
if [ ! -d "venv" ]; then
    echo "📦 Creating virtual environment..."
    python3 -m venv venv
fi

# Activate virtual environment
echo "🔄 Activating virtual environment..."
source venv/bin/activate

# Upgrade pip
echo "⬆️ Upgrading pip..."
pip install --upgrade pip

# Install required packages
echo "📚 Installing required Python packages..."
pip install markdown pdfkit

# Verify installations
echo ""
echo "✅ Verifying installations..."

python3 -c "import markdown; print('✓ Markdown module installed')"
python3 -c "import pdfkit; print('✓ PDFKit module installed')"

# Test wkhtmltopdf integration
echo "🧪 Testing wkhtmltopdf integration..."
python3 -c "
import pdfkit
try:
    pdfkit.configuration(wkhtmltopdf='wkhtmltopdf')
    print('✓ wkhtmltopdf integration working')
except Exception as e:
    print(f'❌ wkhtmltopdf integration error: {e}')
"

# Create requirements.txt for future reference
echo "📝 Creating requirements.txt..."
cat > requirements.txt << EOF
# Python Pocket IDE - Academic Submission Dependencies
# Generated for: Ankit (O23MCA110241)
# Subject: Major Project (23ONMCR-753)

markdown>=3.4.4
pdfkit>=1.0.0
pathlib2>=2.3.7
EOF

echo ""
echo "✅ Setup completed successfully!"
echo ""
echo "📋 Next steps:"
echo "1. Run: source venv/bin/activate"
echo "2. Run: python3 generate_project_pdf.py"
echo "3. Check the Academic_Submission/ directory for output"
echo ""
echo "📁 Files created:"
echo "   - venv/ (Python virtual environment)"
echo "   - requirements.txt (Python dependencies)"
echo ""
echo "🎯 Ready to generate academic submission package!"

# Make the PDF generation script executable
if [ -f "generate_project_pdf.py" ]; then
    chmod +x generate_project_pdf.py
    echo "✅ Made generate_project_pdf.py executable"
fi

echo ""
echo "🔧 Troubleshooting tips:"
echo "   - If wkhtmltopdf fails: sudo apt-get install wkhtmltopdf"
echo "   - If permissions error: make sure script is executable"
echo "   - If PDF generation fails: check wkhtmltopdf path"
echo ""
echo "📞 Support: Check INSTALLATION_GUIDE.md for detailed instructions" 