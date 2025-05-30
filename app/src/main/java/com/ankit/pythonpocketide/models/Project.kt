/*
 * Copyright (c) 2024 Ankit Kumar
 * Python Pocket IDE - Enhanced Mobile Python Development Environment
 * 
 * Project models for project-based Python development
 */

package com.ankit.pythonpocketide.models

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Represents a Python project with files, dependencies, and metadata
 */
data class Project(
    val name: String,
    val description: String,
    val path: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val modifiedAt: LocalDateTime = LocalDateTime.now(),
    val mainFile: String = "main.py",
    val dependencies: MutableList<Dependency> = mutableListOf(),
    val files: MutableList<ProjectFile> = mutableListOf(),
    val template: ProjectTemplate? = null
) {
    
    fun getProjectDirectory(): File = File(path)
    
    fun getMainFilePath(): String = "$path${File.separator}$mainFile"
    
    fun getRequirementsPath(): String = "$path${File.separator}requirements.txt"
    
    fun getPyprojectPath(): String = "$path${File.separator}pyproject.toml"
    
    fun getCreatedDateString(): String = 
        createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    
    fun getModifiedDateString(): String = 
        modifiedAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
    
    fun addDependency(dependency: Dependency) {
        if (!dependencies.contains(dependency)) {
            dependencies.add(dependency)
        }
    }
    
    fun removeDependency(packageName: String) {
        dependencies.removeIf { it.name == packageName }
    }
    
    fun addFile(file: ProjectFile) {
        if (!files.contains(file)) {
            files.add(file)
        }
    }
    
    fun removeFile(filePath: String) {
        files.removeIf { it.path == filePath }
    }
    
    fun getAllPythonFiles(): List<ProjectFile> = 
        files.filter { it.path.endsWith(".py") }
    
    fun getFileCount(): Int = files.size
    
    fun getDependencyCount(): Int = dependencies.size
}

/**
 * Represents a Python package dependency
 */
data class Dependency(
    val name: String,
    val version: String = "",
    val isRequired: Boolean = true,
    val description: String = ""
) {
    fun getDisplayName(): String = if (version.isNotEmpty()) "$name==$version" else name
    
    fun toRequirementString(): String = getDisplayName()
}

/**
 * Represents a file within a project
 */
data class ProjectFile(
    val name: String,
    val path: String,
    val relativePath: String,
    val isDirectory: Boolean = false,
    val size: Long = 0,
    val lastModified: Long = System.currentTimeMillis()
) {
    fun getExtension(): String = if (name.contains('.')) name.substringAfterLast('.') else ""
    
    fun isPythonFile(): Boolean = getExtension() == "py"
    
    fun isConfigFile(): Boolean = name in listOf("requirements.txt", "pyproject.toml", "__init__.py")
    
    fun getDisplaySize(): String {
        return when {
            size < 1024 -> "${size}B"
            size < 1024 * 1024 -> "${size / 1024}KB"
            else -> "${size / (1024 * 1024)}MB"
        }
    }
}

/**
 * Project templates for different types of Python projects
 */
enum class ProjectTemplate(
    val displayName: String,
    val description: String,
    val mainFileContent: String,
    val dependencies: List<String> = emptyList(),
    val additionalFiles: Map<String, String> = emptyMap()
) {
    EMPTY(
        displayName = "Empty Project",
        description = "A minimal Python project structure",
        mainFileContent = """# Main entry point for your Python project
print("Hello, Python Pocket IDE!")

def main():
    '''
    Main function - implement your logic here
    '''
    pass

if __name__ == "__main__":
    main()
""",
        dependencies = emptyList()
    ),
    
    FLASK_API(
        displayName = "Flask API",
        description = "A starter RESTful API using Flask framework",
        mainFileContent = """from flask import Flask, jsonify, request

app = Flask(__name__)

# Sample data
users = [
    {"id": 1, "name": "Alice", "email": "alice@example.com"},
    {"id": 2, "name": "Bob", "email": "bob@example.com"}
]

@app.route('/')
def home():
    return jsonify({"message": "Welcome to Flask API!", "version": "1.0"})

@app.route('/users', methods=['GET'])
def get_users():
    return jsonify(users)

@app.route('/users/<int:user_id>', methods=['GET'])
def get_user(user_id):
    user = next((u for u in users if u["id"] == user_id), None)
    if user:
        return jsonify(user)
    return jsonify({"error": "User not found"}), 404

@app.route('/users', methods=['POST'])
def create_user():
    data = request.get_json()
    new_user = {
        "id": len(users) + 1,
        "name": data.get("name"),
        "email": data.get("email")
    }
    users.append(new_user)
    return jsonify(new_user), 201

if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=5000)
""",
        dependencies = listOf("flask==2.3.3"),
        additionalFiles = mapOf(
            "api/__init__.py" to "",
            "api/routes.py" to "# API routes module\n",
            "config.py" to "# Configuration settings\nDEBUG = True\nPORT = 5000\n"
        )
    ),
    
    DATA_ANALYSIS(
        displayName = "Data Analysis",
        description = "Data analysis project with pandas and matplotlib",
        mainFileContent = """import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

def load_sample_data():
    '''Create sample dataset for analysis'''
    np.random.seed(42)
    data = {
        'name': [f'Item_{i}' for i in range(100)],
        'value': np.random.normal(50, 15, 100),
        'category': np.random.choice(['A', 'B', 'C'], 100),
        'date': pd.date_range('2024-01-01', periods=100, freq='D')
    }
    return pd.DataFrame(data)

def analyze_data(df):
    '''Perform basic data analysis'''
    print("Dataset Overview:")
    print(f"Shape: {df.shape}")
    print(f"Columns: {list(df.columns)}")
    print("\nBasic Statistics:")
    print(df.describe())
    
    return df

def create_visualizations(df):
    '''Create data visualizations'''
    fig, axes = plt.subplots(2, 2, figsize=(12, 8))
    
    # Value distribution
    axes[0, 0].hist(df['value'], bins=20, alpha=0.7)
    axes[0, 0].set_title('Value Distribution')
    axes[0, 0].set_xlabel('Value')
    axes[0, 0].set_ylabel('Frequency')
    
    # Category counts
    df['category'].value_counts().plot(kind='bar', ax=axes[0, 1])
    axes[0, 1].set_title('Category Distribution')
    axes[0, 1].set_xlabel('Category')
    axes[0, 1].set_ylabel('Count')
    
    # Time series
    daily_avg = df.groupby('date')['value'].mean()
    axes[1, 0].plot(daily_avg.index, daily_avg.values)
    axes[1, 0].set_title('Daily Average Values')
    axes[1, 0].set_xlabel('Date')
    axes[1, 0].set_ylabel('Average Value')
    
    # Box plot by category
    categories = df['category'].unique()
    category_data = [df[df['category'] == cat]['value'] for cat in categories]
    axes[1, 1].boxplot(category_data, labels=categories)
    axes[1, 1].set_title('Value Distribution by Category')
    axes[1, 1].set_xlabel('Category')
    axes[1, 1].set_ylabel('Value')
    
    plt.tight_layout()
    plt.savefig('analysis_results.png', dpi=300, bbox_inches='tight')
    print("Visualizations saved as 'analysis_results.png'")

def main():
    '''Main analysis workflow'''
    print("Starting Data Analysis...")
    
    # Load and analyze data
    df = load_sample_data()
    df = analyze_data(df)
    
    # Create visualizations
    create_visualizations(df)
    
    print("\\nAnalysis complete!")

if __name__ == "__main__":
    main()
""",
        dependencies = listOf("pandas==2.1.3", "matplotlib==3.8.2", "numpy==1.25.2"),
        additionalFiles = mapOf(
            "data/__init__.py" to "",
            "data/loader.py" to "# Data loading utilities\n",
            "analysis/__init__.py" to "",
            "analysis/statistics.py" to "# Statistical analysis functions\n"
        )
    ),
    
    MACHINE_LEARNING(
        displayName = "Machine Learning",
        description = "ML project template with scikit-learn",
        mainFileContent = """import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, classification_report
from sklearn.datasets import make_classification
import joblib

def create_sample_dataset():
    '''Generate sample classification dataset'''
    X, y = make_classification(
        n_samples=1000,
        n_features=10,
        n_informative=5,
        n_redundant=3,
        n_classes=3,
        random_state=42
    )
    
    feature_names = [f'feature_{i}' for i in range(X.shape[1])]
    df = pd.DataFrame(X, columns=feature_names)
    df['target'] = y
    
    return df

def preprocess_data(df):
    '''Preprocess the dataset'''
    print("Preprocessing data...")
    
    # Separate features and target
    X = df.drop('target', axis=1)
    y = df['target']
    
    # Split into train and test sets
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y
    )
    
    print(f"Training set: {X_train.shape[0]} samples")
    print(f"Test set: {X_test.shape[0]} samples")
    
    return X_train, X_test, y_train, y_test

def train_model(X_train, y_train):
    '''Train a Random Forest classifier'''
    print("Training Random Forest model...")
    
    model = RandomForestClassifier(
        n_estimators=100,
        max_depth=10,
        random_state=42
    )
    
    model.fit(X_train, y_train)
    
    print("Model training completed!")
    return model

def evaluate_model(model, X_test, y_test):
    '''Evaluate the trained model'''
    print("Evaluating model...")
    
    # Make predictions
    y_pred = model.predict(X_test)
    
    # Calculate accuracy
    accuracy = accuracy_score(y_test, y_pred)
    print(f"Accuracy: {accuracy:.4f}")
    
    # Detailed classification report
    report = classification_report(y_test, y_pred)
    print("Classification Report:")
    print(report)
    
    return accuracy

def save_model(model, filename='model.pkl'):
    '''Save the trained model'''
    joblib.dump(model, filename)
    print(f"Model saved as '{filename}'")

def main():
    '''Main ML workflow'''
    print("Starting Machine Learning Pipeline...")
    
    # Create and preprocess data
    df = create_sample_dataset()
    X_train, X_test, y_train, y_test = preprocess_data(df)
    
    # Train and evaluate model
    model = train_model(X_train, y_train)
    accuracy = evaluate_model(model, X_test, y_test)
    
    # Save the model
    save_model(model)
    
    print(f"\\nML Pipeline completed! Final accuracy: {accuracy:.4f}")

if __name__ == "__main__":
    main()
""",
        dependencies = listOf(
            "scikit-learn==1.3.2",
            "pandas==2.1.3",
            "numpy==1.25.2",
            "joblib==1.3.2"
        ),
        additionalFiles = mapOf(
            "models/__init__.py" to "",
            "models/train.py" to "# Model training utilities\n",
            "models/evaluate.py" to "# Model evaluation utilities\n",
            "data/__init__.py" to "",
            "utils/__init__.py" to "",
            "utils/preprocessing.py" to "# Data preprocessing utilities\n"
        )
    );
    
    fun getInitFileContent(): String = "# ${displayName} - Generated by Python Pocket IDE\n"
    
    fun getPyprojectContent(projectName: String): String = """[build-system]
requires = ["setuptools>=45", "wheel"]
build-backend = "setuptools.build_meta"

[project]
name = "$projectName"
version = "0.1.0"
description = "$description"
authors = [
    {name = "Python Pocket IDE User", email = "user@example.com"},
]
dependencies = [
${dependencies.joinToString(",\n") { "    \"$it\"" }}
]
requires-python = ">=3.8"

[project.scripts]
$projectName = "$projectName.main:main"
"""
    
    fun getRequirementsContent(): String = dependencies.joinToString("\n")
} 