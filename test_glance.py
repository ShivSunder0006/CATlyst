import os
with open("app/src/main/java/com/example/widgets/DailyProgressWidget.kt", "r") as f:
    content = f.read()

content = content.replace('Row(modifier = GlanceModifier.fillMaxWidth().height(12.dp).cornerRadius(6.dp).background(GlanceTheme.colors.surfaceVariant)) {\n                        Spacer(modifier = GlanceModifier.defaultWeight().height(12.dp).cornerRadius(6.dp).background(GlanceTheme.colors.primary))\n                        Spacer(modifier = GlanceModifier.defaultWeight())\n                    }',
'''androidx.glance.appwidget.LinearProgressIndicator(
                        progress = percent,
                        modifier = GlanceModifier.fillMaxWidth().height(12.dp).cornerRadius(6.dp),
                        color = GlanceTheme.colors.primary,
                        backgroundColor = GlanceTheme.colors.surfaceVariant
                    )''')

with open("app/src/main/java/com/example/widgets/DailyProgressWidget.kt", "w") as f:
    f.write(content)
