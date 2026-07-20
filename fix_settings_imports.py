with open('app/src/main/java/com/example/ui/settings/SettingsScreen.kt', 'r') as f:
    content = f.read()

if 'import androidx.compose.foundation.clickable' not in content:
    content = content.replace('import androidx.compose.foundation.interaction.MutableInteractionSource', 'import androidx.compose.foundation.interaction.MutableInteractionSource\nimport androidx.compose.foundation.clickable')

with open('app/src/main/java/com/example/ui/settings/SettingsScreen.kt', 'w') as f:
    f.write(content)
