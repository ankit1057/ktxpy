/*
 * Copyright (c) 2024 Ankit Kumar
 * Python Pocket IDE - Enhanced Mobile Python Development Environment
 * 
 * ProjectManager handles all project-related operations
 */

package com.ankit.pythonpocketide.utils

import android.content.Context
import android.util.Log
import com.ankit.pythonpocketide.models.Dependency
import com.ankit.pythonpocketide.models.Project
import com.ankit.pythonpocketide.models.ProjectFile
import com.ankit.pythonpocketide.models.ProjectTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class ProjectManager private constructor(private val context: Context) {
    
    companion object {
        private const val TAG = "ProjectManager"
        private const val PROJECTS_DIR = "projects"
        private const val LEGACY_FILES_DIR = "pythonFiles"
        
        @Volatile
        private var INSTANCE: ProjectManager? = null
        
        fun getInstance(context: Context): ProjectManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProjectManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    private val projectsDirectory: File by lazy {
        File(context.filesDir, PROJECTS_DIR).apply {
            if (!exists()) mkdirs()
        }
    }
    
    private val legacyFilesDirectory: File by lazy {
        File(context.filesDir, LEGACY_FILES_DIR).apply {
            if (!exists()) mkdirs()
        }
    }
    
    /**
     * Get all projects
     */
    suspend fun getAllProjects(): List<Project> = withContext(Dispatchers.IO) {
        try {
            projectsDirectory.listFiles()
                ?.filter { it.isDirectory }
                ?.mapNotNull { loadProject(it.name) }
                ?.sortedByDescending { it.modifiedAt }
                ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading projects", e)
            emptyList()
        }
    }
    
    /**
     * Create a new project from template
     */
    suspend fun createProject(
        name: String,
        description: String,
        template: ProjectTemplate
    ): Result<Project> = withContext(Dispatchers.IO) {
        try {
            // Validate project name
            if (!isValidProjectName(name)) {
                return@withContext Result.failure(IllegalArgumentException("Invalid project name"))
            }
            
            val projectDir = File(projectsDirectory, name)
            if (projectDir.exists()) {
                return@withContext Result.failure(IllegalArgumentException("Project already exists"))
            }
            
            // Create project directory
            projectDir.mkdirs()
            
            // Create project structure
            val project = Project(
                name = name,
                description = description,
                path = projectDir.absolutePath,
                template = template
            )
            
            // Create main file
            val mainFile = File(projectDir, "main.py")
            mainFile.writeText(template.mainFileContent)
            
            // Create __init__.py
            val initFile = File(projectDir, "__init__.py")
            initFile.writeText(template.getInitFileContent())
            
            // Create requirements.txt
            if (template.dependencies.isNotEmpty()) {
                val requirementsFile = File(projectDir, "requirements.txt")
                requirementsFile.writeText(template.getRequirementsContent())
            }
            
            // Create pyproject.toml
            val pyprojectFile = File(projectDir, "pyproject.toml")
            pyprojectFile.writeText(template.getPyprojectContent(name))
            
            // Create additional files and directories
            template.additionalFiles.forEach { (path, content) ->
                val file = File(projectDir, path)
                file.parentFile?.mkdirs()
                file.writeText(content)
            }
            
            // Scan and add files to project
            val updatedProject = project.copy(
                files = scanProjectFiles(projectDir),
                dependencies = parseDependencies(File(projectDir, "requirements.txt"))
            )
            
            Result.success(updatedProject)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating project", e)
            Result.failure(e)
        }
    }
    
    /**
     * Load an existing project
     */
    suspend fun loadProject(projectName: String): Project? = withContext(Dispatchers.IO) {
        try {
            val projectDir = File(projectsDirectory, projectName)
            if (!projectDir.exists() || !projectDir.isDirectory) {
                return@withContext null
            }
            
            val pyprojectFile = File(projectDir, "pyproject.toml")
            val description = if (pyprojectFile.exists()) {
                parsePyprojectDescription(pyprojectFile) ?: "Python project"
            } else {
                "Python project"
            }
            
            Project(
                name = projectName,
                description = description,
                path = projectDir.absolutePath,
                createdAt = LocalDateTime.ofEpochSecond(
                    projectDir.lastModified() / 1000, 0, 
                    java.time.ZoneOffset.systemDefault().rules.getOffset(LocalDateTime.now())
                ),
                modifiedAt = LocalDateTime.ofEpochSecond(
                    projectDir.lastModified() / 1000, 0,
                    java.time.ZoneOffset.systemDefault().rules.getOffset(LocalDateTime.now())
                ),
                files = scanProjectFiles(projectDir),
                dependencies = parseDependencies(File(projectDir, "requirements.txt"))
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error loading project: $projectName", e)
            null
        }
    }
    
    /**
     * Delete a project
     */
    suspend fun deleteProject(projectName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val projectDir = File(projectsDirectory, projectName)
            if (projectDir.exists()) {
                projectDir.deleteRecursively()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting project", e)
            Result.failure(e)
        }
    }
    
    /**
     * Convert a standalone Python file to a project
     */
    suspend fun convertFileToProject(
        filePath: String,
        projectName: String,
        description: String
    ): Result<Project> = withContext(Dispatchers.IO) {
        try {
            val sourceFile = File(filePath)
            if (!sourceFile.exists()) {
                return@withContext Result.failure(IllegalArgumentException("Source file not found"))
            }
            
            // Read original file content
            val fileContent = sourceFile.readText()
            
            // Create project using empty template
            val result = createProject(projectName, description, ProjectTemplate.EMPTY)
            
            if (result.isSuccess) {
                val project = result.getOrThrow()
                // Replace main.py content with original file content
                val mainFile = File(project.path, "main.py")
                mainFile.writeText(fileContent)
                
                Result.success(project)
            } else {
                result
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error converting file to project", e)
            Result.failure(e)
        }
    }
    
    /**
     * Export project as ZIP
     */
    suspend fun exportProject(project: Project, outputFile: File): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            ZipOutputStream(FileOutputStream(outputFile)).use { zipOut ->
                val projectDir = File(project.path)
                zipDirectory(projectDir, "", zipOut)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting project", e)
            Result.failure(e)
        }
    }
    
    /**
     * Import project from ZIP
     */
    suspend fun importProject(zipFile: File, projectName: String): Result<Project> = withContext(Dispatchers.IO) {
        try {
            val projectDir = File(projectsDirectory, projectName)
            if (projectDir.exists()) {
                return@withContext Result.failure(IllegalArgumentException("Project already exists"))
            }
            
            projectDir.mkdirs()
            
            ZipInputStream(FileInputStream(zipFile)).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    val file = File(projectDir, entry.name)
                    if (entry.isDirectory) {
                        file.mkdirs()
                    } else {
                        file.parentFile?.mkdirs()
                        FileOutputStream(file).use { output ->
                            zipIn.copyTo(output)
                        }
                    }
                    entry = zipIn.nextEntry
                }
            }
            
            // Load the imported project
            loadProject(projectName)?.let { project ->
                Result.success(project)
            } ?: Result.failure(Exception("Failed to load imported project"))
        } catch (e: Exception) {
            Log.e(TAG, "Error importing project", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get legacy Python files (for backward compatibility)
     */
    suspend fun getLegacyFiles(): List<File> = withContext(Dispatchers.IO) {
        try {
            legacyFilesDirectory.listFiles()
                ?.filter { it.isFile && it.extension == "py" }
                ?.toList()
                ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading legacy files", e)
            emptyList()
        }
    }
    
    /**
     * Install project dependencies
     */
    suspend fun installDependencies(project: Project): Result<String> = withContext(Dispatchers.IO) {
        try {
            val requirementsFile = File(project.path, "requirements.txt")
            if (!requirementsFile.exists()) {
                return@withContext Result.success("No dependencies to install")
            }
            
            val command = Commands.getEnhancedPipInstallCommand(context, "-r ${requirementsFile.absolutePath}")
            Result.success(command)
        } catch (e: Exception) {
            Log.e(TAG, "Error installing dependencies", e)
            Result.failure(e)
        }
    }
    
    // Private helper methods
    
    private fun isValidProjectName(name: String): Boolean {
        return name.isNotBlank() && 
               name.matches(Regex("[a-zA-Z0-9_-]+")) && 
               name.length <= 50
    }
    
    private fun scanProjectFiles(projectDir: File): MutableList<ProjectFile> {
        val files = mutableListOf<ProjectFile>()
        
        fun scanDirectory(dir: File, relativePath: String = "") {
            dir.listFiles()?.forEach { file ->
                val relPath = if (relativePath.isEmpty()) file.name else "$relativePath/${file.name}"
                files.add(
                    ProjectFile(
                        name = file.name,
                        path = file.absolutePath,
                        relativePath = relPath,
                        isDirectory = file.isDirectory,
                        size = if (file.isFile) file.length() else 0,
                        lastModified = file.lastModified()
                    )
                )
                
                if (file.isDirectory) {
                    scanDirectory(file, relPath)
                }
            }
        }
        
        scanDirectory(projectDir)
        return files
    }
    
    private fun parseDependencies(requirementsFile: File): MutableList<Dependency> {
        if (!requirementsFile.exists()) return mutableListOf()
        
        return requirementsFile.readLines()
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .mapNotNull { line ->
                val trimmed = line.trim()
                when {
                    "==" in trimmed -> {
                        val parts = trimmed.split("==")
                        if (parts.size == 2) {
                            Dependency(parts[0], parts[1])
                        } else null
                    }
                    else -> Dependency(trimmed)
                }
            }
            .toMutableList()
    }
    
    private fun parsePyprojectDescription(file: File): String? {
        return try {
            file.readLines()
                .find { it.trim().startsWith("description") }
                ?.substringAfter("=")
                ?.trim()
                ?.removeSurrounding("\"")
        } catch (e: Exception) {
            null
        }
    }
    
    private fun zipDirectory(dir: File, parentPath: String, zipOut: ZipOutputStream) {
        dir.listFiles()?.forEach { file ->
            val entryPath = if (parentPath.isEmpty()) file.name else "$parentPath/${file.name}"
            
            if (file.isDirectory) {
                zipOut.putNextEntry(ZipEntry("$entryPath/"))
                zipOut.closeEntry()
                zipDirectory(file, entryPath, zipOut)
            } else {
                zipOut.putNextEntry(ZipEntry(entryPath))
                FileInputStream(file).use { input ->
                    input.copyTo(zipOut)
                }
                zipOut.closeEntry()
            }
        }
    }
} 