import re

with open('app/src/main/java/com/example/ui/theme/Color.kt', 'r') as f:
    content = f.read()

# Remove the MIDNIGHT block
content = re.sub(r'// MIDNIGHT.*?(?=val TextDisabled)', '', content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/theme/Color.kt', 'w') as f:
    f.write(content)
