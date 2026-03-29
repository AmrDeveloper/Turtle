package com.amrdeveloper.lilo

import com.amrdeveloper.lilo.common.isSuccess
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.parser.LiloLexer
import com.amrdeveloper.lilo.parser.LiloParser
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import org.junit.Test

import org.junit.Assert.*

class LiloCompilerTest {

    @Test
    fun test_new_features() {
        val sourceCode = """
            a = [1, 2, 3, 4]
            b = [1, 2, 3, 4]
            print(1)

            def foo(p) {
                print(p)
            }

            foo(10)
        """.trimIndent()

        val lexerResult = LiloLexer(source = sourceCode).tokenize()
        assertTrue("Lexer error", lexerResult.isSuccess())

        val parseResult = LiloParser(tokens = lexerResult.toSuccessData()).parse()
        assertTrue("Parser error", parseResult.isSuccess())

        val liloTree = parseResult.toSuccessData()
        val interpreter = LiloInterpreter()
        val interpreterResult = interpreter.evaluate(program = liloTree)
        assertTrue("Interpreter error", interpreterResult.isSuccess())
    }
}