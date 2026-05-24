package com.amrdeveloper.lilo.utils

import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailureError
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.parser.LiloLexer
import com.amrdeveloper.lilo.parser.LiloParser

fun isValidLiloTokens(sourceCode: String) : Boolean {
    val lexerResult = LiloLexer(source = sourceCode).tokenize()
    if (lexerResult.isFailure()) {
        println("Error[Lexer]: " + lexerResult.toFailureError<LiloDiagnostic>().message)
        return false
    }
    return true
}

fun isValidLiloSyntax(sourceCode: String): Boolean {
    val lexerResult = LiloLexer(source = sourceCode).tokenize()
    if (lexerResult.isFailure()) {
        println("Error[Lexer]: " + lexerResult.toFailureError<LiloDiagnostic>().message)
        return false
    }

    val parseResult = LiloParser(tokens = lexerResult.toSuccessData()).parse()
    if (parseResult.isFailure()) {
        println("Error[Parser]: " + parseResult.toFailureError<LiloDiagnostic>().message)
        return false
    }
    return true
}
