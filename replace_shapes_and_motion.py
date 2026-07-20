import os
import re

UI_DIR = 'app/src/main/java/com/example/ui'

replacements = [
    # RoundedCornerShape
    (r'RoundedCornerShape\(16\.dp\)', 'MaterialTheme.shapes.medium'),
    (r'RoundedCornerShape\(12\.dp\)', 'MaterialTheme.shapes.medium'),
    (r'RoundedCornerShape\(24\.dp\)', 'MaterialTheme.shapes.large'),
    (r'RoundedCornerShape\(8\.dp\)', 'MaterialTheme.shapes.small'),
    (r'RoundedCornerShape\(4\.dp\)', 'MaterialTheme.shapes.extraSmall'),
    (r'RoundedCornerShape\(40\.dp\)', 'CircleShape'),
    (r'RoundedCornerShape\(50\.?d?p?\)', 'CircleShape'),
    
    # Animations
    (r'tween\(150\)', 'AppMotion.quick()'),
    (r'tween\(200\)', 'AppMotion.quick()'),
    (r'tween\(durationMillis = 200, easing = LinearOutSlowInEasing\)', 'AppMotion.quick()'),
    (r'tween\(durationMillis = 200, easing = FastOutLinearInEasing\)', 'AppMotion.quick()'),
    (r'tween\(250\)', 'AppMotion.standard()'),
    (r'tween\(300\)', 'AppMotion.standard()'),
    (r'tween\(durationMillis = 350\)', 'AppMotion.theme()'),
    (r'tween\(350\)', 'AppMotion.theme()'),
    (r'tween\(500\)', 'AppMotion.success()'),
    (r'tween\(700\)', 'AppMotion.success()'),
    (r'tween\(800\)', 'AppMotion.success()'),
    (r'tween\(durationMillis = 800, easing = FastOutSlowInEasing\)', 'AppMotion.success()'),
    (r'tween\(1500, easing = LinearEasing\)', 'tween(1500, easing = LinearEasing)'), # maybe keep this for DailyGoalRing
    
    # Easing
    (r'FastOutSlowInEasing', 'AppMotion.StandardEasing'),
    (r'LinearOutSlowInEasing', 'AppMotion.DecelerateEasing'),
]

for root, _, files in os.walk(UI_DIR):
    for file in files:
        if file.endswith('.kt'):
            path = os.path.join(root, file)
            with open(path, 'r') as f:
                content = f.read()
            
            original_content = content
            
            for old, new in replacements:
                content = re.sub(old, new, content)
            
            if content != original_content:
                # Add imports if necessary
                if 'AppMotion' in content and 'import com.example.ui.theme.AppMotion' not in content:
                    content = re.sub(r'package com\.example\.ui.*?\n', r'\g<0>\nimport com.example.ui.theme.AppMotion\n', content)
                if 'MaterialTheme.shapes' in content and 'import androidx.compose.material3.MaterialTheme' not in content:
                    content = re.sub(r'package com\.example\.ui.*?\n', r'\g<0>\nimport androidx.compose.material3.MaterialTheme\n', content)
                if 'CircleShape' in content and 'import androidx.compose.foundation.shape.CircleShape' not in content:
                    content = re.sub(r'package com\.example\.ui.*?\n', r'\g<0>\nimport androidx.compose.foundation.shape.CircleShape\n', content)
                    
                with open(path, 'w') as f:
                    f.write(content)

