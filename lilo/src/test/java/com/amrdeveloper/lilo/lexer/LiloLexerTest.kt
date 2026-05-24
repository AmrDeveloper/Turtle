package com.amrdeveloper.lilo.lexer

import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailureError
import com.amrdeveloper.lilo.parser.LiloLexer
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

fun isValidLiloTokens(sourceCode: String) : Boolean {
    val lexerResult = LiloLexer(source = sourceCode).tokenize()
    if (lexerResult.isFailure()) {
        println("Error[Lexer]: " + lexerResult.toFailureError<LiloDiagnostic>().message)
        return false
    }
    return true
}
