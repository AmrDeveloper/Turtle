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
    TOKEN_WHILE,
    TOKEN_REPEAT,

    TOKEN_CUBE,
    TOKEN_CIRCLE,
    TOKEN_MOVE,
    TOKEN_MOVE_X,
    TOKEN_MOVE_Y,
    TOKEN_COLOR,
    TOKEN_SLEEP,
    TOKEN_STOP,

    TOKEN_OPEN_PAREN,
    TOKEN_CLOSE_PAREN,
    TOKEN_OPEN_BRACE,
    TOKEN_CLOSE_BRACE,

    TOKEN_COMMA,

    TOKEN_EQ,
    TOKEN_EQ_EQ,
    TOKEN_BANG,
    TOKEN_BANG_EQ,
    TOKEN_GT,
    TOKEN_GT_EQ,
    TOKEN_LS,
    TOKEN_LS_EQ,

    TOKEN_PLUS,
    TOKEN_MINUS,
    TOKEN_MUL,
    TOKEN_DIV,

    TOKEN_IDENTIFIER,
    TOKEN_NUMBER,
    TOKEN_TRUE,
    TOKEN_FALSE,

    TOKEN_INVALID,
    TOKEN_END_OF_INPUT,
}