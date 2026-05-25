package com.amrdeveloper.lilo.lexer

import com.amrdeveloper.lilo.utils.isValidLiloTokens
import org.junit.Test

class LiloLexerTest {

    @Test
    fun `test consume correct indentation`() {
        val sourceCodes = listOf(
            """
            while True:
                while True:
                    pass
                print(1)
            """,
            """
            while True:
                while True:
                    pass
            print(1)    
            """.trimIndent()
        )

        sourceCodes.forEach { assert(isValidLiloTokens(sourceCode = it)) }
    }

    @Test
    fun `test consume invalid indentation`() {
        val sourceCodes = listOf(
            """
            while True:
                while True:
                    pass
                 print(1)
            """
        )

        sourceCodes.forEach { assert(!isValidLiloTokens(sourceCode = it)) }
    }

    @Test
    fun `test consume comments`() {
        val sourceCodes = listOf(
            """
            # Hello comments
            "Hello"
            a = 10
            """
        )

        sourceCodes.forEach { assert(isValidLiloTokens(sourceCode = it)) }
    }

}
