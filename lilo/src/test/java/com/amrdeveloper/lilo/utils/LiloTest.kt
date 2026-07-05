package com.amrdeveloper.lilo.utils

import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailureError
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.parser.LiloLexer
import com.amrdeveloper.lilo.parser.LiloParser
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter

fun testLiloLexer(sourceCode: String) : Boolean {
    val lexerResult = LiloLexer(source = sourceCode).tokenize()
    if (lexerResult.isFailure()) {
        println("Error[Lexer]: " + lexerResult.toFailureError<LiloDiagnostic>())
        return false
    }
    return true
}

fun testLiloParser(sourceCode: String): Boolean {
    val lexerResult = LiloLexer(source = sourceCode).tokenize()
    if (lexerResult.isFailure()) {
        println("Error[Lexer]: " + lexerResult.toFailureError<LiloDiagnostic>().message)
        return false
    }

    val parseResult = LiloParser(tokens = lexerResult.toSuccessData()).parse()
    if (parseResult.isFailure()) {
        println("Tokens: ")
        for (token in lexerResult.toSuccessData()) {
            println("  ${token}")
        }
        println("Error[Parser]: " + parseResult.toFailureError<LiloDiagnostic>().message)
        return false
    }
    return true
}

fun testLiloInterpreter(sourceCode: String): String? {
    val lexerResult = LiloLexer(source = sourceCode).tokenize()
    if (lexerResult.isFailure()) {
        println("Error[Lexer]: " + lexerResult.toFailureError<LiloDiagnostic>().message)
        return null
    }

    val parseResult = LiloParser(tokens = lexerResult.toSuccessData()).parse()
    if (parseResult.isFailure()) {
        println("Tokens: ")
        for (token in lexerResult.toSuccessData()) {
            println("  ${token}")
        }
        println("Error[Parser]: " + parseResult.toFailureError<LiloDiagnostic>().message)
        return null
    }

    val liloTree = parseResult.toSuccessData()
    val liloMachine = LiloMockMachine()
    val interpreter = LiloInterpreter(liloMachine = liloMachine)
    val interpreterResult = interpreter.evaluate(program = liloTree)
    if (interpreterResult.isFailure()) {
        val message = interpreterResult.toFailureError<LiloExceptionMessage>().message
        println("Error[Interpreter]: $message")
        return message
    }

    return liloMachine.getHost().buffer.toString()
}
