package com.unitedpay.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Toast semantic categorization matching React modern toast libraries (Sonner / React Hot Toast).
 */
enum class ToastType {
    SUCCESS,
    ERROR,
    INFO,
    WARNING
}

data class ToastData(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val type: ToastType = ToastType.INFO,
    val durationMs: Long = 2500L
)

/**
 * Global reactive Toast controller accessible from any screen or component.
 */
object UnitedToast {
    private val _currentToast = MutableStateFlow<ToastData?>(null)
    val currentToast: StateFlow<ToastData?> = _currentToast.asStateFlow()

    fun show(message: String, type: ToastType = ToastType.INFO, durationMs: Long = 2500L) {
        _currentToast.value = ToastData(
            id = System.currentTimeMillis(),
            message = message,
            type = type,
            durationMs = durationMs
        )
    }

    fun success(message: String) = show(message, ToastType.SUCCESS)
    fun error(message: String) = show(message, ToastType.ERROR)
    fun info(message: String) = show(message, ToastType.INFO)
    fun warning(message: String) = show(message, ToastType.WARNING)

    fun dismiss() {
        _currentToast.value = null
    }
}

/**
 * Host overlay that renders floating, animated React-style toasts at the top of the screen.
 */
@Composable
fun UnitedToastHost(
    modifier: Modifier = Modifier
) {
    val toastData by UnitedToast.currentToast.collectAsState()

    LaunchedEffect(toastData?.id) {
        val toast = toastData ?: return@LaunchedEffect
        delay(toast.durationMs)
        if (UnitedToast.currentToast.value?.id == toast.id) {
            UnitedToast.dismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(bottom = 24.dp, start = 20.dp, end = 20.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = toastData != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn() + scaleIn(initialScale = 0.92f),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut() + scaleOut(targetScale = 0.92f)
        ) {
            toastData?.let { toast ->
                UnitedToastPill(
                    toast = toast,
                    onDismiss = { UnitedToast.dismiss() }
                )
            }
        }
    }
}

@Composable
private fun UnitedToastPill(
    toast: ToastData,
    onDismiss: () -> Unit
) {
    val (icon: ImageVector, iconColor: Color, badgeBgColor: Color) = when (toast.type) {
        ToastType.SUCCESS -> Triple(
            Icons.Default.CheckCircle,
            Color(0xFF10B981),
            Color(0xFFECFDF5)
        )
        ToastType.ERROR -> Triple(
            Icons.Default.Error,
            Color(0xFFEF4444),
            Color(0xFFFEF2F2)
        )
        ToastType.WARNING -> Triple(
            Icons.Default.Warning,
            Color(0xFFF59E0B),
            Color(0xFFFFFBEB)
        )
        ToastType.INFO -> Triple(
            Icons.Default.Info,
            UnitedMoneyBlue,
            UnitedMoneyBlue.copy(alpha = 0.10f)
        )
    }

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .shadow(
                elevation = 14.dp,
                shape = shape,
                spotColor = Color(0xFF0F172A).copy(alpha = 0.16f),
                ambientColor = Color(0xFF0F172A).copy(alpha = 0.08f)
            )
            .clip(shape)
            .background(Color.White.copy(alpha = 0.96f))
            .border(
                BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.90f)),
                shape
            )
            .clickable(onClick = onDismiss)
            .padding(horizontal = 16.dp, vertical = 11.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(badgeBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = toast.message,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
