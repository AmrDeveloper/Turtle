package com.amrdeveloper.lilo.interpreter

import com.amrdeveloper.lilo.utils.testLiloInterpreter
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloListComp {

    @Test
    fun test_list_comp() {
        val sourceCodes = mutableListOf(
            "print([x for x in range(3)])",
            "print([x for x in range(3) for x in range(3)])",
            "print([x for x in range(3) if x > 0])",
            "print([x + y for x, y in ((1, 2), (2, 3), (4, 5))])",
        )

        val expectedOutput = listOf(
            "[0, 1, 2]",
            "[0, 1, 2, 0, 1, 2]",
            "[1, 2]",
            "[3, 5, 9]",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }
}
