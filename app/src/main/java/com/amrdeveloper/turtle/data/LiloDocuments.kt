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

package com.amrdeveloper.turtle.data

import com.amrdeveloper.easyadapter.adapter.ExpandableAdapter
import com.amrdeveloper.easyadapter.bind.BindExpandableMap
import com.amrdeveloper.turtle.BuildConfig

@ExpandableAdapter(BuildConfig.APPLICATION_ID)
data class LiloDocuments(
    @BindExpandableMap
    val documents: Map<DocumentCategory, List<Document>> = liloFullDocuments
)

private val liloFullDocuments = mapOf(
    DocumentCategory("Declarations") to listOf(
        Document("Let Declaration", "Used to declare value with name, let name = value"),
        Document("Function Declaration", "Functions used to declare block of code to call it with name and parameters, fun name(params) { }")
    ),
    DocumentCategory("Control flow") to listOf(
        Document("If Statement", "Take condition and body to execute if condition is true, if (condition) {}"),
        Document("Else if Statement", "Execute the body if the condition is true and if not executed, elif (condition) {}"),
        Document("Else Statement", "Execute the body if all if and elif branches not executed, else {}"),
        Document("while Statement", "Take condition and body to execute while condition is true, while (condition) {}"),
        Document("Repeat Statement", "Take number n and body to execute it n times, repeat (n) {}"),
    ),
    DocumentCategory("Instructions") to listOf(
        Document("Cube", "Used to draw cube in current position and take size, cube n"),
        Document("Circle", "Used to draw Circle in current position and take radius, circle r"),
        Document("Move", "Used set the current x and y position, move x, y"),
        Document("Move X", "Used set the current x position, movex x"),
        Document("Move Y", "Used set the current y position, movey y"),
        Document("Color", "Used set the current color, color RED"),
        Document("background", "Used set the current background, background RED"),
        Document("Speed", "Used set the speed of the execution and take time in ms, sleep 100"),
        Document("Sleep", "Used set the sleep at current instruction and take time in ms, sleep 100"),
        Document("Show", "Used to show the turtle pointer, show"),
        Document("Hide", "Used set hide the turtle pointer, hide"),
        Document("Stop", "Used set the stop the execution, stop"),
        Document("Rotate", "Used set the update the current degree, rotate n"),
        Document("Forward", "Used set move forward with n space, forward n"),
        Document("Backward", "Used set move backward with n space, backward n"),
        Document("Right", "Used set move right with n space, right n"),
        Document("Left", "Used set move left with n space, left n"),
    ),
    DocumentCategory("Collections") to listOf(
        Document("Array", "You can easily create array inside [v1, v2, v3]"),
        Document("Get Element from Array", "You can use index operator to get element with index array[i]"),
        Document("Set Element from Array", "You can use index operator to set element with index array[i] = v")
    ),
    DocumentCategory("Constants") to listOf(
        Document("Numbers", "Numbers can be integers or real"),
        Document("Booleans", "Booleans values can be true or false")
    ),
    DocumentCategory("Builtin functions") to listOf(
        Document("len", "Receive collection for example list and return the size of it, len([1, 2, 3])"),
    )
)