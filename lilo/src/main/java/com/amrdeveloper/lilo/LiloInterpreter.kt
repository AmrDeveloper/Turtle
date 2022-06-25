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

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.amrdeveloper.lilo.ast.*
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
            if (::onDegreeChangeListener.isInitialized) {
                onDegreeChangeListener.onDegreeChange(value)
            }
        }

    private var currentColor: Int = Color.BLACK
    private var shouldTerminate = false

    private val turtlePaint by lazy { Paint() }
    private lateinit var canvas: Canvas

    private val globalsScope = LiloScope()
    private var currentScope = globalsScope

    private lateinit var onDegreeChangeListener: OnDegreeChangeListener

    fun executeLiloScript(currentCanvas: Canvas, script: LiloScript): ExecutionState {
        canvas = currentCanvas
        preExecuteLiloScript()
        val nodes = script.statements
        for (node in nodes) {
            if (shouldTerminate) return ExecutionState.FAILURE
            node.accept(this)
        }
        return ExecutionState.SUCCESS
    }

    private fun preExecuteLiloScript() {
        currentXPosition = 0f
        currentYPosition = 0f
        currentDegree = 90.0f
        shouldTerminate = false
        currentColor = Color.BLACK
    }

    override fun visit(statement: ExpressionStatement) {
        statement.expression.accept(this)
    }

    override fun visit(statement: FunctionStatement) {
        Timber.tag(TAG).d("FunctionStatement yet implemented")
    }

    override fun visit(statement: ReturnStatement) {
        Timber.tag(TAG).d("ReturnStatement yet implemented")
    }

    override fun visit(statement: LetStatement) {
        Timber.tag(TAG).d("LetStatement yet implemented")
        val values = statement.value.accept(this)
        currentScope.define(statement.name, values)
    }

    override fun visit(statement: BlockStatement) {
        Timber.tag(TAG).d("Evaluate BlockStatement")
        val previousScope = currentScope
        statement.statements.forEach { it.accept(this) }
        currentScope = previousScope
    }

    override fun visit(statement: IfStatement) {
        Timber.tag(TAG).d("Evaluate IfStatement")
        val condition = statement.condition.accept(this)
        if (condition is Boolean && condition == true) {
            statement.body.accept(this)
        }
    }

    override fun visit(statement: WhileStatement) {
        Timber.tag(TAG).d("Evaluate WhileStatement")
        val condition = statement.condition.accept(this)
        if (condition is Boolean) {
            while (condition == true) {
                statement.body.accept(this)
            }
        } else {
            Timber.tag(TAG).d("ERROR: While condition must be a boolean")
        }
    }

    override fun visit(statement: RepeatStatement) {
        Timber.tag(TAG).d("Evaluate RepeatStatement")
        val counter = statement.condition.accept(this)
        if (counter is Float) {
            repeat(counter.toInt()) {
                statement.body.accept(this)
            }
        } else {
            Timber.tag(TAG).d("ERROR: Repeat counter must be a number")
        }
    }

    override fun visit(statement: CubeStatement) {
        Timber.tag(TAG).d("Evaluate CubeStatement")
        val value = statement.radius.accept(this)
        if (value is Float) {
            canvas.drawRect(currentXPosition, currentYPosition, value.toFloat(), value.toFloat(), turtlePaint)
        } else {
            Timber.tag(TAG).d("ERROR: Cube value must be a number")
        }
    }

    override fun visit(statement: CircleStatement) {
        Timber.tag(TAG).d("Evaluate CircleStatement")
        val radius = statement.radius.accept(this)
        if (radius is Float) {
            canvas.drawCircle(currentXPosition, currentYPosition, radius.toFloat(), turtlePaint)
        } else {
            Timber.tag(TAG).d("ERROR: Circle radius must be a number")
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
        }
    }

    override fun visit(statement: MoveXStatement) {
        Timber.tag(TAG).d("Evaluate MoveXStatement")
        val value = statement.amount.accept(this)
        if (value is Float) {
            currentXPosition = value.toFloat()
        } else {
            Timber.tag(TAG).d("ERROR: Move X amount must be a number")
        }
    }

    override fun visit(statement: MoveYStatement) {
        Timber.tag(TAG).d("Evaluate MoveYStatement")
        val value = statement.amount.accept(this)
        if (value is Float) {
            currentYPosition = value.toFloat()
        } else {
            Timber.tag(TAG).d("ERROR: Move Y amount must be a number")
        }
    }

    override fun visit(statement: ColorStatement) {
        Timber.tag(TAG).d("Evaluate ColorStatement")
        val colorValue = statement.color.accept(this)
        if (colorValue is String) {
            currentColor = Color.RED
            turtlePaint.color = currentColor
        } else {
            Timber.tag(TAG).d("ERROR: Color value must be identifier or hexadecimal")
        }
    }

    override fun visit(statement: SleepStatement) {
        Timber.tag(TAG).d("SleepStatement yet implemented")
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
        } else {
            Timber.tag(TAG).d("ERROR: Rotate degree must be a number")
        }
    }

    override fun visit(statement: ForwardStatement) {
        Timber.tag(TAG).d("Evaluate ForwardStatement")
        val value = statement.value.accept(this)
        if (value is Float) {
            drawLineWithAngel(value)
        } else {
            Timber.tag(TAG).d("ERROR: Forward value must be a number")
        }
    }

    override fun visit(statement: BackwardStatement) {
        Timber.tag(TAG).d("Evaluate BackwardStatement")
        val value = statement.value.accept(this)
        if (value is Float) {
            currentDegree -= 180
            drawLineWithAngel(value)
        } else {
            Timber.tag(TAG).d("ERROR: Backward value must be a number")
        }
    }

    override fun visit(statement: RightStatement) {
        Timber.tag(TAG).d("Evaluate RightStatement")
        val value = statement.value.accept(this)
        if (value is Float) {
            currentDegree -= 90
            drawLineWithAngel(value)
        } else {
            Timber.tag(TAG).d("ERROR: Right value must be a number")
        }
    }

    override fun visit(statement: LeftStatement) {
        Timber.tag(TAG).d("Evaluate LeftStatement")
        val value = statement.value.accept(this)
        if (value is Float) {
            currentDegree += 90
            drawLineWithAngel(value)
        } else {
            Timber.tag(TAG).d("ERROR: Left value must be a number")
        }
    }

    override fun visit(expression: GroupExpression): Any {
        Timber.tag(TAG).d("Evaluate GroupExpression")
        return expression.expression.accept(this)
    }

    override fun visit(expression: VariableExpression): Any {
        Timber.tag(TAG).d("Evaluate VariableExpression")
        return currentScope.lookup(expression.value) ?: 0
    }

    override fun visit(expression: NumberExpression): Any {
        Timber.tag(TAG).d("Evaluate NumberExpression")
        return expression.value
    }

    override fun visit(expression: BooleanExpression): Any {
        Timber.tag(TAG).d("Evaluate BooleanExpression")
        return expression.value
    }

    private fun drawLineWithAngel(length : Float) {
        val angel = currentDegree * Math.PI / 180
        val endX = (currentXPosition + length * sin(angel)).toFloat()
        val endY = (currentYPosition + length * cos(angel)).toFloat()
        canvas.drawLine(currentXPosition, currentYPosition, endX, endY, turtlePaint)
        currentXPosition = endX
        currentYPosition = endY
    }

    fun setOnDegreeChangeListener(listener: OnDegreeChangeListener) {
        onDegreeChangeListener = listener
    }
}