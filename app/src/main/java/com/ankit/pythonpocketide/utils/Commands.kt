package com.ankit.pythonpocketide.utils

import android.content.Context
import java.io.File

object Commands {
    fun getBasicCommand(context: Context): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecName = "libpython3.so"
        val aliasCommand = "alias python=\"$pythonExecName\" && alias pip=\"$pythonExecName -m pip \""
        return "export PATH=\$PATH:$appLibDirPath && export PYTHONHOME=$pythonBuildDirPath && export PYTHONPATH=$appLibDirPath && export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH:\" && export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH${pythonLibDirPath}\" && $aliasCommand && clear"
    }
    fun getInterpreterCommand(context: Context, filePath: String): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecName = "libpython3.so"
        return "export PATH=\$PATH:$appLibDirPath && export PYTHONHOME=$pythonBuildDirPath && export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH:\" && export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH${pythonLibDirPath}\" && clear && $pythonExecName $filePath && echo \'[Enter to Exit]\' && read junk && exit"
    }

    fun getPythonShellCommand(context: Context): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        return "export PATH=\$PATH:$appLibDirPath && export PYTHONHOME=$pythonBuildDirPath && export PYTHONPATH=$appLibDirPath && export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH:\" && export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH${pythonLibDirPath}\" && clear && libpython3.so && echo \'[Enter to Exit]\' && read junk && exit"
    }

    // Enhanced commands for better functionality
    fun getEnhancedBasicCommand(context: Context): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecName = "libpython3.so"
        val aliasCommand = "alias python=\"$pythonExecName\" && alias pip=\"$pythonExecName -m pip\" && alias python3=\"$pythonExecName\""
        val envSetup = "export PATH=\$PATH:$appLibDirPath && export PYTHONHOME=$pythonBuildDirPath && export PYTHONPATH=$appLibDirPath:$pythonLibDirPath && export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH:$pythonLibDirPath\""
        return "$envSetup && $aliasCommand && clear && echo 'Python Pocket IDE Terminal' && echo 'Python 3.11.5 ready - type python to start'"
    }

    fun getEnhancedInterpreterCommand(context: Context, filePath: String): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecName = "libpython3.so"
        val envSetup = "export PATH=\$PATH:$appLibDirPath && export PYTHONHOME=$pythonBuildDirPath && export PYTHONPATH=$appLibDirPath:$pythonLibDirPath && export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH:$pythonLibDirPath\""
        return "$envSetup && clear && echo 'Running: $filePath' && $pythonExecName $filePath && echo '' && echo '[Execution completed - Enter to exit]' && read junk && exit"
    }

    fun getEnhancedPythonShellCommand(context: Context): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecName = "libpython3.so"
        val envSetup = "export PATH=\$PATH:$appLibDirPath && export PYTHONHOME=$pythonBuildDirPath && export PYTHONPATH=$appLibDirPath:$pythonLibDirPath && export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH:$pythonLibDirPath\""
        return "$envSetup && clear && echo 'Python 3.11.5 Interactive Shell' && echo 'Type exit() to quit' && $pythonExecName && echo '[Enter to Exit]' && read junk && exit"
    }

    fun getEnhancedPipInstallCommand(context: Context, packageName: String): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecName = "libpython3.so"
        val envSetup = "export PATH=\$PATH:$appLibDirPath && export PYTHONHOME=$pythonBuildDirPath && export PYTHONPATH=$appLibDirPath:$pythonLibDirPath && export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH:$pythonLibDirPath\""
        return "$envSetup && clear && echo 'Installing package: $packageName' && $pythonExecName -m pip install --user --upgrade $packageName && echo '' && echo '[Installation completed - Enter to exit]' && read junk && exit"
    }

    // PROJECT-AWARE COMMANDS FOR PROJECT-BASED DEVELOPMENT
    
    /**
     * Get command to run a project file with proper project context
     */
    fun getProjectFileCommand(context: Context, projectPath: String, filePath: String): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecName = "libpython3.so"
        
        // Set up Python path to include project directory for imports
        val envSetup = "export PATH=\$PATH:$appLibDirPath && " +
                      "export PYTHONHOME=$pythonBuildDirPath && " +
                      "export PYTHONPATH=\"$projectPath:$appLibDirPath:$pythonLibDirPath\" && " +
                      "export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH:$pythonLibDirPath\""
        
        return "$envSetup && cd \"$projectPath\" && clear && " +
               "echo 'Running: ${File(filePath).name} (Project: ${File(projectPath).name})' && " +
               "$pythonExecName \"$filePath\" && echo '' && " +
               "echo '[Execution completed - Enter to exit]' && read junk && exit"
    }
    
    /**
     * Get command to run project main file
     */
    fun getProjectMainCommand(context: Context, projectPath: String, mainFile: String = "main.py"): String {
        val mainFilePath = "$projectPath${File.separator}$mainFile"
        return getProjectFileCommand(context, projectPath, mainFilePath)
    }
    
    /**
     * Get command to start Python shell in project context
     */
    fun getProjectShellCommand(context: Context, projectPath: String): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecName = "libpython3.so"
        
        val envSetup = "export PATH=\$PATH:$appLibDirPath && " +
                      "export PYTHONHOME=$pythonBuildDirPath && " +
                      "export PYTHONPATH=\"$projectPath:$appLibDirPath:$pythonLibDirPath\" && " +
                      "export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH:$pythonLibDirPath\""
        
        return "$envSetup && cd \"$projectPath\" && clear && " +
               "echo 'Python 3.11.5 Shell - Project: ${File(projectPath).name}' && " +
               "echo 'Project modules are available for import' && " +
               "$pythonExecName && echo '[Enter to Exit]' && read junk && exit"
    }
    
    /**
     * Get command to install project dependencies
     */
    fun getProjectDependencyInstallCommand(context: Context, projectPath: String): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecName = "libpython3.so"
        val requirementsFile = "$projectPath${File.separator}requirements.txt"
        
        val envSetup = "export PATH=\$PATH:$appLibDirPath && " +
                      "export PYTHONHOME=$pythonBuildDirPath && " +
                      "export PYTHONPATH=\"$projectPath:$appLibDirPath:$pythonLibDirPath\" && " +
                      "export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH:$pythonLibDirPath\""
        
        return "$envSetup && cd \"$projectPath\" && clear && " +
               "echo 'Installing dependencies for project: ${File(projectPath).name}' && " +
               "if [ -f \"requirements.txt\" ]; then " +
               "$pythonExecName -m pip install --user -r requirements.txt; " +
               "else echo 'No requirements.txt found'; fi && " +
               "echo '' && echo '[Installation completed - Enter to exit]' && read junk && exit"
    }
    
    /**
     * Get command for project terminal with all project context
     */
    fun getProjectTerminalCommand(context: Context, projectPath: String): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecName = "libpython3.so"
        
        val aliasCommand = "alias python=\"$pythonExecName\" && " +
                          "alias pip=\"$pythonExecName -m pip\" && " +
                          "alias python3=\"$pythonExecName\""
        
        val envSetup = "export PATH=\$PATH:$appLibDirPath && " +
                      "export PYTHONHOME=$pythonBuildDirPath && " +
                      "export PYTHONPATH=\"$projectPath:$appLibDirPath:$pythonLibDirPath\" && " +
                      "export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH:$pythonLibDirPath\""
        
        return "$envSetup && $aliasCommand && cd \"$projectPath\" && clear && " +
               "echo 'Python Pocket IDE - Project Terminal' && " +
               "echo 'Project: ${File(projectPath).name}' && " +
               "echo 'Working directory: $projectPath' && " +
               "echo 'Python 3.11.5 ready - project modules available for import'"
    }
    
    /**
     * Get command to run a specific Python module within a project
     */
    fun getProjectModuleCommand(context: Context, projectPath: String, moduleName: String): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        val pythonExecName = "libpython3.so"
        
        val envSetup = "export PATH=\$PATH:$appLibDirPath && " +
                      "export PYTHONHOME=$pythonBuildDirPath && " +
                      "export PYTHONPATH=\"$projectPath:$appLibDirPath:$pythonLibDirPath\" && " +
                      "export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH:$pythonLibDirPath\""
        
        return "$envSetup && cd \"$projectPath\" && clear && " +
               "echo 'Running module: $moduleName (Project: ${File(projectPath).name})' && " +
               "$pythonExecName -m $moduleName && echo '' && " +
               "echo '[Execution completed - Enter to exit]' && read junk && exit"
    }
    
    /**
     * Get environment setup string for project context (helper function)
     */
    private fun getProjectEnvironmentSetup(context: Context, projectPath: String): String {
        val appLibDirPath = context.applicationInfo.nativeLibraryDir
        val appFileDirPath = context.filesDir.absolutePath
        val pythonBuildDirPath = "$appFileDirPath/files/usr"
        val pythonLibDirPath = "$pythonBuildDirPath/lib"
        
        return "export PATH=\$PATH:$appLibDirPath && " +
               "export PYTHONHOME=$pythonBuildDirPath && " +
               "export PYTHONPATH=\"$projectPath:$appLibDirPath:$pythonLibDirPath\" && " +
               "export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH:$pythonLibDirPath\""
    }
}