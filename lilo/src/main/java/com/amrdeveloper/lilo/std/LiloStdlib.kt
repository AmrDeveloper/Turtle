package com.amrdeveloper.lilo.std

import com.amrdeveloper.lilo.std.builtins.LiloPrintFunction
import com.amrdeveloper.lilo.std.modules.liloRandomModule
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.std.builtins.LiloLenFunction
import com.amrdeveloper.lilo.std.builtins.LiloTypeFunction

fun supportedLiloStdlib(): Map<String, LiloObject> {
    return mapOf(
        // Modules
        "random" to liloRandomModule,

        // Builtins Functions
        "print" to LiloPrintFunction,
        "len" to LiloLenFunction,
        "type" to LiloTypeFunction,
    )
}
