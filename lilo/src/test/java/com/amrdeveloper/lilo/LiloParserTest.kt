package com.amrdeveloper.lilo

import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.isSuccess
import com.amrdeveloper.lilo.common.toFailureError
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.parser.LiloLexer
import com.amrdeveloper.lilo.parser.LiloParser
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import org.junit.Test

import org.junit.Assert.*

class LiloParserTest {

    @Test
    fun `test parse import statement`() {
        val sourceCodes = listOf(
            "import a",
            "import complex",
            "import random as r",
            "import a, b",
            "import one as o, two as t",
            "from random import random"
        )

        for (sourceCode in sourceCodes) {
            val lexerResult = LiloLexer(source = sourceCode).tokenize()
            if (lexerResult.isFailure()) {
                println("Error[Lexer]: " + lexerResult.toFailureError<LiloDiagnostic>().message)
            }
            assertTrue("Lexer error", lexerResult.isSuccess())

            val parseResult = LiloParser(tokens = lexerResult.toSuccessData()).parse()
            if (parseResult.isFailure()) {
                println("Error[Parser]: " + parseResult.toFailureError<LiloDiagnostic>().message)
            }
            assertTrue("Parser error", parseResult.isSuccess())
        }
    }

    @Test
    fun `test parse dot expression`() {
        val sourceCodes = listOf(
            "a.b",
            "random.random",
            "a.__add__"
        )

        for (sourceCode in sourceCodes) {
            val lexerResult = LiloLexer(source = sourceCode).tokenize()
            if (lexerResult.isFailure()) {
                println("Error[Lexer]: " + lexerResult.toFailureError<LiloDiagnostic>().message)
            }
            assertTrue("Lexer error", lexerResult.isSuccess())

            val parseResult = LiloParser(tokens = lexerResult.toSuccessData()).parse()
            if (parseResult.isFailure()) {
                println("Error[Parser]: " + parseResult.toFailureError<LiloDiagnostic>().message)
            }
            assertTrue("Parser error", parseResult.isSuccess())
        }
    }

    @Test
    fun `test parse list`() {
        val sourceCodes = listOf(
            "a = [1, 3, 4, 5]",
            "b = []"
        )

        for (sourceCode in sourceCodes) {
            val lexerResult = LiloLexer(source = sourceCode).tokenize()
            if (lexerResult.isFailure()) {
                println("Error[Lexer]: " + lexerResult.toFailureError<LiloDiagnostic>().message)
            }
            assertTrue("Lexer error", lexerResult.isSuccess())

            val parseResult = LiloParser(tokens = lexerResult.toSuccessData()).parse()
            if (parseResult.isFailure()) {
                println("Error[Parser]: " + parseResult.toFailureError<LiloDiagnostic>().message)
            }
            assertTrue("Parser error", parseResult.isSuccess())
        }
    }
}