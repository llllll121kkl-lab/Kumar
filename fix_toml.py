import re

with open('gradle/libs.versions.toml', 'r') as f:
    content = f.read()

# Remove the appended parts
content = re.sub(r'kotlinSerialization = "1\.8\.0"\s*\[libraries\]\s*kotlinx-serialization-json = \{ group = "org\.jetbrains\.kotlinx", name = "kotlinx-serialization-json", version\.ref = "kotlinSerialization" \}\s*retrofit-converter-serialization = \{ group = "com\.squareup\.retrofit2", name = "converter-kotlinx-serialization", version\.ref = "retrofit" \}\s*\[plugins\]\s*kotlin-serialization = \{ id = "org\.jetbrains\.kotlin\.plugin\.serialization", version\.ref = "kotlin" \}\s*', '', content)

# Add to versions
content = content.replace('[versions]', '[versions]\nkotlinSerialization = "1.8.0"')

# Add to libraries
content = content.replace('[libraries]', '[libraries]\nkotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinSerialization" }\nretrofit-converter-serialization = { group = "com.squareup.retrofit2", name = "converter-kotlinx-serialization", version.ref = "retrofit" }')

# Add to plugins
content = content.replace('[plugins]', '[plugins]\nkotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }')

with open('gradle/libs.versions.toml', 'w') as f:
    f.write(content)

