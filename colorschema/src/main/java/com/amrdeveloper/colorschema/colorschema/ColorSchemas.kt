package com.amrdeveloper.colorschema.colorschema

fun colorSchemasMap() = mapOf(
    "GitHub Light" to GitHubLightLiloColorSchema,
    "GitHub Dark" to GitHubDarkLiloColorSchema,
    "VSCode Light" to VSCodeLightLiloColorSchema,
    "VSCode Dark" to VSCodeDarkLiloColorSchema,
    "Tokyo Night Light" to TokyoNightLightLiloColorSchema,
    "Tokyo Night Storm" to TokyoNightDarkLiloColorSchema,
    "Monokai Light" to MonokaiLightLiloColorSchema,
    "Monokai" to MonokaiLiloColorSchema,
    "JetBrains Light" to JetBrainsLightLiloColorSchema,
    "JetBrains Dark" to JetBrainsDarkLiloColorSchema,
)

fun defaultColorSchema(isDarkTheme: Boolean) =
    if (isDarkTheme) VSCodeDarkLiloColorSchema else VSCodeLightLiloColorSchema
