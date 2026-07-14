package com.amrdeveloper.lilo.parser

import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.valueOr
import java.util.Stack

class LiloLexer(val source: String) {

    private var startPos: Int = 0
    private var currentPos: Int = 0

    private var columnStart: Int = 0
    private var columnEnd: Int = 0
    private var line: Int = 1

    private val indentStack = Stack<Int>().apply { push(0) }

    // Tracking nested level to align with the Implicit Line Joining rule
    private var nestingLevel = 0

    fun tokenize(): LiloResult<List<LiloToken>> {
        val tokens: MutableList<LiloToken> = mutableListOf()
        while (!isAtEnd()) {
            val consumedToken = consumeToken().valueOr { return it.toFailure() }
            tokens.addAll(elements = consumedToken)
        }

        // Generate `DEDENT` for all remaining `INDENT` in the stack
        while (indentStack.size > 1) {
            tokens.add(createToken(kind = LiloTokenKind.DEDENT))
            indentStack.pop()
        }

        tokens.add(createToken(kind = LiloTokenKind.END_MARKER))
        return LiloResult.Success(data = tokens)
    }

    private fun consumeToken(): LiloResult<List<LiloToken>> {
        val tokens: MutableList<LiloToken> = mutableListOf()
        val indentTokens = consumeCommentsAndIndentations().valueOr { return it.toFailure() }
        tokens.addAll(elements = indentTokens)

        if (isAtEnd()) return LiloResult.Success(data = tokens)

        startPos = currentPos
        columnStart = columnEnd

        when (val c = peek()) {
            in 'a'..'z', in 'A'..'Z', '_' -> {
                if (c == 'f' && (peekNext() == '"' || peekNext() == '\'')) {
                    val joinedStrTokens = consumeJoinedStringLiteral().valueOr { return it.toFailure() }
                    tokens.addAll(elements = joinedStrTokens)
                } else {
                    val token = consumeSymbolOrKeyword().valueOr { return it.toFailure() }
                    tokens.add(token)
                }
            }

            in '0'..'9' -> {
                val token = consumeNumber().valueOr { return it.toFailure() }
                tokens.add(token)
            }

            '+', '%' -> {
                tokens.add(createToken(kind = liloOneCharTokenMap[advance()]!!))
            }

            '-' -> {
                advance()
                if (peek() == '>') {
                    advance()
                    tokens.add(createToken(kind = LiloTokenKind.R_ARROW))
                } else {
                    tokens.add(createToken(kind = LiloTokenKind.MINUS))
                }
            }

            '*' -> {
                advance()
                if (peek() == '*') {
                    advance()
                    tokens.add(createToken(kind = LiloTokenKind.DOUBLE_STAR))
                } else {
                    tokens.add(createToken(kind = LiloTokenKind.STAR))
                }
            }

            '/' -> {
                advance()
                if (peek() == '/') {
                    advance()
                    tokens.add(createToken(kind = LiloTokenKind.DOUBLE_SLASH))
                } else {
                    tokens.add(createToken(kind = LiloTokenKind.SLASH))
                }
            }

            '=' -> {
                advance()
                if (peek() == '=') {
                    advance()
                    tokens.add(createToken(kind = LiloTokenKind.EQ_EQ))
                }
                else {
                    tokens.add(createToken(kind = LiloTokenKind.EQ))
                }
            }

            '!' -> {
                advance()
                if (peek() == '=') {
                    advance()
                    tokens.add(createToken(kind = LiloTokenKind.BANG_EQ))
                } else {
                    tokens.add(createToken(kind = LiloTokenKind.BANG))
                }
            }

            '>' -> {
                advance()
                if (peek() == '=') {
                    advance()
                    tokens.add(createToken(kind = LiloTokenKind.GE))
                } else if (peek() == '>') {
                    advance()
                    tokens.add(createToken(kind = LiloTokenKind.RIGHT_SHIFT))
                } else {
                    tokens.add(createToken(kind = LiloTokenKind.GT))
                }
            }

            '<' -> {
                advance()
                if (peek() == '=') {
                    advance()
                    tokens.add(createToken(kind = LiloTokenKind.LE))
                } else if (peek() == '<') {
                    advance()
                    tokens.add(createToken(kind = LiloTokenKind.LEFT_SHIFT))
                } else {
                    tokens.add(createToken(kind = LiloTokenKind.LT))
                }
            }

            '(', ')', '[', ']', '{', '}' -> {
                val kind = liloOneCharTokenMap[peek()]!!
                if (kind == LiloTokenKind.L_PAR || kind == LiloTokenKind.L_SQB || kind == LiloTokenKind.L_BRACE) {
                    nestingLevel++
                } else {
                    nestingLevel--
                }
                tokens.add(createToken(kind = kind))
                advance()
            }

            ':' -> {
                advance()
                if (peek() == '=') {
                    advance()
                    tokens.add(createToken(kind = LiloTokenKind.COLON_EQ))
                } else {
                    tokens.add(createToken(kind = LiloTokenKind.COLON))
                }
            }

            '@', '.', ',', ';' -> {
                tokens.add(createToken(kind = liloOneCharTokenMap[advance()]!!))
            }

            '&', '|', '^' -> {
                tokens.add(createToken(kind = liloOneCharTokenMap[advance()]!!))
            }

            '\'', '"' -> {
                val stringToken = consumeStringLiteral().valueOr { return it.toFailure() }
                tokens.add(stringToken)
            }

            else -> {
                return createDiagnostic(message = "Unexpected char `${c}`")
            }
        }

        return LiloResult.Success(data = tokens)
    }

    private fun consumeSymbolOrKeyword(): LiloResult<LiloToken> {
        while (!isAtEnd() && peek().isLetterOrDigitOrUnderscore()) advance()
        val lexeme = source.substring(startPos, currentPos)
        val tokenKind =
            liloKeywordsMap.getOrDefault(key = lexeme, defaultValue = LiloTokenKind.NAME)
        return LiloResult.Success(data = createToken(kind = tokenKind, lexeme))
    }

    private fun consumeNumber(): LiloResult<LiloToken> {
        return consumeIntOrFloatOrComplexNumber()
    }

    private fun consumeIntOrFloatOrComplexNumber(): LiloResult<LiloToken> {
        var isFloatingPoint = false
        while (!isAtEnd() && peek().isDigitOrDot()) {
            if (peek().isDot()) {
                if (isFloatingPoint) {
                    return LiloResult.Failure(error = createDiagnostic(message = "Can't have two `.` in a number"))
                }
                isFloatingPoint = true
            }
            advance()
        }

        val lexeme = source.substring(startPos, currentPos)
        if (!isAtEnd() && (peek() == 'j' || peek() == 'J')) {
            // Consume `j`
            advance()
            return LiloResult.Success(
                data = createToken(
                    kind = LiloTokenKind.COMPLEX_LITERAL,
                    lexeme
                )
            )
        }

        val numberKind =
            if (isFloatingPoint) LiloTokenKind.FLOAT_LITERAL else LiloTokenKind.INT_LITERAL
        return LiloResult.Success(data = createToken(kind = numberKind, lexeme))
    }

    private fun consumeStringLiteral(): LiloResult<LiloToken> {
        val start = advance()
        while (!isAtEnd() && peek() != start) advance()
        if (isAtEnd() || peek() != start) {
            return LiloResult.Failure(error = createDiagnostic(message = "Unterminated string literal"))
        }

        // Consume terminator
        advance()

        val lexeme = source.substring(startPos + 1, currentPos - 1)
        val str = createToken(kind = LiloTokenKind.STRING, lexeme = lexeme)
        return LiloResult.Success(data = str)
    }

    private fun consumeJoinedStringLiteral(): LiloResult<List<LiloToken>> {
        val tokens = mutableListOf<LiloToken>()

        // 'f'
        advance()
        // ' or "
        val start = advance()

        tokens.add(createToken(kind = LiloTokenKind.F_STRING_START))

        var fStringMiddle = ""
        while (!isAtEnd() && peek() != start) {
            if (peek() == '{') {
                if (fStringMiddle.isNotEmpty()) {
                    tokens.add(createToken(kind = LiloTokenKind.F_STRING_MIDDLE, lexeme = fStringMiddle))
                    fStringMiddle = ""
                }

                advance()
                tokens.add(createToken(kind = LiloTokenKind.L_BRACE))

                while (!isAtEnd() && peek() != '}') {
                    tokens.addAll(elements = consumeToken().valueOr { return it.toFailure() })
                }

                if (peek() == '}') {
                    advance()
                    tokens.add(createToken(kind = LiloTokenKind.R_BRACE))
                }
                continue
            }

            fStringMiddle += advance()
        }

        // Consume fString middle before the fString end
        if (fStringMiddle.isNotEmpty()) {
            val token = createToken(kind = LiloTokenKind.F_STRING_MIDDLE, lexeme = fStringMiddle)
            tokens.add(token)
        }

        if (isAtEnd() || peek() != start) {
            return LiloResult.Failure(error = createDiagnostic(message = "Unterminated f-string"))
        }

        // Consume terminator
        advance()

        tokens.add(createToken(kind = LiloTokenKind.F_STRING_END))
        return LiloResult.Success(data = tokens)
    }

    private fun consumeCommentsAndIndentations() : LiloResult<List<LiloToken>> {
        val indentTokens = mutableListOf<LiloToken>()
        while (!isAtEnd()) {
            var c = peek()
            when (c) {
                '\n' -> {
                    if (nestingLevel > 0) {
                        advance()
                        continue
                    }

                    // Consume '\n'
                    advance()

                    // Calculate the indentation for the new line
                    var indent = 0
                    while (!isAtEnd() && peek() == ' ') {
                        advance()
                        indent += 1
                    }

                    if (!isAtEnd()) {
                        // If the current line is empty, consume it and don't emit extra token
                        if (peek() == '\n') {
                            continue
                        }

                        // If the current line is comment, consume it and don't emit extra token
                        if (peek() == '#') {
                            while (!isAtEnd() && peek() != '\n') {
                                advance()
                            }
                            continue
                        }
                    }

                    // Push new line token
                    indentTokens.add(createToken(kind = LiloTokenKind.NEW_LINE))

                    // Compare the indentation with the top stack
                    val currIndent = indentStack.peek() ?: 0

                    // If current indentation is bigger that means entering a new scope
                    if (indent > currIndent) {
                        indentTokens.add(createToken(kind = LiloTokenKind.INDENT))
                        indentStack.push(indent)
                        continue
                    }

                    // If current indentation is smaller that means leaving the current scope
                    if (indent < currIndent) {
                        indentTokens.add(createToken(kind = LiloTokenKind.DEDENT))
                        indentStack.pop()
                        while (indentStack.isNotEmpty()) {
                            val previousIndent = indentStack.peek()
                            if (indent == previousIndent) break
                            indentTokens.add(createToken(kind = LiloTokenKind.DEDENT))
                            indentStack.pop()
                        }

                        if (indentStack.isEmpty()) {
                            val diagnostic = createDiagnostic("Unindent amount does not match previous indent")
                            return LiloResult.Failure(error = diagnostic)
                        }
                        continue
                    }

                    continue
                }
                ' ', '\t', '\r' -> {
                    advance()
                    continue
                }
                '#' -> {
                    while (!isAtEnd() && c != '\n' && c != '\r') {
                        c = advance()
                    }
                    continue
                }

                else -> break
            }
        }
        return LiloResult.Success(data = indentTokens)
    }

    private fun createToken(kind: LiloTokenKind): LiloToken {
        val loc = createSourceLoc()
        return LiloToken(kind = kind, loc = loc, lexeme = "")
    }

    private fun createToken(kind: LiloTokenKind, lexeme: String): LiloToken {
        val loc = createSourceLoc()
        return LiloToken(kind = kind, loc = loc, lexeme = lexeme)
    }

    private fun createDiagnostic(message: String): LiloResult.Failure<LiloDiagnostic> {
        val loc = createSourceLoc()
        val diagnostic = LiloDiagnostic(loc, message)
        return LiloResult.Failure(error = diagnostic)
    }

    private fun createSourceLoc(): LiloLoc {
        return LiloLoc(line = line, startPos, end = currentPos)
    }

    private fun Char.isDigit() = this in '0'..'9'

    private fun Char.isDot() = this == '.'

    private fun Char.isDigitOrDot() = this.isDigit() || this.isDot()

    private fun Char.isLetterOrDigitOrUnderscore() = this.isLetterOrDigit() || this == '_'

    private fun advance(): Char {
        val currentCh = source[currentPos]
        if (currentCh == '\n') {
            line += 1
            columnEnd = 0
        } else {
            columnEnd += 1
        }
        currentPos += 1
        return currentCh
    }

    private fun peek() = if (isAtEnd()) '\u0000' else source[currentPos]

    private fun peekNext() = if (currentPos + 1 < source.length) source[currentPos + 1] else '\u0000'

    private fun isAtEnd() = currentPos >= source.length
}
