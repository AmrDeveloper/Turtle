package com.amrdeveloper.lilo.interpreter

import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.isSuccess
import com.amrdeveloper.lilo.common.toFailureError
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.parser.LiloLexer
import com.amrdeveloper.lilo.parser.LiloParser
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.utils.LiloMockMachine
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloIterTest {

    @Test
    fun test_range_iterator() {
        val sourceCodes = mutableListOf(
            """
            for i in range(5):
                print(i)
            """,
            """
            for i in range(5, 0, -1):
                print(i)
            """,
            """
            for i in iter(range(5, 0, -1)):
                print(i)
            """,
            """
            for i in reversed(range(5, 0, -1)):
                print(i)
            """,
            """
            for i in range(0):
                print(i)
            else:
                print(-1)
            """,
        )

        val expectedOutput = listOf(
            "01234",
            "54321",
            "54321",
            "12345",
            "-1"
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
            val liloHostTest = LiloMockMachine()
            val interpreter = LiloInterpreter(liloHostTest)
            val interpreterResult = interpreter.evaluate(program = liloTree)
            if (interpreterResult.isFailure()) {
                println("Error[RT]: " + interpreterResult.toFailureError<LiloExceptionMessage>().message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            assert(liloHostTest.getHost().buffer.toString() == expectedOutput[index]) {
                println(liloHostTest.getHost().buffer.toString())
            }
        }
    }

    @Test
    fun test_tuple_iterator() {
        val sourceCodes = mutableListOf(
            """
            for i in (1, 2, 3):
                print(i)
            """,
            """
            for i in ((1, 1), (2, 2), (3, 3)):
                print(i)
            """,
            """
            for f, s in ((1, 1), (2, 2), (3, 3)):
                print(f, s)
            """,
            """
            for i in 1, 2, 3:
                print(i)
            """,
            """
            for i in reversed((1, 2, 3)):
                print(i)
            """,
        )

        val expectedOutput = listOf(
            "123",
            "(1, 1)(2, 2)(3, 3)",
            "112233",
            "123",
            "321",
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
            val liloHostTest = LiloMockMachine()
            val interpreter = LiloInterpreter(liloHostTest)
            val interpreterResult = interpreter.evaluate(program = liloTree)
            if (interpreterResult.isFailure()) {
                println("Error[RT]: " + interpreterResult.toFailureError<LiloExceptionMessage>().message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            assert(liloHostTest.getHost().buffer.toString() == expectedOutput[index]) {
                println(liloHostTest.getHost().buffer.toString())
            }
        }
    }
}
