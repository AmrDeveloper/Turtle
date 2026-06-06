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

class LiloInterpreterTest {

    @Test
    fun test_evaluate_imported_function() {
        val sourceCodes = mutableListOf(
            """
            import random
            a = random.random()
            """,
            """
            import random as r
            a = r.random()
            """,
            """
            from random import random
            a = random()
            """,
            """
            from random import (random)
            a = random()
            """,
        )

        for (sourceCode in sourceCodes) {
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
            val interpreter = LiloInterpreter(liloMachine)
            val interpreterResult = interpreter.evaluate(program = liloTree)
            if (interpreterResult.isFailure()) {
                println("Error[RT]: " + interpreterResult.toFailureError<LiloExceptionMessage>().message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
        }
    }

    @Test
    fun test_evaluate_call() {
        val sourceCodes = mutableListOf(
            """
            def foo(p):
                pass
            foo(10)
            """,
            """
            import random
            a = random.random()
            """
        )

        for (sourceCode in sourceCodes) {
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
            val interpreter = LiloInterpreter(liloMachine)
            val interpreterResult = interpreter.evaluate(program = liloTree)
            if (interpreterResult.isFailure()) {
                println("Error[RT]: " + interpreterResult.toFailureError<LiloExceptionMessage>().message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            assertTrue(liloMachine.getHost().buffer.isEmpty())
        }
    }

    @Test
    fun test_evaluate_print_call() {
        val sourceCodes = mutableListOf(
            """
            print(42)
            """,
        )

        for (sourceCode in sourceCodes) {
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
            val interpreter = LiloInterpreter(liloMachine)
            val interpreterResult = interpreter.evaluate(program = liloTree)
            if (interpreterResult.isFailure()) {
                println("Error[RT]: " + interpreterResult.toFailureError<LiloExceptionMessage>().message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            assertTrue(liloMachine.getHost().buffer.toString() == "42")
        }
    }

    @Test
    fun `test evaluate if expr`() {
        val sourceCodes = mutableListOf(
            """
            a = 1 if True else 2
            print(a)
            """,
            """
            a = 1 if False else (2 if True else 3)
            print(a)  
            """,
            """
            a = (2 if (False) else 3) if (True) else 2
            print(a)
            """,
            """
            a = 1 if 1 else 2
            print(a)
            """,
            """
            a = 1 if 0 else 2
            print(a)
            """,
        )

        val expectedOutput = listOf(
            "1",
            "2",
            "3",
            "1",
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
    fun `test evaluate unary expr`() {
        val sourceCodes = mutableListOf(
            """
            print(-10)
            """,
            """
            print(+10)
            """
        )

        val expectedOutput = listOf(
            "-10",
            "10"
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
            val interpreter = LiloInterpreter(liloMachine)
            val interpreterResult = interpreter.evaluate(program = liloTree)
            if (interpreterResult.isFailure()) {
                println("Error[RT]: " + interpreterResult.toFailureError<LiloExceptionMessage>().message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            assertTrue(liloMachine.getHost().buffer.toString() == expectedOutput[index])
        }
    }

    @Test
    fun `test evaluate lambda expr`() {
        val sourceCodes = mutableListOf(
            """
            identity = lambda a : a
            print(identity(1))
            """,
            """
            a = lambda a : a + 1
            print(a(1))
            """,
            """
            x = 10
            add_x = lambda a : a + x
            print(add_x(5))
            """,
        )

        val expectedOutput = listOf(
            "1",
            "2",
            "15",
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
    fun `test evaluate if stmt`() {
        val sourceCodes = mutableListOf(
            """
            if True:
               print(1)
            """,
            """
            if False:
               print(1)
            """,
            """
            if False:
               print(1)
            elif True:
               print(2)
            """,
            """
            if False:
               print(1)
            else:
               print(2)
            """,
            """
            if False:
               print(1)
            elif False:
               print(2)
            else:
               print(3)
            """,
            """
            if 2:
               print(1)
            """,
            """
            if 0:
               print(1)
            """,
        )

        val expectedOutput = listOf(
            "1",
            "",
            "2",
            "2",
            "3",
            "1",
            "",
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
    fun `test evaluate while stmt`() {
        val sourceCodes = mutableListOf(
            """
            x = 5
            while x > 0:
                print(x)
                x = x - 1
            """,
            """
            x = 5
            while x > 0:
                print(x)
                break
            """,
            """
            x = 5
            while x > 0:
                x = x - 1
                continue
                print(x)
            """,
            """
            x = 5
            while x > 5:
                print(x)
                x = x - 1
            else:
                print(0)
            """,
        )

        val expectedOutput = listOf(
            "54321",
            "5",
            "",
            "0",
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
    fun `test evaluate for stmt`() {
        val sourceCodes = mutableListOf(
            """
            for i in range(5):
                print(i)
            """,
            """
            for i in range(5, 0, -1):
                print(i)
            """,
        )

        val expectedOutput = listOf(
            "01234",
            "54321",
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
    fun test_binding() {
        val sourceCodes = mutableListOf(
            """
            print(type(list.append))
            l = [2, 3]
            print(type(l.append))    
            """.trimIndent()
        )

        val expectedOutput = listOf(
            "<class 'function'><class 'method'>",
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
            print(liloHostTest.getHost().buffer.toString())
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            assertTrue(liloHostTest.getHost().buffer.toString() == expectedOutput[index])
        }
    }

    @Test
    fun `test evaluate raise stmt`() {
        val sourceCodes = mutableListOf(
            "raise BaseException",
            "raise Exception",
            "raise StopIteration",
            "raise BaseException from StopIteration",
        )

        val expectedOutput = listOf(
            "BaseException",
            "Exception",
            "StopIteration",
            "BaseException from StopIteration",
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
            assert(interpreterResult.isFailure())
            val message = interpreterResult.toFailureError<LiloExceptionMessage>().message
            assert(message == expectedOutput[index]) {
                println("Interpreter Idx ${index}, expected ${expectedOutput[index]}, got $message")
            }
        }
    }

    @Test
    fun `test evaluate globals stmt`() {
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

}
