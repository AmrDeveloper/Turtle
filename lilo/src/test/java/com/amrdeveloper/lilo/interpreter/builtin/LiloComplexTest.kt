package com.amrdeveloper.lilo.interpreter.builtin

import com.amrdeveloper.lilo.utils.testLiloInterpreter
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloComplexTest {

    @Test
    fun complex_init_test() {
        val sourceCodes = mutableListOf(
            """
            c = 2j
            print(type(c))
            """,
            "print(complex())",
            "print(complex(1))",
            "print(complex(1, 2))",
            "print(complex(1, 2) + complex(2, 1))",
            "print(complex(3, 1) - complex(2, 0))",
        )

        val expectedOutput = listOf(
            "<class 'complex'>",
            "(0.0+0.0j)",
            "(1.0+0.0j)",
            "(1.0+2.0j)",
            "(3.0+3.0j)",
            "(1.0+1.0j)",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun complex_methods_test() {
        val sourceCodes = mutableListOf(
            "print(complex(1, 2).real())",
            "print(complex(2, 1).imag())",
        )

        val expectedOutput = listOf(
            "1.0",
            "1.0",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }
}
