/*
 * MIT License
 *
 * Copyright (c) 2022 AmrDeveloper (Amr Hesham)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.amrdeveloper.lilo

import com.amrdeveloper.lilo.ast.BlockStatement
import com.amrdeveloper.lilo.ast.ExpressionStatement
import com.amrdeveloper.lilo.ast.FunctionStatement
import com.amrdeveloper.lilo.ast.ReturnStatement

class LiloFunction(
    private val declaration: FunctionStatement,
    private val closure: LiloScope
) : LiloCallable {

    override fun arity(): Int {
        return declaration.parameters.size
    }

    override fun call(interpreter: LiloInterpreter, arguments: List<Any>): Any {
        val environment = LiloScope(closure)
        declaration.parameters.forEach { environment.define(it.literal, it) }

        val functionBody = declaration.body
        if (functionBody is ReturnStatement) {
            return functionBody.value.accept(interpreter)
        }

        if (functionBody is ExpressionStatement) {
            return functionBody.expression.accept(interpreter)
        }

        if (functionBody is BlockStatement) {
            val statements = functionBody.statements
            for (statement in statements) {
                if (statement is ReturnStatement) {
                    return statement.value.accept(interpreter)
                } else {
                    statement.accept(interpreter)
                }
            }
        }

        return 0
    }
}