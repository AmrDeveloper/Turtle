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

package com.amrdeveloper.turtle.ui.editor

import androidx.core.content.ContextCompat
import com.amrdeveloper.codeview.CodeView
import com.amrdeveloper.lilo.LiloTokenizer
import com.amrdeveloper.turtle.R
import java.util.regex.Pattern

private val turtleKeywords = LiloTokenizer.keywords.keys
private val PATTERN_KEYWORDS = Pattern.compile("\\b(${turtleKeywords.joinToString(separator = "|")})\\b")
private val PATTERN_NUMBERS = Pattern.compile("\\b(\\d*[.]?\\d+)\\b")

fun configCodeViewForTurtle(codeView: CodeView) {
    val context = codeView.context ?: return
    codeView.setBackgroundColor(ContextCompat.getColor(context, R.color.monokia_pro_black))
    codeView.setTextColor(ContextCompat.getColor(context, R.color.monokia_pro_white))
    codeView.addSyntaxPattern(PATTERN_KEYWORDS, ContextCompat.getColor(context, R.color.monokia_pro_pink))
    codeView.addSyntaxPattern(PATTERN_NUMBERS, ContextCompat.getColor(context, R.color.monokia_pro_purple))
}