package com.amrdeveloper.lilo.interpreter

import com.amrdeveloper.lilo.utils.testLiloInterpreter
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloInterpreterTest {

    @Test
    fun test_evaluate_imported_function() {
        val sourceCodes = mutableListOf(
            """
            import random
            a = random.random()
            """,
            """
            import random as r
            a = r.random()
            """,
            """
            from random import random
            a = random()
            """,
            """
            from random import (random)
            a = random()
            """,
        )

        for (sourceCode in sourceCodes) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
        }
    }

    @Test
    fun test_evaluate_call() {
        val sourceCodes = mutableListOf(
            """
            def foo(p):
                pass
            foo(10)
            """,
            """
            import random
            a = random.random()
            """
        )

        for (sourceCode in sourceCodes) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assertTrue(result!!.isEmpty())
        }
    }

    @Test
    fun test_evaluate_print_call() {
        val sourceCodes = mutableListOf(
            """
            print(42)
            """,
        )

        val expectedOutput = listOf(
            "42",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun `test evaluate if expr`() {
        val sourceCodes = mutableListOf(
            """
            a = 1 if True else 2
            print(a)
            """,
            """
            a = 1 if False else (2 if True else 3)
            print(a)  
            """,
            """
            a = (2 if (False) else 3) if (True) else 2
            print(a)
            """,
            """
            a = 1 if 1 else 2
            print(a)
            """,
            """
            a = 1 if 0 else 2
            print(a)
            """,
        )

        val expectedOutput = listOf(
            "1",
            "2",
            "3",
            "1",
            "2",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun `test evaluate unary expr`() {
        val sourceCodes = mutableListOf(
            """
            print(-10)
            """,
            """
            print(+10)
            """
        )

        val expectedOutput = listOf(
            "-10",
            "10"
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun `test evaluate lambda expr`() {
        val sourceCodes = mutableListOf(
            """
            identity = lambda a : a
            print(identity(1))
            """,
            """
            a = lambda a : a + 1
            print(a(1))
            """,
            """
            x = 10
            add_x = lambda a : a + x
            print(add_x(5))
            """,
        )

        val expectedOutput = listOf(
            "1",
            "2",
            "15",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun `test evaluate if stmt`() {
        val sourceCodes = mutableListOf(
            """
            if True:
               print(1)
            """,
            """
            if False:
               print(1)
            """,
            """
            if False:
               print(1)
            elif True:
               print(2)
            """,
            """
            if False:
               print(1)
            else:
               print(2)
            """,
            """
            if False:
               print(1)
            elif False:
               print(2)
            else:
               print(3)
            """,
            """
            if 2:
               print(1)
            """,
            """
            if 0:
               print(1)
            """,
        )

        val expectedOutput = listOf(
            "1",
            "",
            "2",
            "2",
            "3",
            "1",
            "",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun `test evaluate while stmt`() {
        val sourceCodes = mutableListOf(
            """
            x = 5
            while x > 0:
                print(x)
                x = x - 1
            """,
            """
            x = 5
            while x > 0:
                print(x)
                break
            """,
            """
            x = 5
            while x > 0:
                x = x - 1
                continue
                print(x)
            """,
            """
            x = 5
            while x > 5:
                print(x)
                x = x - 1
            else:
                print(0)
            """,
        )

        val expectedOutput = listOf(
            "54321",
            "5",
            "",
            "0",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun test_binding() {
        val sourceCodes = mutableListOf(
            """
            print(type(list.append))
            l = [2, 3]
            print(type(l.append))    
            """.trimIndent()
        )

        val expectedOutput = listOf(
            "<class 'function'><class 'method'>",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun `test evaluate raise stmt`() {
        val sourceCodes = mutableListOf(
            "raise BaseException",
            "raise Exception",
            "raise StopIteration",
            "raise BaseException from StopIteration",
        )

        val expectedOutput = listOf(
            "BaseException",
            "Exception",
            "StopIteration",
            "BaseException from StopIteration",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }

    @Test
    fun test_assignment() {
        val sourceCodes = mutableListOf(
            """
            a = 10
            print(a)
            """.trimIndent(),
            """
            a, b = (10, 20)
            print(a, ",", b)
            """.trimIndent(),
            """
            a, b, c = (10, 20, 30)
            print(a, ",", b, ",", c)
            """.trimIndent(),
            """
            a, b, c = 10, 20, 30
            print(a, ",", b, ",", c)
            """.trimIndent(),
            """
            t = 10, 20, 30
            a, b, c = t
            print(a, ",", b, ",", c)
            """.trimIndent(),
        )

        val expectedOutput = listOf(
            "10",
            "10,20",
            "10,20,30",
            "10,20,30",
            "10,20,30",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }
}
