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
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.amrdeveloper.lilo.instruction.ColorInst
import com.amrdeveloper.lilo.instruction.DrawInstruction
import com.amrdeveloper.lilo.instruction.Instruction
import com.amrdeveloper.lilo.instruction.PointerInst
import com.amrdeveloper.lilo.instruction.PointerVisibilityInst
import com.amrdeveloper.lilo.instruction.SleepInst
import com.amrdeveloper.lilo.instruction.SpeedInst
import com.amrdeveloper.turtle.R

/**
 * Turtle Canvas View is a custom view that receive a list of instructions (Bytecode like),
 * generated from the evaluator and render them, also can support do operations on them,
 * such as modify, clear, delay ...etc
 */
class TurtleCanvasView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val turtlePaint  = Paint()

    private var instructionPointer = 0
    private var instructionSpeed = 0L
    private var shouldStopRendering = false
    private var shouldDrawPointer = true
    private val instructionList = ArrayList<Instruction>()

    private val turtlePointerMatrix = Matrix()
    private var turtlePointer = ContextCompat.getDrawable(context, R.drawable.ic_turtle_pointer)!!.toBitmap()

    private lateinit var onRenderStarted : () -> Unit
    private lateinit var onRenderFinished : () -> Unit

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        if (::onRenderStarted.isInitialized && instructionPointer == 0) {
            onRenderStarted()
        }

        // Set the default color
        turtlePaint.color = Color.BLACK

        // TODO: optimize this code and cache it, no need to draw it each time
        // evaluate old UI only instructions previous instructions without sleep
        // this approach is working for now but can be optimized and cashed later
        var index = 0
        var lastPointerInstruction : PointerInst? = null
        while (index < instructionPointer) {
            when (val inst = instructionList[index++]) {
                is DrawInstruction -> {
                    inst.draw(canvas, turtlePaint)
                }
                is ColorInst -> {
                    turtlePaint.color = inst.color
                }
                is PointerInst -> {
                    lastPointerInstruction = inst
                }
            }
        }

        // Draw only the last pointer instruction if exists
        if (shouldDrawPointer && (lastPointerInstruction != null)) {
            turtlePointerMatrix.reset()
            turtlePointerMatrix.postRotate(lastPointerInstruction.degree - 180)
            turtlePointerMatrix.postTranslate(lastPointerInstruction.x, lastPointerInstruction.y)
            canvas.drawBitmap(turtlePointer, turtlePointerMatrix, turtlePaint)
        }

        // Handle the terminate flag
        if (shouldStopRendering) {
            if (::onRenderFinished.isInitialized) onRenderFinished()
            return
        }

        if (instructionPointer > instructionList.lastIndex) return

        // Evaluate current instruction pointer with specific speed
        when (val instruction = instructionList[instructionPointer]) {
            is DrawInstruction -> {
                // If this instruction is a draw instruction, evaluate it with canvas and paint
                instruction.draw(canvas, turtlePaint)
                // Update instruction pointer to point to next instruction
                instructionPointer++
                // Redraw the next instruction after instruction speed time
                if (instructionPointer < instructionList.lastIndex) {
                    postInvalidateDelayed(instructionSpeed)
                }
            }
            is PointerInst -> {
                // TODO: Can be optimized later
                // Pointer instruction shouldn't drawn now
                // but will draw only the last one when evaluate the prev instructions
                instructionPointer++
                postInvalidateDelayed(0)
            }
            is ColorInst -> {
                // Update the paint color
                turtlePaint.color = instruction.color
                // Update instruction pointer to point to next instruction
                instructionPointer++
                // Redraw the next instruction after instruction speed time
                if (instructionPointer < instructionList.lastIndex) {
                    postInvalidateDelayed(instructionSpeed)
                }
            }
            is SpeedInst -> {
                // Update the instruction speed
                instructionSpeed = instruction.time.toLong()
                // Update instruction pointer to point to next instruction
                instructionPointer++
                // Redraw the next instruction after instruction speed time
                invalidate()
            }
            is SleepInst -> {
                // Reset the instruction pointer
                instructionPointer = 0
                // Remove this sleep instruction because it should evaluated once
                instructionList.remove(instruction)
                // Redraw after n time
                postInvalidateDelayed((instructionSpeed.plus(instruction.time)))
            }
            is PointerVisibilityInst -> {
                // Update draw pointer flag
                shouldDrawPointer = instruction.visible
                // Update instruction pointer to point to next instruction
                instructionPointer++
                // Redraw the next instruction after instruction speed time
                invalidate()
            }
            else -> {
                // Update instruction pointer to point to next instruction
                instructionPointer++
                // Redraw the next instruction after instruction speed time
                invalidate()
            }
        }

        if (::onRenderFinished.isInitialized && instructionPointer >= instructionList.lastIndex) {
            onRenderFinished()
        }
    }

    fun addInstruction(instruction: Instruction) {
        instructionList.add(instruction)
    }

    fun resetRenderAttributes() {
        instructionPointer = 0
        instructionSpeed = 0L
        instructionList.clear()
    }

    fun setStoppingRenderScript(shouldStop : Boolean) {
        shouldStopRendering = shouldStop
    }

    fun setOnRenderStartedListener(listener : () -> Unit) {
        onRenderStarted = listener
    }

    fun setOnRenderFinishedListener(listener : () -> Unit) {
        onRenderFinished = listener
    }
}