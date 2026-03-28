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

abstract class Statement {
    abstract fun <R> accept(visitor: StatementVisitor<R>): R
}

class ExpressionStatement(
    val expression: Expression
) : Statement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class FunctionStatement(
    val name: String,
    val parameters: List<Token>,
    val body: Statement
) : Statement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class ReturnStatement(
    val value: Expression
) : Statement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class LetStatement(
    val name: String,
    val value: Expression,
) : Statement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class BlockStatement(
    val statements: List<Statement>
) : Statement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class IfStatement(
    val keyword: Token,
    val condition: Expression,
    val body: Statement,
    val alternatives: List<IfStatement> = listOf()
) : Statement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class WhileStatement(
    val keyword: Token,
    val condition: Expression,
    val body: Statement,
) : Statement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class RepeatStatement(
    val keyword: Token,
    val condition: Expression,
    val body: Statement,
) : Statement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

abstract class TurtleStatement(open var id: Int) : Statement()

class CubeStatement(
    val keyword: Token,
    val radius: Expression,
    override var id: Int = 0,
) : TurtleStatement(id) {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class CircleStatement(
    val keyword: Token,
    val radius: Expression,
    override var id: Int = 0,
) : TurtleStatement(id) {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class MoveStatement(
    val keyword: Token,
    val xValue: Expression,
    val yValue: Expression,
    override var id: Int = 0,
) : TurtleStatement(id) {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class MoveXStatement(
    val keyword: Token,
    val amount: Expression,
    override var id: Int = 0,
) : TurtleStatement(id) {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class MoveYStatement(
    val keyword: Token,
    val amount: Expression,
    override var id: Int = 0,
) : TurtleStatement(id) {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class ColorStatement(
    val keyword: Token,
    val color: Expression,
    override var id: Int = 0,
) : TurtleStatement(id) {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class BackgroundStatement(
    val keyword: Token,
    val color: Expression
) : Statement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class SpeedStatement(
    val keyword: Token,
    val amount: Expression
) : Statement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class SleepStatement(
    val keyword: Token,
    val amount: Expression
) : Statement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class RotateStatement(
    val keyword: Token,
    val value: Expression,
    override var id: Int = 0,
) : TurtleStatement(id) {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class ForwardStatement(
    val keyword: Token,
    val value: Expression,
    override var id: Int = 0,
) : TurtleStatement(id) {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class BackwardStatement(
    val keyword: Token,
    val value: Expression,
    override var id: Int = 0,
) : TurtleStatement(id) {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class RightStatement(
    val keyword: Token,
    val value: Expression,
    override var id: Int = 0,
) : TurtleStatement(id) {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class LeftStatement(
    val keyword: Token,
    val value: Expression,
    override var id: Int = 0,
) : TurtleStatement(id) {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class ShowPointerStatement(
    override var id: Int = 0,
) : TurtleStatement(id) {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class HidePointerStatement(
    override var id: Int = 0,
) : TurtleStatement(id) {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class PenUpStatement(
    override var id: Int = 0,
) : TurtleStatement(id) {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class PenDownStatement(
    override var id: Int = 0,
) : TurtleStatement(id) {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}

class StopStatement : Statement() {
    override fun <R> accept(visitor: StatementVisitor<R>): R {
        return visitor.visit(this)
    }
}