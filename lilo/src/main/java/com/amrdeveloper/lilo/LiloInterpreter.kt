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

import android.graphics.Color
import com.amrdeveloper.lilo.ast.*
import com.amrdeveloper.lilo.instruction.CircleInst
import com.amrdeveloper.lilo.instruction.ColorInst
import com.amrdeveloper.lilo.instruction.Instruction
import com.amrdeveloper.lilo.instruction.LineInst
import com.amrdeveloper.lilo.instruction.PointerInst
import com.amrdeveloper.lilo.instruction.PointerVisibilityInst
import com.amrdeveloper.lilo.instruction.RectangleInst
import com.amrdeveloper.lilo.instruction.SleepInst
import com.amrdeveloper.lilo.instruction.SpeedInst
import com.amrdeveloper.lilo.std.bindStandardModules
import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin

private const val TAG = "LiloInterpreter"

class LiloInterpreter : StatementVisitor<Unit>, ExpressionVisitor<Any> {

    private var currentXPosition: Float = 0f
    private var currentYPosition: Float = 0f
    private var currentDegree: Float = 0.0f
        set(value) {
            field = if (value < 0) value + 360 else if (value > 360) value - 360 else value
            if (::onDegreeChangeListener.isInitialized) onDegreeChangeListener(value)
            emitInstruction(PointerInst(currentXPosition, currentYPosition, field))
        }

    private var currentColor: Int = Color.BLACK
    private var shouldTerminate = false

    private val globalsScope = LiloScope()
    private var currentScope = globalsScope

    private lateinit var onEvaluatorStarted : () -> Unit
    private lateinit var onEvaluatorFinished : () -> Unit
    private lateinit var onInstructionEmitterListener : (Instruction) -> Unit
    private lateinit var onDegreeChangeListener: (Float) -> Unit
    private lateinit var onBackgroundChangeListener: (Int) -> Unit
    private lateinit var onExceptionListener: (LiloException) -> Unit

    fun executeLiloScript(script: LiloScript): ExecutionState {
        preExecuteLiloScript()
        if (::onEvaluatorStarted.isInitialized) onEvaluatorStarted()
        try {
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
        currentXPosition = 0f
        currentYPosition = 0f
        currentDegree = 90.0f
        shouldTerminate = false
        currentColor = Color.BLACK
        bindBuiltinColors(globalsScope)
        bindStandardModules(globalsScope)
    }

    override fun visit(statement: ExpressionStatement) {
        statement.expression.accept(this)
    }

    override fun visit(statement: FunctionStatement) {
        Timber.tag(TAG).d("Evaluate FunctionStatement")
        val function = LiloFunction(statement, currentScope)
        currentScope.define(statement.name, function)
    }

    override fun visit(statement: ReturnStatement) {
        Timber.tag(TAG).d("Evaluate ReturnStatement")
        statement.value.accept(this)
    }

    override fun visit(statement: LetStatement) {
        Timber.tag(TAG).d("Evaluate LetStatement")
        val values = statement.value.accept(this)
        currentScope.define(statement.name, values)
    }

    override fun visit(statement: BlockStatement) {
        Timber.tag(TAG).d("Evaluate BlockStatement")
        executeBlockInScope(LiloScope(currentScope), *statement.statements.toTypedArray())
    }

    override fun visit(statement: IfStatement) {
        Timber.tag(TAG).d("Evaluate IfStatement")
        val condition = statement.condition.accept(this)
        if (condition !is Boolean) {
            Timber.tag(TAG).d("If condition must be boolean")
            throw LiloException(statement.keyword.position, "If condition must be boolean")
        }

        // If condition is true execute the body in new sub scope
        if (condition == true) {
            executeBlockInScope(LiloScope(currentScope), statement.body)
            return
        }

        Timber.tag(TAG).d("Evaluate elif's statements if exists")
        for (alternative in statement.alternatives) {
            val alternativeCondition = alternative.condition.accept(this)
            if (alternativeCondition !is Boolean) {
                Timber.tag(TAG).d("condition must be boolean")
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
        Timber.tag(TAG).d("Evaluate WhileStatement")
        val condition = statement.condition.accept(this)
        if (condition is Boolean) {
            while (statement.condition.accept(this) == true) {
                executeBlockInScope(LiloScope(currentScope), statement.body)
            }
            return
        }

        Timber.tag(TAG).d("ERROR: While condition must be a boolean")
        throw LiloException(statement.keyword.position, "If condition must be boolean")
    }

    override fun visit(statement: RepeatStatement) {
        Timber.tag(TAG).d("Evaluate RepeatStatement")
        val counter = statement.condition.accept(this)
        if (counter is Float) {
            repeat(counter.toInt()) { executeBlockInScope(LiloScope(currentScope), statement.body) }
            return
        }

        Timber.tag(TAG).d("ERROR: Repeat counter must be a number")
        throw LiloException(statement.keyword.position, "Repeat counter must be a number")
    }

    override fun visit(statement: CubeStatement) {
        Timber.tag(TAG).d("Evaluate CubeStatement")
        val value = statement.radius.accept(this)
        if (value is Float) {
            emitInstruction(RectangleInst(currentXPosition, currentYPosition, value.toFloat(), value.toFloat()))
            return
        }

        Timber.tag(TAG).d("ERROR: Cube value must be a number")
        throw LiloException(statement.keyword.position, "Cube value must be a number")
    }

    override fun visit(statement: CircleStatement) {
        Timber.tag(TAG).d("Evaluate CircleStatement")
        val radius = statement.radius.accept(this)
        if (radius is Float) {
            val circleInst = CircleInst(currentXPosition, currentYPosition, radius.toFloat())
            onInstructionEmitterListener(circleInst)
        } else {
            Timber.tag(TAG).d("ERROR: Circle radius must be a number")
            throw LiloException(statement.keyword.position, "Circle radius must be a number")
        }
    }

    override fun visit(statement: MoveStatement) {
        Timber.tag(TAG).d("Evaluate MoveStatement")
        val xValue = statement.xValue.accept(this)
        val yValue = statement.yValue.accept(this)
        if (xValue is Float && yValue is Float) {
            currentXPosition = xValue
            currentYPosition = yValue
        } else {
            Timber.tag(TAG).d("ERROR: Move X and y values must be a numbers")
            throw LiloException(statement.keyword.position, "Move X and y values must be a numbers")
        }
    }

    override fun visit(statement: MoveXStatement) {
        Timber.tag(TAG).d("Evaluate MoveXStatement")
        val value = statement.amount.accept(this)
        if (value is Float) {
            currentXPosition = value.toFloat()
        } else {
            Timber.tag(TAG).d("ERROR: Move X amount must be a number")
            throw LiloException(statement.keyword.position, "Move X amount must be a number")
        }
    }

    override fun visit(statement: MoveYStatement) {
        Timber.tag(TAG).d("Evaluate MoveYStatement")
        val value = statement.amount.accept(this)
        if (value is Float) {
            currentYPosition = value.toFloat()
        } else {
            Timber.tag(TAG).d("ERROR: Move Y amount must be a number")
            throw LiloException(statement.keyword.position, "Move Y amount must be a number")
        }
    }

    override fun visit(statement: ColorStatement) {
        Timber.tag(TAG).d("Evaluate ColorStatement")
        val colorValue = statement.color.accept(this)
        if (colorValue is Int) {
            currentColor = colorValue
            emitInstruction(ColorInst(colorValue))
            return
        }
        throw LiloException(statement.keyword.position, "Color value must be Identifier")
    }

    override fun visit(statement: BackgroundStatement) {
        Timber.tag(TAG).d("Evaluate BackgroundStatement")
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
        Timber.tag(TAG).d("SpeedStatement implemented")
        val timeValue = statement.amount.accept(this)
        if (timeValue is Number) {
            emitInstruction(SpeedInst(timeValue.toInt()))
            return
        }
        throw LiloException(statement.keyword.position, "Speed value must ba an integer")
    }

    override fun visit(statement: SleepStatement) {
        Timber.tag(TAG).d("SleepStatement implemented")
        val timeValue = statement.amount.accept(this)
        if (timeValue is Number) {
            emitInstruction(SleepInst(timeValue.toInt()))
            return
        }
        throw LiloException(statement.keyword.position, "Sleep value must ba an integer")
    }

    override fun visit(statement: ShowPointerStatement) {
        emitInstruction(PointerVisibilityInst(true))
    }

    override fun visit(statement: HidePointerStatement) {
        emitInstruction(PointerVisibilityInst(false))
    }

    override fun visit(statement: StopStatement) {
        Timber.tag(TAG).d("Evaluate StopStatement")
        shouldTerminate = true
    }

    override fun visit(statement: RotateStatement) {
        Timber.tag(TAG).d("Evaluate RotateStatement")
        val degree = statement.value.accept(this)
        if (degree is Float) {
            currentDegree += degree.toFloat()
            return
        }

        Timber.tag(TAG).d("ERROR: Rotate degree must be a number")
        throw LiloException(statement.keyword.position, "Rotate degree must be a number")
    }

    override fun visit(statement: ForwardStatement) {
        Timber.tag(TAG).d("Evaluate ForwardStatement")
        val value = statement.value.accept(this)
        if (value is Float) {
            drawLineWithAngel(value)
            return
        }
        Timber.tag(TAG).d("ERROR: Forward value must be a number")
        throw LiloException(statement.keyword.position, "Forward value must be a number")
    }

    override fun visit(statement: BackwardStatement) {
        Timber.tag(TAG).d("Evaluate BackwardStatement")
        val value = statement.value.accept(this)
        if (value is Float) {
            currentDegree -= 180
            drawLineWithAngel(value)
            return
        }

        Timber.tag(TAG).d("ERROR: Backward value must be a number")
        throw LiloException(statement.keyword.position, "Backward value must be a number")
    }

    override fun visit(statement: RightStatement) {
        Timber.tag(TAG).d("Evaluate RightStatement")
        val value = statement.value.accept(this)
        if (value is Float) {
            currentDegree -= 90
            drawLineWithAngel(value)
            return
        }

        Timber.tag(TAG).d("ERROR: Right value must be a number")
        throw LiloException(statement.keyword.position, "Right value must be a number")
    }

    override fun visit(statement: LeftStatement) {
        Timber.tag(TAG).d("Evaluate LeftStatement")
        val value = statement.value.accept(this)
        if (value is Float) {
            currentDegree += 90
            drawLineWithAngel(value)
            return
        }

        Timber.tag(TAG).d("ERROR: Left value must be a number")
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
                Timber.tag(TAG).d("ERROR: Invalid assignment.")
                throw LiloException(expression.operator.position, "Invalid assignment.")
            }
        }
        return value
    }

    override fun visit(expression: BinaryExpression): Any {
        val left = expression.left.accept(this)
        val right = expression.right.accept(this)
        val operatorType = expression.operator.type

        if (operatorType == TokenType.TOKEN_EQ_EQ) return left == right
        if (operatorType == TokenType.TOKEN_BANG_EQ) return left != right

        if (left is Float && right is Float) {
            when (operatorType) {
                TokenType.TOKEN_PLUS -> return left + right
                TokenType.TOKEN_MINUS -> return left - right
                TokenType.TOKEN_MUL -> return left * right
                TokenType.TOKEN_DIV -> return left / right
                TokenType.TOKEN_REMINDER -> return left % right

                TokenType.TOKEN_GT -> return left > right
                TokenType.TOKEN_GT_EQ -> return left >= right

                TokenType.TOKEN_LS -> return left < right
                TokenType.TOKEN_LS_EQ -> return left <= right
            }
        }

        throw LiloException(expression.operator.position, "Invalid binary expression.")
    }

    override fun visit(expression: LogicalExpression): Any {
        val left = expression.left.accept(this)
        when (expression.operator.type) {
            TokenType.TOKEN_LOGICAL_OR -> {
                if (left is Boolean) {
                    if (left == true) return left
                } else {
                    throw LiloException(expression.operator.position, "Logical or (||) requires booleans")
                }
            }
            TokenType.TOKEN_LOGICAL_AND -> {
                if (left is Boolean) {
                    if (left == false) return left
                } else {
                    throw LiloException(expression.operator.position, "Logical and (&&) requires booleans")
                }
            }
            else -> {
                Timber.tag(TAG).d("ERROR: Invalid logical operator.")
                throw LiloException(expression.operator.position, "Invalid logical operator.")
            }
        }
        return expression.right.accept(this)
    }

    override fun visit(expression: UnaryExpression): Any {
        val right = expression.right.accept(this)
        return when (expression.operator.type) {
            TokenType.TOKEN_BANG -> {
                if (right is Boolean) {
                    return right.not()
                } else {
                    Timber.tag(TAG).d("ERROR: Unary (!) expect booleans only.")
                    throw LiloException(expression.operator.position, "Unary (!) expect booleans only.")
                }
            }
            TokenType.TOKEN_MINUS -> {
                if (right is Float) {
                    return -right
                } else {
                    Timber.tag(TAG).d("ERROR: Unary (-) expect numbers only.")
                    throw LiloException(expression.operator.position, "Unary (-) expect numbers only.")
                }
            }
            else -> {
                Timber.tag(TAG).d("ERROR: Invalid unary operator.")
                throw LiloException(expression.operator.position, "ERROR: Invalid unary operator.")
            }
        }
    }

    override fun visit(expression: GroupExpression): Any {
        Timber.tag(TAG).d("Evaluate GroupExpression")
        return expression.expression.accept(this)
    }

    override fun visit(expression: VariableExpression): Any {
        Timber.tag(TAG).d("Evaluate VariableExpression")
        val value = currentScope.lookup(expression.value.literal)
        if (value == null) {
            throw LiloException(expression.value.position, "Can't resolve variable ${expression.value.literal}")
        }
        return value
    }

    override fun visit(expression: CallExpression): Any {
        Timber.tag(TAG).d("Evaluate CallExpression")
        val callee = expression.callee.accept(this)

        if (callee !is LiloCallable) {
            throw LiloException(expression.paren.position, "Can only call functions.")
        }

        val arguments = mutableListOf<Any>()
        expression.arguments.forEach { arguments.add(it.accept(this)) }

        if (callee.arity() != arguments.size) {
            throw LiloException(expression.paren.position, "Expected ${callee.arity()} arguments but got ${arguments.size}")
        }

        return callee.call(this, arguments)
    }

    override fun visit(expression: IndexExpression): Any {
        Timber.tag(TAG).d("Evaluate IndexExpression")
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
        val elements = mutableListOf<Any>()
        for (element in expression.values) {
            elements.add(element.accept(this))
        }
        return LiloList(elements)
    }

    override fun visit(expression: NumberExpression): Any {
        Timber.tag(TAG).d("Evaluate NumberExpression")
        return expression.value
    }

    override fun visit(expression: BooleanExpression): Any {
        Timber.tag(TAG).d("Evaluate BooleanExpression")
        return expression.value
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

    private fun drawLineWithAngel(length: Float) {
        val angel = currentDegree * Math.PI / 180
        val endX = (currentXPosition + length * sin(angel)).toFloat()
        val endY = (currentYPosition + length * cos(angel)).toFloat()
        emitInstruction(LineInst(currentXPosition, currentYPosition, endX, endY))
        currentXPosition = endX
        currentYPosition = endY
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

    fun setOnDegreeChangeListener(listener : (Float) -> Unit) {
        onDegreeChangeListener = listener
    }

    fun setOnBackgroundChangeListener(listener: (Int) -> Unit) {
        onBackgroundChangeListener = listener
    }

    fun setOnExceptionListener(listener: (LiloException) -> Unit) {
        onExceptionListener = listener
    }
}