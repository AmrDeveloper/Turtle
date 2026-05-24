package com.amrdeveloper.lilo.lexer

import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailureError
import com.amrdeveloper.lilo.parser.LiloLexer
import com.amrdeveloper.lilo.utils.isValidLiloTokens
import org.junit.Test

class LiloLexerTest {

    @Test
    fun `test consume comments`() {
        val sourceCodes = listOf(
            """
            # Hello comments
            "Hello"
            a = 10
            """
        )

        for (sourceCode in sourceCodes) {
            assert(isValidLiloTokens(sourceCode))
        }
    }

}
