package com.amrdeveloper.editor.plugin

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.amrdeveloper.colorschema.core.EditorSchema
import com.amrdeveloper.editor.constant.keywords

class SyntaxHighlighter(colorSchema: EditorSchema) : OutputTransformation {

    private val keywordStyle = SpanStyle(color = colorSchema.keyword, fontWeight = FontWeight.Bold)
    private val numberStyle = SpanStyle(color = colorSchema.number)
    private val stringStyle = SpanStyle(color = colorSchema.string)
    private val commentStyle = SpanStyle(color = colorSchema.comment)
    private val classStyle = SpanStyle(color = colorSchema.classType)
    private val functionStyle = SpanStyle(color = colorSchema.function)
    private val operatorStyle = SpanStyle(color = colorSchema.operator)

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
