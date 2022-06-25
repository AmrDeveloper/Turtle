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

val colorsTable = mapOf (
    "RED" to Color.RED,
    "BLUE" to Color.BLUE,
    "BLACK" to Color.BLACK,
    "CYAN" to Color.CYAN,
    "DKGRAY" to Color.DKGRAY,
    "GRAY" to Color.GRAY,
    "GREEN" to Color.GREEN,
    "LTGRAY" to Color.LTGRAY,
    "MAGENTA" to Color.MAGENTA,
    "WHITE" to Color.WHITE,
    "YELLOW" to Color.YELLOW,
)

fun defineColorsInScope(scope: LiloScope) {
    colorsTable.forEach { scope.define(it.key, it.value) }
}