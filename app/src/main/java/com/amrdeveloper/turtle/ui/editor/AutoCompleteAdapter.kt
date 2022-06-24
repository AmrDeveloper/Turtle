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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.amrdeveloper.codeview.Code
import com.amrdeveloper.codeview.CodeViewAdapter
import com.amrdeveloper.codeview.Snippet
import com.amrdeveloper.turtle.R

private const val LAYOUT_ID = R.layout.list_item_autocomplete

class AutoCompleteAdapter(
    private val context: Context,
    private val codes: List<Code>
) : CodeViewAdapter(context, LAYOUT_ID, 0, codes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(LAYOUT_ID, parent, false)
        val currentCode = getItem(position) as Code

        val codeTitle = view.findViewById<TextView>(R.id.code_title)
        codeTitle.text = currentCode.codeTitle

        val codeType = view.findViewById<ImageView>(R.id.code_type)
        val icon = if (currentCode is Snippet) R.drawable.ic_snippet else R.drawable.ic_keyword
        codeType.setImageResource(icon)

        return view
    }
}