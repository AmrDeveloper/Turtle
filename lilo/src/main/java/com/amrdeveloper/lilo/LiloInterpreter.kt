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
            Timber.tag(TAG).d("While condition must be a boolean")
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
            Timber.tag(TAG).d("Repeat counter must be a number")
        }
    }

    override fun visit(statement: CubeStatement) {
        Timber.tag(TAG).d("Evaluate CubeStatement")
        val radius = statement.radius
        canvas.drawRect(currentXPosition, currentYPosition, radius, radius, turtlePaint)
    }

    override fun visit(statement: CircleStatement) {
        Timber.tag(TAG).d("Evaluate CircleStatement")
        val radius = statement.radius
        canvas.drawCircle(currentXPosition, currentYPosition, radius, turtlePaint)
    }

    override fun visit(statement: MoveStatement) {
        Timber.tag(TAG).d("Evaluate MoveStatement")
        currentXPosition = statement.xValue
        currentYPosition = statement.yValue
    }

    override fun visit(statement: MoveXStatement) {
        Timber.tag(TAG).d("Evaluate MoveXStatement")
        currentXPosition = statement.amount
    }

    override fun visit(statement: MoveYStatement) {
        Timber.tag(TAG).d("Evaluate MoveYStatement")
        currentYPosition = statement.amount
    }

    override fun visit(statement: ColorStatement) {
        Timber.tag(TAG).d("Evaluate ColorStatement")
        currentColor = Color.RED
        turtlePaint.color = currentColor
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
        val degree = statement.value
        currentDegree += degree
    }

    override fun visit(statement: ForwardStatement) {
        Timber.tag(TAG).d("Evaluate ForwardStatement")
        val value = statement.value
        drawLineWithAngel(value)
    }

    override fun visit(statement: BackwardStatement) {
        Timber.tag(TAG).d("Evaluate BackwardStatement")
        val value = statement.value
        currentDegree -= 180
        drawLineWithAngel(value)
    }

    override fun visit(statement: RightStatement) {
        Timber.tag(TAG).d("Evaluate RightStatement")
        val value = statement.value
        currentDegree -= 90
        drawLineWithAngel(value)
    }

    override fun visit(statement: LeftStatement) {
        Timber.tag(TAG).d("Evaluate LeftStatement")
        val value = statement.value
        currentDegree += 90
        drawLineWithAngel(value)
    }

    override fun visit(statement: VariableExpression): Any {
        Timber.tag(TAG).d("Evaluate VariableExpression")
        return currentScope.lookup(statement.value) ?: 0
    }

    override fun visit(statement: NumberExpression): Any {
        Timber.tag(TAG).d("Evaluate NumberExpression")
        return statement.value
    }

    override fun visit(statement: BooleanExpression): Any {
        Timber.tag(TAG).d("Evaluate BooleanExpression")
        return statement.value
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