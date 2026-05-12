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

fun registerLiloAutoImportedModule() {
    // Builtin types
    LiloEnvironment.builtins["bool"] = liloBoolType
    LiloEnvironment.builtins["int"] = liloIntType
    LiloEnvironment.builtins["float"] = liloFloatType
    LiloEnvironment.builtins["str"] = liloStrType
    LiloEnvironment.builtins["tuple"] = liloTupleType
    LiloEnvironment.builtins["list"] = liloListType
    LiloEnvironment.builtins["set"] = liloSetType
    LiloEnvironment.builtins["dict"] = liloDictType

    // Builtin exception types
    LiloEnvironment.builtins["BaseException"]  = liloBaseExceptionType
    LiloEnvironment.builtins["Exception"]      = liloExceptionType
    LiloEnvironment.builtins["AssertionError"] = liloAssertionErrorType
    LiloEnvironment.builtins["StopIterator"]   = liloStopIteratorType
}

fun registerLiloStandardLibrary() {
    // Register Stdlib modules
    LiloEnvironment.builtins["random"] = liloRandomModule
    LiloEnvironment.builtins["math"] = liloMathModule
    LiloEnvironment.builtins["gpu"] = liloGPUModule
    LiloEnvironment.builtins["time"] = liloTimeModule
    LiloEnvironment.builtins["turtle"] = liloTurtleModule

    // Register Stdlib functions
    LiloEnvironment.builtins["print"] = LiloPrintFunction
    LiloEnvironment.builtins["len"] = LiloLenFunction
    LiloEnvironment.builtins["type"] = LiloTypeFunction
    LiloEnvironment.builtins["id"] = LiloIdFunction
    LiloEnvironment.builtins["hasattr"] = LiloHasAttrFunction
    LiloEnvironment.builtins["bin"] = LiloBinFunction
    LiloEnvironment.builtins["oct"] = LiloOctFunction
    LiloEnvironment.builtins["hex"] = LiloHexFunction
    LiloEnvironment.builtins["range"] = LiloRangeFunction
}
