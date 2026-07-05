package com.amrdeveloper.lilo.interpreter.builtin

import com.amrdeveloper.lilo.utils.testLiloInterpreter
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloBuiltinTest {

    @Test
    fun `test builtin type`() {
        val sourceCodes = mutableListOf(
            "print(type(1))",
            "print(type(True))",
            "print(type(1.0))",
        )

        val expectedOutput = listOf(
            "<class 'int'>",
            "<class 'bool'>",
            "<class 'float'>"
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun `test builtin print`() {
        val sourceCodes = mutableListOf(
            """
            print(1)
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
