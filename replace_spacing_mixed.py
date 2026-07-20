import os
import re

UI_DIR = 'app/src/main/java/com/example/ui'

replacements = [
    (r'padding\(horizontal = 16\.dp, vertical = 8\.dp\)', 'padding(horizontal = AppSpacing.Medium, vertical = AppSpacing.Small)'),
    (r'padding\(start = 16\.dp, end = 16\.dp, top = 8\.dp, bottom = 8\.dp\)', 'padding(horizontal = AppSpacing.Medium, vertical = AppSpacing.Small)'),
    (r'padding\(horizontal = 8\.dp, vertical = 4\.dp\)', 'padding(horizontal = AppSpacing.Small, vertical = AppSpacing.ExtraSmall)'),
    (r'padding\(horizontal = 24\.dp, vertical = 12\.dp\)', 'padding(horizontal = AppSpacing.Large, vertical = 12.dp)'),
    (r'padding\(start = 16\.dp, end = 16\.dp\)', 'padding(horizontal = AppSpacing.Medium)'),
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
                if 'AppSpacing' in content and 'import com.example.ui.theme.AppSpacing' not in content:
                    content = re.sub(r'package com\.example\.ui.*?\n', r'\g<0>\nimport com.example.ui.theme.AppSpacing\n', content)
                with open(path, 'w') as f:
                    f.write(content)

