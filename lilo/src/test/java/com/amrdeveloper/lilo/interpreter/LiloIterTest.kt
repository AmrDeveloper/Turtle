package com.amrdeveloper.lilo.interpreter

import com.amrdeveloper.lilo.utils.testLiloInterpreter
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloIterTest {

    @Test
    fun test_range_iterator() {
        val sourceCodes = mutableListOf(
            """
            for i in range(5):
                print(i)
            """,
            """
            for i in range(5, 0, -1):
                print(i)
            """,
            """
            for i in iter(range(5, 0, -1)):
                print(i)
            """,
            """
            for i in reversed(range(5, 0, -1)):
                print(i)
            """,
            """
            for i in range(0):
                print(i)
            else:
                print(-1)
            """,
        )

        val expectedOutput = listOf(
            "01234",
            "54321",
            "54321",
            "12345",
            "-1"
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun test_tuple_iterator() {
        val sourceCodes = mutableListOf(
            """
            for i in (1, 2, 3):
                print(i)
            """,
            """
            for i in ((1, 1), (2, 2), (3, 3)):
                print(i)
            """,
            """
            for f, s in ((1, 1), (2, 2), (3, 3)):
                print(f, s)
            """,
            """
            for i in 1, 2, 3:
                print(i)
            """,
            """
            for i in reversed((1, 2, 3)):
                print(i)
            """,
        )

        val expectedOutput = listOf(
            "123",
            "(1, 1)(2, 2)(3, 3)",
            "112233",
            "123",
            "321",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }
}
