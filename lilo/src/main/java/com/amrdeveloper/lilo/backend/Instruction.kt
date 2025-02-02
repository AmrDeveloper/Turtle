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

open class Instruction

enum class Operator {
    EQUAL,
    PLUS,
    MINUS,
}

class NewTurtleInst(var id : Int) : Instruction()

class MoveXInst(val id : Int, val amount : Float) : Instruction()

class MoveYInst(val id : Int, val amount : Float) : Instruction()

class DegreeInst(val id : Int, val degree: Float, val op : Operator) : Instruction()

class ColorInst(val id: Int, val color: Int) : Instruction()

class RectangleInst(var id : Int, val right: Float, val bottom: Float) : Instruction()

class CircleInst(val id : Int, val radius : Float) : Instruction()

class LineInst(var id : Int, val length : Float) : Instruction()

class VisibilityInst(val id : Int, val isVisible : Boolean) : Instruction()

class PenInst(val id : Int, val isDown : Boolean) : Instruction()

class SleepInst(val time: Int) : Instruction()

class SpeedInst ( val time : Int ) : Instruction()