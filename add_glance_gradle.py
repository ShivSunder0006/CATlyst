with open("app/build.gradle.kts", "r") as f:
    content = f.read()

content = content.replace('dependencies {\n', 'dependencies {\n  implementation(libs.androidx.glance.appwidget)\n  implementation(libs.androidx.glance.material3)\n')

with open("app/build.gradle.kts", "w") as f:
    f.write(content)
