package com.amrdeveloper.lilo.std

import com.amrdeveloper.lilo.std.core.LiloStdObject
import com.amrdeveloper.lilo.std.modules.LiloRandomModule

fun supportedLiloStdlib(): Map<String, LiloStdObject> {
    return mapOf(
        // Modules
        "random" to LiloRandomModule(),
    )
}
