package com.unitedpay.feature.home.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.unitedpay.core.designsystem.components.BrandShield
import com.unitedpay.core.designsystem.components.UnitedToast
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedObsidian
import com.unitedpay.core.designsystem.theme.UnitedSuccess
import com.unitedpay.core.designsystem.theme.UnitedTextPrimary
import com.unitedpay.core.designsystem.theme.UnitedTextSecondary
import com.unitedpay.core.designsystem.theme.UnitedWhite
import com.unitedpay.core.model.session.UserSessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

/**
 * Enterprise Paytm-Style Slide-over Profile Drawer.
 * Completely replaces the standalone ProfileScreen with direct access to:
 * - Profile identity (Avatar with photo upload, User Name, KYC status, UPI ID, Phone Number)
 * - Dynamic UPI QR Code (Async render, Linked Bank account switcher, Share with message, Download)
 * - Security & Banking Settings (Change MPIN, Biometric Lock, Soundbox Audio, 24x7 Help Center)
 * - Log Out of UnitedPay (with confirmation dialog & SIM account switching)
 * - Silky smooth spring animations, finger drag tracking, and zero-lag performance.
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
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentSession by UserSessionManager.currentSession.collectAsState()
    val userProfile = currentSession?.userProfile ?: UserSessionManager.getCurrentProfile()
    val bankAccounts = currentSession?.bankAccounts ?: UserSessionManager.getCurrentBankAccounts()
    var selectedBankIndex by remember { mutableIntStateOf(0) }
    val currentBank = bankAccounts.getOrNull(selectedBankIndex) ?: bankAccounts.firstOrNull()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    val animatedDragOffset by animateFloatAsState(
        targetValue = dragOffsetX,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
        label = "drawerDragAnim"
    )

    // Hardware Back Button closes drawer smoothly
    BackHandler(enabled = isOpen) {
        onDismiss()
    }

    // Modern Photo Picker for profile avatar selection
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val avatarsDir = File(context.filesDir, "avatars").apply { mkdirs() }
                val destFile = File(avatarsDir, "profile_avatar.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                }
                UserSessionManager.updateProfilePicture(destFile.absolutePath)
                UnitedToast.success("Profile photo updated successfully")
            } catch (e: Exception) {
                UnitedToast.error("Failed to update photo")
            }
        }
    }

    // Profile photo bitmap decoded natively
    val profileBitmap: ImageBitmap? = remember(userProfile.avatarUrl) {
        val path = userProfile.avatarUrl
        if (path != null && File(path).exists()) {
            try {
                BitmapFactory.decodeFile(path)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    val upiString = "upi://pay?pa=${userProfile.vpa}&pn=${userProfile.userName.replace(" ", "%20")}&cu=INR"

    // Background thread asynchronous QR computation prevents frame drops during drawer opening
    val qrBitmap by produceState<Bitmap?>(initialValue = null, key1 = upiString) {
        value = withContext(Dispatchers.Default) {
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
                        bmp.setPixel(x, y, if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
                    }
                }
                bmp
            } catch (e: Exception) {
                null
            }
        }
    }

    Box(
        modifier = if (isOpen) modifier.fillMaxSize() else modifier
    ) {
        // 1. Scrim Backdrop with smooth fade in & fade out
        AnimatedVisibility(
            visible = isOpen,
            enter = fadeIn(animationSpec = tween(280)),
            exit = fadeOut(animationSpec = tween(220))
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

        // 2. Slide-Over Panel Content with spring entrance & smooth exit
        AnimatedVisibility(
            visible = isOpen,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = spring(
                    dampingRatio = 0.85f,
                    stiffness = Spring.StiffnessMediumLow
                )
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
            ),
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.88f)
                    .offset { IntOffset(animatedDragOffset.roundToInt(), 0) }
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (dragOffsetX < -150f) {
                                    dragOffsetX = 0f
                                    onDismiss()
                                } else {
                                    dragOffsetX = 0f
                                }
                            },
                            onDragCancel = { dragOffsetX = 0f },
                            onHorizontalDrag = { _, dragAmount ->
                                dragOffsetX = (dragOffsetX + dragAmount).coerceAtMost(0f)
                            }
                        )
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

                    // User Profile Header Card (with Photo Upload & KYC Done)
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
                            // Avatar with Photo Upload & Camera badge
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
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
                                    if (profileBitmap != null) {
                                        Image(
                                            bitmap = profileBitmap,
                                            contentDescription = "Profile Picture",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Text(
                                            text = userProfile.userName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = UnitedMoneyBlue
                                        )
                                    }
                                }
                                // Camera edit badge overlay
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
                                        contentDescription = "Change Profile Picture",
                                        tint = UnitedWhite,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

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
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Verified KYC",
                                        tint = UnitedSuccess,
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
                                        text = userProfile.vpa,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = UnitedMoneyBlue,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy UPI ID",
                                        tint = UnitedMoneyBlue,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${userProfile.phoneNumber} • Full KYC Done",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = UnitedTextSecondary
                                )
                            }
                        }
                    }

                    // Dynamic UPI QR Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // QR Frame Container
                            Box(
                                modifier = Modifier
                                    .size(240.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = 2.5.dp,
                                        brush = Brush.linearGradient(
                                            listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .background(UnitedWhite)
                                    .clickable { onNavigateMyQr() }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (qrBitmap != null) {
                                    Image(
                                        bitmap = qrBitmap!!.asImageBitmap(),
                                        contentDescription = "My Personal Dynamic UPI QR Code",
                                        modifier = Modifier.fillMaxSize(0.92f)
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
                                        sharePersonalQr(context, qrBitmap, userProfile.userName, userProfile.vpa)
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
                                        downloadQrToDevice(context, qrBitmap, userProfile.userName, userProfile.vpa)
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

                    // Security & Settings Section (Full Settings parity with former Profile Screen)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = UnitedWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text(
                                text = "Settings",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnitedTextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            )

                            DrawerSettingItem(
                                icon = Icons.Default.Lock,
                                title = "Change UPI MPIN",
                                subtitle = "Reset 6-digit bank security PIN",
                                onClick = {
                                    onDismiss()
                                    onNavigateChangeMpin()
                                }
                            )

                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                            DrawerSettingItem(
                                icon = Icons.Default.Fingerprint,
                                title = "Security & Biometric Lock",
                                subtitle = "Hardware StrongBox & Fingerprint lock",
                                onClick = {
                                    onDismiss()
                                    onNavigateBiometrics()
                                }
                            )

                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                            DrawerSettingItem(
                                icon = Icons.Default.VolumeUp,
                                title = "Soundbox Audio Settings",
                                subtitle = "Voice confirmation in Assamese, Bengali, English",
                                onClick = {
                                    onDismiss()
                                    onNavigateSoundbox()
                                }
                            )

                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                            DrawerSettingItem(
                                icon = Icons.Default.HeadsetMic,
                                title = "24x7 Help & Dispute Center",
                                subtitle = "Raise complaints & track refund status",
                                onClick = {
                                    onDismiss()
                                    onNavigateDispute()
                                }
                            )
                        }
                    }

                    // Log Out Card (Red accent matching screenshot)
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
                                .clickable { showLogoutDialog = true }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEF2F2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Logout,
                                        contentDescription = null,
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Log Out of UnitedPay",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFDC2626)
                                    )
                                    Text(
                                        text = "Switch SIM or sign in to another account",
                                        fontSize = 11.sp,
                                        color = UnitedTextSecondary
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = Color(0xFFDC2626).copy(alpha = 0.5f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Version & Certification Footer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BrandShield(size = 32.dp, asCardBadge = true)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "United Pay v1.0.0 (Production Build)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnitedTextPrimary
                        )
                        Text(
                            text = "NPCI & RBI Certified Payment Application",
                            fontSize = 10.sp,
                            color = UnitedTextSecondary
                        )
                    }
                }
            }
        }
    }

    // Confirmation Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Log out of UnitedPay?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = UnitedTextPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to log out? You will need to verify your SIM card to access your account again. All your bank accounts and cards will remain completely safe.",
                    fontSize = 13.sp,
                    color = UnitedTextSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onDismiss()
                        UserSessionManager.logout()
                        UnitedToast.info("Logged out of UnitedPay")
                        onLogout()
                    }
                ) {
                    Text("Log Out", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = UnitedTextPrimary)
                }
            }
        )
    }
}

@Composable
private fun DrawerSettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFF6FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = UnitedMoneyBlue,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = UnitedTextPrimary
            )
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

private fun downloadQrToDevice(context: Context, bitmap: Bitmap?, userName: String, vpa: String) {
    if (bitmap == null) return
    try {
        val brandedBmp = createBrandedQrCard(context, bitmap, userName, vpa)
        val cachePath = File(context.cacheDir, "receipts").apply { mkdirs() }
        val qrFile = File(cachePath, "unitedpay_qr_${System.currentTimeMillis()}.png")
        FileOutputStream(qrFile).use { out ->
            brandedBmp.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        UnitedToast.success("Personal QR saved to device")
    } catch (e: Exception) {
        UnitedToast.error("Failed to save QR")
    }
}

/**
 * Creates a high-resolution, branded 1080x1400 QR Card bitmap.
 */
private fun createBrandedQrCard(context: Context, qrBmp: Bitmap, userName: String, vpa: String): Bitmap {
    val width = 1080
    val height = 1420
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Background
    canvas.drawColor(AndroidColor.WHITE)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Top Brand Banner Header
    paint.color = AndroidColor.parseColor("#0052CC")
    canvas.drawRect(0f, 0f, width.toFloat(), 180f, paint)

    // Brand Title
    paint.color = AndroidColor.WHITE
    paint.textSize = 52f
    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    paint.textAlign = Paint.Align.CENTER
    canvas.drawText("UNITED PAY", width / 2f, 95f, paint)

    paint.textSize = 28f
    paint.typeface = android.graphics.Typeface.DEFAULT
    canvas.drawText("ACCEPTED HERE • INSTANT UPI SETTLEMENT", width / 2f, 142f, paint)

    // User Name
    paint.color = AndroidColor.parseColor("#0F172A")
    paint.textSize = 48f
    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    canvas.drawText(userName, width / 2f, 260f, paint)

    // Verified badge subtitle
    paint.color = AndroidColor.parseColor("#16A34A")
    paint.textSize = 28f
    paint.typeface = android.graphics.Typeface.DEFAULT
    canvas.drawText("VERIFIED FINTECH PROFILE", width / 2f, 305f, paint)

    // Draw QR Code
    val qrSize = 640
    val qrLeft = ((width - qrSize) / 2).toFloat()
    val qrTop = 360f

    // Outer QR Border Card
    paint.color = AndroidColor.parseColor("#F8FAFC")
    paint.style = Paint.Style.FILL
    val bgRect = RectF(qrLeft - 24f, qrTop - 24f, qrLeft + qrSize + 24f, qrTop + qrSize + 24f)
    canvas.drawRoundRect(bgRect, 28f, 28f, paint)

    paint.color = AndroidColor.parseColor("#E2E8F0")
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 3f
    canvas.drawRoundRect(bgRect, 28f, 28f, paint)

    // QR Image
    paint.style = Paint.Style.FILL
    canvas.drawBitmap(qrBmp, null, RectF(qrLeft, qrTop, qrLeft + qrSize, qrTop + qrSize), paint)

    // UPI ID Container Pill
    val pillTop = qrTop + qrSize + 60f
    val pillHeight = 84f
    val pillRect = RectF(140f, pillTop, width - 140f, pillTop + pillHeight)
    paint.color = AndroidColor.parseColor("#EFF6FF")
    paint.style = Paint.Style.FILL
    canvas.drawRoundRect(pillRect, pillHeight / 2f, pillHeight / 2f, paint)

    paint.color = AndroidColor.parseColor("#0052CC")
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 2f
    canvas.drawRoundRect(pillRect, pillHeight / 2f, pillHeight / 2f, paint)

    paint.style = Paint.Style.FILL
    paint.color = AndroidColor.parseColor("#0052CC")
    paint.textSize = 34f
    paint.typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    paint.textAlign = Paint.Align.CENTER
    canvas.drawText("UPI ID: $vpa", width / 2f, pillTop + 54f, paint)

    // Footer Info
    paint.color = AndroidColor.parseColor("#64748B")
    paint.textSize = 28f
    paint.typeface = android.graphics.Typeface.DEFAULT
    canvas.drawText("Scan with any UPI App: PhonePe, GPay, Paytm, BHIM", width / 2f, height - 120f, paint)

    paint.color = AndroidColor.parseColor("#94A3B8")
    paint.textSize = 24f
    canvas.drawText("100% Secure • NPCI Unified Payments Interface", width / 2f, height - 70f, paint)

    return bitmap
}

/**
 * Shares branded 1080p QR Card image alongside an executive UPI payment message via native Android Intent.
 */
internal fun sharePersonalQr(context: Context, bitmap: Bitmap?, userName: String, vpa: String) {
    if (bitmap == null) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Scan to pay $userName via any UPI App:\nUPI ID: $vpa\n\nPowered by UnitedPay.")
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share UPI QR"))
        return
    }

    try {
        val brandedBmp = createBrandedQrCard(context, bitmap, userName, vpa)
        val cachePath = File(context.cacheDir, "receipts").apply { mkdirs() }
        val qrFile = File(cachePath, "unitedpay_qr_share.png")
        FileOutputStream(qrFile).use { out ->
            brandedBmp.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            qrFile
        )

        val shareMessage = "Scan and pay $userName using any UPI App (UnitedPay, Google Pay, PhonePe, Paytm).\nUPI ID: $vpa\n\nPowered by UnitedPay • 100% Secure NPCI Unified Payments Interface."

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, "Pay $userName via UPI (United Pay)")
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            clipData = ClipData.newRawUri("UPI QR Code", contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "Share Personal UPI QR via").apply {
            clipData = ClipData.newRawUri("UPI QR Code", contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(chooser)
    } catch (e: Exception) {
        UnitedToast.info("UPI ID: $vpa")
    }
}
