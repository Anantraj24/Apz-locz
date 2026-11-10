package com.apzlocz.app.lock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apzlocz.app.ui.components.PinActionKey
import com.apzlocz.app.ui.components.PinDots
import com.apzlocz.app.ui.components.PinKey
import com.apzlocz.app.ui.theme.OneUiTheme

@Composable
fun PinInput(
    pinLength: Int = 4,
    onPinComplete: (String) -> Unit,
    isError: Boolean = false,
    enabled: Boolean = true,
    onBiometricClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var enteredPin by remember { mutableStateOf("") }

    val currentIsError = isError
    if (currentIsError && enteredPin.isNotEmpty()) {
        enteredPin = ""
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        PinDots(
            pinLength = pinLength,
            enteredCount = enteredPin.length,
            isError = isError
        )

        // Keypad Grid 3x4
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                PinKey(digit = "1", enabled = enabled, onClick = {
                    if (enteredPin.length < pinLength) {
                        val next = enteredPin + "1"
                        enteredPin = next
                        if (next.length == pinLength) onPinComplete(next)
                    }
                })
                PinKey(digit = "2", subLetters = "ABC", enabled = enabled, onClick = {
                    if (enteredPin.length < pinLength) {
                        val next = enteredPin + "2"
                        enteredPin = next
                        if (next.length == pinLength) onPinComplete(next)
                    }
                })
                PinKey(digit = "3", subLetters = "DEF", enabled = enabled, onClick = {
                    if (enteredPin.length < pinLength) {
                        val next = enteredPin + "3"
                        enteredPin = next
                        if (next.length == pinLength) onPinComplete(next)
                    }
                })
            }

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                PinKey(digit = "4", subLetters = "GHI", enabled = enabled, onClick = {
                    if (enteredPin.length < pinLength) {
                        val next = enteredPin + "4"
                        enteredPin = next
                        if (next.length == pinLength) onPinComplete(next)
                    }
                })
                PinKey(digit = "5", subLetters = "JKL", enabled = enabled, onClick = {
                    if (enteredPin.length < pinLength) {
                        val next = enteredPin + "5"
                        enteredPin = next
                        if (next.length == pinLength) onPinComplete(next)
                    }
                })
                PinKey(digit = "6", subLetters = "MNO", enabled = enabled, onClick = {
                    if (enteredPin.length < pinLength) {
                        val next = enteredPin + "6"
                        enteredPin = next
                        if (next.length == pinLength) onPinComplete(next)
                    }
                })
            }

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                PinKey(digit = "7", subLetters = "PQRS", enabled = enabled, onClick = {
                    if (enteredPin.length < pinLength) {
                        val next = enteredPin + "7"
                        enteredPin = next
                        if (next.length == pinLength) onPinComplete(next)
                    }
                })
                PinKey(digit = "8", subLetters = "TUV", enabled = enabled, onClick = {
                    if (enteredPin.length < pinLength) {
                        val next = enteredPin + "8"
                        enteredPin = next
                        if (next.length == pinLength) onPinComplete(next)
                    }
                })
                PinKey(digit = "9", subLetters = "WXYZ", enabled = enabled, onClick = {
                    if (enteredPin.length < pinLength) {
                        val next = enteredPin + "9"
                        enteredPin = next
                        if (next.length == pinLength) onPinComplete(next)
                    }
                })
            }

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                // Bottom-left action key: Biometric or Clear
                if (onBiometricClick != null) {
                    PinActionKey(
                        onClick = onBiometricClick,
                        contentDescriptionText = "Unlock with biometrics",
                        enabled = enabled
                    ) {
                        Text(
                            text = "🧬",
                            fontSize = 20.sp
                        )
                    }
                } else {
                    PinActionKey(
                        onClick = { enteredPin = "" },
                        contentDescriptionText = "Clear all",
                        enabled = enabled && enteredPin.isNotEmpty()
                    ) {
                        Text(
                            text = "C",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (enteredPin.isNotEmpty() && enabled) OneUiTheme.colors.onSurface else OneUiTheme.colors.onSurfaceMuted.copy(alpha = 0.3f)
                        )
                    }
                }

                PinKey(digit = "0", subLetters = "+", enabled = enabled, onClick = {
                    if (enteredPin.length < pinLength) {
                        val next = enteredPin + "0"
                        enteredPin = next
                        if (next.length == pinLength) onPinComplete(next)
                    }
                })

                // Bottom-right action key: Delete
                PinActionKey(
                    onClick = {
                        if (enteredPin.isNotEmpty()) {
                            enteredPin = enteredPin.dropLast(1)
                        }
                    },
                    contentDescriptionText = "Delete digit",
                    enabled = enabled && enteredPin.isNotEmpty()
                ) {
                    Text(
                        text = "⌫",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (enteredPin.isNotEmpty() && enabled) OneUiTheme.colors.onSurface else OneUiTheme.colors.onSurfaceMuted.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}
