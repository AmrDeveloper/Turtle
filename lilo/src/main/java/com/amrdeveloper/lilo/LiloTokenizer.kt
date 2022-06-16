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

class LiloTokenizer(private val script : String) {

    private var line = 1
    private var startPosition = 0
    private var currentPosition = 0
    private var startColumn = 1
    private var currentColumn = 1

    private val keywords = mapOf (
        "let" to TokenType.TOKEN_LET,
        "fun" to TokenType.TOKEN_FUN,
        "if" to TokenType.TOKEN_IF,
        "while" to TokenType.TOKEN_WHILE,
        "repeat" to TokenType.TOKEN_REPEAT,
        "true" to TokenType.TOKEN_TRUE,
        "false" to TokenType.TOKEN_FALSE,
    )

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
            ',' -> makeToken(TokenType.TOKEN_COMMA)
            '+' -> makeToken(TokenType.TOKEN_PLUS)
            '-' -> makeToken(TokenType.TOKEN_MINUS)
            '*' -> makeToken(TokenType.TOKEN_MUL)
            '/' -> makeToken(TokenType.TOKEN_DIV)
            '=' -> makeToken(if (match('=')) TokenType.TOKEN_EQ_EQ else TokenType.TOKEN_EQ)
            '!' -> makeToken(if (match('=')) TokenType.TOKEN_BANG_EQ else TokenType.TOKEN_BANG)
            '>' -> makeToken(if (match('=')) TokenType.TOKEN_GT_EQ else TokenType.TOKEN_GT)
            '<' -> makeToken(if (match('=')) TokenType.TOKEN_LS_EQ else TokenType.TOKEN_LS)
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
        if (isAtEnd()) return '\u0000'
        return script[currentPosition]
    }

    private fun peekNext() : Char {
        if (currentPosition + 1 >= script.length) return '\u0000'
        return script[currentPosition + 1]
    }

    private fun advance() : Char {
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
}