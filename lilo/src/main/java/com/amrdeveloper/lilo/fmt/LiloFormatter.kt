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

package com.amrdeveloper.lilo.fmt

import com.amrdeveloper.lilo.frontend.Token
import com.amrdeveloper.lilo.frontend.TokenType
import com.amrdeveloper.lilo.ast.AssignExpression
import com.amrdeveloper.lilo.ast.BackgroundStatement
import com.amrdeveloper.lilo.ast.BackwardStatement
import com.amrdeveloper.lilo.ast.BinaryExpression
import com.amrdeveloper.lilo.ast.BlockStatement
import com.amrdeveloper.lilo.ast.BooleanExpression
import com.amrdeveloper.lilo.ast.CallExpression
import com.amrdeveloper.lilo.ast.CircleStatement
import com.amrdeveloper.lilo.ast.ColorStatement
import com.amrdeveloper.lilo.ast.CubeStatement
import com.amrdeveloper.lilo.ast.DotExpression
import com.amrdeveloper.lilo.ast.ExpressionStatement
import com.amrdeveloper.lilo.ast.ForwardStatement
import com.amrdeveloper.lilo.ast.FunctionStatement
import com.amrdeveloper.lilo.ast.GroupExpression
import com.amrdeveloper.lilo.ast.HidePointerStatement
import com.amrdeveloper.lilo.ast.IfStatement
import com.amrdeveloper.lilo.ast.IndexExpression
import com.amrdeveloper.lilo.ast.LeftStatement
import com.amrdeveloper.lilo.ast.LetStatement
import com.amrdeveloper.lilo.ast.LiloScript
import com.amrdeveloper.lilo.ast.ListExpression
import com.amrdeveloper.lilo.ast.LogicalExpression
import com.amrdeveloper.lilo.ast.MoveStatement
import com.amrdeveloper.lilo.ast.MoveXStatement
import com.amrdeveloper.lilo.ast.MoveYStatement
import com.amrdeveloper.lilo.ast.NewTurtleExpression
import com.amrdeveloper.lilo.ast.NumberExpression
import com.amrdeveloper.lilo.ast.RepeatStatement
import com.amrdeveloper.lilo.ast.ReturnStatement
import com.amrdeveloper.lilo.ast.RightStatement
import com.amrdeveloper.lilo.ast.RotateStatement
import com.amrdeveloper.lilo.ast.ShowPointerStatement
import com.amrdeveloper.lilo.ast.SleepStatement
import com.amrdeveloper.lilo.ast.SpeedStatement
import com.amrdeveloper.lilo.ast.StopStatement
import com.amrdeveloper.lilo.ast.ThieExpression
import com.amrdeveloper.lilo.ast.TreeVisitor
import com.amrdeveloper.lilo.ast.UnaryExpression
import com.amrdeveloper.lilo.ast.VariableExpression
import com.amrdeveloper.lilo.ast.WhileStatement

private const val TAG = "LiloFormatter"

class LiloFormatter : TreeVisitor<String, String> {

    private var indentation = 0

    fun formatLiloScript(script : LiloScript) : String {
        indentation = 0
        val stringBuilder = StringBuilder()
        script.statements.forEach { node ->
            stringBuilder.append(node.accept(this))
        }
        return stringBuilder.toString()
    }

    override fun visit(statement: ExpressionStatement): String {
        return statement.expression.accept(this)
    }

    override fun visit(statement: FunctionStatement): String {
        val builder = StringBuilder()
        builder.append("fun ")
        builder.append(statement.name)
        val parameters = statement.parameters
        if (parameters.isNotEmpty()) {
            builder.append(" (")
            parameters.forEachIndexed { index, parameter ->
                if (index == parameters.lastIndex) {
                    builder.append(parameter.literal)
                } else {
                    builder.append(parameter.literal)
                    builder.append(", ")
                }
            }
            builder.append(")")
        }

        if (statement.body is ReturnStatement) {
            builder.append(" = ")
            val returnNode = statement.body.value
            builder.append(returnNode.accept(this))
            builder.append("\n")
        } else {
            builder.append(statement.body.accept(this))
        }
        return builder.toString()
    }

    override fun visit(statement: ReturnStatement): String {
        val returnValue = statement.value.accept(this)
        return indentation() + "return $returnValue\n"
    }

    override fun visit(statement: BlockStatement): String {
        val builder = StringBuilder()
        builder.append("{\n")
        indentation += 2
        statement.statements.forEach { node ->
            builder.append(node.accept(this))
        }
        indentation -= 2
        if (indentation == 0) builder.append("}\n")
        else builder.append(indentation() + "}\n")
        return builder.toString()
    }

    override fun visit(statement: LetStatement): String {
        return indentation() + "let ${statement.name} = ${statement.value.accept(this)}\n"
    }

    override fun visit(statement: IfStatement): String {
        val builder = StringBuilder()
        builder.append(indentation() + "if ")
        builder.append(statement.condition.accept(this))
        builder.append(" ")
        builder.append(statement.body.accept(this))
        statement.alternatives.forEachIndexed { index, ifStatement ->
            if (index == statement.alternatives.lastIndex) {
                if (ifStatement.keyword.type == TokenType.TOKEN_ELSE) {
                    builder.append(indentation() + "else ")
                } else {
                    builder.append(indentation() + "elif ")
                    builder.append(statement.condition.accept(this))
                    builder.append(" ")
                }
                builder.append(ifStatement.body.accept(this))
            } else {
                builder.append(indentation() + "elif ")
                builder.append(statement.condition.accept(this))
                builder.append(" ")
                builder.append(ifStatement.body.accept(this))
            }
        }
        return builder.toString()
    }

    override fun visit(statement: WhileStatement): String {
        val builder = StringBuilder()
        builder.append(indentation())
        builder.append("while ")
        builder.append(statement.condition.accept(this))
        builder.append(" ")
        builder.append(statement.body.accept(this))
        return builder.toString()
    }

    override fun visit(statement: RepeatStatement): String {
        val builder = StringBuilder()
        builder.append(indentation())
        builder.append("repeat ")
        builder.append(statement.condition.accept(this))
        builder.append(" ")
        builder.append(statement.body.accept(this))
        return builder.toString()
    }

    override fun visit(statement: CubeStatement): String {
        return indentation() + "cube " + statement.radius.accept(this) + "\n"
    }

    override fun visit(statement: CircleStatement): String {
        return indentation() + "circle " + statement.radius.accept(this) + "\n"
    }

    override fun visit(statement: MoveStatement): String {
        val x = statement.xValue.accept(this)
        val y = statement.yValue.accept(this)
        return indentation() + "move " + x + ", " + y + "\n"
    }

    override fun visit(statement: MoveXStatement): String {
        return indentation() + "movex " + statement.amount.accept(this) + "\n"
    }

    override fun visit(statement: MoveYStatement): String {
        return indentation() + "movey " + statement.amount.accept(this) + "\n"
    }

    override fun visit(statement: ColorStatement): String {
        return indentation() + "color " + statement.color.accept(this) + "\n"
    }

    override fun visit(statement: BackgroundStatement): String {
        return indentation() + "background " + statement.color.accept(this) + "\n"
    }

    override fun visit(statement: SpeedStatement): String {
        return indentation() + "speed " + statement.amount.accept(this) + "\n"
    }

    override fun visit(statement: SleepStatement): String {
        return indentation() + "sleep " + statement.amount.accept(this) + "\n"
    }

    override fun visit(statement: ShowPointerStatement): String {
        return indentation() + "show\n"
    }

    override fun visit(statement: HidePointerStatement): String {
        return indentation() + "hide\n"
    }

    override fun visit(statement: StopStatement): String {
        return indentation() + "stop\n"
    }

    override fun visit(statement: RotateStatement): String {
        return indentation() + "rotate " + statement.value.accept(this) + "\n"
    }

    override fun visit(statement: ForwardStatement): String {
        return indentation() + "forward " + statement.value.accept(this) + "\n"
    }

    override fun visit(statement: BackwardStatement): String {
        return indentation() + "backward " + statement.value.accept(this) + "\n"
    }

    override fun visit(statement: RightStatement): String {
        return indentation() + "right " + statement.value.accept(this) + "\n"
    }

    override fun visit(statement: LeftStatement): String {
        return indentation() + "left " + statement.value.accept(this) + "\n"
    }

    override fun visit(expression: AssignExpression): String {
        return indentation() + expression.left.accept(this) + " = " + expression.value.accept(this) + "\n"
    }

    override fun visit(expression: GroupExpression): String {
        return indentation() + "(" + expression.expression.accept(this) + ")"
    }

    override fun visit(expression: BinaryExpression): String {
        return expression.left.accept(this) + operatorLiteral(expression.operator) + expression.right.accept(this)
    }

    override fun visit(expression: LogicalExpression): String {
        return expression.left.accept(this) + operatorLiteral(expression.operator) + expression.right.accept(this)
    }

    override fun visit(expression: UnaryExpression): String {
        return operatorLiteral(expression.operator) + expression.right.accept(this)
    }

    override fun visit(expression: CallExpression): String {
        val builder = StringBuilder()
        builder.append(indentation())
        builder.append(expression.callee.accept(this))
        builder.append("(")
        expression.arguments.forEachIndexed { index, argument ->
            if (index == expression.arguments.lastIndex) {
                builder.append(argument.accept(this))
            } else {
                builder.append(argument.accept(this))
                builder.append(", ")
            }
        }
        builder.append(")")
        builder.append("\n")
        return builder.toString()
    }

    override fun visit(expression: DotExpression): String {
        return indentation() + expression.caller.accept(this) + "." + expression.callee.accept(this)
    }

    override fun visit(expression: IndexExpression): String {
        return expression.left.accept(this) + "[" + expression.index.accept(this) + "]"
    }

    override fun visit(expression: ListExpression): String {
        val builder = StringBuilder()
        builder.append("[")
        expression.values.forEachIndexed { index, value ->
            if (index == expression.values.lastIndex) {
                builder.append(value.accept(this))
            } else {
                builder.append(value.accept(this))
                builder.append(", ")
            }
        }
        builder.append("]")
        return builder.toString()
    }

    override fun visit(expression: VariableExpression): String {
        return expression.value.literal
    }

    override fun visit(expression: NumberExpression): String {
        return expression.value.toString()
    }

    override fun visit(expression: BooleanExpression): String {
        return expression.value.toString()
    }

    override fun visit(expression: NewTurtleExpression): String {
        return indentation() + "new_turtle"
    }

    override fun visit(expression: ThieExpression): String {
        return "this"
    }

    private fun operatorLiteral(operator : Token) : String {
        return when (operator.type) {
            // Binary Operators
            TokenType.TOKEN_PLUS -> "+"
            TokenType.TOKEN_MINUS -> "-"
            TokenType.TOKEN_MUL -> "*"
            TokenType.TOKEN_DIV -> "/"
            TokenType.TOKEN_REMINDER -> "%"

            // Comparisons Operators
            TokenType.TOKEN_EQ -> "="
            TokenType.TOKEN_EQ_EQ -> "=="
            TokenType.TOKEN_BANG -> "!"
            TokenType.TOKEN_BANG_EQ -> "!="
            TokenType.TOKEN_GT -> ">"
            TokenType.TOKEN_GT_EQ -> ">="
            TokenType.TOKEN_LS -> "<"
            TokenType.TOKEN_LS_EQ -> "<="

            // Logical Operators
            TokenType.TOKEN_OR -> "|"
            TokenType.TOKEN_LOGICAL_OR -> "||"
            TokenType.TOKEN_AND -> "&"
            TokenType.TOKEN_LOGICAL_AND -> "&&"

            else -> ""
        }
    }

    private fun indentation() : String {
        if (indentation == 0) return ""
        return " ".repeat(indentation)
    }
}