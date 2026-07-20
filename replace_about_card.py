import re

with open('app/src/main/java/com/example/ui/settings/SettingsScreen.kt', 'r') as f:
    content = f.read()

# find:
#         item {
#             Card(modifier = Modifier.fillMaxWidth()) {
#                 Column(modifier = Modifier.padding(AppSpacing.Medium)) {
#                     Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
#                     Spacer(modifier = Modifier.height(AppSpacing.Small))
#                     Text("CATlyst - Minimalist CAT prep tracker", style = MaterialTheme.typography.bodyMedium)
#                 }
#             }
#         }

# replace with:
#         item {
#             var showAboutDialog by remember { mutableStateOf(false) }
#             if (showAboutDialog) {
#                 AboutDialog(onDismiss = { showAboutDialog = false })
#             }
#             val aboutInteractionSource = remember { MutableInteractionSource() }
#             Card(
#                 modifier = Modifier.fillMaxWidth().bounceClick(aboutInteractionSource, MaterialTheme.shapes.medium).clickable(
#                     interactionSource = aboutInteractionSource,
#                     indication = androidx.compose.foundation.LocalIndication.current
#                 ) {
#                     showAboutDialog = true
#                 },
#                 shape = MaterialTheme.shapes.medium
#             ) {
#                 Column(modifier = Modifier.padding(AppSpacing.Medium)) {
#                     Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
#                     Spacer(modifier = Modifier.height(AppSpacing.Small))
#                     Text("CATlyst - Minimalist CAT prep tracker", style = MaterialTheme.typography.bodyMedium)
#                 }
#             }
#         }

pattern = r'item \{\s*Card\(modifier = Modifier\.fillMaxWidth\(\)\) \{\s*Column\(modifier = Modifier\.padding\(AppSpacing\.Medium\)\) \{\s*Text\("About", style = MaterialTheme\.typography\.titleMedium, fontWeight = FontWeight\.SemiBold\)\s*Spacer\(modifier = Modifier\.height\(AppSpacing\.Small\)\)\s*Text\("CATlyst - Minimalist CAT prep tracker", style = MaterialTheme\.typography\.bodyMedium\)\s*\}\s*\}\s*\}'

replacement = """item {
            var showAboutDialog by remember { mutableStateOf(false) }
            if (showAboutDialog) {
                AboutDialog(onDismiss = { showAboutDialog = false })
            }
            val aboutInteractionSource = remember { MutableInteractionSource() }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick(aboutInteractionSource, MaterialTheme.shapes.medium)
                    .clickable(
                        interactionSource = aboutInteractionSource,
                        indication = androidx.compose.material.ripple.rememberRipple(color = MaterialTheme.colorScheme.onSurface)
                    ) {
                        showAboutDialog = true
                    },
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(AppSpacing.Medium)) {
                    Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(AppSpacing.Small))
                    Text("CATlyst - Minimalist CAT prep tracker", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }"""

new_content = re.sub(pattern, replacement, content)

with open('app/src/main/java/com/example/ui/settings/SettingsScreen.kt', 'w') as f:
    f.write(new_content)
