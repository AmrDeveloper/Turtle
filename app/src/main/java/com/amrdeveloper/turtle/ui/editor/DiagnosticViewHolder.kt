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

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.amrdeveloper.lilo.Diagnostic
import com.amrdeveloper.lilo.DiagnosticType
import com.amrdeveloper.treeview.TreeNode
import com.amrdeveloper.treeview.TreeViewHolder
import com.amrdeveloper.turtle.R

class DiagnosticViewHolder(itemView : View) : TreeViewHolder(itemView) {

    private val diagnosticTitle : TextView by lazy { itemView.findViewById(R.id.diagnostic_name) }
    private val diagnosticStateIcon : ImageView by lazy { itemView.findViewById(R.id.diagnostic_state_icon) }
    private val diagnosticTypeIcon : ImageView by lazy { itemView.findViewById(R.id.diagnostic_type_icon) }

    override fun bindTreeNode(node: TreeNode?) {
        super.bindTreeNode(node)
        if (node != null && node.value is Diagnostic) {
            val diagnostic = node.value as Diagnostic
            val position = diagnostic.position
            val positionLiteral = "L${position.line} (${position.columnStart}-${position.columnEnd})"
            diagnosticTitle.text = "${positionLiteral} :${diagnostic.message}"

            if (diagnostic.type == DiagnosticType.ERROR) {
                diagnosticTypeIcon.setImageResource(R.drawable.ic_error)
            } else {
                diagnosticTypeIcon.setImageResource(R.drawable.ic_warning)
            }

            if (node.children.isEmpty()) {
                diagnosticStateIcon.visibility = View.INVISIBLE
            } else {
                diagnosticStateIcon.visibility = View.VISIBLE
                val stateIcon = if (node.isExpanded) R.drawable.ic_arrow_down else R.drawable.ic_arrow_right
                diagnosticStateIcon.setImageResource(stateIcon)
            }
        }
    }
}