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

package com.amrdeveloper.lilo.backend

import com.amrdeveloper.lilo.utils.LiloException
import com.amrdeveloper.lilo.ast.*
import com.amrdeveloper.lilo.utils.bindBuiltinColors
import com.amrdeveloper.lilo.std.bindStandardModules
import com.amrdeveloper.lilo.frontend.TokenType

class LiloEvaluator : TreeVisitor<Unit, Any> {

    private var shouldTerminate = false

    private val globalsScope = LiloScope()
    private var currentScope = globalsScope

    private lateinit var onEvaluatorStarted : () -> Unit
    private lateinit var onEvaluatorFinished : () -> Unit
    private lateinit var onInstructionEmitterListener : (Instruction) -> Unit
    private lateinit var onBackgroundChangeListener: (Int) -> Unit
    private lateinit var onExceptionListener: (LiloException) -> Unit

    private var turtlePointerId = 0
    private val mainTurtlePointer = TurtleObject(0)

    fun executeLiloScript(script: LiloScript): ExecutionState {
        preExecuteLiloScript()
        if (::onEvaluatorStarted.isInitialized) onEvaluatorStarted()
        try {
            emitInstruction(NewTurtleInst(turtlePointerId++))
            script.statements.forEach { node ->
                if (shouldTerminate) return ExecutionState.FAILURE
                node.accept(this)
            }
        } catch (exception: LiloException) {
            if (::onExceptionListener.isInitialized) onExceptionListener(exception)
            if (::onEvaluatorFinished.isInitialized) onEvaluatorFinished()
            return ExecutionState.FAILURE
        }
        if (::onEvaluatorFinished.isInitialized) onEvaluatorFinished()
        return ExecutionState.SUCCESS
    }

    private fun preExecuteLiloScript() {
        shouldTerminate = false
        turtlePointerId = 0
        bindBuiltinColors(globalsScope)
        bindStandardModules(globalsScope)
    }

    override fun visit(statement: ExpressionStatement) {
        statement.expression.accept(this)
    }

    override fun visit(statement: FunctionStatement) {
        val function = LiloFunction(statement, currentScope)
        currentScope.define(statement.name, function)
    }

    override fun visit(statement: ReturnStatement) {
        statement.value.accept(this)
    }

    override fun visit(statement: LetStatement) {
        val values = statement.value.accept(this)
        currentScope.define(statement.name, values)
    }

    override fun visit(statement: BlockStatement) {
        executeBlockInScope(LiloScope(currentScope), *statement.statements.toTypedArray())
    }

    override fun visit(statement: IfStatement) {
        val condition = statement.condition.accept(this)
        if (condition !is Boolean) {
            throw LiloException(statement.keyword.position, "If condition must be boolean")
        }

        // If condition is true execute the body in new sub scope
        if (condition == true) {
            executeBlockInScope(LiloScope(currentScope), statement.body)
            return
        }

        for (alternative in statement.alternatives) {
            val alternativeCondition = alternative.condition.accept(this)
            if (alternativeCondition !is Boolean) {
                throw LiloException(alternative.keyword.position, "condition must be boolean")
            }

            // If one of alternative conditions is true execute the body in new sub scope
            if (alternativeCondition == true) {
                executeBlockInScope(LiloScope(currentScope), alternative.body)
                return
            }
        }
    }

    override fun visit(statement: WhileStatement) {
        val condition = statement.condition.accept(this)

        if (condition is Boolean) {
            while (statement.condition.accept(this) == true) {
                executeBlockInScope(LiloScope(currentScope), statement.body)
            }
            return
        }

        throw LiloException(statement.keyword.position, "If condition must be boolean")
    }

    override fun visit(statement: RepeatStatement) {
        val counter = statement.condition.accept(this)
        if (counter is Float) {
            repeat(counter.toInt()) { executeBlockInScope(LiloScope(currentScope), statement.body) }
            return
        }

        throw LiloException(statement.keyword.position, "Repeat counter must be a number")
    }

    override fun visit(statement: CubeStatement) {
        val value = statement.radius.accept(this)

        if (value is Float) {
            emitInstruction(RectangleInst(statement.id, value, value))
            return
        }

        throw LiloException(statement.keyword.position, "Cube value must be a number")
    }

    override fun visit(statement: CircleStatement) {
        val radius = statement.radius.accept(this)

        if (radius is Float) {
            emitInstruction(CircleInst(statement.id, radius))
            return
        }

        throw LiloException(statement.keyword.position, "Circle radius must be a number")
    }

    override fun visit(statement: MoveStatement) {
        val xValue = statement.xValue.accept(this)
        val yValue = statement.yValue.accept(this)

        if (xValue is Float && yValue is Float) {
            emitInstruction(MoveXInst(statement.id, xValue))
            emitInstruction(MoveYInst(statement.id,yValue))
            return
        }

        throw LiloException(statement.keyword.position, "Move X and y values must be a numbers")
    }

    override fun visit(statement: MoveXStatement) {
        val value = statement.amount.accept(this)

        if (value is Float) {
            emitInstruction(MoveXInst(statement.id, value))
            return
        }

        throw LiloException(statement.keyword.position, "Move X amount must be a number")
    }

    override fun visit(statement: MoveYStatement) {
        val value = statement.amount.accept(this)

        if (value is Float) {
            emitInstruction(MoveYInst(statement.id, value))
            return
        }

        throw LiloException(statement.keyword.position, "Move Y amount must be a number")
    }

    override fun visit(statement: ColorStatement) {
        val colorValue = statement.color.accept(this)

        if (colorValue is Int) {
            emitInstruction(ColorInst(statement.id, colorValue))
            return
        }

        throw LiloException(statement.keyword.position, "Color value must be Identifier")
    }

    override fun visit(statement: BackgroundStatement) {
        val colorValue = statement.color.accept(this)

        if (colorValue is Int) {
            if (::onBackgroundChangeListener.isInitialized) {
                onBackgroundChangeListener(colorValue)
            }
            return
        }

        throw LiloException(statement.keyword.position, "Color value must be Identifier")
    }

    override fun visit(statement: SpeedStatement) {
        val timeValue = statement.amount.accept(this)

        if (timeValue is Number) {
            emitInstruction(SpeedInst(timeValue.toInt()))
            return
        }

        throw LiloException(statement.keyword.position, "Speed value must ba an integer")
    }

    override fun visit(statement: SleepStatement) {
        val timeValue = statement.amount.accept(this)

        if (timeValue is Number) {
            emitInstruction(SleepInst(timeValue.toInt()))
            return
        }

        throw LiloException(statement.keyword.position, "Sleep value must ba an integer")
    }

    override fun visit(statement: ShowPointerStatement) {
        emitInstruction(VisibilityInst(statement.id, true))
    }

    override fun visit(statement: HidePointerStatement) {
        emitInstruction(VisibilityInst(statement.id, false))
    }

    override fun visit(statement: StopStatement) {
        shouldTerminate = true
    }

    override fun visit(statement: RotateStatement) {
        val degree = statement.value.accept(this)

        if (degree is Float) {
            emitInstruction(DegreeInst(statement.id, degree, Operator.PLUS))
            return
        }

        throw LiloException(statement.keyword.position, "Rotate degree must be a number")
    }

    override fun visit(statement: ForwardStatement) {
        val value = statement.value.accept(this)

        if (value is Float) {
            emitInstruction(LineInst(statement.id, value))
            return
        }

        throw LiloException(statement.keyword.position, "Forward value must be a number")
    }

    override fun visit(statement: BackwardStatement) {
        val value = statement.value.accept(this)

        if (value is Float) {
            emitInstruction(DegreeInst(statement.id, 180f, Operator.MINUS))
            emitInstruction(LineInst(statement.id, value))
            return
        }

        throw LiloException(statement.keyword.position, "Backward value must be a number")
    }

    override fun visit(statement: RightStatement) {
        val value = statement.value.accept(this)

        if (value is Float) {
            emitInstruction(DegreeInst(statement.id, 90f, Operator.MINUS))
            emitInstruction(LineInst(statement.id, value))
            return
        }

        throw LiloException(statement.keyword.position, "Right value must be a number")
    }

    override fun visit(statement: LeftStatement) {
        val value = statement.value.accept(this)

        if (value is Float) {
            emitInstruction(DegreeInst(statement.id, 90f, Operator.PLUS))
            emitInstruction(LineInst(statement.id, value))
            return
        }

        throw LiloException(statement.keyword.position, "Left value must be a number")
    }

    override fun visit(expression: AssignExpression): Any {
        val left = expression.left
        val value = expression.value.accept(this)
        if (left is IndexExpression) {
            val variable = left.left
            if (variable is VariableExpression) {
                val liloList = currentScope.lookup(variable.value.literal)
                if (liloList is LiloList) {
                    val index = left.index.accept(this)
                    if (index is Float) {
                        if (index.toInt() < liloList.values.size) {
                            liloList.values[index.toInt()] = value
                            currentScope.assign(variable.value.literal, liloList)
                        } else {
                            throw LiloException(expression.operator.position, "Index can't be large than collection size")
                        }
                    } else {
                        throw LiloException(expression.operator.position, "Index must be a number")
                    }
                } else {
                    throw LiloException(expression.operator.position, "Index expression work only with collections.")
                }
            } else {
                throw LiloException(expression.operator.position, "Assign Collection index require variable in the left, x[i]=v")
            }
        }
        else if (left is VariableExpression) {
            val name = left.value.literal
            val isAssigned = currentScope.assign(name, value)
            if (isAssigned.not()) {
                throw LiloException(expression.operator.position, "Invalid assignment.")
            }
        }
        return value
    }

    override fun visit(expression: BinaryExpression): Any {
        val left = expression.left.accept(this)
        val right = expression.right.accept(this)
        val op = expression.operator.type

        if (left is Float && right is Float) {
            return when (op) {
                TokenType.TOKEN_PLUS -> left + right
                TokenType.TOKEN_MINUS -> left - right
                TokenType.TOKEN_MUL -> left * right
                TokenType.TOKEN_DIV -> left / right
                TokenType.TOKEN_REMINDER -> left % right
                else ->  throw LiloException(expression.operator.position, "Invalid binary operator.")
            }
        }

        throw LiloException(expression.operator.position, "Invalid binary expression.")
    }

    override fun visit(expression: ComparisonExpression): Any {
        val left = expression.left.accept(this)
        val right = expression.right.accept(this)
        val op = expression.operator.type

        if (op == TokenType.TOKEN_EQ_EQ) return left == right
        if (op == TokenType.TOKEN_BANG_EQ) return left != right

        if (left is Float && right is Float) {
            return when (op) {
                TokenType.TOKEN_GT -> left > right
                TokenType.TOKEN_GT_EQ -> left >= right

                TokenType.TOKEN_LS -> left < right
                TokenType.TOKEN_LS_EQ -> left <= right
                else ->  throw LiloException(expression.operator.position, "Invalid comparison operator.")
            }
        }

        throw LiloException(expression.operator.position, "Invalid comparison expression.")
    }

    override fun visit(expression: LogicalExpression): Any {
        val left = expression.left.accept(this)
        val op = expression.operator.type

        if (op == TokenType.TOKEN_LOGICAL_OR) {
            if (left is Boolean) {
                if (left == true) return left

                val right = expression.right.accept(this)
                if (right is Boolean) return left || right
            }

            throw LiloException(expression.operator.position, "Logical or (||) requires booleans")
        }

        if (op == TokenType.TOKEN_LOGICAL_AND) {
            if (left is Boolean) {
                if (left == false) return left

                val right = expression.right.accept(this)
                if (right is Boolean) return left && right
            }

            throw LiloException(expression.operator.position, "Logical or (||) requires booleans")
        }

        throw LiloException(expression.operator.position, "Invalid logical operator.")
    }

    override fun visit(expression: UnaryExpression): Any {
        val right = expression.right.accept(this)
        val op = expression.operator.type
        val position = expression.operator.position

        if (op == TokenType.TOKEN_BANG) {
            if (right is Boolean) return right.not()
            throw LiloException(position, "Unary (!) expect booleans only.")
        }

        if (op == TokenType.TOKEN_MINUS) {
            if (right is Float) return -right
            throw LiloException(expression.operator.position, "Unary (-) expect numbers only.")
        }

        throw LiloException(expression.operator.position, "ERROR: Invalid unary operator.")
    }

    override fun visit(expression: GroupExpression): Any {
        return expression.expression.accept(this)
    }

    override fun visit(expression: VariableExpression): Any {
        val value = currentScope.lookup(expression.value.literal)
        if (value == null) {
            throw LiloException(expression.value.position, "Can't resolve variable ${expression.value.literal}")
        }
        return value
    }

    override fun visit(expression: CallExpression): Any {
        val callee = expression.callee.accept(this)

        if (callee !is LiloCallable) {
            throw LiloException(expression.paren.position, "Can only call functions.")
        }

        if (callee.arity() != expression.arguments.size) {
            throw LiloException(expression.paren.position, "Expected ${callee.arity()} arguments but got ${expression.arguments.size}")
        }

        return callee.call(this, expression.arguments.map { it.accept(this) })
    }

    override fun visit(expression: DotExpression): Any {
        val caller = expression.caller.accept(this)
        if (caller is TurtleObject) {
            (expression.callee as TurtleStatement).id = caller.id
            return expression.callee.accept(this)
        }
        throw LiloException(expression.dot.position, "Invalid dot expression")
    }

    override fun visit(expression: IndexExpression): Any {
        val collection = expression.left.accept(this)
        if (collection is LiloList) {
            val index = expression.index.accept(this)
            if (index is Float) {
                if (collection.values.size <= index.toInt()) {
                    throw LiloException(expression.bracket.position, "Index can't be large than collection size")
                }
                return collection.values[index.toInt()]
            } else {
                throw LiloException(expression.bracket.position, "Index must be a number")
            }
        }
        throw LiloException(expression.bracket.position, "Index expression work only with collections.")
    }

    override fun visit(expression: ListExpression): Any {
        return LiloList(expression.values.map { it.accept(this) }.toMutableList())
    }

    override fun visit(expression: NumberExpression): Any {
        return expression.value
    }

    override fun visit(expression: BooleanExpression): Any {
        return expression.value
    }

    override fun visit(expression: NewTurtleExpression): Any {
        emitInstruction(NewTurtleInst(turtlePointerId))
        return TurtleObject(turtlePointerId++)
    }

    override fun visit(expression: ThieExpression): Any {
        return mainTurtlePointer
    }

    fun executeBlockInScope(scope : LiloScope, vararg statements: Statement) : Any {
        val previousScope = currentScope
        currentScope = scope
        var returnValue : Any = 0.0f
        for (statement in statements) {
            if (statement is ReturnStatement) {
                returnValue = statement.value.accept(this)
                break
            }
            statement.accept(this)
        }
        currentScope = previousScope
        return returnValue
    }

    private fun emitInstruction(instruction: Instruction) {
        if (::onInstructionEmitterListener.isInitialized) {
            onInstructionEmitterListener(instruction)
        }
    }

    fun setOnEvaluatorStartedListener(listener : () -> Unit) {
        onEvaluatorStarted = listener
    }

    fun setOnEvaluatorFinishedListener(listener : () -> Unit) {
        onEvaluatorFinished = listener
    }

    fun setOnInstructionEmitterListener(instructionFlow : (Instruction) -> Unit) {
        onInstructionEmitterListener = instructionFlow
    }

    fun setOnBackgroundChangeListener(listener: (Int) -> Unit) {
        onBackgroundChangeListener = listener
    }

    fun setOnExceptionListener(listener: (LiloException) -> Unit) {
        onExceptionListener = listener
    }
}