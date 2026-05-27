package com.amrdeveloper.lilo.parser

import com.amrdeveloper.lilo.utils.isValidLiloSyntax
import org.junit.Test

class LiloParserSimpleTest {

    @Test
    fun `test parse import statement`() {
        val sourceCodes = listOf(
            "import a",
            "import complex",
            "import complex;",
            "import random as r",
            "import random as r;",
            "import a, b",
            "import a, b;",
            "import one as o, two as t",
            "import one as o, two as t;",
            "from random import random",
            "from random import (random)",
            "from random import (random);",
            "from random import *",
            "from random import *;"
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }

    @Test
    fun `test raise statement`() {
        val sourceCodes = listOf(
            "raise",
            "raise;",
            "raise StopIterator",
            "raise StopIterator;",
            "raise StopIterator from Exception",
            "raise StopIterator from Exception;",
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }

    @Test
    fun `test parse dot expression`() {
        val sourceCodes = listOf(
            "a.b", "random.random", "a.__add__"
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }

    @Test
    fun `test parse list`() {
        val sourceCodes = listOf(
            "b = [1]", "b = [1, 2, 3]"
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }

    @Test
    fun `test parse set`() {
        val sourceCodes = listOf(
            "b = { 1 }", "b = {1, 2, 3}"
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }

    @Test
    fun `test parse tuple`() {
        val sourceCodes = listOf(
            "b = (1)", "b = (1, 2, 3)", "b = (1,)"
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }

    @Test
    fun `test parse dictionary`() {
        val sourceCodes = listOf(
            "a = { 1 : 2 }", "a = {}", "a = { 1 : [1, 2, 3], 2 : [4, 5, 6] }"
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }


    @Test
    fun `test subscript list`() {
        val sourceCodes = listOf(
            "list[1]", "list[1][2]", "list[1, 2]", "list[1, 2, 3]", "kernal[blocks, threads]"
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }

    @Test
    fun `test if expr`() {
        val sourceCodes = listOf(
            "a = 1 if (True) else 2",
            "a = 1 if (True) else (2 if (True) else 3)",
            "a = (2 if (True) else 3) if (True) else 2",
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }

    @Test
    fun `test lambda expr`() {
        val sourceCodes = listOf(
            "lambda a : a",
            "foo = lambda a : a + 1",
            "foo = lambda a : a + 1;",
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }

    @Test
    fun `test global and nonlocal statements`() {
        val sourceCodes = listOf(
            "global a",
            "global a, b",
            "nonlocal a",
            "nonlocal a, b",
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }

    @Test
    fun `test annotated assignment statement`() {
        val sourceCodes = listOf(
            "a : int = 1",
            "a : int = 1;",
            "a : float = 1.0",
            "a : float = 1.0;",
        )
        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }

    @Test
    fun `test assert statement`() {
        val sourceCodes = listOf(
            "assert a",
            "assert a, b",
            "assert a;",
            "assert a, b;",
        )

        sourceCodes.forEach { assert(isValidLiloSyntax(sourceCode = it)) }
    }
}
