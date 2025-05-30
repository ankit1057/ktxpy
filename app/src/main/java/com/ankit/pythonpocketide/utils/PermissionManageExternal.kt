package com.ankit.pythonpocketide.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.RequiresApi

@RequiresApi(30)
object PermissionManageExternal {
    fun request(activity: Activity): Boolean {
        try {
            val uri = Uri.parse("package:" + com.ankit.pythonpocketide.BuildConfig.APPLICATION_ID)
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
            activity.startActivity(intent)
            return true
        } catch (ignore: Exception) {
        }
        try {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            activity.startActivity(intent)
            return true
        } catch (ignore: Exception) {
        }
        return false
    }
}