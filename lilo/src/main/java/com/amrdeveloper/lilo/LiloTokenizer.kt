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

private const val TERMINATE_CHAR = '\u0000'

class LiloTokenizer(private val script : String) {

    private var line = 1
    private var startPosition = 0
    private var currentPosition = 0
    private var startColumn = 1
    private var currentColumn = 1

    fun scanTokens() : List<Token> {
        val tokens = mutableListOf<Token>()
        while (isAtEnd().not()) {
            skipWhiteSpaces()
            startPosition = currentPosition
            startColumn = currentColumn
            tokens.add(scanToken())
        }
        tokens.add(makeToken(TokenType.TOKEN_END_OF_INPUT))
        return tokens
    }

    private fun scanToken() : Token {
        return when (advance()) {
            '(' -> makeToken(TokenType.TOKEN_OPEN_PAREN)
            ')' -> makeToken(TokenType.TOKEN_CLOSE_PAREN)
            '{' -> makeToken(TokenType.TOKEN_OPEN_BRACE)
            '}' -> makeToken(TokenType.TOKEN_CLOSE_BRACE)
            '[' -> makeToken(TokenType.TOKEN_OPEN_BRACKET)
            ']' -> makeToken(TokenType.TOKEN_CLOSE_BRACKET)
            ',' -> makeToken(TokenType.TOKEN_COMMA)
            '+' -> makeToken(if (match('=')) TokenType.TOKEN_PLUS_EQ else TokenType.TOKEN_PLUS)
            '-' -> makeToken(if (match('=')) TokenType.TOKEN_MINUS_EQ else TokenType.TOKEN_MINUS)
            '*' -> makeToken(if (match('=')) TokenType.TOKEN_MUL_EQ else TokenType.TOKEN_MUL)
            '/' -> makeToken(if (match('=')) TokenType.TOKEN_DIV_EQ else TokenType.TOKEN_DIV)
            '%' -> makeToken(if (match('=')) TokenType.TOKEN_REMINDER_EQ else TokenType.TOKEN_REMINDER)
            '=' -> makeToken(if (match('=')) TokenType.TOKEN_EQ_EQ else TokenType.TOKEN_EQ)
            '!' -> makeToken(if (match('=')) TokenType.TOKEN_BANG_EQ else TokenType.TOKEN_BANG)
            '>' -> makeToken(if (match('=')) TokenType.TOKEN_GT_EQ else TokenType.TOKEN_GT)
            '<' -> makeToken(if (match('=')) TokenType.TOKEN_LS_EQ else TokenType.TOKEN_LS)
            '|' -> makeToken(if (match('|')) TokenType.TOKEN_LOGICAL_OR else TokenType.TOKEN_OR)
            '&' -> makeToken(if (match('&')) TokenType.TOKEN_LOGICAL_AND else TokenType.TOKEN_AND)
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> scanNumberToken()
            in ('a'..'z') + ('A'..'Z'), '_' -> scanIdentifierToken()
            else -> makeToken(TokenType.TOKEN_INVALID, "Unexpected letter ${peek()}.")
        }
    }

    private fun scanIdentifierToken() : Token {
        while (isAlphaNumeric(peek())) advance()
        val literal = script.substring(startPosition, currentPosition)
        val tokenType = keywords[literal] ?: TokenType.TOKEN_IDENTIFIER
        return makeToken(tokenType, literal)
    }

    private fun scanNumberToken() : Token {
        while (isDigit(peek())) advance()

        if (peek() == '.' && isDigit(peekNext())) {
            advance()
            while (isDigit(peek())) advance()
        }

        val numberLiteral = script.substring(startPosition, currentPosition)
        return makeToken(TokenType.TOKEN_NUMBER, numberLiteral)
    }

    private fun skipWhiteSpaces() {
        while (true) {
            when (peek()) {
                ' ', '\r', '\t' -> {
                    advance()
                }
                '\n' -> {
                    line++
                    currentColumn = 0
                    advance()
                }
                else -> return
            }
        }
    }

    private fun makeToken(type : TokenType, literal : String = "") : Token {
        val location = makeTokenPosition()
        return Token(type, location, literal)
    }

    private fun makeTokenPosition() : TokenPosition {
        return TokenPosition(line, startColumn, currentColumn)
    }

    private fun match(expected : Char) : Boolean {
        if (isAtEnd()) return false
        if  (script[currentPosition] != expected) return false
        currentPosition++
        currentColumn++
        return true
    }

    private fun peek() : Char {
        if (isAtEnd()) return TERMINATE_CHAR
        return script[currentPosition]
    }

    private fun peekNext() : Char {
        if (currentPosition + 1 >= script.length) return TERMINATE_CHAR
        return script[currentPosition + 1]
    }

    private fun advance() : Char {
        if (isAtEnd()) return TERMINATE_CHAR
        currentColumn++
        return script[currentPosition++]
    }

    private fun isAlphaNumeric(c : Char) : Boolean {
        return isDigit(c) || isAlpha(c)
    }

    private fun isAlpha(c: Char): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z' || c == '_'
    }

    private fun isDigit(c : Char) : Boolean {
        return c in '0'..'9'
    }

    private fun isAtEnd() : Boolean {
        return currentPosition >= script.length
    }

    companion object {
        val keywords = mapOf(
            "let" to TokenType.TOKEN_LET,
            "fun" to TokenType.TOKEN_FUN,
            "if" to TokenType.TOKEN_IF,
            "elif" to TokenType.TOKEN_ELIF,
            "else" to TokenType.TOKEN_ELSE,
            "while" to TokenType.TOKEN_WHILE,
            "repeat" to TokenType.TOKEN_REPEAT,
            "true" to TokenType.TOKEN_TRUE,
            "false" to TokenType.TOKEN_FALSE,
            "cube" to TokenType.TOKEN_CUBE,
            "circle" to TokenType.TOKEN_CIRCLE,
            "move" to TokenType.TOKEN_MOVE,
            "movex" to TokenType.TOKEN_MOVE_X,
            "movey" to TokenType.TOKEN_MOVE_Y,
            "color" to TokenType.TOKEN_COLOR,
            "background" to TokenType.TOKEN_BACKGROUND,
            "speed" to TokenType.TOKEN_SPEED,
            "sleep" to TokenType.TOKEN_SLEEP,
            "stop" to TokenType.TOKEN_STOP,
            "rotate" to TokenType.TOKEN_ROTATE,
            "forward" to TokenType.TOKEN_FORWARD,
            "backward" to TokenType.TOKEN_BACKWARD,
            "right" to TokenType.TOKEN_RIGHT,
            "left" to TokenType.TOKEN_LEFT,
        )
    }
}