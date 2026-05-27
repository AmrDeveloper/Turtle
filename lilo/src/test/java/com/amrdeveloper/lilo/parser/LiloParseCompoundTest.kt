package com.amrdeveloper.lilo.parser

import com.amrdeveloper.lilo.utils.isValidLiloSyntax
import org.junit.Test

class LiloParserCompoundTest {

    @Test
    fun `test function decl statement`() {
        val sourceCodes = listOf(
            "def foo() : return 1",
            "def identity(x) : return x",
            "def one() : 1",
            """
            def identity():
               return 1
            """,
            """
            def return_none():
               return
            """
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }

    @Test
    fun `test while statement`() {
        val sourceCodes = listOf(
            """
            while true:
               print(a)
            """,
            """
            while true:
               print(1)
            else:
               print(2)
            """
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }

    @Test
    fun `test for statement`() {
        val sourceCodes = listOf(
            """
            for a in list:
               print(a)
            """,
            """
            for a in list:
               print(a)
            else:
               print(b)
            """
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }

    @Test
    fun `test if statement`() {
        val sourceCodes = listOf(
            """
            if True:
               print(1)
            """,
            """
            if True:
                pass
            else:
                pass
            """,
            """
            if True:
               pass
            elif True:
               pass
            else:
               pass
            """,
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }
}
