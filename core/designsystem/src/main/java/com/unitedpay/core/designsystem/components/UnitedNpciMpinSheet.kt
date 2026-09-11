package com.unitedpay.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedSuccess
import com.unitedpay.core.designsystem.theme.UnitedTextPrimary
import com.unitedpay.core.designsystem.theme.UnitedTextSecondary
import com.unitedpay.core.designsystem.theme.UnitedWhite

/**
 * Standardized NPCI-compliant 6-Digit UPI MPIN sheet content.
 * Enforces strict 6-digit MPIN entry with isolated circular soft keypad.
 */
@Composable
fun UnitedNpciMpinSheet(
    title: String = "NPCI UPI SECURE MPIN",
    subtitle: String,
    pinLength: Int = 6,
    onPinSubmitted: (CharArray) -> Unit,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val enteredDigits = remember { mutableStateListOf<Char>() }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = UnitedWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // NPCI Brand Header Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "NPCI Protected",
                    tint = UnitedSuccess,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "  $title",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnitedSuccess,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle Description (e.g. Paying ₹X to Y or Checking Balance)
            Text(
                text = subtitle,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = UnitedTextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 6-Pin Dots Indicator
            UpiPinDotsIndicator(
                pinLength = pinLength,
                enteredCount = enteredDigits.size
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Numeric Keypad (4 rows)
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("DEL", "0", "OK")
            )

            for (row in keys) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (key in row) {
                        val isPinComplete = enteredDigits.size == pinLength
                        MpinKeypadButton(
                            text = key,
                            isComplete = isPinComplete,
                            onClick = {
                                when (key) {
                                    "DEL" -> {
                                        if (enteredDigits.isNotEmpty()) enteredDigits.removeLast()
                                    }
                                    "OK" -> {
                                        if (enteredDigits.size == pinLength) {
                                            onPinSubmitted(enteredDigits.toCharArray())
                                        }
                                    }
                                    else -> {
                                        if (enteredDigits.size < pinLength) {
                                            enteredDigits.add(key[0])
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun MpinKeypadButton(
    text: String,
    isComplete: Boolean,
    onClick: () -> Unit
) {
    val isOk = text == "OK"
    val bg = when {
        isOk && isComplete -> UnitedMoneyBlue
        else -> Color(0xFFF8FAFD)
    }

    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        when (text) {
            "DEL" -> Icon(
                imageVector = Icons.Default.Backspace,
                contentDescription = "Delete",
                tint = UnitedTextPrimary,
                modifier = Modifier.size(22.dp)
            )
            "OK" -> Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Submit",
                tint = if (isComplete) UnitedWhite else Color(0xFF94A3B8),
                modifier = Modifier.size(26.dp)
            )
            else -> Text(
                text = text,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = UnitedTextPrimary
            )
        }
    }
}

/**
 * Standardized NPCI UPI 6-Digit MPIN Modal Bottom Sheet wrapper.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitedNpciMpinModalSheet(
    subtitle: String,
    onDismissRequest: () -> Unit,
    onPinSubmitted: (CharArray) -> Unit,
    title: String = "NPCI UPI SECURE MPIN",
    pinLength: Int = 6,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = UnitedWhite
    ) {
        UnitedNpciMpinSheet(
            title = title,
            subtitle = subtitle,
            pinLength = pinLength,
            onPinSubmitted = onPinSubmitted,
            onDismiss = onDismissRequest
        )
    }
}
