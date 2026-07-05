package com.amrdeveloper.lilo.interpreter.builtin

import com.amrdeveloper.lilo.utils.testLiloInterpreter
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloBuiltinTypesTest {

    @Test
    fun `test builtin Int type`() {
        val sourceCodes = mutableListOf(
            "print(int(1))",
            "print(int(1.0))",
            "print(int(True))",
            "print(int.__init__(True))"
        )

        val expectedOutput = listOf(
            "1",
            "1",
            "1",
            "1",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }


    @Test
    fun `test builtin Complex type`() {
        val sourceCodes = mutableListOf(
            "print(1j)",
        )

        val expectedOutput = listOf(
            "(0.0+1.0j)",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }
}
