package com.amrdeveloper.lilo.interpreter

import com.amrdeveloper.lilo.utils.testLiloInterpreter
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloExceptionTest {

    @Test
    fun test_try_except() {
        val sourceCodes = mutableListOf(
            """
            try:
               print(1)
            except:
               print(2)
            """,
            """
            try:
               print(1)
            finally:
               print(2)
            """,
            """
            try:
               raise NameError
            except:
               print(2)
            """,
            """
            try:
               print(1)
            except:
               print(2)
            else:
               print(3)
            """,
            """
            try:
               print(1)
            except:
               print(2)
            finally:
               print(3)
            """,
            """
            try:
               print(1)
            except:
               print(2)
            else:
               print(3)
            finally:
               print(4)
            """,
            """
            try:
               raise NameError
            except:
               print(2)
            else:
               print(3)
            finally:
               print(4)
            """,
            """
            try:
               raise NameError
            except NameError:
               print(2)
            else:
               print(3)
            finally:
               print(4)
            """,
            """
            def foo():
                try:
                   print(1)
                   return
                finally:
                   print(4)
                print(5)
            foo()
            """,
            """
            def foo():
                try:
                   print(1)
                   raise NameError
                except:
                   print(2)
                   return
                finally:
                   print(4)
            foo()
            """
        )

        val expectedOutput = listOf(
            "1",
            "12",
            "2",
            "13",
            "13",
            "134",
            "24",
            "24",
            "14",
            "124",
        )

        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val result = testLiloInterpreter(sourceCode)
            assertTrue(result != null)
            assert(value = result == expectedOutput[index])
        }
    }
}
