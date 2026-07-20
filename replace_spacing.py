import os
import re

UI_DIR = 'app/src/main/java/com/example/ui'

replacements = [
    (r'padding\(16\.dp\)', 'padding(AppSpacing.Medium)'),
    (r'padding\(8\.dp\)', 'padding(AppSpacing.Small)'),
    (r'padding\(24\.dp\)', 'padding(AppSpacing.Large)'),
    (r'padding\(4\.dp\)', 'padding(AppSpacing.ExtraSmall)'),
    (r'padding\(32\.dp\)', 'padding(AppSpacing.ExtraLarge)'),
    (r'padding\(horizontal = 16\.dp\)', 'padding(horizontal = AppSpacing.Medium)'),
    (r'padding\(vertical = 16\.dp\)', 'padding(vertical = AppSpacing.Medium)'),
    (r'padding\(horizontal = 8\.dp\)', 'padding(horizontal = AppSpacing.Small)'),
    (r'padding\(vertical = 8\.dp\)', 'padding(vertical = AppSpacing.Small)'),
    (r'padding\(horizontal = 24\.dp\)', 'padding(horizontal = AppSpacing.Large)'),
    (r'padding\(vertical = 24\.dp\)', 'padding(vertical = AppSpacing.Large)'),
    (r'padding\(horizontal = 4\.dp\)', 'padding(horizontal = AppSpacing.ExtraSmall)'),
    (r'padding\(vertical = 4\.dp\)', 'padding(vertical = AppSpacing.ExtraSmall)'),
    (r'spacedBy\(16\.dp\)', 'spacedBy(AppSpacing.Medium)'),
    (r'spacedBy\(8\.dp\)', 'spacedBy(AppSpacing.Small)'),
    (r'spacedBy\(24\.dp\)', 'spacedBy(AppSpacing.Large)'),
    (r'spacedBy\(4\.dp\)', 'spacedBy(AppSpacing.ExtraSmall)'),
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

