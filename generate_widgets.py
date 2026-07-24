import os

widgets = [
    ("TodayProgress", "Today's Progress", 2, 2, "today_progress"),
    ("CurrentSession", "Current Session", 2, 2, "current_session"),
    ("QuickStats", "Quick Stats", 4, 2, "quick_stats"),
    ("QuickActions", "Quick Actions", 4, 1, "quick_actions"),
    ("MonthlyHeatmap", "Monthly Heatmap", 4, 3, "monthly_heatmap"),
    ("WeeklySummary", "Weekly Summary", 4, 2, "weekly_summary"),
    ("Motivation", "Motivation", 4, 2, "motivation")
]

xml_dir = "app/src/main/res/xml"
os.makedirs(xml_dir, exist_ok=True)

for class_prefix, label, min_width_cells, min_height_cells, file_suffix in widgets:
    min_width = 73 * min_width_cells - 16
    min_height = 118 * min_height_cells - 16
    
    # Generate appwidget-provider XML
    xml_content = f"""<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="{min_width}dp"
    android:minHeight="{min_height}dp"
    android:updatePeriodMillis="86400000"
    android:initialLayout="@layout/glance_default_loading_layout"
    android:description="@string/widget_{file_suffix}_desc"
    android:widgetCategory="home_screen"
    android:targetCellWidth="{min_width_cells}"
    android:targetCellHeight="{min_height_cells}"
    >
</appwidget-provider>
"""
    with open(f"{xml_dir}/widget_{file_suffix}_info.xml", "w") as f:
        f.write(xml_content)

print("Generated XMLs")
