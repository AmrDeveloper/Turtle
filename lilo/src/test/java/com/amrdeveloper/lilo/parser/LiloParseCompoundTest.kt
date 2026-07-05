package com.amrdeveloper.lilo.parser

import com.amrdeveloper.lilo.utils.testLiloParser
import org.junit.Test

class LiloParserCompoundTest {

    @Test
    fun `test function decl statement`() {
        val sourceCodes = listOf(
            "def foo() : return 1",
            "def identity(x) : return x",
            "def one() : 1",
            """
            def identity():
               return 1
            
              # Comment with different indentation
            
            def identity2():
               return 1
            """,
            """
            def return_none():
               return
            """,
            """
            def add(a : int, b : int):
               return a + b
            """,
            """
            def add(a : int, b : int) -> int:
               return a + b
            """,
            """
            def add(a : int, b : int) : return a + b
            """,
            """
            @kernal
            def add(a : int, b : int) : return a + b
            """,
            """
            def add(a, b, out c):
               return
            """,
            """
            def add(a, b, out c, out d):
               return
            """,
            """
            def add(a : int, b : int, out c : int):
               return
            """,
            """
            @decorator1
            @decorator2
            @decorator3
            def add(a : int, b : int, out c : int):
               return
            """,
            """
            @gpu
            def add(a : int, b : int, out c : int):
               return
            """,
            """
            @kernal
            def add(a : int, b : int, out c : int):
               return
            """,
        )

        sourceCodes.forEach { assert(testLiloParser(sourceCode = it)) }
    }

    @Test
    fun `test while statement`() {
        val sourceCodes = listOf(
            """
            while true:
               print(a)
            """,
            """
            while true:
               print(1)
            else:
               print(2)
            """
        )

        sourceCodes.forEach { assert(testLiloParser(sourceCode = it)) }
    }

    @Test
    fun `test for statement`() {
        val sourceCodes = listOf(
            """
            for a in list:
               print(a)
            """,
            """
            for a in list:
               print(a)
            else:
               print(b)
            """
        )

        sourceCodes.forEach { assert(testLiloParser(sourceCode = it)) }
    }

    @Test
    fun `test if statement`() {
        val sourceCodes = listOf(
            """
            if True:
               print(1)
            """,
            """
            if True:
                pass
            else:
                pass
            """,
            """
            if True:
               pass
            elif True:
               pass
            else:
               pass
            """,
        )

        sourceCodes.forEach { assert(testLiloParser(sourceCode = it)) }
    }
}
