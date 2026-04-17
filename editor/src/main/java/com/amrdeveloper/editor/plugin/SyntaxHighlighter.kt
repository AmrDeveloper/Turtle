package com.amrdeveloper.editor.plugin

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.amrdeveloper.editor.constant.keywords

class SyntaxHighlighter : OutputTransformation {

    private val keywordStyle = SpanStyle(color = Color(0xFF3F51B5), fontWeight = FontWeight.Bold)
    private val numberStyle = SpanStyle(color = Color(0xFFB5CEA8))
    private val stringStyle = SpanStyle(color = Color(0xFFCE9178))
    private val commentStyle = SpanStyle(color = Color(0xFF6A9955))
    private val classStyle = SpanStyle(color = Color(0xFF0F8A75))
    private val functionStyle = SpanStyle(color = Color(0xFFC573C4))
    private val operatorStyle = SpanStyle(color = Color(0xFF3F51B5))

    override fun TextFieldBuffer.transformOutput() {
        val text = asCharSequence()

        // Keywords
        Regex(pattern = "\\b(${keywords.joinToString("|")})\\b").findAll(input = text)
            .forEach { match ->
                addStyle(
                    keywordStyle,
                    match.range.first,
                    match.range.last + 1
                )
            }

        // Strings
        Regex(pattern = "'[^']*'|\"[^\"]*\"").findAll(text).forEach { match ->
            addStyle(
                stringStyle,
                match.range.first,
                match.range.last + 1
            )
        }

        // Comments
        Regex(pattern = "#.*").findAll(text).forEach { match ->
            addStyle(
                commentStyle,
                match.range.first,
                match.range.last + 1
            )
        }

        // Functions
        Regex(pattern = "def\\s+(\\w+)").findAll(text).forEach { match ->
            match.groups[1]?.let { name ->
                addStyle(
                    functionStyle,
                    name.range.first,
                    name.range.last + 1
                )
            }
        }

        // Classes
        Regex(pattern = "class\\s+(\\w+)").findAll(text).forEach { match ->
            match.groups[1]?.let { name ->
                addStyle(
                    classStyle,
                    name.range.first,
                    name.range.last + 1
                )
            }
        }

        // Numbers
        Regex(pattern = "\\b\\d+\\b").findAll(text).forEach { match ->
            addStyle(
                numberStyle,
                match.range.first,
                match.range.last + 1
            )
        }

        // Operators
        Regex(pattern = "[+\\-*/%=<>!&|^~]").findAll(text).forEach { match ->
            addStyle(
                operatorStyle,
                match.range.first,
                match.range.last + 1
            )
        }
    }
}
