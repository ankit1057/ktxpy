
package com.ankit.pythonpocketide.viewModels

import android.app.Application
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File


class HomeScreenViewModel(application: Application) :AndroidViewModel(application) {

    private val _mDrawerState= mutableStateOf(DrawerState(DrawerValue.Closed))
    val mDrawerState
        get()=_mDrawerState
    private val _mFileName= mutableStateOf("")
    val mFileName
        get() = _mFileName
    private val _mDialogState= mutableStateOf(false)
    val mDialogState
        get() = _mDialogState
    fun saveFile(fileName: String){
        CoroutineScope(Dispatchers.IO).run {
            // create a new file in files-dir
            val fileDir = File(getApplication<Application>().filesDir.toString()+"/pythonFiles")
            val nameWithoutSpaces=fileName.replace(" ","_")
            val file= File(fileDir,"$nameWithoutSpaces.py")
            file.createNewFile()
        }
    }
    fun changeFileName(fileName: String) {
        _mFileName.value=fileName
    }
    fun dismissDialog() {
        _mFileName.value=""
        _mDialogState.value=false

    }
    fun showDialog() {
        _mDialogState.value=true
    }
}