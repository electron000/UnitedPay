package com.unitedpay.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unitedpay.core.designsystem.theme.UnitedBorderLight
import com.unitedpay.core.designsystem.theme.UnitedError
import com.unitedpay.core.designsystem.theme.UnitedMoneyBlue
import com.unitedpay.core.designsystem.theme.UnitedSurfaceSubtle
import com.unitedpay.core.designsystem.theme.UnitedTextPrimary
import com.unitedpay.core.designsystem.theme.UnitedTextSecondary
import com.unitedpay.core.designsystem.theme.UnitedWhite

/**
 * Standardized Search Bar component enforcing single-line input, non-expanding fixed height (48dp),
 * and an ellipsis-truncated placeholder so narrow viewports never stretch the container vertically.
 */
@Composable
fun UnitedSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    onClear: (() -> Unit)? = null,
    leadingIcon: ImageVector = Icons.Default.Search
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 13.5.sp,
                color = UnitedTextSecondary,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = "Search",
                tint = UnitedMoneyBlue,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = {
                    onValueChange("")
                    onClear?.invoke()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = UnitedTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        maxLines = 1,
        textStyle = TextStyle(
            color = UnitedTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = UnitedTextPrimary,
            unfocusedTextColor = UnitedTextPrimary,
            focusedContainerColor = Color(0xFFF8FAFC),
            unfocusedContainerColor = Color(0xFFF8FAFC),
            focusedBorderColor = UnitedMoneyBlue,
            unfocusedBorderColor = UnitedBorderLight,
            cursorColor = UnitedMoneyBlue
        )
    )
}

/**
 * Standardized Enterprise Form Input Field.
 * Prevents vertical height increases by strictly enforcing maxLines = 1 and TextOverflow.Ellipsis
 * on both the input content and the placeholder.
 */
@Composable
fun UnitedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true,
    isError: Boolean = false,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label?.let {
            {
                Text(
                    text = it,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isError) UnitedError else UnitedTextSecondary,
                    fontSize = 13.sp
                )
            }
        },
        placeholder = placeholder?.let {
            {
                Text(
                    text = it,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    color = UnitedTextSecondary,
                    fontSize = 14.sp
                )
            }
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        enabled = enabled,
        isError = isError,
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else Int.MAX_VALUE,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        textStyle = TextStyle(
            color = UnitedTextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = UnitedTextPrimary,
            unfocusedTextColor = UnitedTextPrimary,
            focusedBorderColor = UnitedMoneyBlue,
            unfocusedBorderColor = UnitedBorderLight,
            focusedContainerColor = UnitedSurfaceSubtle,
            unfocusedContainerColor = UnitedSurfaceSubtle,
            cursorColor = UnitedMoneyBlue,
            errorBorderColor = UnitedError,
            errorTextColor = UnitedTextPrimary
        )
    )
}

/**
 * Standardized Primary CTA Button in UnitedMoneyBlue with strictly fixed non-shrinking 52dp height.
 */
@Composable
fun UnitedPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .defaultMinSize(minHeight = 52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = UnitedMoneyBlue,
            contentColor = UnitedWhite,
            disabledContainerColor = Color(0xFFCBD5E1),
            disabledContentColor = Color(0xFF64748B)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = if (enabled) UnitedWhite else Color(0xFF64748B)
            )
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailingIcon()
            }
        }
    }
}

/**
 * Standardized Outlined Secondary Button with 8dp radius and fixed 52dp height.
 */
@Composable
fun UnitedSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .defaultMinSize(minHeight = 52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        border = BorderStroke(1.5.dp, if (enabled) UnitedMoneyBlue else UnitedBorderLight),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = UnitedWhite,
            contentColor = UnitedMoneyBlue,
            disabledContentColor = Color(0xFF94A3B8)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = if (enabled) UnitedMoneyBlue else Color(0xFF94A3B8)
            )
        }
    }
}

/**
 * Pinned Bottom Action Container that guarantees the CTA button NEVER shrinks in height
 * when the software keyboard opens, and stays firmly positioned directly above the IME / navigation bar.
 * The content above this container in the page or modal sheet should use
 * Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()).
 */
@Composable
fun UnitedPinnedBottomBar(
    modifier: Modifier = Modifier,
    containerColor: Color = UnitedWhite,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding(),
        color = containerColor,
        shadowElevation = 8.dp,
        border = BorderStroke(0.5.dp, UnitedBorderLight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

/**
 * Standardized TopAppBar with explicit dark back button tint (immune to system dark mode).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitedTopAppBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = UnitedTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = UnitedTextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        actions = {
            actions?.invoke(this)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = UnitedWhite,
            titleContentColor = UnitedTextPrimary,
            navigationIconContentColor = UnitedTextPrimary,
            actionIconContentColor = UnitedTextPrimary
        )
    )
}

/**
 * Standardized Modern Card Surface with subtle border and translucent option.
 */
@Composable
fun UnitedCard(
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 14.dp,
    containerColor: Color = UnitedWhite,
    borderColor: Color = UnitedBorderLight,
    elevation: androidx.compose.ui.unit.Dp = 2.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, shape)
                .padding(16.dp),
            content = content
        )
    }
}

/**
 * Standardized Preset/Filter Chip (e.g. +₹100, +₹500).
 */
@Composable
fun UnitedChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF1F5F9))
            .border(
                1.dp,
                if (isSelected) UnitedMoneyBlue else UnitedBorderLight,
                RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isSelected) UnitedMoneyBlue else UnitedTextPrimary
        )
    }
}
