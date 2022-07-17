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

enum class TokenType {
    TOKEN_LET,
    TOKEN_FUN,
    TOKEN_IF,
    TOKEN_ELIF,
    TOKEN_ELSE,
    TOKEN_WHILE,
    TOKEN_REPEAT,

    TOKEN_CUBE,
    TOKEN_CIRCLE,
    TOKEN_MOVE,
    TOKEN_MOVE_X,
    TOKEN_MOVE_Y,
    TOKEN_COLOR,
    TOKEN_BACKGROUND,
    TOKEN_SLEEP,
    TOKEN_STOP,
    TOKEN_ROTATE,
    TOKEN_FORWARD,
    TOKEN_BACKWARD,
    TOKEN_RIGHT,
    TOKEN_LEFT,

    TOKEN_OPEN_PAREN,
    TOKEN_CLOSE_PAREN,
    TOKEN_OPEN_BRACE,
    TOKEN_CLOSE_BRACE,
    TOKEN_OPEN_BRACKET,
    TOKEN_CLOSE_BRACKET,

    TOKEN_COMMA,

    TOKEN_EQ,
    TOKEN_EQ_EQ,
    TOKEN_BANG,
    TOKEN_BANG_EQ,
    TOKEN_GT,
    TOKEN_GT_EQ,
    TOKEN_LS,
    TOKEN_LS_EQ,

    TOKEN_OR,
    TOKEN_AND,
    TOKEN_LOGICAL_OR,
    TOKEN_LOGICAL_AND,

    TOKEN_PLUS,
    TOKEN_PLUS_EQ,
    TOKEN_MINUS,
    TOKEN_MINUS_EQ,
    TOKEN_MUL,
    TOKEN_MUL_EQ,
    TOKEN_DIV,
    TOKEN_DIV_EQ,
    TOKEN_REMINDER,
    TOKEN_REMINDER_EQ,

    TOKEN_IDENTIFIER,
    TOKEN_NUMBER,
    TOKEN_TRUE,
    TOKEN_FALSE,

    TOKEN_INVALID,
    TOKEN_END_OF_INPUT,
}

/**
 * A list of all assignments operators
 */
val assignOperators = mutableSetOf(
    TokenType.TOKEN_EQ,
    TokenType.TOKEN_PLUS_EQ,
    TokenType.TOKEN_MINUS_EQ,
    TokenType.TOKEN_MUL_EQ,
    TokenType.TOKEN_DIV_EQ,
    TokenType.TOKEN_REMINDER_EQ,
)

/**
 * A Map between special assignment operators and it binary operator
 * for example += operator is equal to assignment with binary plus operator
 */
val specialAssignToBinary = mutableMapOf(
    TokenType.TOKEN_PLUS_EQ to TokenType.TOKEN_PLUS,
    TokenType.TOKEN_MINUS_EQ to TokenType.TOKEN_MINUS,
    TokenType.TOKEN_MUL_EQ to TokenType.TOKEN_MUL,
    TokenType.TOKEN_DIV_EQ to TokenType.TOKEN_DIV,
    TokenType.TOKEN_REMINDER_EQ to TokenType.TOKEN_REMINDER,
)