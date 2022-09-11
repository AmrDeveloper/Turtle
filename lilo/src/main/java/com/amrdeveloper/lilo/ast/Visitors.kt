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

interface StatementVisitor<R> {
    fun visit(statement: ExpressionStatement): R
    fun visit(statement: FunctionStatement): R
    fun visit(statement: ReturnStatement): R
    fun visit(statement: BlockStatement): R
    fun visit(statement: LetStatement): R
    fun visit(statement: IfStatement): R
    fun visit(statement: WhileStatement): R
    fun visit(statement: RepeatStatement): R
    fun visit(statement: CubeStatement): R
    fun visit(statement: CircleStatement): R
    fun visit(statement: MoveStatement): R
    fun visit(statement: MoveXStatement): R
    fun visit(statement: MoveYStatement): R
    fun visit(statement: ColorStatement): R
    fun visit(statement: BackgroundStatement): R
    fun visit(statement: SpeedStatement): R
    fun visit(statement: SleepStatement): R
    fun visit(statement: StopStatement): R
    fun visit(statement: RotateStatement): R
    fun visit(statement: ForwardStatement): R
    fun visit(statement: BackwardStatement): R
    fun visit(statement: RightStatement): R
    fun visit(statement: LeftStatement): R
}

interface ExpressionVisitor<R> {
    fun visit(expression: AssignExpression): R
    fun visit(expression: GroupExpression): R
    fun visit(expression: BinaryExpression): R
    fun visit(expression: LogicalExpression): R
    fun visit(expression: UnaryExpression): R
    fun visit(expression: CallExpression): R
    fun visit(expression: IndexExpression): R
    fun visit(expression: ListExpression): R
    fun visit(expression: VariableExpression): R
    fun visit(expression: NumberExpression): R
    fun visit(expression: BooleanExpression): R
}