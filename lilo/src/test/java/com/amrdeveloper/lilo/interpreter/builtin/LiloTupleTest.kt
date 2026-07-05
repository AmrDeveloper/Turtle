package com.amrdeveloper.lilo.interpreter.builtin

import com.amrdeveloper.lilo.utils.testLiloInterpreter
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloTupleTest {

    @Test
    fun tuple_count_test() {
        val sourceCodes = mutableListOf(
            """
            c = (1, 2, 3, 1).count(5)
            print(c)
            """,
            """
            c = (1, 2, 3, 1).count(1)
            print(c)
            """,
        )

        val expectedOutput = listOf(
            "0",
            "2",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun tuple_index_test() {
        val sourceCodes = mutableListOf(
            """
            i = (1, 2, 3, 1).index(1)
            print(i)
            """,
            """
            i = (1, 2, 3, 1).index(2)
            print(i)
            """,
            """
            i = (1, 2, 3, 1).index(3)
            print(i)
            """,
            """
            i = (1, 2, 3, 1).index(1, 2)
            print(i)
            """,
        )

        val expectedOutput = listOf(
            "0",
            "1",
            "2",
            "3",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }
}
