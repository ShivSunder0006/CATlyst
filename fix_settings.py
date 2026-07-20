import re

with open('app/src/main/java/com/example/ui/settings/SettingsScreen.kt', 'r') as f:
    content = f.read()

# Fix MutableInteractionSource import
if 'import androidx.compose.foundation.interaction.MutableInteractionSource' not in content:
    content = re.sub(r'package com\.example\.ui\.settings\n', r'\g<0>\nimport androidx.compose.foundation.interaction.MutableInteractionSource\nimport com.example.ui.theme.bounceClick\nimport androidx.compose.material3.ripple\n', content)

content = content.replace('val aboutInteractionSource = remember { MutableInteractionSource() }', 'val aboutInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }')
content = content.replace('indication = androidx.compose.material.ripple.rememberRipple(color = MaterialTheme.colorScheme.onSurface)', 'indication = ripple(color = MaterialTheme.colorScheme.onSurface)')

with open('app/src/main/java/com/example/ui/settings/SettingsScreen.kt', 'w') as f:
    f.write(content)
