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

class LiloComplexTest {

    @Test
    fun complex_init_test() {
        val sourceCodes = mutableListOf(
            """
            c = 2j
            print(type(c))
            """,
            "print(complex())",
            "print(complex(1))",
            "print(complex(1, 2))",
            "print(complex(1, 2) + complex(2, 1))",
        )

        val expectedOutput = listOf(
            "<class 'complex'>",
            "(0.0+0.0j)",
            "(1.0+0.0j)",
            "(1.0+2.0j)",
            "(3.0+3.0j)",
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
                println("Error[RT]: " + interpreterResult.toFailureError<LiloResult.Failure<LiloExceptionMessage>>().error.message)
            }
            assertTrue("Interpreter error", interpreterResult.isSuccess())
            assert(value = liloMachine.getHost().buffer.toString() == expectedOutput[index]) {
                print((liloMachine.getHost().buffer.toString()))
            }
        }
    }
}
