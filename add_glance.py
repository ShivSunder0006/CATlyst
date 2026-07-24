with open("gradle/libs.versions.toml", "r") as f:
    content = f.read()

content = content.replace('[versions]\n', '[versions]\nglance = "1.1.1"\n')
content = content.replace('[libraries]\n', '[libraries]\nandroidx-glance-appwidget = { group = "androidx.glance", name = "glance-appwidget", version.ref = "glance" }\nandroidx-glance-material3 = { group = "androidx.glance", name = "glance-material3", version.ref = "glance" }\n')

with open("gradle/libs.versions.toml", "w") as f:
    f.write(content)
