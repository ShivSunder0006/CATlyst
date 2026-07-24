with open("app/src/main/java/com/example/widgets/QuickSessionWidget.kt", "r") as f:
    content = f.read()

content = content.replace('import androidx.glance.appwidget.provideContent', 'import androidx.glance.appwidget.provideContent\nimport androidx.glance.appwidget.updateAll')

with open("app/src/main/java/com/example/widgets/QuickSessionWidget.kt", "w") as f:
    f.write(content)
