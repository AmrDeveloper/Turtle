package com.amrdeveloper.lilo.interpreter

import com.amrdeveloper.lilo.utils.testLiloInterpreter
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloAssignTest {

    @Test
    fun test_assign_expr() {
        val sourceCodes = mutableListOf(
            """
            print(a := 1)
            """,
            """
            (a := 1)
            print(a)
            """,
        )

        val expectedOutput = listOf(
            "1",
            "1",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }
}
