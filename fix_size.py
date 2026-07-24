with open("app/src/main/java/com/example/widgets/DailyProgressWidget.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.glance.layout.padding", "import androidx.glance.layout.padding\nimport androidx.glance.layout.size\nimport androidx.glance.layout.width")

with open("app/src/main/java/com/example/widgets/DailyProgressWidget.kt", "w") as f:
    f.write(content)
