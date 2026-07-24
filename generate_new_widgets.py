import os

widgets = [
    ("DailyProgress", "daily_progress", 2, 2),
    ("QuickSession", "quick_session", 4, 2),
    ("Dashboard", "dashboard", 4, 2),
    ("QuickLaunch", "quick_launch", 4, 1)
]

xml_dir = "app/src/main/res/xml"
os.makedirs(xml_dir, exist_ok=True)

for class_prefix, file_suffix, min_width_cells, min_height_cells in widgets:
    min_width = 73 * min_width_cells - 16
    min_height = 118 * min_height_cells - 16
    
    xml_content = f"""<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="{min_width}dp"
    android:minHeight="{min_height}dp"
    android:updatePeriodMillis="0"
    android:initialLayout="@layout/glance_default_loading_layout"
    android:description="@string/widget_{file_suffix}_desc"
    android:widgetCategory="home_screen"
    android:targetCellWidth="{min_width_cells}"
    android:targetCellHeight="{min_height_cells}"
    android:configure="com.example.widgets.WidgetConfigActivity"
    >
</appwidget-provider>
"""
    with open(f"{xml_dir}/widget_{file_suffix}_info.xml", "w") as f:
        f.write(xml_content)

print("Generated new XMLs")
