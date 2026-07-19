package com.example.ui.navigation

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

class CustomHapticFeedback(
    private val delegate: HapticFeedback,
    private val isEnabled: Boolean
) : HapticFeedback {
    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
        if (isEnabled) {
            delegate.performHapticFeedback(hapticFeedbackType)
        }
    }
}
