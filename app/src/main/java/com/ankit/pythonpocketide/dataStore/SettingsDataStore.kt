
package com.ankit.pythonpocketide.dataStore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataStore(private val mContext: Context) {
       companion object{
          val Context.dataStore by preferencesDataStore(
            name = "Settings"
        )
    }

    private object PreferencesKeys {
        val Theme = stringPreferencesKey("Theme")
        val areFilesExtracted= booleanPreferencesKey("FilesStatus")
    }
    val mThemeString: Flow<String?> = mContext.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.Theme]
    }
    val  areFilesExtracted: Flow<Boolean?> = mContext.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.areFilesExtracted]
    }
    suspend fun updateTheme(mTheme: String) {
        mContext.dataStore.edit { preferences ->
            preferences[PreferencesKeys.Theme] = mTheme
        }
    }
    suspend fun updateFileStatus(fileStatus:Boolean) {
        mContext.dataStore.edit { preferences ->
            preferences[PreferencesKeys.areFilesExtracted] = fileStatus
        }
    }
}