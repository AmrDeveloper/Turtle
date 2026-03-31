package com.amrdeveloper.lilo.std

import com.amrdeveloper.lilo.std.builtins.LiloPrintFunction
import com.amrdeveloper.lilo.std.modules.LiloRandomModule
import com.amrdeveloper.lilo.value.LiloModule
import com.amrdeveloper.lilo.value.LiloValue

fun supportedLiloStdlib(): Map<String, LiloValue> {
    return mapOf(
        // Modules
        "random" to LiloModule("random", LiloRandomModule()),

        // Builtins Functions
        "print" to LiloPrintFunction()
    )
}
