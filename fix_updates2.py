with open("app/src/main/java/com/example/widgets/QuickSessionWidget.kt", "r") as f:
    content = f.read()

content = content.replace('QuickSessionWidget().update(context, glanceId)', 'androidx.glance.appwidget.updateAll(context)')
# Wait, actually `update(context, glanceId)` works for `GlanceAppWidget`. It is a function on `GlanceAppWidget`.

