package com.amrdeveloper.lilo.interpreter

import com.amrdeveloper.lilo.common.LiloDiagnostic
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

class LiloExceptionTest {

    @Test
    fun test_try_except() {
        val sourceCodes = mutableListOf(
            """
            try:
               print(1)
            except:
               print(2)
            """,
            """
            try:
               print(1)
            finally:
               print(2)
            """,
            """
            try:
               raise NameError
            except:
               print(2)
            """,
            """
            try:
               print(1)
            except:
               print(2)
            else:
               print(3)
            """,
            """
            try:
               print(1)
            except:
               print(2)
            finally:
               print(3)
            """,
            """
            try:
               print(1)
            except:
               print(2)
            else:
               print(3)
            finally:
               print(4)
            """,
            """
            try:
               raise NameError
            except:
               print(2)
            else:
               print(3)
            finally:
               print(4)
            """,
            """
            try:
               raise NameError
            except NameError:
               print(2)
            else:
               print(3)
            finally:
               print(4)
            """,
        )

        val expectedOutput = listOf(
            "1",
            "12",
            "2",
            "13",
            "13",
            "134",
            "24",
            "24",
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
                println("Error[RT]: " + interpreterResult.toFailureError<LiloExceptionMessage>().message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            assert(liloMachine.getHost().buffer.toString() == expectedOutput[index]) {
                println(liloMachine.getHost().buffer.toString())
            }
        }
    }

}
