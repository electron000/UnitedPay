package com.unitedpay.feature.home.profile

import com.unitedpay.core.designsystem.components.UnitedToast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.common.result.Resource
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.SoundboxConfig
import com.unitedpay.core.model.api.UnitedPayApi
import com.unitedpay.core.model.mock.UnitedMockData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundboxSettingsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var soundboxConfig by remember { mutableStateOf(UnitedMockData.soundboxConfig) }
    var selectedLanguage by remember { mutableStateOf(soundboxConfig.selectedLanguage) }

    LaunchedEffect(Unit) {
        UnitedPayApi.client.communications.getSoundboxConfig().collect { result ->
            if (result is Resource.Success) {
                soundboxConfig = result.data
                selectedLanguage = result.data.selectedLanguage
            }
        }
    }

    val languages = soundboxConfig.availableLanguages

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("Smart Soundbox Language", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = UnitedTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UnitedWhite,
                    titleContentColor = UnitedTextPrimary,
                    navigationIconContentColor = UnitedTextPrimary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = UnitedWhite)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("United Soundbox Pro (4G)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = UnitedTextPrimary)
                            Text("Instant voice confirmation for all received payments", fontSize = 12.sp, color = UnitedTextSecondary)
                        }
                    }
                }
            }

            item {
                Text("SELECT VOICE ANNOUNCEMENT LANGUAGE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnitedTextSecondary, letterSpacing = 1.sp)
            }

            items(languages) { (lang, sample) ->
                val isSelected = selectedLanguage == lang
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedLanguage = lang },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFEFF6FF) else UnitedWhite)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (isSelected) UnitedMoneyBlue else UnitedBorderLight, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(lang, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = if (isSelected) UnitedMoneyBlue else UnitedTextPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("\"$sample\"", fontSize = 12.sp, color = UnitedTextSecondary)
                        }
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = UnitedMoneyBlue, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        UnitedToast.info("Voice preview: 100 received on United Pay!")
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF), contentColor = UnitedMoneyBlue)
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test Voice Alert", fontWeight = FontWeight.Bold, color = UnitedMoneyBlue, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        UnitedToast.success("Soundbox language updated to $selectedLanguage")
                        onBackClick()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UnitedMoneyBlue, contentColor = UnitedWhite)
                ) {
                    Text("Apply Language", fontWeight = FontWeight.Bold, color = UnitedWhite, fontSize = 15.sp)
                }
            }
        }
    }
}
