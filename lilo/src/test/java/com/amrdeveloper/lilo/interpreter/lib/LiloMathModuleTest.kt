package com.amrdeveloper.lilo.interpreter.lib

import com.amrdeveloper.lilo.utils.testLiloInterpreter
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloMathModuleTest {

    @Test
    fun `test math modules`() {
        val sourceCodes = mutableListOf(
            """
            import math
            print(math.inf)
            """,
            """
            import math
            print(math.nan)
            """,
        )

        val expectedOutput = listOf(
            "inf",
            "nan"
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }
}
