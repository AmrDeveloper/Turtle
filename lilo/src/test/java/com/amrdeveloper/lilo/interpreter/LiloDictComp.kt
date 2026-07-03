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

class LiloDictComp {

    @Test
    fun test_dict_comp() {
        val sourceCodes = mutableListOf(
            "print({x:x for x in range(3)})",
            "print({x:x for x in range(3) for x in range(3)})",
            "print({x:x for x in range(3) if x > 0})",
            "print({x:x+y for x, y in ((1, 2), (2, 3), (4, 5)) if x > 0})",
        )

        val expectedOutput = listOf(
            "{0:0, 1:1, 2:2}",
            "{0:0, 1:1, 2:2}",
            "{1:1, 2:2}",
            "{1:3, 2:5, 4:9}",
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
