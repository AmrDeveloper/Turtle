package com.amrdeveloper.lilo.lib.keyword

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloBool
import com.amrdeveloper.lilo.objects.LiloList
import com.amrdeveloper.lilo.objects.LiloModule
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.LiloStr
import com.amrdeveloper.lilo.objects.createLiloException
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloTypeErrorType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

private const val MODULE_NAME = "keyword"

private val liloKeywords = listOf(
    "False",
    "None",
    "True",
    "and",
    "as",
    "assert",
    "async",
    "await",
    "break",
    "class",
    "continue",
    "def",
    "del",
    "elif",
    "else",
    "except",
    "finally",
    "for",
    "from",
    "global",
    "if",
    "import",
    "in",
    "is",
    "lambda",
    "nonlocal",
    "not",
    "or",
    "pass",
    "raise",
    "return",
    "try",
    "while",
    "with",
    "yield"
)

private val liloSoftKeywords = listOf("_", "case", "lazy", "match", "type")

private val liloKeywordsObj : LiloObject by lazy {
    LiloList(values = liloKeywords.map { LiloStr(value = it) }.toMutableList())
}

private val liloSoftKeywordsObj : LiloObject by lazy {
    LiloList(values = liloSoftKeywords.map { LiloStr(value = it) }.toMutableList())
}

val liloKeywordModule = LiloModule(name = MODULE_NAME).also {
    // Constants
    it.setAttr(name = "kwlist", value = liloKeywordsObj)
    it.setAttr(name = "softkwlist", value = liloSoftKeywordsObj)

    // Functions
    it.setAttr(name = "iskeyword", value = LiloIsKeyword)
    it.setAttr(name = "issoftkeyword", value = LiloIsSoftKeyword)
}

object LiloIsKeyword : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`keyword.iskeyword` expected 1 argument, got ${args.size}")
        }

        val argument = args[0]
        if (argument !is LiloStr) {
            throw createLiloException(liloTypeErrorType, "`keyword.iskeyword` expect `string` type but got `${argument.type.toString()}`")
        }

        return LiloResult.Success(data = LiloBool(value = liloKeywords.contains(argument.value)))
    }
}

object LiloIsSoftKeyword : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`keyword.issoftkeyword` expected 1 argument, got ${args.size}")
        }

        val argument = args[0]
        if (argument !is LiloStr) {
            throw createLiloException(liloTypeErrorType, "`keyword.issoftkeyword` expect `string` type but got `${argument.type.toString()}`")
        }

        return LiloResult.Success(data = LiloBool(value = liloSoftKeywords.contains(argument.value)))
    }
}
