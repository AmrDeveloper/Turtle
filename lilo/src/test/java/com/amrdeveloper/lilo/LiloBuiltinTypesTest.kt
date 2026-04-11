package com.amrdeveloper.lilo

import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.isSuccess
import com.amrdeveloper.lilo.common.toFailureError
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.parser.LiloLexer
import com.amrdeveloper.lilo.parser.LiloParser
import com.amrdeveloper.lilo.runtime.LiloException
import com.amrdeveloper.lilo.runtime.LiloHost
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloBuiltinTypesTest {

    class LiloHostTest : LiloHost {
        var buffer = StringBuilder()

        override fun write(message: String) {
            buffer.append(message)
        }

        fun clear() {
            buffer = buffer.clear()
        }
    }

    @Test
    fun `test builtin Int type`() {
        val sourceCodes = mutableListOf(
            "print(int(1))",
            "print(int(1.0))",
            "print(int(True))",
            "print(int.__init__(True))"
        )

        val expectedOutput = listOf(
            "1",
            "1",
            "1",
            "1",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val lexerResult = LiloLexer(source = sourceCode).tokenize()
            if (lexerResult.isFailure()) {
                println("Error[Lexer]: " + lexerResult.toFailureError<LiloResult.Failure<LiloDiagnostic>>().error.message)
            }
            assertTrue("Lexer error", lexerResult.isSuccess())

            val parseResult = LiloParser(tokens = lexerResult.toSuccessData()).parse()
            if (parseResult.isFailure()) {
                println("Error[Parser]: " + parseResult.toFailureError<LiloResult.Failure<LiloDiagnostic>>().error.message)
            }
            assertTrue("Parser error", parseResult.isSuccess())

            val liloTree = parseResult.toSuccessData()
            val liloHostTest = LiloHostTest()
            val interpreter = LiloInterpreter(liloHostTest)
            val interpreterResult = interpreter.evaluate(program = liloTree)
            if (interpreterResult.isFailure()) {
                println("Error[RT]: " + interpreterResult.toFailureError<LiloResult.Failure<LiloException>>().error.message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            println(liloHostTest.buffer.toString())
            assertTrue(liloHostTest.buffer.toString() == expectedOutput[index])
            liloHostTest.clear()
        }
    }


    @Test
    fun `test builtin Complex type`() {
        val sourceCodes = mutableListOf(
            "print(1j)",
        )

        val expectedOutput = listOf(
            "(0.0 + 1.0j)",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val lexerResult = LiloLexer(source = sourceCode).tokenize()
            if (lexerResult.isFailure()) {
                println("Error[Lexer]: " + lexerResult.toFailureError<LiloResult.Failure<LiloDiagnostic>>().error.message)
            }
            assertTrue("Lexer error", lexerResult.isSuccess())

            val parseResult = LiloParser(tokens = lexerResult.toSuccessData()).parse()
            if (parseResult.isFailure()) {
                println("Error[Parser]: " + parseResult.toFailureError<LiloResult.Failure<LiloDiagnostic>>().error.message)
            }
            assertTrue("Parser error", parseResult.isSuccess())

            val liloTree = parseResult.toSuccessData()
            val liloHostTest = LiloHostTest()
            val interpreter = LiloInterpreter(liloHostTest)
            val interpreterResult = interpreter.evaluate(program = liloTree)
            if (interpreterResult.isFailure()) {
                println("Error[RT]: " + interpreterResult.toFailureError<LiloResult.Failure<LiloException>>().error.message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            println(liloHostTest.buffer.toString())
            assertTrue(liloHostTest.buffer.toString() == expectedOutput[index])
            liloHostTest.clear()
        }
    }
}
