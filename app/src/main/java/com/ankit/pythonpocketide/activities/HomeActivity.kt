
package com.ankit.pythonpocketide.activities
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.hzy.libp7zip.P7ZipApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.dependency
import com.ankit.pythonpocketide.dataStore.SettingsDataStore
import com.ankit.pythonpocketide.ui.theme.PythonPocketIDETheme
import com.ankit.pythonpocketide.utils.Keys
import com.ankit.pythonpocketide.utils.PythonFileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
class HomeActivity: ComponentActivity() {
    private lateinit var dataStore:SettingsDataStore
    private val isFileExtracting= mutableStateOf(false)
    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStore=SettingsDataStore(applicationContext)
        // create a directory for python files if not exists
        val pythonFilesDir = File(filesDir.absolutePath+"/pythonFiles")
        if (!pythonFilesDir.exists()) {
            pythonFilesDir.mkdir()
        }
        PythonFileManager.filesDir=pythonFilesDir.absolutePath
        PythonFileManager.init()
        setContent()
        {
            PythonPocketIDETheme()
            {
                if (isFileExtracting.value){
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text(text = "Extracting files...")
                    }
                }
                else{
                    val navHostEngine = rememberAnimatedNavHostEngine(
                        navHostContentAlignment = Alignment.TopCenter,
                        rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING)

                    DestinationsNavHost(
                        navGraph = com.ankit.pythonpocketide.ui.screens.NavGraphs.root,
                        dependenciesContainerBuilder = { dependency(this@HomeActivity) },
                        engine = navHostEngine
                    )
                }
            }
        }
        extractFiles()
    }
    private fun extractFiles() {
        isFileExtracting.value = true
        CoroutineScope(Dispatchers.IO).launch {
            if (dataStore.areFilesExtracted.first() == true) {
                isFileExtracting.value = false
            } else {
                dataStore.updateFileStatus(false)
                CoroutineScope(Dispatchers.IO).launch {
                    val temp7zStream = assets.open("python.7z")
                    val file = File("${filesDir.absolutePath}/python.7z")
                    file.createNewFile()
                    Files.copy(temp7zStream,file.toPath(),StandardCopyOption.REPLACE_EXISTING)
                    val exitCode = P7ZipApi.executeCommand("7z x ${file.absolutePath} -o${filesDir.absolutePath}")
                    Log.d(TAG, "extractFiles: $exitCode")
                    file.delete()
                    temp7zStream.close()
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStore.updateFileStatus(true)
                    }
                    isFileExtracting.value = false
                }
            }
        }
    }
    fun startEditorActivity(file: File)
    {
        val mIntent = Intent(this, EditorActivity::class.java)
        mIntent.putExtra(Keys.KEY_FILE_PATH,file.absoluteFile.toString())
        startActivity(mIntent)
    }

    companion object {
        const val TAG = "WelcomeActivity"
    }
}