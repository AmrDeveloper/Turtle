package com.amrdeveloper.lilo.std

import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.std.builtins.LiloHasAttrFunction
import com.amrdeveloper.lilo.std.builtins.LiloIdFunction
import com.amrdeveloper.lilo.std.builtins.LiloLenFunction
import com.amrdeveloper.lilo.std.builtins.LiloPrintFunction
import com.amrdeveloper.lilo.std.builtins.LiloTypeFunction
import com.amrdeveloper.lilo.std.modules.liloRandomModule

fun supportedLiloStdlib(): Map<String, LiloObject> {
    return mapOf(
        // Modules
        "random" to liloRandomModule,

        // Builtins Functions
        "print" to LiloPrintFunction,
        "len" to LiloLenFunction,
        "type" to LiloTypeFunction,
        "id" to LiloIdFunction,
        "hasattr" to LiloHasAttrFunction,
    )
}
