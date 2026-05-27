package com.amrdeveloper.lilo

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

class LiloListTest {

    @Test
    fun `test builtin list`() {
        val sourceCodes = mutableListOf(
            """
            v = []
            v.append(1)
            print(len(v))
            """,
            """
            a = [1]
            b = [2]
            a.extend(b)
            print(len(a))
            """,
        )

        val expectedOutput = listOf(
            "1",
            "2"
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
