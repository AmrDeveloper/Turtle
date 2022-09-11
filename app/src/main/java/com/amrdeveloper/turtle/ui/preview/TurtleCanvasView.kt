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
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

import com.amrdeveloper.lilo.instruction.ColorInst
import com.amrdeveloper.lilo.instruction.DrawInstruction
import com.amrdeveloper.lilo.instruction.Instruction
import com.amrdeveloper.lilo.instruction.SleepInst
import com.amrdeveloper.lilo.instruction.SpeedInst

class TurtleCanvasView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val turtlePaint  = Paint()

    private var instructionPointer = 0
    private var instructionSpeed = 0L
    private val instructionList = ArrayList<Instruction>()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        // Set the default color
        turtlePaint.color = Color.BLACK

        // evaluate old UI only instructions previous instructions without sleep
        var index = 0
        while (index <= instructionPointer) {
            val inst = instructionList[index++]
            if (inst is DrawInstruction) {
                inst.draw(canvas, turtlePaint)
            }

            if (inst is ColorInst) {
                turtlePaint.color = inst.color
            }
        }

        if (instructionPointer > instructionList.lastIndex) return

        // Evaluate current instruction pointer with specific speed
        val instruction = instructionList[instructionPointer]
        if (instruction is DrawInstruction) {
            // If this instruction is a draw instruction, evaluate it with canvas and paint
            instruction.draw(canvas, turtlePaint)
            // Update instruction pointer to point to next instruction
            instructionPointer++
            // Redraw the next instruction after instruction speed time
            if (instructionPointer < instructionList.lastIndex) {
                postInvalidateDelayed(instructionSpeed)
            }
        }

        if (instruction is ColorInst) {
            // Update the paint color
            turtlePaint.color = instruction.color
            // Update instruction pointer to point to next instruction
            instructionPointer++
            // Redraw the next instruction after instruction speed time
            if (instructionPointer < instructionList.lastIndex) {
                postInvalidateDelayed(instructionSpeed)
            }
        }

        if (instruction is SpeedInst) {
            // Update the instruction speed
            instructionSpeed = instruction.time.toLong()
            // Update instruction pointer to point to next instruction
            instructionPointer++
            // Redraw the next instruction after instruction speed time
            invalidate()
        }

        if (instruction is SleepInst) {
            // Reset the instruction pointer
            instructionPointer = 0
            // Remove this sleep instruction because it should evaluated once
            instructionList.remove(instruction)
            // Redraw after n time
            postInvalidateDelayed((instructionSpeed.plus(instruction.time)))
        }
    }

    fun addInstruction(instruction: Instruction) {
        instructionList.add(instruction)
    }
}