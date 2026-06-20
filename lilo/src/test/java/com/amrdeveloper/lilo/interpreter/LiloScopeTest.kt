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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloScopeTest {

    @Test
    fun test_globals() {
        val sourceCodes = mutableListOf(
            """
            x = 5
            def foo():
              global x
              x = 10
            foo()
            print(x)
            """,
            """
            x = 5
            def foo():
              x = 10
            foo()
            print(x)
            """,
            """
            def foo():
              return 10
            def foo2():
              return foo()
            print(foo2())
            """,
            """
            x = 5
            def foo():
              return x
            print(foo())
            """,
            """
            x = 5
            def foo():
              x = 10
              return x
            print(foo())
            """,
            """
            x = 10
            def foo():
                x = 20
            foo()
            print(x)
            """,
            """
            x = 10
            def outer():
                x = 20
                def inner():
                    return x
                return inner()
            print(outer())
            """,
            """
            x = 1
            def outer():
                global x
                x = 2
                def inner():
                    x = 3
                    return x
                return inner() + x
            print(outer())
            """,
        )

        val expectedOutput = listOf(
            "10",
            "5",
            "10",
            "5",
            "10",
            "10",
            "20",
            "5",
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
            assertTrue(liloHostTest.getHost().buffer.toString() == expectedOutput[index])
        }
    }

    @Test
    fun test_delete() {
        val sourceCodes = mutableListOf(
            """
            x = 5
            del x;
            print(x)
            """,
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
            assertFalse("Interpreter error", interpreterResult.isSuccess())
        }
    }
}
