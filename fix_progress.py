with open("app/src/main/java/com/example/widgets/TodayProgressWidget.kt", "r") as f:
    content = f.read()

content = content.replace(
    'Spacer(modifier = GlanceModifier.fillMaxWidth(0.5f).height(8.dp).cornerRadius(4.dp).background(GlanceTheme.colors.primary))',
    'Spacer(modifier = GlanceModifier.defaultWeight().height(8.dp).cornerRadius(4.dp).background(GlanceTheme.colors.primary))\nSpacer(modifier = GlanceModifier.defaultWeight())'
)

with open("app/src/main/java/com/example/widgets/TodayProgressWidget.kt", "w") as f:
    f.write(content)
