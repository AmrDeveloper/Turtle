package com.amrdeveloper.lilo.interpreter.builtin

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

class LiloTupleTest {

    @Test
    fun tuple_count_test() {
        val sourceCodes = mutableListOf(
            """
            c = (1, 2, 3, 1).count(5)
            print(c)
            """,
            """
            c = (1, 2, 3, 1).count(1)
            print(c)
            """,
        )

        val expectedOutput = listOf(
            "0",
            "2",
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
            val liloMachine = LiloMockMachine()
            val interpreter = LiloInterpreter(liloMachine = liloMachine)
            val interpreterResult = interpreter.evaluate(program = liloTree)
            if (interpreterResult.isFailure()) {
                println("Error[RT]: " + interpreterResult.toFailureError<LiloExceptionMessage>().message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            assert(value = liloMachine.getHost().buffer.toString() == expectedOutput[index]) {
                print(liloMachine.getHost().buffer.toString())
            }
        }
    }

    @Test
    fun tuple_index_test() {
        val sourceCodes = mutableListOf(
            """
            i = (1, 2, 3, 1).index(1)
            print(i)
            """,
            """
            i = (1, 2, 3, 1).index(2)
            print(i)
            """,
            """
            i = (1, 2, 3, 1).index(3)
            print(i)
            """,
            """
            i = (1, 2, 3, 1).index(1, 2)
            print(i)
            """,
        )

        val expectedOutput = listOf(
            "0",
            "1",
            "2",
            "3",
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
            val liloMachine = LiloMockMachine()
            val interpreter = LiloInterpreter(liloMachine = liloMachine)
            val interpreterResult = interpreter.evaluate(program = liloTree)
            if (interpreterResult.isFailure()) {
                println("Error[RT]: " + interpreterResult.toFailureError<LiloExceptionMessage>().message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            assert(value = liloMachine.getHost().buffer.toString() == expectedOutput[index]) {
                print(liloMachine.getHost().buffer.toString())
            }
        }
    }
}
