package com.amrdeveloper.lilo.std

import com.amrdeveloper.lilo.`object`.liloAssertionErrorType
import com.amrdeveloper.lilo.`object`.liloBaseExceptionType
import com.amrdeveloper.lilo.`object`.liloExceptionType
import com.amrdeveloper.lilo.`object`.liloStopIteratorType
import com.amrdeveloper.lilo.runtime.LiloEnvironment
import com.amrdeveloper.lilo.std.builtins.LiloBinFunction
import com.amrdeveloper.lilo.std.builtins.LiloHasAttrFunction
import com.amrdeveloper.lilo.std.builtins.LiloHexFunction
import com.amrdeveloper.lilo.std.builtins.LiloIdFunction
import com.amrdeveloper.lilo.std.builtins.LiloLenFunction
import com.amrdeveloper.lilo.std.builtins.LiloOctFunction
import com.amrdeveloper.lilo.std.builtins.LiloPrintFunction
import com.amrdeveloper.lilo.std.builtins.LiloRangeFunction
import com.amrdeveloper.lilo.std.builtins.LiloTypeFunction
import com.amrdeveloper.lilo.std.modules.gpu.liloGPUModule
import com.amrdeveloper.lilo.std.modules.math.liloMathModule
import com.amrdeveloper.lilo.std.modules.random.liloRandomModule
import com.amrdeveloper.lilo.std.modules.time.liloTimeModule
import com.amrdeveloper.lilo.std.modules.turtle.liloTurtleModule
import com.amrdeveloper.lilo.type.liloBoolType
import com.amrdeveloper.lilo.type.liloDictType
import com.amrdeveloper.lilo.type.liloFloatType
import com.amrdeveloper.lilo.type.liloIntType
import com.amrdeveloper.lilo.type.liloListType
import com.amrdeveloper.lilo.type.liloSetType
import com.amrdeveloper.lilo.type.liloStrType
import com.amrdeveloper.lilo.type.liloTupleType

fun registerLiloAutoImportedModule(environment: LiloEnvironment) {
    environment.apply {
        // Builtin types
        values["bool"] = liloBoolType
        values["int"] = liloIntType
        values["float"] = liloFloatType
        values["str"] = liloStrType
        values["tuple"] = liloTupleType
        values["list"] = liloListType
        values["set"] = liloSetType
        values["dict"] = liloDictType

        // Builtin exception types
        values["BaseException"]  = liloBaseExceptionType
        values["Exception"]      = liloExceptionType
        values["AssertionError"] = liloAssertionErrorType
        values["StopIterator"]   = liloStopIteratorType
    }
}

fun registerLiloStandardLibrary(environment: LiloEnvironment) {
    environment.apply {
        // Register Stdlib modules
        environment.values["random"] = liloRandomModule
        environment.values["math"] = liloMathModule
        environment.values["gpu"] = liloGPUModule
        environment.values["time"] = liloTimeModule
        environment.values["turtle"] = liloTurtleModule

        // Register Stdlib functions
        environment.values["print"] = LiloPrintFunction
        environment.values["len"] = LiloLenFunction
        environment.values["type"] = LiloTypeFunction
        environment.values["id"] = LiloIdFunction
        environment.values["hasattr"] = LiloHasAttrFunction
        environment.values["bin"] = LiloBinFunction
        environment.values["oct"] = LiloOctFunction
        environment.values["hex"] = LiloHexFunction
        environment.values["range"] = LiloRangeFunction
    }
}
