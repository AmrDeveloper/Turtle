package com.amrdeveloper.colorschema.colorschema

fun colorSchemasMap() = mapOf(
    "VSCode Light" to VSCodeLightLiloColorSchema,
    "VSCode Dark" to VSCodeDarkLiloColorSchema,
    "Tokyo Night Storm" to TokyoNightDarkLiloColorSchema,
    "Tokyo Night Light" to TokyoNightLightLiloColorSchema,
    "Monokai" to MonokaiLiloColorSchema,
    "Monokai Light" to MonokaiLightLiloColorSchema,
    "JetBrains Dark" to JetBrainsDarkLiloColorSchema,
    "JetBrains Light" to JetBrainsLightLiloColorSchema
)

fun defaultColorSchema(isDarkTheme : Boolean) =
    if (isDarkTheme) VSCodeDarkLiloColorSchema else VSCodeLightLiloColorSchema
