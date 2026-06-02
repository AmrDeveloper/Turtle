package com.amrdeveloper.colorschema

fun colorSchemasMap() = mapOf(
    "VSCode Light" to VSCodeLightLiloColorSchema,
    "VSCode Dark" to VSCodeDarkLiloColorSchema
)

fun defaultColorSchema(isDarkTheme : Boolean) =
    if (isDarkTheme) VSCodeDarkLiloColorSchema else VSCodeLightLiloColorSchema
