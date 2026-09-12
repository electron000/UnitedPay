package com.unitedpay.feature.home.profile

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.unitedpay.core.designsystem.components.UnitedToast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.session.UserSessionManager
import com.unitedpay.feature.home.components.createBrandedQrCard
import com.unitedpay.feature.home.components.sharePersonalQr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyQrScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var copiedToast by remember { mutableStateOf(false) }

    val currentSession by UserSessionManager.currentSession.collectAsState()
    val userProfile = currentSession?.userProfile ?: UserSessionManager.getCurrentProfile()
    val primaryBank = currentSession?.bankAccounts?.firstOrNull() ?: UserSessionManager.getCurrentBankAccounts().firstOrNull()

    val upiString = "upi://pay?pa=${userProfile.vpa}&pn=${userProfile.userName.replace(" ", "%20")}&cu=INR"
    val qrBitmap: Bitmap? = remember(upiString) {
        try {
            val hints = mapOf(
                EncodeHintType.MARGIN to 1,
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H
            )
            val bitMatrix = QRCodeWriter().encode(upiString, BarcodeFormat.QR_CODE, 512, 512, hints)
            val w = bitMatrix.width
            val h = bitMatrix.height
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            for (x in 0 until w) {
                for (y in 0 until h) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            bmp
        } catch (e: Exception) {
            null
        }
    }

    Scaffold(
        containerColor = UnitedCanvasLight,
        topBar = {
            TopAppBar(
                title = { Text("My QR Code", fontWeight = FontWeight.Bold, color = UnitedTextPrimary, fontSize = 18.sp) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Brand
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BrandShield(size = 28.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("United Pay UPI", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = UnitedMoneyBlue)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // User Identity
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(UnitedMoneyBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        val initials = userProfile.userName.split(" ")
                            .take(2)
                            .mapNotNull { it.firstOrNull()?.toString() }
                            .joinToString("")
                            .ifEmpty { "UP" }
                        Text(initials, color = UnitedWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(userProfile.userName, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = UnitedTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEFF6FF))
                            .clickable {
                                UnitedToast.success("UPI ID copied: ${userProfile.vpa}")
                                copiedToast = true
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(userProfile.vpa, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = UnitedMoneyBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = UnitedMoneyBlue, modifier = Modifier.size(15.dp))
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .border(2.dp, UnitedBorderLight, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (qrBitmap != null) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "Real UPI QR Code",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("Generating QR...", fontSize = 12.sp, color = UnitedTextSecondary)
                        }

                        // Central Shield Emblem
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(UnitedWhite)
                                .border(1.dp, UnitedBorderLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            BrandShield(size = 24.dp)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text("Scan with any UPI app to pay me", fontSize = 12.sp, color = UnitedTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    val bankDisplay = if (primaryBank != null) {
                        "${primaryBank.bankName} (${primaryBank.accountNumberMasked})"
                    } else {
                        "UnitedPay UPI Payments"
                    }
                    Text(bankDisplay, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = UnitedTextPrimary)
                }
            }

            // Action Buttons
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        sharePersonalQr(context, qrBitmap, userProfile.userName, userProfile.vpa)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .defaultMinSize(minHeight = 52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UnitedMoneyBlue,
                        contentColor = UnitedWhite
                    )
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = UnitedWhite, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share QR Code", fontWeight = FontWeight.Bold, color = UnitedWhite, fontSize = 15.sp)
                }

                OutlinedButton(
                    onClick = {
                        if (qrBitmap != null) {
                            try {
                                val brandedBmp = createBrandedQrCard(context, qrBitmap, userProfile.userName, userProfile.vpa)
                                val filename = "UnitedPay_QR_${System.currentTimeMillis()}.png"
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    val values = ContentValues().apply {
                                        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                                        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/UnitedPay")
                                        put(MediaStore.Images.Media.IS_PENDING, 1)
                                    }
                                    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                                    if (uri != null) {
                                        val outputStream: java.io.OutputStream? = context.contentResolver.openOutputStream(uri)
                                        outputStream?.use { out ->
                                            brandedBmp.compress(Bitmap.CompressFormat.PNG, 100, out)
                                        }
                                        values.clear()
                                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
                                        context.contentResolver.update(uri, values, null, null)
                                    }
                                } else {
                                    @Suppress("DEPRECATION")
                                    MediaStore.Images.Media.insertImage(
                                        context.contentResolver,
                                        brandedBmp,
                                        filename,
                                        "UnitedPay QR"
                                    )
                                }
                                UnitedToast.success("Branded QR Card saved to Gallery successfully")
                            } catch (e: Exception) {
                                UnitedToast.error("Saved to Gallery: ${e.localizedMessage}")
                            }
                        } else {
                            UnitedToast.warning("QR Code not ready")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .defaultMinSize(minHeight = 52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = UnitedMoneyBlue)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = UnitedMoneyBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save to Gallery", fontWeight = FontWeight.Bold, color = UnitedMoneyBlue, fontSize = 15.sp)
                }
            }
        }
    }
}
