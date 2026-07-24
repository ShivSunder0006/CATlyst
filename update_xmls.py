import os

xml_dir = "app/src/main/res/xml"

for file in os.listdir(xml_dir):
    if file.startswith("widget_") and file.endswith("_info.xml"):
        path = os.path.join(xml_dir, file)
        with open(path, "r") as f:
            content = f.read()
        
        content = content.replace('    >', '    android:configure="com.example.widgets.WidgetConfigActivity"\n    >')
        
        with open(path, "w") as f:
            f.write(content)

print("Updated XMLs")
