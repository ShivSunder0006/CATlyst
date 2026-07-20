import os
import re

UI_DIR = 'app/src/main/java/com/example/ui'

replacements = [
    (r'Modifier\.height\(16\.dp\)', 'Modifier.height(AppSpacing.Medium)'),
    (r'Modifier\.height\(8\.dp\)', 'Modifier.height(AppSpacing.Small)'),
    (r'Modifier\.height\(24\.dp\)', 'Modifier.height(AppSpacing.Large)'),
    (r'Modifier\.height\(4\.dp\)', 'Modifier.height(AppSpacing.ExtraSmall)'),
    (r'Modifier\.height\(32\.dp\)', 'Modifier.height(AppSpacing.ExtraLarge)'),
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

