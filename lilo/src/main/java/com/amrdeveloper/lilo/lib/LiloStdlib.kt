package com.amrdeveloper.lilo.lib

import com.amrdeveloper.lilo.objects.liloAssertionErrorType
import com.amrdeveloper.lilo.objects.liloBaseExceptionType
import com.amrdeveloper.lilo.objects.liloBoolType
import com.amrdeveloper.lilo.objects.liloDictType
import com.amrdeveloper.lilo.objects.liloExceptionType
import com.amrdeveloper.lilo.objects.liloFloatType
import com.amrdeveloper.lilo.objects.liloIntType
import com.amrdeveloper.lilo.objects.liloListType
import com.amrdeveloper.lilo.objects.liloRuntimeErrorType
import com.amrdeveloper.lilo.objects.liloSetType
import com.amrdeveloper.lilo.objects.liloStopIterationType
import com.amrdeveloper.lilo.objects.liloStrType
import com.amrdeveloper.lilo.objects.liloTupleType
import com.amrdeveloper.lilo.runtime.LiloEnvironment
import com.amrdeveloper.lilo.lib.builtins.LiloBinFunction
import com.amrdeveloper.lilo.lib.builtins.LiloHasAttrFunction
import com.amrdeveloper.lilo.lib.builtins.LiloHexFunction
import com.amrdeveloper.lilo.lib.builtins.LiloIdFunction
import com.amrdeveloper.lilo.lib.builtins.LiloIterFunction
import com.amrdeveloper.lilo.lib.builtins.LiloLenFunction
import com.amrdeveloper.lilo.lib.builtins.LiloOctFunction
import com.amrdeveloper.lilo.lib.builtins.LiloPrintFunction
import com.amrdeveloper.lilo.lib.builtins.LiloRangeFunction
import com.amrdeveloper.lilo.lib.builtins.LiloTypeFunction
import com.amrdeveloper.lilo.lib.colorsys.liloColorSysModule
import com.amrdeveloper.lilo.lib.gpu.liloGPUModule
import com.amrdeveloper.lilo.lib.inspect.liloInspectModule
import com.amrdeveloper.lilo.lib.keyword.liloKeywordModule
import com.amrdeveloper.lilo.lib.math.liloMathModule
import com.amrdeveloper.lilo.lib.random.liloRandomModule
import com.amrdeveloper.lilo.lib.time.liloTimeModule
import com.amrdeveloper.lilo.lib.turtle.liloTurtleModule
import com.amrdeveloper.lilo.objects.liloAttributeErrorType
import com.amrdeveloper.lilo.objects.liloComplexType
import com.amrdeveloper.lilo.objects.liloImportErrorType
import com.amrdeveloper.lilo.objects.liloModuleNotFoundErrorType
import com.amrdeveloper.lilo.objects.liloNameErrorType
import com.amrdeveloper.lilo.objects.liloNotImplementedError
import com.amrdeveloper.lilo.objects.liloSyntaxErrorType
import com.amrdeveloper.lilo.objects.liloTypeErrorType

fun registerLiloAutoImportedModule() {
    // Builtin types
    LiloEnvironment.builtins["bool"]    = liloBoolType
    LiloEnvironment.builtins["int"]     = liloIntType
    LiloEnvironment.builtins["float"]   = liloFloatType
    LiloEnvironment.builtins["str"]     = liloStrType
    LiloEnvironment.builtins["tuple"]   = liloTupleType
    LiloEnvironment.builtins["list"]    = liloListType
    LiloEnvironment.builtins["set"]     = liloSetType
    LiloEnvironment.builtins["dict"]    = liloDictType
    LiloEnvironment.builtins["complex"] = liloComplexType

    // Builtin exception types
    LiloEnvironment.builtins["BaseException"]           = liloBaseExceptionType
    LiloEnvironment.builtins["Exception"]               = liloExceptionType
    LiloEnvironment.builtins["AttributeError"]          = liloAttributeErrorType
    LiloEnvironment.builtins["SyntaxError"]             = liloSyntaxErrorType
    LiloEnvironment.builtins["NameError"]               = liloNameErrorType
    LiloEnvironment.builtins["ImportError"]             = liloImportErrorType
    LiloEnvironment.builtins["ModuleNotFoundError"]     = liloModuleNotFoundErrorType
    LiloEnvironment.builtins["TypeError"]               = liloTypeErrorType
    LiloEnvironment.builtins["RuntimeError"]            = liloRuntimeErrorType
    LiloEnvironment.builtins["AssertionError"]          = liloAssertionErrorType
    LiloEnvironment.builtins["StopIteration"]           = liloStopIterationType
    LiloEnvironment.builtins["NotImplementedError"]     = liloNotImplementedError
}

fun registerLiloStandardLibrary() {
    // Register Stdlib modules
    LiloEnvironment.builtins["random"]    = liloRandomModule
    LiloEnvironment.builtins["math"]      = liloMathModule
    LiloEnvironment.builtins["gpu"]       = liloGPUModule
    LiloEnvironment.builtins["time"]      = liloTimeModule
    LiloEnvironment.builtins["turtle"]    = liloTurtleModule
    LiloEnvironment.builtins["colorsys"]  = liloColorSysModule
    LiloEnvironment.builtins["keyword"]   = liloKeywordModule
    LiloEnvironment.builtins["inspect"]   = liloInspectModule

    // Register Stdlib functions
    LiloEnvironment.builtins["print"]    = LiloPrintFunction
    LiloEnvironment.builtins["len"]      = LiloLenFunction
    LiloEnvironment.builtins["type"]     = LiloTypeFunction
    LiloEnvironment.builtins["id"]       = LiloIdFunction
    LiloEnvironment.builtins["hasattr"]  = LiloHasAttrFunction
    LiloEnvironment.builtins["bin"]      = LiloBinFunction
    LiloEnvironment.builtins["oct"]      = LiloOctFunction
    LiloEnvironment.builtins["hex"]      = LiloHexFunction
    LiloEnvironment.builtins["range"]    = LiloRangeFunction
    LiloEnvironment.builtins["iter"]     = LiloIterFunction
}
