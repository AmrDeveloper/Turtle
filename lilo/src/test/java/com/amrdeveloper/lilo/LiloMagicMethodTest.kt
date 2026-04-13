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
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.utils.LiloMockMachine
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloMagicMethodTest {

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
            val liloMachine = LiloMockMachine()
            val interpreter = LiloInterpreter(liloMachine)
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
            """,
            """
            print(1)
            """,
            """
            print(None)
            """,
            """
            print('Lilo')
            """,
            """
            print("Lilo")
            """,
            """
            print(int)
            """,
            """
            print(float)
            """
        )

        val expectedOutput = listOf(
            "3",
            "1",
            "None",
            "Lilo",
            "Lilo",
            "<class 'int'>",
            "<class 'float'>"
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
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
            val liloHostTest = LiloMockMachine()
            val interpreter = LiloInterpreter(liloHostTest)
            val interpreterResult = interpreter.evaluate(program = liloTree)
            if (interpreterResult.isFailure()) {
                println("Error[RT]: " + interpreterResult.toFailureError<LiloException>().message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            assertTrue(liloHostTest.getHost().buffer.toString() == expectedOutput[index])
            liloHostTest.getHost().clear()
        }
    }

    @Test
    fun `test __getitem__ magic method`() {
        val sourceCodes = mutableListOf(
            // List
            """
            a = [1, 2, 3]
            print(a[0])
            """,
            """
            a = [1, 2, 3]
            print(a[1])
            """,
            """
            a = [1, 2, 3]
            print(a[2])
            """,
            // Tuple
            """
            a = (1, 2, 3)
            print(a[0])
            """,
            """
            a = (1, 2, 3)
            print(a[1])
            """,
            """
            a = (1, 2, 3)
            print(a[2])
            """,
        )

        val expectedOutput = listOf(
            "1",
            "2",
            "3",
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
            val liloHostTest = LiloMockMachine()
            val interpreter = LiloInterpreter(liloHostTest)
            val interpreterResult = interpreter.evaluate(program = liloTree)
            if (interpreterResult.isFailure()) {
                println("Error[RT]: " + interpreterResult.toFailureError<LiloException>().message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            assertTrue(liloHostTest.getHost().buffer.toString() == expectedOutput[index])
            liloHostTest.getHost().clear()
        }
    }

    @Test
    fun `test __len__ magic method`() {
        val sourceCodes = mutableListOf(
            // List
            """
            a = [1, 2, 3]
            print(len(a))
            """,
            // Set
            """
            a = {1, True, "Hello"}
            print(len(a))
            """,
            """
            a = {1, 1, 1}
            print(len(a))
            """,
            // Tuple
            """
            a = (1, True, "Hello")
            print(len(a))
            """
        )

        val expectedOutput = listOf(
            "3",
            "3",
            "1",
            "3",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
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
            val liloMachine = LiloMockMachine()
            val interpreter = LiloInterpreter(liloMachine)
            val interpreterResult = interpreter.evaluate(program = liloTree)
            if (interpreterResult.isFailure()) {
                println("Error[RT]: " + interpreterResult.toFailureError<LiloException>().message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            assertTrue(liloMachine.getHost().buffer.toString() == expectedOutput[index])
            liloMachine.getHost().clear()
        }
    }
}
