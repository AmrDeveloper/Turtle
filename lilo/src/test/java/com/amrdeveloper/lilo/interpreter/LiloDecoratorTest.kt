package com.amrdeveloper.lilo.interpreter

import com.amrdeveloper.lilo.utils.testLiloInterpreter
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloDecoratorTest {

    @Test
    fun test_decorator() {
        val sourceCodes = mutableListOf(
            """
            def decorator(fn):
                return 1

            @decorator
            def foo():
                return 2

            print(foo)
            """,
        )

        val expectedOutput = listOf(
            "1"
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }
}
