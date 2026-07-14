package com.amrdeveloper.lilo.interpreter

import com.amrdeveloper.lilo.utils.testLiloInterpreter
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloFStringTest {

    @Test
    fun test_formatted_string_expr() {
        val sourceCodes = mutableListOf(
            """
            print(f"Hello")
            """,
            """
            print(f"Hello {1}")
            """,
            """
            print(f"Hello {10}")
            """,
            """
            print(f"Hello {10 * 2}")
            """,
            """
            print(f"Hello {10 * 2 * 2}")
            """,
        )

        val expectedOutput = listOf(
            "Hello",
            "Hello 1",
            "Hello 10",
            "Hello 20",
            "Hello 40",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }
}
