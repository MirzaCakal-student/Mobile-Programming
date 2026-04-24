package com.example.mealplanner.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Top App Bar ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text       = title,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 18.sp
            )
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector        = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = actions,
        colors  = TopAppBarDefaults.topAppBarColors(
            containerColor             = MaterialTheme.colorScheme.primary,
            titleContentColor          = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor     = Color.White
        )
    )
}

// ── Gradient Top App Bar (for Home) ───────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradientTopBar(
    title: String,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text       = title,
                fontWeight = FontWeight.Bold,
                fontSize   = 20.sp,
                color      = Color.White
            )
        },
        actions = actions,
        colors  = TopAppBarDefaults.topAppBarColors(
            containerColor         = Color.Transparent,
            actionIconContentColor = Color.White
        ),
        modifier = Modifier.background(
            Brush.horizontalGradient(
                colors = listOf(Color(0xFF2E7D32), Color(0xFF43A047))
            )
        )
    )
}

// ── Primary Button ─────────────────────────────────────────────────────────────

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape  = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor         = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ── Secondary / Outlined Button ────────────────────────────────────────────────

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick  = onClick,
        enabled  = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape  = RoundedCornerShape(14.dp),
        border = ButtonDefaults.outlinedButtonBorder
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

// ── Danger Button ──────────────────────────────────────────────────────────────

@Composable
fun DangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick  = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape    = RoundedCornerShape(14.dp),
        colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}

// ── Input Field with error ─────────────────────────────────────────────────────

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    isPassword: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    placeholder: String? = null,
    enabled: Boolean = true
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value            = value,
            onValueChange    = onValueChange,
            label            = { Text(label) },
            placeholder      = if (placeholder != null) {
                { Text(placeholder, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) }
            } else null,
            isError          = errorMessage != null,
            singleLine       = singleLine,
            enabled          = enabled,
            modifier         = Modifier.fillMaxWidth(),
            shape            = RoundedCornerShape(12.dp),
            trailingIcon     = trailingIcon,
            leadingIcon      = leadingIcon,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions  = KeyboardOptions(keyboardType = keyboardType),
            colors           = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor  = MaterialTheme.colorScheme.primary,
                cursorColor        = MaterialTheme.colorScheme.primary
            )
        )
        if (errorMessage != null) {
            Text(
                text     = "⚠ $errorMessage",
                color    = MaterialTheme.colorScheme.error,
                style    = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 6.dp, top = 3.dp)
            )
        }
    }
}

// ── Search Bar ─────────────────────────────────────────────────────────────────

@Composable
fun AppSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search…",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value         = query,
        onValueChange = onQueryChange,
        placeholder   = {
            Text(placeholder, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))
        },
        leadingIcon   = {
            Icon(
                Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        },
        singleLine = true,
        shape      = RoundedCornerShape(16.dp),
        modifier   = modifier.fillMaxWidth(),
        colors     = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            focusedContainerColor   = MaterialTheme.colorScheme.surface
        )
    )
}

// ── Macro Chip ─────────────────────────────────────────────────────────────────

@Composable
fun MacroChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape    = RoundedCornerShape(10.dp),
        color    = color.copy(alpha = 0.13f),
        modifier = modifier
    ) {
        Column(
            modifier            = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
            Text(text = label, fontSize = 10.sp, color = color.copy(alpha = 0.8f))
        }
    }
}

// ── Calorie Status Banner ──────────────────────────────────────────────────────

@Composable
fun CalorieStatusBanner(consumed: Int, goal: Int) {
    val percent = if (goal > 0) consumed.toFloat() / goal else 0f
    val (statusText, statusColor) = when {
        percent < 0.8f  -> Pair("Under target – eat a bit more",      Color(0xFF1976D2))
        percent <= 1.1f -> Pair("On track – great job! ✓",            Color(0xFF2E7D32))
        else            -> Pair("Over target – consider adjusting",    Color(0xFFC62828))
    }
    Surface(
        color    = statusColor.copy(alpha = 0.1f),
        shape    = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier          = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text       = statusText,
                    color      = statusColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 13.sp
                )
                Text(
                    text     = "$consumed / $goal kcal",
                    color    = statusColor.copy(alpha = 0.75f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ── Section Header ─────────────────────────────────────────────────────────────

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text       = title,
            fontWeight = FontWeight.Bold,
            fontSize   = 17.sp,
            color      = MaterialTheme.colorScheme.onSurface
        )
        action?.invoke()
    }
}

// ── Empty State ────────────────────────────────────────────────────────────────

@Composable
fun EmptyState(
    emoji: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier            = modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = emoji, fontSize = 52.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            text       = title,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 17.sp,
            textAlign  = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text      = subtitle,
            fontSize  = 13.sp,
            color     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            textAlign = TextAlign.Center
        )
    }
}

// ── Stat Card (used on Home & Profile) ────────────────────────────────────────

@Composable
fun StatCard(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier            = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
            Text(text = label, fontSize = 11.sp, color = color.copy(alpha = 0.75f))
        }
    }
}

// ── Divider with label ─────────────────────────────────────────────────────────

@Composable
fun LabelDivider(label: String) {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text     = "  $label  ",
            fontSize = 12.sp,
            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

// ── Loading spinner full-screen ────────────────────────────────────────────────

@Composable
fun FullScreenLoading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}
