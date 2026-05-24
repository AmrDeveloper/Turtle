package com.amrdeveloper.lilo.parser

import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.valueOr

class LiloLexer(val source: String) {

    private var startPos: Int = 0
    private var currentPos: Int = 0

    private var columnStart: Int = 0
    private var columnEnd: Int = 0
    private var line: Int = 1

    fun tokenize(): LiloResult<List<LiloToken>> {
        val tokens: MutableList<LiloToken> = mutableListOf()
        while (!isAtEnd()) {
            ignoreCommentsAndSpaces()
            if (isAtEnd()) break

            startPos = currentPos
            columnStart = columnEnd

            when (val c = peek()) {
                in 'a'..'z', in 'A'..'Z', '_' -> {
                    val token = consumeSymbolOrKeyword().valueOr { return it.toFailure() }
                    tokens.add(token)
                }

                in '0'..'9' -> {
                    val token = consumeNumber().valueOr { return it.toFailure() }
                    tokens.add(token)
                }

                '+', '-', '*', '/', '%' -> {
                    tokens.add(createToken(kind = getLiloOneCharTokenMap()[advance()]!!))
                }

                '=' -> {
                    advance()
                    if (peek() == '=') {
                        advance()
                        tokens.add(createToken(kind = LiloTokenKind.EQ_EQ))
                    } else {
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
                    } else {
                        tokens.add(createToken(kind = LiloTokenKind.GT))
                    }
                }

                '<' -> {
                    advance()
                    if (peek() == '=') {
                        advance()
                        tokens.add(createToken(kind = LiloTokenKind.LE))
                    } else {
                        tokens.add(createToken(kind = LiloTokenKind.LT))
                    }
                }

                '(', ')', '[', ']', '{', '}' -> {
                    tokens.add(createToken(kind = getLiloOneCharTokenMap()[advance()]!!))
                }

                '.', ',', ':', ';' -> {
                    tokens.add(createToken(kind = getLiloOneCharTokenMap()[advance()]!!))
                }

                '\'', '"' -> {
                    val stringToken = consumeStringLiteral().valueOr { return it.toFailure() }
                    tokens.add(stringToken)
                }

                else -> {
                    return createDiagnostic(message = "Unexpected char `${c}`")
                }
            }
        }

        tokens.add(createToken(kind = LiloTokenKind.END_OF_FILE))
        return LiloResult.Success(data = tokens)
    }

    private fun consumeSymbolOrKeyword(): LiloResult<LiloToken> {
        while (!isAtEnd() && peek().isLetterOrDigitOrUnderscore()) advance()
        val lexeme = source.substring(startPos, currentPos)
        val tokenKind =
            getLiloKeywordsMap().getOrDefault(key = lexeme, defaultValue = LiloTokenKind.SYMBOL)
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
        val str = createToken(kind = LiloTokenKind.STR_LITERAL, lexeme = lexeme)
        return LiloResult.Success(data = str)
    }

    private fun ignoreCommentsAndSpaces() {
        while (!isAtEnd()) {
            val c = peek()
            when (c) {
                ' ', '\n', '\t', '\r' -> {
                    advance()
                    continue
                }

                '#' -> {
                    while (!isAtEnd() && peek() != '\n') {
                        advance()
                    }
                    continue
                }

                else -> break
            }
        }
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

    private fun peekNext() = if (currentPos + 1 < source.length) '\u0000' else source[currentPos]

    private fun isAtEnd() = currentPos >= source.length
}
