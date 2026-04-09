package com.amrdeveloper.lilo.std

import com.amrdeveloper.lilo.runtime.LiloEnvironment
import com.amrdeveloper.lilo.std.builtins.LiloHasAttrFunction
import com.amrdeveloper.lilo.std.builtins.LiloIdFunction
import com.amrdeveloper.lilo.std.builtins.LiloLenFunction
import com.amrdeveloper.lilo.std.builtins.LiloPrintFunction
import com.amrdeveloper.lilo.std.builtins.LiloTypeFunction
import com.amrdeveloper.lilo.std.modules.liloRandomModule

fun registerLiloStandardLibrary(environment: LiloEnvironment) {
    // Register Stdlib modules
    environment.values["random"] = liloRandomModule

    // Register Stdlib functions
    environment.values["print"] = LiloPrintFunction
    environment.values["len"] = LiloLenFunction
    environment.values["type"] = LiloTypeFunction
    environment.values["id"] = LiloIdFunction
    environment.values["hasattr"] = LiloHasAttrFunction
}
