package com.amrdeveloper.lilo

import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.isSuccess
import com.amrdeveloper.lilo.common.toFailureError
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.parser.LiloLexer
import com.amrdeveloper.lilo.parser.LiloParser
import com.amrdeveloper.lilo.runtime.LiloException
import com.amrdeveloper.lilo.runtime.LiloHost
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import org.junit.Test

import org.junit.Assert.*

class LiloMagicMethodTest {

    class LiloHostTest : LiloHost {
        val buffer = StringBuilder()

        override fun write(message: String) {
            buffer.append(message)
        }
    }

    @Test
    fun `test __add__ magic method`() {
        val sourceCodes = mutableListOf(
            """
            a = 1
            b = 2
            c = a + b
            """,
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

            val liloTree = parseResult.toSuccessData()
            val liloHostTest = LiloHostTest()
            val interpreter = LiloInterpreter(liloHostTest)
            val interpreterResult = interpreter.evaluate(program = liloTree)
            if (interpreterResult.isFailure()) {
                println("Error[RT]: " + interpreterResult.toFailureError<LiloException>().message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
        }
    }

    @Test
    fun `test __str__ magic method`() {
        val sourceCodes = mutableListOf(
            """
            a = 1
            b = 2
            c = a + b
            print(c)
            """
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

            val liloTree = parseResult.toSuccessData()
            val liloHostTest = LiloHostTest()
            val interpreter = LiloInterpreter(liloHostTest)
            val interpreterResult = interpreter.evaluate(program = liloTree)
            if (interpreterResult.isFailure()) {
                println("Error[RT]: " + interpreterResult.toFailureError<LiloException>().message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            assertTrue(liloHostTest.buffer.toString() == "3")
        }
    }
}