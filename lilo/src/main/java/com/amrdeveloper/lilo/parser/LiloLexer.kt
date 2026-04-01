package com.amrdeveloper.lilo.parser

import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.toSuccessData

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
                    val tokenOrErr = consumeSymbolOrKeyword()
                    if (tokenOrErr.isFailure()) {
                        return tokenOrErr.toFailure()
                    }
                    tokens.add(tokenOrErr.toSuccessData())
                }

                in '0'..'9' -> {
                    val tokenOrErr = consumeNumber()
                    if (tokenOrErr.isFailure()) {
                        return tokenOrErr.toFailure()
                    }
                    tokens.add(tokenOrErr.toSuccessData())
                }

                '+', '-', '*', '/', '%' -> {
                    tokens.add(createToken(kind = getLiloOneCharTokenMap()[advance()]!!))
                }

                '(', ')', '[', ']', '{', '}', '=' -> {
                    tokens.add(createToken(kind = getLiloOneCharTokenMap()[advance()]!!))
                }

                '.', ',' -> {
                    tokens.add(createToken(kind = getLiloOneCharTokenMap()[advance()]!!))
                }

                '\'', '"' -> {
                    val stringTokenOrErr = consumeStringLiteral()
                    if (stringTokenOrErr.isFailure()) {
                        return stringTokenOrErr.toFailure()
                    }
                    tokens.add(stringTokenOrErr.toSuccessData())
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
        return consumeIntOrFloatNumber()
    }

    private fun consumeIntOrFloatNumber(): LiloResult<LiloToken> {
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
        val numberKind =
            if (isFloatingPoint) LiloTokenKind.FLOAT_LITERAL else LiloTokenKind.INT_LITERAL
        return LiloResult.Success(data = createToken(kind = numberKind, lexeme))
    }

    private fun consumeStringLiteral() : LiloResult<LiloToken> {
        val start = advance()
        var literal = ""
        while (!isAtEnd() && peek() != start) literal += advance()
        if (isAtEnd() || peek() != start) {
            return LiloResult.Failure(error = createDiagnostic(message = "Unterminated string literal"))
        }

        // Consume terminator
        advance()

        val str = createToken(kind = LiloTokenKind.STR_LITERAL, lexeme = literal)
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
        val disagnostic = LiloDiagnostic(loc, message)
        return LiloResult.Failure(error = disagnostic)
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

    private fun isAtEnd() = currentPos >= source.length
}