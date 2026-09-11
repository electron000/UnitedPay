package com.unitedpay.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.theme.UnitedBorderLight
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedTextPrimary
import com.unitedpay.core.designsystem.theme.UnitedTextSecondary
import com.unitedpay.core.designsystem.theme.UnitedWhite

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Standardized Paytm & UPI-style centered amount input component.
 *
 * Centers the Rupee symbol (₹) and the amount digits together as a unified cluster
 * in the center of the field, preventing the Rupee symbol from being pinned to the
 * extreme left edge and ensuring digits expand symmetrically from the center.
 */
@Composable
fun UnitedCenteredAmountField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxDigits: Int = 6,
    symbolSize: TextUnit = 36.sp,
    amountSize: TextUnit = 42.sp,
    symbolColor: Color = UnitedMoneyBlue,
    textColor: Color = UnitedTextPrimary,
    placeholder: String = "0",
    enabled: Boolean = true
) {
    val focusRequester = remember { FocusRequester() }
    val visualTransformation = remember(symbolColor, symbolSize, textColor, amountSize, placeholder) {
        VisualTransformation { text ->
            val prefix = "₹ "
            val isPlaceholder = text.text.isEmpty()
            val displayText = if (isPlaceholder) placeholder else text.text

            val builder = AnnotatedString.Builder()
            builder.pushStyle(
                SpanStyle(
                    color = symbolColor,
                    fontSize = symbolSize,
                    fontWeight = FontWeight.Bold
                )
            )
            builder.append(prefix)
            builder.pop()

            builder.pushStyle(
                SpanStyle(
                    color = if (isPlaceholder) Color(0xFFCBD5E1) else textColor,
                    fontSize = amountSize,
                    fontWeight = FontWeight.Bold
                )
            )
            builder.append(displayText)
            builder.pop()

            val offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return (offset + prefix.length).coerceAtMost(prefix.length + displayText.length)
                }

                override fun transformedToOriginal(offset: Int): Int {
                    if (offset <= prefix.length) return 0
                    return (offset - prefix.length).coerceIn(0, text.length)
                }
            }

            TransformedText(builder.toAnnotatedString(), offsetMapping)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusRequester.requestFocus()
            },
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = { input ->
                val digits = input.filter { it.isDigit() }
                if (digits.length <= maxDigits) {
                    onValueChange(digits)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            enabled = enabled,
            singleLine = true,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(
                fontSize = amountSize,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            ),
            cursorBrush = SolidColor(UnitedMoneyBlue)
        )
    }
}

/**
 * A stylized card container wrapping UnitedCenteredAmountField with a top label and
 * optional quick denomination chips, perfect for modern Fintech payment flows.
 */
@Composable
fun UnitedCenteredAmountCard(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "ENTER AMOUNT",
    maxDigits: Int = 6,
    symbolSize: TextUnit = 36.sp,
    amountSize: TextUnit = 42.sp,
    placeholder: String = "0",
    quickChips: List<Int>? = null,
    onChipClick: ((Int) -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = UnitedWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, UnitedBorderLight, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = UnitedTextSecondary,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            UnitedCenteredAmountField(
                value = value,
                onValueChange = onValueChange,
                maxDigits = maxDigits,
                symbolSize = symbolSize,
                amountSize = amountSize,
                placeholder = placeholder
            )

            if (!quickChips.isNullOrEmpty() && onChipClick != null) {
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    quickChips.forEach { chipAmount ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, UnitedBorderLight, RoundedCornerShape(6.dp))
                                .clickable { onChipClick(chipAmount) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "+₹$chipAmount",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = UnitedMoneyBlue,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
