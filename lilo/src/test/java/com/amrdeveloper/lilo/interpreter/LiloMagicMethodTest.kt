package com.amrdeveloper.lilo.interpreter

import com.amrdeveloper.lilo.utils.testLiloInterpreter
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloMagicMethodTest {

    @Test
    fun test__add__magic_method() {
        val sourceCodes = mutableListOf(
            """
            a = 1
            b = 2
            c = a + b
            print(c)
            """,
        )

        val expectedOutput = listOf(
            "3",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun test_comparisons_magic_method() {
        val sourceCodes = mutableListOf(
            "print(2 == 1)",
            "print(2 != 1)",
            "print(2 > 1)",
            "print(2 >= 1)",
            "print(2 < 1)",
            "print(2 <= 1)",
        )

        val expectedOutput = listOf(
            "False",
            "True",
            "True",
            "True",
            "False",
            "False",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun test__str__magic_method() {
        val sourceCodes = mutableListOf(
            """
            a = 1
            b = 2
            c = a + b
            print(c)
            """,
            """
            print(1)
            """,
            """
            print(None)
            """,
            """
            print('Lilo')
            """,
            """
            print("Lilo")
            """,
            """
            print(int)
            """,
            """
            print(float)
            """
        )

        val expectedOutput = listOf(
            "3",
            "1",
            "None",
            "Lilo",
            "Lilo",
            "<class 'int'>",
            "<class 'float'>"
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun test__getitem__magic_method() {
        val sourceCodes = mutableListOf(
            // List
            """
            a = [1, 2, 3]
            print(a[0])
            """,
            """
            a = [1, 2, 3]
            print(a[1])
            """,
            """
            a = [1, 2, 3]
            print(a[2])
            """,
            // Tuple
            """
            a = (1, 2, 3)
            print(a[0])
            """,
            """
            a = (1, 2, 3)
            print(a[1])
            """,
            """
            a = (1, 2, 3)
            print(a[2])
            """,
        )

        val expectedOutput = listOf(
            "1",
            "2",
            "3",
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

    @Test
    fun test__setitem__magic_method() {
        val sourceCodes = mutableListOf(
            // List
            """
            a = [1, 2, 3]
            a[0] = 4
            print(a[0])
            """,
        )

        val expectedOutput = listOf(
            "4",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }


    @Test
    fun test__len__magic_method() {
        val sourceCodes = mutableListOf(
            // List
            """
            a = [1, 2, 3]
            print(len(a))
            """,
            // Set
            """
            a = {1, True, "Hello"}
            print(len(a))
            """,
            """
            a = {1, 1, 1}
            print(len(a))
            """,
            // Tuple
            """
            a = (1, True, "Hello")
            print(len(a))
            """,
            // Dict
            """
            a = { 1 : [1, 2, 3], 2 : [4, 5, 6] }
            print(len(a))
            """
        )

        val expectedOutput = listOf(
            "3",
            "3",
            "1",
            "3",
            "2",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun test_boolean_magic_method() {
        val sourceCodes = mutableListOf(
            "print(True or True)",
            "print(True or False)",
            "print(False or False)",
            "print(True and False and False)",
            "print(False and False)",
        )

        val expectedOutput = listOf(
            "True",
            "True",
            "False",
            "False",
            "False",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }
}
