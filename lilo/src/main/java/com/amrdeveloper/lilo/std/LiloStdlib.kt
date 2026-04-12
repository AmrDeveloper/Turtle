package com.amrdeveloper.lilo.std

import com.amrdeveloper.lilo.runtime.LiloEnvironment
import com.amrdeveloper.lilo.std.builtins.LiloBinFunction
import com.amrdeveloper.lilo.std.builtins.LiloHasAttrFunction
import com.amrdeveloper.lilo.std.builtins.LiloHexFunction
import com.amrdeveloper.lilo.std.builtins.LiloIdFunction
import com.amrdeveloper.lilo.std.builtins.LiloLenFunction
import com.amrdeveloper.lilo.std.builtins.LiloOctFunction
import com.amrdeveloper.lilo.std.builtins.LiloPrintFunction
import com.amrdeveloper.lilo.std.builtins.LiloTypeFunction
import com.amrdeveloper.lilo.std.modules.gpu.liloGPUModule
import com.amrdeveloper.lilo.std.modules.math.liloMathModule
import com.amrdeveloper.lilo.std.modules.random.liloRandomModule

fun registerLiloStandardLibrary(environment: LiloEnvironment) {
    // Register Stdlib modules
    environment.values["random"] = liloRandomModule
    environment.values["math"] = liloMathModule
    environment.values["gpu"] = liloGPUModule

    // Register Stdlib functions
    environment.values["print"] = LiloPrintFunction
    environment.values["len"] = LiloLenFunction
    environment.values["type"] = LiloTypeFunction
    environment.values["id"] = LiloIdFunction
    environment.values["hasattr"] = LiloHasAttrFunction
    environment.values["bin"] = LiloBinFunction
    environment.values["oct"] = LiloOctFunction
    environment.values["hex"] = LiloHexFunction
}
