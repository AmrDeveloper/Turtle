package com.amrdeveloper.lilo.std

fun supportedLiloStdlib(): Map<String, LiloStdObject> {
    return mapOf(
        // Modules
        "random" to LiloRandomModule(),
    )
}
