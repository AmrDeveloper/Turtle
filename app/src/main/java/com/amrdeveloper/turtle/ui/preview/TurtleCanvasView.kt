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

package com.amrdeveloper.turtle.ui.preview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.amrdeveloper.lilo.backend.CircleInst
import com.amrdeveloper.lilo.backend.ColorInst
import com.amrdeveloper.lilo.backend.DegreeInst
import com.amrdeveloper.lilo.backend.Instruction
import com.amrdeveloper.lilo.backend.LineInst
import com.amrdeveloper.lilo.backend.MoveXInst
import com.amrdeveloper.lilo.backend.MoveYInst
import com.amrdeveloper.lilo.backend.NewTurtleInst
import com.amrdeveloper.lilo.backend.Operator
import com.amrdeveloper.lilo.backend.PenInst
import com.amrdeveloper.lilo.backend.RectangleInst
import com.amrdeveloper.lilo.backend.SleepInst
import com.amrdeveloper.lilo.backend.SpeedInst
import com.amrdeveloper.lilo.backend.VisibilityInst
import com.amrdeveloper.turtle.R
import kotlin.math.cos
import kotlin.math.sin

/**
 * Turtle Canvas View is a custom view that receive a list of instructions (Bytecode like),
 * generated from the evaluator and render them, also can support do operations on them,
 * such as modify, clear, delay ...etc
 */
class TurtleCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var instructionPointer = 0
    private var instructionSpeed = 0L
    private var instructionBreakPoint = Integer.MAX_VALUE
    private var shouldStopRendering = false
    private val instructionList = ArrayList<Instruction>()

    private var turtleDefaultX = 0f
    private var turtleDefaultY = 100f
    private var turtleDefaultDegree = 90f

    private val turtlePaint = Paint()
    private val turtlePointerMatrix = Matrix()
    private var turtlePointerBitmap = ContextCompat.getDrawable(context, R.drawable.ic_turtle_pointer)!!.toBitmap()
    private val turtlePointers = ArrayList<TurtlePointer>()

    private lateinit var onRenderStarted: () -> Unit
    private lateinit var onRenderStopped: () -> Unit
    private lateinit var onRenderFinished: () -> Unit

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        interpretInstructions(canvas)
    }

    private fun interpretInstructions(canvas: Canvas) {
        resetTurtlePointers()

        if (::onRenderStarted.isInitialized && instructionPointer == 0) {
            onRenderStarted()
        }

        // evaluate old UI only instructions previous instructions without sleep
        // this approach is working for now but can be optimized and cashed later
        var sp = 0
        var isSleepLastInst = false
        while (sp < instructionPointer && sp < instructionBreakPoint && sp < instructionList.lastIndex) {
            when (val inst = instructionList[sp++]) {
                is RectangleInst -> {
                    val turtle = turtlePointers[inst.id]
                    if (turtle.isPenDown) {
                        turtlePaint.color = turtle.color
                        canvas.drawRect(turtle.x, turtle.y, turtle.y + inst.right, turtle.y + inst.bottom, turtlePaint)
                    }
                }
                is CircleInst -> {
                    val turtle = turtlePointers[inst.id]
                    if (turtle.isPenDown) {
                        turtlePaint.color = turtle.color
                        canvas.drawCircle(turtle.x, turtle.y, inst.radius, turtlePaint)
                    }
                }
                is LineInst -> {
                    val turtle = turtlePointers[inst.id]
                    if (turtle.isPenDown) {
                        val angel = turtle.degree * Math.PI / 180
                        val length = inst.length
                        val endX = (turtle.x + length * sin(angel)).toFloat()
                        val endY = (turtle.y + length * cos(angel)).toFloat()

                        turtlePaint.color = turtle.color
                        canvas.drawLine(turtle.x, turtle.y, endX, endY, turtlePaint)

                        turtlePointers[inst.id].x = endX
                        turtlePointers[inst.id].y = endY
                    }
                }
                is MoveXInst -> {
                    turtlePointers[inst.id].x = inst.amount
                }
                is MoveYInst -> {
                    turtlePointers[inst.id].y = inst.amount
                }
                is DegreeInst -> {
                    when (inst.op) {
                        Operator.EQUAL -> turtlePointers[inst.id].degree = inst.degree
                        Operator.PLUS -> turtlePointers[inst.id].degree += inst.degree
                        Operator.MINUS -> turtlePointers[inst.id].degree -= inst.degree
                    }
                }
                is NewTurtleInst -> {
                    if (inst.id > turtlePointers.lastIndex) {
                        turtlePointers.add(TurtlePointer(turtleDefaultX, turtleDefaultY, turtleDefaultDegree))
                    }
                }
                is ColorInst -> {
                    turtlePointers[inst.id].color = inst.color
                }
                is VisibilityInst -> {
                    turtlePointers[inst.id].isVisible = inst.isVisible
                }
                is PenInst -> {
                    turtlePointers[inst.id].isPenDown = inst.isDown
                }
                is SpeedInst -> {
                    instructionSpeed = inst.time.toLong()
                }
                is SleepInst -> {
                    // Remove this sleep instruction because it should evaluated once
                    instructionList.removeAt(sp - 1)
                    // Redraw after n time
                    postInvalidateDelayed((instructionSpeed.plus(inst.time)))
                    isSleepLastInst = true
                }
            }
        }

        // Draw only the last pointer instruction if exists
        drawTurtlePointers(canvas)

        // Handle the terminate flag
        if (shouldStopRendering) {
            if (::onRenderStopped.isInitialized) onRenderStopped()
            return
        }

        if (isSleepLastInst.not()) {
            instructionPointer++
            invalidate()
        }

        if (::onRenderFinished.isInitialized && instructionPointer == instructionList.lastIndex) {
           onRenderFinished()
        }
    }

    private fun resetTurtlePointers() {
        for (turtlePointer in turtlePointers) {
            with (turtlePointer) {
                x = turtleDefaultX
                y = turtleDefaultY
                degree = turtleDefaultDegree
                color = Color.BLACK
                isVisible = true
            }
        }
    }

    private fun drawTurtlePointers(canvas: Canvas) {
        for (turtlePointer in turtlePointers) {
            if (turtlePointer.isVisible.not()) continue
            turtlePointerMatrix.reset()
            turtlePointerMatrix.postRotate(turtlePointer.degree - 180)
            turtlePointerMatrix.postTranslate(turtlePointer.x, turtlePointer.y)
            canvas.drawBitmap(turtlePointerBitmap, turtlePointerMatrix, turtlePaint)
        }
    }

    fun addInstruction(instruction: Instruction) {
        instructionList.add(instruction)
    }

    fun getInstructionsSize() = instructionList.size

    fun setInstructionLimit(limit: Int) {
        instructionBreakPoint = limit
        invalidate()
    }

    fun resetRenderAttributes() {
        instructionPointer = 0
        instructionSpeed = 0L
        instructionBreakPoint = Integer.MAX_VALUE
        instructionList.clear()
    }

    fun setStoppingRenderScript(shouldStop: Boolean) {
        shouldStopRendering = shouldStop
    }

    fun setOnRenderStartedListener(listener: () -> Unit) {
        onRenderStarted = listener
    }

    fun setOnRenderStoppedListener(listener: () -> Unit) {
        onRenderStopped = listener
    }

    fun setOnRenderFinishedListener(listener: () -> Unit) {
        onRenderFinished = listener
    }
}