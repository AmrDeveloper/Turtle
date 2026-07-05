package com.amrdeveloper.lilo.lexer

import com.amrdeveloper.lilo.utils.testLiloLexer
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
            """,
            """
            while True:
                while True:
                    pass
                while True:
                    pass
            print(1)    
            """
        )

        sourceCodes.forEach { assert(testLiloLexer(sourceCode = it)) }
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

        sourceCodes.forEach { assert(!testLiloLexer(sourceCode = it)) }
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

        sourceCodes.forEach { assert(testLiloLexer(sourceCode = it)) }
    }

    @Test
    fun `test implicit line joining`() {
        val sourceCodes = listOf(
            """
            x = [
                1,
                2,
                3
            ]
            """,
            """
            y = (
            1,
              2,
                3
            )
            """,
            """
            z = {
                "a": 1,
            "b": 2
            }
            """
        )

        sourceCodes.forEach { assert(testLiloLexer(sourceCode = it)) }
    }

    @Test
    fun `test nested implicit line joining`() {
        val sourceCodes = listOf(
            """
            matrix = [
                [1, 2],
                [3, 4]
            ]
            """
        )

        sourceCodes.forEach { assert(testLiloLexer(sourceCode = it)) }
    }

    @Test
    fun `test indentation after implicit line joining`() {
        val sourceCode = """
            if True:
                x = [
                    1,
                    2
                ]
                print(x)
        """.trimIndent()

        assert(testLiloLexer(sourceCode = sourceCode))
    }
}
