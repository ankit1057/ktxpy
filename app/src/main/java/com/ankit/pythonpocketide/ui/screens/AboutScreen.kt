package com.ankit.pythonpocketide.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import com.ankit.pythonpocketide.R

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
fun AboutScreen(){
Scaffold(
topBar =
{
    TopAppBar(title = { Text(text = "About", fontFamily =FontFamily(Font(R.font.roboto_condensed_bold))) },
        modifier = Modifier.padding(10.dp,0.dp,10.dp,2.dp),
        actions = {
            Icon(painter = painterResource(id = R.drawable.app_icon), contentDescription = null)
        })
}
){
    Column(modifier = Modifier.padding(it))
    {
        Divider()
        Text(modifier = Modifier.padding(12.dp),text = "Python Pocket IDE (PPIDE)", fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.roboto_condensed_bold)))
        Text(modifier = Modifier.padding(12.dp),text = "Developer = Ankit", fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.roboto_condensed_bold)))
        Text(modifier = Modifier.padding(12.dp),text = "Final Year Project - MCA", fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.roboto_condensed_bold)))
        Text(modifier = Modifier.padding(12.dp),text = "Chandigarh University (Batch July 2023)", fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.roboto_condensed_bold)))
        Divider(modifier = Modifier.padding(12.dp))
        Text(modifier = Modifier.padding(20.dp),text = "Python Pocket IDE is an advanced mobile development environment for Python programming. Enhanced with modern Material Design 3, AI-powered features, and comprehensive educational tools. Built on Python 3.12+ with cross-compiled binaries for optimal Android performance.", fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.custom_sans)))
        Divider(modifier = Modifier.padding(12.dp))
        Text(text = "Based on KtxPy by PsiCodes (Pranjal)", fontSize = 13.sp, fontFamily = FontFamily(Font(R.font.roboto_condensed_bold)), modifier = Modifier.padding(12.dp,6.dp,2.dp,6.dp))
        Text(text = "Special Thanks to Rosemoe, Termux team & hzy3774", fontSize = 13.sp, fontFamily = FontFamily(Font(R.font.roboto_condensed_bold)), modifier = Modifier.padding(12.dp,6.dp,2.dp,6.dp))
        Divider(modifier = Modifier.padding(12.dp))
        Text(modifier = Modifier.padding(12.dp),text = "License: GPL v3", fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.custom_sans)))
        Text(modifier = Modifier.padding(12.dp),text = "Version: 2.0.0 (PPIDE Enhanced)", fontSize = 12.sp, fontFamily = FontFamily(Font(R.font.custom_sans)))
    }
}}
