package com.amrdeveloper.lilo.interpreter.builtin

import com.amrdeveloper.lilo.utils.testLiloInterpreter
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloListTest {

    @Test
    fun `test builtin list`() {
        val sourceCodes = mutableListOf(
            """
            v = []
            v.append(1)
            print(len(v))
            """,
            """
            a = [1]
            b = [2]
            a.extend(b)
            print(len(a))
            """,
        )

        val expectedOutput = listOf(
            "1",
            "2"
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun list_clear() {
        val sourceCodes = mutableListOf(
            """
            v = [1, 2, 3]
            v.clear()
            print(len(v))
            """,
        )

        val expectedOutput = listOf(
            "0",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

}
