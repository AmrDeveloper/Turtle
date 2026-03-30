package com.amrdeveloper.lilo

import com.amrdeveloper.lilo.common.isSuccess
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.parser.LiloLexer
import com.amrdeveloper.lilo.parser.LiloParser
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import org.junit.Test

import org.junit.Assert.*

class LiloParserTest {

    @Test
    fun `test parse list`() {
        val sourceCodes = listOf(
            "a = [1, 3, 4, 5]",
            "b = []"
        )

        for (sourceCode in sourceCodes) {
            val lexerResult = LiloLexer(source = sourceCode).tokenize()
            assertTrue("Lexer error", lexerResult.isSuccess())

            val parseResult = LiloParser(tokens = lexerResult.toSuccessData()).parse()
            assertTrue("Parser error", parseResult.isSuccess())
        }
    }
}