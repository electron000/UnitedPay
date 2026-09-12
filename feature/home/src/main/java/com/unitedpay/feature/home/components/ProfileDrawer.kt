package com.unitedpay.feature.home.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.components.UnitedToast
import com.unitedpay.core.designsystem.theme.*
import com.unitedpay.core.model.session.UserSessionManager
import java.io.File
import java.io.FileOutputStream

/**
 * Paytm-Style Slide-Over Profile & Dynamic QR Drawer.
 * Slides in from the left when clicking the top-left avatar button on HomeScreen.
 * Displays user identity, dynamic UPI QR code with two-tone cyan/cobalt frame,
 * primary linked bank, quick share/download actions, and full fintech settings menu.
 */
@Composable
fun ProfileDrawer(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onNavigateChangeMpin: () -> Unit = {},
    onNavigateBiometrics: () -> Unit = {},
    onNavigateSoundbox: () -> Unit = {},
    onNavigateDispute: () -> Unit = {},
    onNavigateMyQr: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentSession by UserSessionManager.currentSession.collectAsState()
    val userProfile = currentSession?.userProfile ?: UserSessionManager.getCurrentProfile()
    val bankAccounts = currentSession?.bankAccounts ?: UserSessionManager.getCurrentBankAccounts()
    var selectedBankIndex by remember { mutableIntStateOf(0) }
    val currentBank = bankAccounts.getOrNull(selectedBankIndex) ?: bankAccounts.firstOrNull()

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

    if (isOpen) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            // 1. Scrim Backdrop
            AnimatedVisibility(
                visible = isOpen,
                enter = fadeIn(tween(250)),
                exit = fadeOut(tween(200))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDismiss
                        )
                )
            }

            // 2. Slide-Over Panel Content
            AnimatedVisibility(
                visible = isOpen,
                enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(280)),
                exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(240)),
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.88f)
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                if (dragAmount < -20) {
                                    onDismiss()
                                }
                            }
                        },
                    color = Color(0xFFF6F8FB),
                    shadowElevation = 16.dp,
                    shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 24.dp)
                    ) {
                        // Top Header Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Close Profile Drawer",
                                    tint = UnitedTextPrimary
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    BrandShield(size = 20.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "UNITED PAY",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = UnitedMoneyBlue,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Text(
                                    text = "Accepted Here",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = UnitedTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.width(36.dp))
                        }

                        // User Profile Header Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar Circle with Camera badge
                                Box(
                                    modifier = Modifier.size(54.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD))
                                                )
                                            )
                                            .border(2.dp, UnitedMoneyBlue.copy(alpha = 0.3f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = userProfile.userName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = UnitedMoneyBlue
                                        )
                                    }
                                    // Camera edit badge
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF0F172A))
                                            .border(1.5.dp, UnitedWhite, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = null,
                                            tint = UnitedWhite,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = userProfile.userName,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = UnitedTextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified Profile",
                                            tint = Color(0xFF0078DF),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            copyToClipboard(context, "UPI ID", userProfile.vpa)
                                        }
                                    ) {
                                        Text(
                                            text = "UPI ID: ${userProfile.vpa}",
                                            fontSize = 12.sp,
                                            color = UnitedTextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy UPI ID",
                                            tint = UnitedMoneyBlue,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Prominent Dynamic QR Container (Matching Paytm Two-Tone Border)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Two-tone border container
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color(0xFF00BAF2), Color(0xFF002970))
                                            )
                                        )
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(UnitedWhite),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (qrBitmap != null) {
                                        Image(
                                            bitmap = qrBitmap.asImageBitmap(),
                                            contentDescription = "Personal UPI QR Code",
                                            modifier = Modifier
                                                .fillMaxSize(0.90f)
                                        )
                                    } else {
                                        CircularProgressIndicator(color = UnitedMoneyBlue, modifier = Modifier.size(36.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Linked Bank Indicator
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF8FAFC))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF0078DF)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = currentBank?.bankName?.take(1) ?: "B",
                                                color = UnitedWhite,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${currentBank?.bankName ?: "Bank"} - ${currentBank?.accountNumberMasked?.takeLast(4) ?: "4821"}",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = UnitedTextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Text(
                                        text = "Change Bank",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedMoneyBlue,
                                        modifier = Modifier.clickable {
                                            selectedBankIndex = (selectedBankIndex + 1) % bankAccounts.size
                                            val newBank = bankAccounts[selectedBankIndex]
                                            UnitedToast.success("Primary account switched to ${newBank.bankName}")
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Action Buttons (Share & Download)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            shareQrImage(context, qrBitmap, userProfile.userName, userProfile.vpa)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = UnitedTextPrimary),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = UnitedTextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Share", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            UnitedToast.success("QR saved to device photos")
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = UnitedTextPrimary),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddCircleOutline,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = UnitedTextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Add to Home", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                    }
                                }
                            }
                        }

                        // Settings Section
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Settings",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = UnitedTextPrimary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search Settings",
                                        tint = UnitedTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                DrawerSettingRow(
                                    icon = Icons.Default.AccountBalanceWallet,
                                    title = "Pocket Money / UPI Circle",
                                    subtitle = "Give dependents UPI access under your control",
                                    badge = "New",
                                    onClick = {
                                        UnitedToast.info("Opening UPI Circle...")
                                    }
                                )

                                HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

                                DrawerSettingRow(
                                    icon = Icons.Default.Lock,
                                    title = "Change UPI MPIN",
                                    subtitle = "Reset 6-digit banking pin",
                                    onClick = {
                                        onDismiss()
                                        onNavigateChangeMpin()
                                    }
                                )

                                HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

                                DrawerSettingRow(
                                    icon = Icons.Default.Fingerprint,
                                    title = "Security & Biometric Lock",
                                    subtitle = "Hardware StrongBox & Fingerprint lock",
                                    onClick = {
                                        onDismiss()
                                        onNavigateBiometrics()
                                    }
                                )

                                HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

                                DrawerSettingRow(
                                    icon = Icons.Default.VolumeUp,
                                    title = "Soundbox Audio Settings",
                                    subtitle = "Voice confirmation in Assamese, Bengali, Hindi",
                                    onClick = {
                                        onDismiss()
                                        onNavigateSoundbox()
                                    }
                                )

                                HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))

                                DrawerSettingRow(
                                    icon = Icons.Default.SupportAgent,
                                    title = "24x7 Help & Dispute Center",
                                    subtitle = "Raise complaints & track refund status",
                                    onClick = {
                                        onDismiss()
                                        onNavigateDispute()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DrawerSettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = UnitedMoneyBlue,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = UnitedTextPrimary
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFACC15))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF854D0E)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = UnitedTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(12.dp)
        )
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    UnitedToast.success("$label copied to clipboard")
}

private fun shareQrImage(context: Context, bitmap: Bitmap?, userName: String, vpa: String) {
    if (bitmap == null) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Pay $userName on UnitedPay UPI: $vpa")
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share UPI QR"))
        return
    }

    try {
        val cachePath = File(context.cacheDir, "receipts").apply { mkdirs() }
        val qrFile = File(cachePath, "my_upi_qr.png")
        val stream = FileOutputStream(qrFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()

        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            qrFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_TEXT, "Scan to pay $userName via any UPI app:\nUPI ID: $vpa")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Personal UPI QR"))
    } catch (e: Exception) {
        UnitedToast.info("UPI ID: $vpa")
    }
}
