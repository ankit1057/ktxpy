#!/usr/bin/env python3
"""
Python Pocket IDE - Academic Project PDF Generator
Created for: Ankit (Enrollment No.: O23MCA110241)
Course: MCA Jul 2023 Batch, Chandigarh University
Subject: Major Project (23ONMCR-753)

This script generates a comprehensive PDF containing all project documentation
and source code for academic submission following university guidelines.
"""

import os
import sys
import markdown
import pdfkit
from pathlib import Path
from datetime import datetime
import zipfile
import shutil
import subprocess

class ProjectPDFGenerator:
    def __init__(self):
        self.project_root = Path.cwd()
        self.output_dir = self.project_root / "Academic_Submission"
        self.pdf_output = self.output_dir / "Python_Pocket_IDE_Complete_Project.pdf"
        self.source_code_dir = self.project_root / "app" / "src"
        
        # University project details
        self.student_name = "Ankit"
        self.enrollment_no = "O23MCA110241"
        self.batch = "Jul 2023"
        self.subject_code = "23ONMCR-753"
        self.university = "Chandigarh University"
        
        # Create output directory
        self.output_dir.mkdir(exist_ok=True)
        
    def generate_cover_page(self):
        """Generate HTML cover page for the PDF"""
        cover_html = f"""
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body {{
                    font-family: 'Times New Roman', serif;
                    text-align: center;
                    margin: 0;
                    padding: 40px;
                    line-height: 1.6;
                }}
                .title {{
                    font-size: 24px;
                    font-weight: bold;
                    margin: 40px 0;
                    text-transform: uppercase;
                }}
                .subtitle {{
                    font-size: 18px;
                    margin: 20px 0;
                }}
                .student-info {{
                    font-size: 16px;
                    margin: 30px 0;
                }}
                .university {{
                    font-size: 20px;
                    font-weight: bold;
                    margin: 40px 0;
                }}
                .date {{
                    font-size: 14px;
                    margin-top: 50px;
                }}
                .page-break {{
                    page-break-after: always;
                }}
            </style>
        </head>
        <body>
            <div class="title">
                Python Pocket IDE: Advanced Mobile Integrated Development Environment for Python Programming
            </div>
            
            <div class="subtitle">
                Major Project Report<br>
                Complete Source Code and Documentation Package
            </div>
            
            <div class="student-info">
                <strong>Submitted By:</strong><br>
                {self.student_name}<br>
                Enrollment No.: {self.enrollment_no}<br>
                Batch: {self.batch}<br><br>
                
                <strong>Subject:</strong> Major Project ({self.subject_code})<br>
                <strong>Credits:</strong> 12<br>
                <strong>Programme:</strong> Master of Computer Applications
            </div>
            
            <div class="university">
                Centre for Distance & Online Education<br>
                {self.university}<br>
                Mohali, Punjab
            </div>
            
            <div class="date">
                Submission Date: {datetime.now().strftime('%B %d, %Y')}
            </div>
            
            <div class="page-break"></div>
        </body>
        </html>
        """
        return cover_html
    
    def collect_documentation_files(self):
        """Collect all markdown documentation files in correct order"""
        doc_files = [
            "01_TITLE_PAGE.md",
            "02_CERTIFICATE.md", 
            "03_DECLARATION.md",
            "04_ACKNOWLEDGEMENT.md",
            "05_ABSTRACT.md",
            "06_TABLE_OF_CONTENTS.md",
            "07_INTRODUCTION.md",
            "PROJECT_SYNOPSIS.md",
            "SDLC_PLAN.md",
            "PROJECT_REQUIREMENTS.md",
            "IMPLEMENTATION_PLAN.md",
            "PYTHON_LIMITATIONS_GUIDE.md",
            "ACADEMIC_PROJECT_ROADMAP.md",
            "FYP_COMPREHENSIVE_GUIDE.md",
            "CREDITS_AND_ACKNOWLEDGMENTS.md",
            "README.md",
            "INSTALLATION_GUIDE.md"
        ]
        
        collected_files = []
        for doc_file in doc_files:
            file_path = self.project_root / doc_file
            if file_path.exists():
                collected_files.append(file_path)
                print(f"✓ Found documentation: {doc_file}")
            else:
                print(f"⚠ Missing documentation: {doc_file}")
        
        return collected_files
    
    def collect_source_code_files(self):
        """Collect all source code files from the project"""
        source_files = []
        
        # Kotlin source files
        kotlin_dirs = [
            self.source_code_dir / "main" / "java",
            self.source_code_dir / "androidTest" / "java",
            self.source_code_dir / "test" / "java"
        ]
        
        for kotlin_dir in kotlin_dirs:
            if kotlin_dir.exists():
                for file_path in kotlin_dir.rglob("*.kt"):
                    source_files.append(("Kotlin", file_path))
                for file_path in kotlin_dir.rglob("*.java"):
                    source_files.append(("Java", file_path))
        
        # XML layout and resource files
        res_dir = self.source_code_dir / "main" / "res"
        if res_dir.exists():
            for file_path in res_dir.rglob("*.xml"):
                source_files.append(("XML", file_path))
        
        # Gradle build files
        for file_path in self.project_root.rglob("*.gradle"):
            source_files.append(("Gradle", file_path))
        
        # Manifest file
        manifest_path = self.source_code_dir / "main" / "AndroidManifest.xml"
        if manifest_path.exists():
            source_files.append(("Manifest", manifest_path))
        
        print(f"✓ Collected {len(source_files)} source code files")
        return source_files
    
    def markdown_to_html(self, file_path):
        """Convert markdown file to HTML"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            html = markdown.markdown(content, extensions=['tables', 'fenced_code', 'toc'])
            return f"""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {{
                        font-family: 'Times New Roman', serif;
                        line-height: 1.5;
                        margin: 1.25in;
                        font-size: 12pt;
                    }}
                    h1, h2, h3, h4, h5, h6 {{
                        color: #333;
                        margin-top: 20px;
                        margin-bottom: 10px;
                    }}
                    h1 {{ font-size: 18pt; }}
                    h2 {{ font-size: 16pt; }}
                    h3 {{ font-size: 14pt; }}
                    code {{
                        background-color: #f5f5f5;
                        padding: 2px 4px;
                        border-radius: 3px;
                        font-family: 'Courier New', monospace;
                    }}
                    pre {{
                        background-color: #f5f5f5;
                        padding: 10px;
                        border-radius: 5px;
                        overflow-x: auto;
                        font-family: 'Courier New', monospace;
                        font-size: 10pt;
                    }}
                    table {{
                        border-collapse: collapse;
                        width: 100%;
                        margin: 10px 0;
                    }}
                    th, td {{
                        border: 1px solid #ddd;
                        padding: 8px;
                        text-align: left;
                    }}
                    th {{
                        background-color: #f2f2f2;
                    }}
                    .page-break {{
                        page-break-before: always;
                    }}
                </style>
            </head>
            <body>
                <div class="page-break"></div>
                {html}
            </body>
            </html>
            """
        except Exception as e:
            print(f"Error converting {file_path}: {e}")
            return f"<p>Error reading file: {file_path}</p>"
    
    def source_code_to_html(self, file_type, file_path):
        """Convert source code to formatted HTML"""
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Get relative path for display
            rel_path = file_path.relative_to(self.project_root)
            
            return f"""
            <div class="page-break"></div>
            <h2>{file_type} File: {rel_path}</h2>
            <p><strong>File Path:</strong> {rel_path}</p>
            <p><strong>File Type:</strong> {file_type}</p>
            <p><strong>Lines of Code:</strong> {len(content.splitlines())}</p>
            <pre><code>{self.escape_html(content)}</code></pre>
            """
        except Exception as e:
            return f"<p>Error reading file {file_path}: {e}</p>"
    
    def escape_html(self, text):
        """Escape HTML special characters"""
        return (text.replace('&', '&amp;')
                   .replace('<', '&lt;')
                   .replace('>', '&gt;')
                   .replace('"', '&quot;')
                   .replace("'", '&#x27;'))
    
    def generate_complete_html(self):
        """Generate complete HTML for PDF conversion"""
        print("📄 Generating complete HTML document...")
        
        # Start with cover page
        complete_html = self.generate_cover_page()
        
        # Add documentation files
        doc_files = self.collect_documentation_files()
        for doc_file in doc_files:
            print(f"📝 Processing documentation: {doc_file.name}")
            complete_html += self.markdown_to_html(doc_file)
        
        # Add source code section header
        complete_html += """
        <div class="page-break"></div>
        <h1>APPENDIX A: COMPLETE SOURCE CODE LISTING</h1>
        <p>This section contains the complete source code for the Python Pocket IDE project, 
        organized by file type and directory structure.</p>
        """
        
        # Add source code files
        source_files = self.collect_source_code_files()
        for file_type, file_path in source_files:
            print(f"💻 Processing source code: {file_path.name}")
            complete_html += self.source_code_to_html(file_type, file_path)
        
        return complete_html
    
    def convert_to_pdf(self, html_content):
        """Convert HTML to PDF using wkhtmltopdf"""
        print("📄 Converting to PDF...")
        
        # Save HTML to temporary file
        html_file = self.output_dir / "temp_complete_project.html"
        with open(html_file, 'w', encoding='utf-8') as f:
            f.write(html_content)
        
        # PDF options for academic formatting
        options = {
            'page-size': 'A4',
            'margin-top': '1.25in',
            'margin-right': '1.25in',
            'margin-bottom': '1.25in',
            'margin-left': '1.25in',
            'encoding': "UTF-8",
            'no-outline': None,
            'enable-local-file-access': None,
            'print-media-type': None
        }
        
        try:
            # Convert to PDF
            pdfkit.from_file(str(html_file), str(self.pdf_output), options=options)
            print(f"✅ PDF generated successfully: {self.pdf_output}")
            
            # Clean up temporary file
            html_file.unlink()
            
        except Exception as e:
            print(f"❌ Error generating PDF: {e}")
            print("Make sure wkhtmltopdf is installed: sudo apt-get install wkhtmltopdf")
            
    def create_source_code_archive(self):
        """Create a ZIP archive of all source code"""
        print("📦 Creating source code archive...")
        
        archive_path = self.output_dir / "Python_Pocket_IDE_Source_Code.zip"
        
        with zipfile.ZipFile(archive_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
            # Add source code files
            for root, dirs, files in os.walk(self.source_code_dir):
                for file in files:
                    file_path = Path(root) / file
                    arcname = file_path.relative_to(self.project_root)
                    zipf.write(file_path, arcname)
            
            # Add important project files
            important_files = [
                "build.gradle",
                "settings.gradle", 
                "gradle.properties",
                "README.md",
                "INSTALLATION_GUIDE.md"
            ]
            
            for file_name in important_files:
                file_path = self.project_root / file_name
                if file_path.exists():
                    zipf.write(file_path, file_name)
        
        print(f"✅ Source code archive created: {archive_path}")
        return archive_path
    
    def generate_submission_readme(self):
        """Generate README for submission package"""
        readme_content = f"""# Python Pocket IDE - Academic Submission Package

## Student Information
- **Name:** {self.student_name}
- **Enrollment No:** {self.enrollment_no}
- **Batch:** {self.batch}
- **Subject:** Major Project ({self.subject_code})
- **University:** {self.university}
- **Submission Date:** {datetime.now().strftime('%B %d, %Y')}

## Package Contents

### 1. Complete Project Report PDF
- **File:** `Python_Pocket_IDE_Complete_Project.pdf`
- **Description:** Comprehensive project documentation including all required sections as per university guidelines
- **Pages:** ~60 pages following university formatting requirements
- **Contents:** 
  - Title Page, Certificate, Declaration, Acknowledgement
  - Abstract and Table of Contents
  - Complete project documentation
  - Full source code listing in appendix

### 2. Source Code Archive
- **File:** `Python_Pocket_IDE_Source_Code.zip`
- **Description:** Complete source code of the Python Pocket IDE project
- **Contents:**
  - All Kotlin/Java source files
  - XML layout and resource files
  - Gradle build configuration
  - Android manifest and configuration files

### 3. Individual Documentation Files
All markdown documentation files are included separately for reference:
- Project synopsis and requirements
- SDLC plan and implementation details
- Python limitations guide and academic roadmap
- Installation guide and user documentation

## Project Summary
The Python Pocket IDE is an advanced mobile integrated development environment for Python programming, built on Android using Kotlin and Jetpack Compose. The project enhances the existing KtxPy framework with educational features, improved package management, and modern UI/UX design.

## Technical Specifications
- **Platform:** Android (API 24+)
- **Language:** Kotlin with Jetpack Compose
- **Python Version:** 3.11.5 (ARM64 optimized)
- **Architecture:** MVVM with Clean Architecture
- **Database:** Room Database
- **UI Framework:** Material Design 3

## Installation and Testing
Refer to the `INSTALLATION_GUIDE.md` file in the documentation for complete setup and testing instructions.

## Academic Compliance
This project meets all requirements specified in the Major Project guidelines:
- Individual project completion
- Original work with proper citations
- Complete documentation package
- Ready for examination and presentation

## Contact Information
For any queries regarding this submission:
- **Student:** {self.student_name}
- **Email:** [Student Email]
- **Phone:** [Student Phone]
- **Institution:** {self.university}

---
Generated on: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}
"""
        
        readme_path = self.output_dir / "SUBMISSION_README.md"
        with open(readme_path, 'w', encoding='utf-8') as f:
            f.write(readme_content)
        
        print(f"✅ Submission README created: {readme_path}")
    
    def check_dependencies(self):
        """Check if required dependencies are installed"""
        try:
            import markdown
            import pdfkit
            print("✅ All Python dependencies available")
        except ImportError as e:
            print(f"❌ Missing Python dependency: {e}")
            print("Install with: pip install markdown pdfkit")
            return False
        
        # Check wkhtmltopdf
        try:
            subprocess.run(['wkhtmltopdf', '--version'], 
                         capture_output=True, check=True)
            print("✅ wkhtmltopdf is available")
        except (subprocess.CalledProcessError, FileNotFoundError):
            print("❌ wkhtmltopdf not found")
            print("Install with: sudo apt-get install wkhtmltopdf")
            return False
        
        return True
    
    def generate_complete_package(self):
        """Generate complete academic submission package"""
        print(f"""
🎓 Python Pocket IDE - Academic Submission Generator
Student: {self.student_name} ({self.enrollment_no})
University: {self.university}
Subject: {self.subject_code}
========================================================
        """)
        
        if not self.check_dependencies():
            print("❌ Please install missing dependencies and try again")
            return False
        
        try:
            # Generate complete HTML
            html_content = self.generate_complete_html()
            
            # Convert to PDF
            self.convert_to_pdf(html_content)
            
            # Create source code archive
            self.create_source_code_archive()
            
            # Generate submission README
            self.generate_submission_readme()
            
            print(f"""
✅ Academic submission package generated successfully!

📁 Output Directory: {self.output_dir}
📄 Complete PDF: {self.pdf_output}
📦 Source Archive: Python_Pocket_IDE_Source_Code.zip
📝 Submission Guide: SUBMISSION_README.md

🎯 Ready for submission to Chandigarh University!
            """)
            
            return True
            
        except Exception as e:
            print(f"❌ Error generating package: {e}")
            return False

def main():
    """Main function to run the PDF generator"""
    generator = ProjectPDFGenerator()
    success = generator.generate_complete_package()
    
    if success:
        print("\n🎉 Project package ready for academic submission!")
        print(f"📧 Submit the contents of: {generator.output_dir}")
    else:
        print("\n❌ Package generation failed. Please check errors above.")
        sys.exit(1)

if __name__ == "__main__":
    main() 