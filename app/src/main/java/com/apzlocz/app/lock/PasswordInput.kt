package com.apzlocz.app.lock

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apzlocz.app.ui.components.PillButton
import com.apzlocz.app.ui.components.PillButtonStyle
import com.apzlocz.app.ui.theme.OneUiTheme
import kotlin.math.roundToInt

@Composable
fun PasswordInput(
    onPasswordSubmit: (String) -> Unit,
    isError: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isError) {
        if (isError) {
            password = ""
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 350
                    0f at 0
                    (-20f) at 50
                    20f at 100
                    (-14f) at 150
                    14f at 200
                    (-8f) at 250
                    8f at 300
                    0f at 350
                }
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Password Input Field Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(OneUiTheme.colors.surfaceVariant)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (password.isEmpty()) {
                        Text(
                            text = "Enter password",
                            style = OneUiTheme.typography.body,
                            color = OneUiTheme.colors.onSurfaceMuted
                        )
                    }
                    BasicTextField(
                        value = password,
                        onValueChange = { if (enabled) password = it },
                        singleLine = true,
                        enabled = enabled,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (password.isNotEmpty() && enabled) {
                                    onPasswordSubmit(password)
                                }
                            }
                        ),
                        textStyle = OneUiTheme.typography.body.copy(
                            color = OneUiTheme.colors.onSurface
                        ),
                        cursorBrush = SolidColor(OneUiTheme.colors.accent),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Show/hide toggle button
                Text(
                    text = if (passwordVisible) "Hide" else "Show",
                    style = OneUiTheme.typography.caption.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = OneUiTheme.colors.accent,
                    modifier = Modifier
                        .clickable { passwordVisible = !passwordVisible }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Unlock Pill Button
        PillButton(
            text = "Unlock",
            style = PillButtonStyle.PRIMARY,
            enabled = enabled && password.isNotEmpty(),
            onClick = {
                if (password.isNotEmpty()) {
                    onPasswordSubmit(password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

