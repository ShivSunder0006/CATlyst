import glob

for filename in glob.glob("app/src/main/java/com/example/widgets/*Widget.kt"):
    with open(filename, "r") as f:
        content = f.read()
    
    if "override suspend fun provideGlance" in content:
        content = content.replace("override suspend fun provideGlance(context: Context, id: GlanceId) {", 
                                  "override suspend fun provideGlance(context: Context, id: GlanceId) {\n        val widgetPrefs = WidgetPreferences(context).getConfigFlow(id.hashCode()).first()\n        val corner = widgetPrefs.cornerRadius.dp\n")
        
        # Replace 24.dp with corner
        content = content.replace("cornerRadius(24.dp)", "cornerRadius(corner)")
        
        # Ensure first() is imported
        if "import kotlinx.coroutines.flow.first" not in content:
            content = content.replace("import android.content.Context", "import android.content.Context\nimport kotlinx.coroutines.flow.first")
            
        with open(filename, "w") as f:
            f.write(content)

print("Updated corner radius")
