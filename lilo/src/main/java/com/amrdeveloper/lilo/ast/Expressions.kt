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

package com.amrdeveloper.lilo.ast

import com.amrdeveloper.lilo.frontend.Token

abstract class Expression {
    abstract fun <R> accept(visitor: ExpressionVisitor<R>): R
}

class GroupExpression(
    val expression: Expression
) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class AssignExpression(
    val operator: Token,
    val left: Expression,
    val value: Expression
) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class BinaryExpression(
    val left: Expression,
    val operator: Token,
    val right: Expression,
) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class ComparisonExpression(
    val left: Expression,
    val operator: Token,
    val right: Expression,
) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class LogicalExpression(
    val left: Expression,
    val operator: Token,
    val right: Expression,
) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class UnaryExpression(
    val operator: Token,
    val right: Expression,
) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class CallExpression(
    val callee: Expression,
    val paren : Token,
    val arguments: List<Expression>
) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class DotExpression(
    var dot: Token,
    val caller: Expression,
    val callee: Statement,
) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class IndexExpression(
    val bracket : Token,
    val left: Expression,
    val index: Expression
) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class VariableExpression(
    val value: Token
) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class ListExpression(
    val values: List<Expression>
) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class NumberExpression(
    val value: Float
) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class BooleanExpression(
    val value: Boolean
) : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class NewTurtleExpression : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}

class ThieExpression : Expression() {
    override fun <R> accept(visitor: ExpressionVisitor<R>): R {
        return visitor.visit(this)
    }
}