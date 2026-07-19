import re

with open('app/src/main/java/com/example/ui/theme/Theme.kt', 'r') as f:
    content = f.read()

content = content.replace('MIDNIGHT(Color(0xFF6495ED))', 'FOREST(Color(0xFF2E8B57))')
content = content.replace('val isMidnight = themeType == AppThemeType.MIDNIGHT', 'val isForest = themeType == AppThemeType.FOREST')
content = content.replace('if (isMidnight)', 'if (isForest)')
content = content.replace('isMidnight && isDark', 'isForest && isDark')

with open('app/src/main/java/com/example/ui/theme/Theme.kt', 'w') as f:
    f.write(content)
