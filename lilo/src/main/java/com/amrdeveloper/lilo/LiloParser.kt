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

import com.amrdeveloper.lilo.ast.*
import timber.log.Timber

private const val TAG = "LiloParser"

class LiloParser(private val tokens: List<Token>, private val diagnostics: LiloDiagnostics) {

    private var currentIndex = 0

    fun parseScript(): LiloScript {
        val statements = mutableListOf<Statement>()
        try {
            while (isAtEnd().not()) {
                val statement = parseDeclaration()
                statements.add(statement)
            }
        } catch (e: Exception) {
            Timber.tag(TAG).d("Exception: ${e.message}")
        }
        return LiloScript(statements)
    }

    private fun parseDeclaration(): Statement {
        return when (peek().type) {
            TokenType.TOKEN_LET -> parseLetDeclaration()
            TokenType.TOKEN_FUN -> parseFunctionDeclaration()
            else -> parseStatement()
        }
    }

    private fun parseLetDeclaration(): Statement {
        Timber.tag(TAG).d("Parse let declaration")
        consume(TokenType.TOKEN_LET, "Expect let keyword.")
        val name = consume(TokenType.TOKEN_IDENTIFIER, "Expect variable name.")
        consume(TokenType.TOKEN_EQ, "Expect = after variable name.")
        val value = parseExpression()
        return LetStatement(name.literal, value)
    }

    private fun parseFunctionDeclaration(): Statement {
        Timber.tag(TAG).d("Parse function declaration")
        consume(TokenType.TOKEN_FUN, "Expect fun keyword.")
        val name = consume(TokenType.TOKEN_IDENTIFIER, "Expect identifier as function name.")

        val parameters = mutableListOf<Token>()
        if (checkPeek(TokenType.TOKEN_OPEN_PAREN)) {
            while (isAtEnd().not() && peek().type != TokenType.TOKEN_CLOSE_PAREN) {
                parameters.add(peek())
                advance()
            }
            consume(TokenType.TOKEN_CLOSE_PAREN, "Expect ) after function parameters.")
        }

        // fun <name> = <value> or fun <name> (params) = <value>
        if (checkPeek(TokenType.TOKEN_EQ)) {
            val returnValue = parseExpression()
            val returnStatement = ReturnStatement(returnValue)
            return FunctionStatement(name.literal, parameters, returnStatement)
        }

        // fun <name> { <statements> } or fun <name> (params> { <statements> }
        if (peek().type == TokenType.TOKEN_OPEN_BRACE) {
            val statement = parseBlockStatement()
            return FunctionStatement(name.literal, parameters, statement)
        }

        Timber.tag(TAG).d("Invalid function declaration.")
        reportParserError(peek().position, "Invalid function declaration.")
        throw Exception("Invalid function declaration.")
    }

    private fun parseStatement(): Statement {
        return when (peek().type) {
            TokenType.TOKEN_IF -> parseIfStatement()
            TokenType.TOKEN_WHILE -> parseWhileStatement()
            TokenType.TOKEN_REPEAT -> parseRepeatStatement()
            TokenType.TOKEN_CUBE -> parseCubeStatement()
            TokenType.TOKEN_CIRCLE -> parseCircleStatement()
            TokenType.TOKEN_MOVE -> parseMoveStatement()
            TokenType.TOKEN_MOVE_X -> parseMoveXStatement()
            TokenType.TOKEN_MOVE_Y -> parseMoveYStatement()
            TokenType.TOKEN_COLOR -> parseColorStatement()
            TokenType.TOKEN_SLEEP -> parseSleepStatement()
            TokenType.TOKEN_STOP -> parseStopStatement()
            TokenType.TOKEN_ROTATE -> parseRotateStatement()
            TokenType.TOKEN_FORWARD -> parseForwardStatement()
            TokenType.TOKEN_BACKWARD -> parseBackwardStatement()
            TokenType.TOKEN_RIGHT -> parseRightStatement()
            TokenType.TOKEN_LEFT -> parseLeftStatement()
            TokenType.TOKEN_OPEN_BRACE -> parseBlockStatement()
            else -> parseExpressionStatement()
        }
    }

    private fun parseIfStatement(): Statement {
        Timber.tag(TAG).d("Parse if statement")
        consume(TokenType.TOKEN_IF, "Expect if keyword.")
        val condition = parseExpression()
        val statement = parseStatement()
        return IfStatement(condition, statement)
    }

    private fun parseWhileStatement(): Statement {
        Timber.tag(TAG).d("Parse while statement")
        consume(TokenType.TOKEN_WHILE, "Expect while keyword.")
        val condition = parseExpression()
        val statement = parseStatement()
        return WhileStatement(condition, statement)
    }

    private fun parseRepeatStatement(): Statement {
        Timber.tag(TAG).d("Parse repeat statement")
        consume(TokenType.TOKEN_REPEAT, "Expect repeat keyword.")
        val condition = parseExpression()
        val statement = parseStatement()
        return RepeatStatement(condition, statement)
    }

    private fun parseCubeStatement(): Statement {
        Timber.tag(TAG).d("Parse Cube statement")
        consume(TokenType.TOKEN_CUBE, "Expect Cube keyword.")
        val value =  parseExpression()
        return CubeStatement(value)
    }

    private fun parseCircleStatement(): Statement {
        Timber.tag(TAG).d("Parse Circle statement")
        consume(TokenType.TOKEN_CIRCLE, "Expect Circle keyword.")
        val radius = parseExpression()
        return CircleStatement(radius)
    }

    private fun parseMoveStatement(): Statement {
        Timber.tag(TAG).d("Parse Move statement")
        consume(TokenType.TOKEN_MOVE, "Expect Move keyword.")
        val moveXAmount = parseExpression()
        consume(TokenType.TOKEN_COMMA, "Expect , between 2 numbers of move instruction.")
        val moveYAmount = parseExpression()
        return MoveStatement(moveXAmount, moveYAmount)
    }

    private fun parseMoveXStatement(): Statement {
        Timber.tag(TAG).d("Parse Move X statement")
        consume(TokenType.TOKEN_MOVE_X, "Expect Move X keyword.")
        val amount = parseExpression()
        return MoveXStatement(amount)
    }

    private fun parseMoveYStatement(): Statement {
        Timber.tag(TAG).d("Parse Move Y statement")
        consume(TokenType.TOKEN_MOVE_Y, "Expect Move Y keyword.")
        val amount = parseExpression()
        return MoveYStatement(amount)
    }

    private fun parseColorStatement(): Statement {
        Timber.tag(TAG).d("Parse Color statement")
        consume(TokenType.TOKEN_COLOR, "Expect Color keyword.")
        val colorName = parseExpression()
        return ColorStatement(colorName)
    }

    private fun parseSleepStatement(): Statement {
        Timber.tag(TAG).d("Parse Sleep statement")
        consume(TokenType.TOKEN_SLEEP, "Expect Sleep keyword.")
        val amount = parseExpression()
        return SleepStatement(amount)
    }

    private fun parseStopStatement(): Statement {
        Timber.tag(TAG).d("Parse Stop statement")
        consume(TokenType.TOKEN_STOP, "Expect Stop keyword.")
        return StopStatement()
    }

    private fun parseRotateStatement(): Statement {
        Timber.tag(TAG).d("Parse Rotate statement")
        consume(TokenType.TOKEN_ROTATE, "Expect Rotate keyword.")
        val amount = parseExpression()
        return RotateStatement(amount)
    }

    private fun parseForwardStatement(): Statement {
        Timber.tag(TAG).d("Parse Forward statement")
        consume(TokenType.TOKEN_FORWARD, "Expect Forward keyword.")
        val amount = parseExpression()
        return ForwardStatement(amount)
    }

    private fun parseBackwardStatement(): Statement {
        Timber.tag(TAG).d("Parse Backward statement")
        consume(TokenType.TOKEN_BACKWARD, "Expect Backward keyword.")
        val amount = parseExpression()
        return BackwardStatement(amount)
    }

    private fun parseRightStatement(): Statement {
        Timber.tag(TAG).d("Parse Right statement")
        consume(TokenType.TOKEN_RIGHT, "Expect Right keyword.")
        val amount = parseExpression()
        return RightStatement(amount)
    }

    private fun parseLeftStatement(): Statement {
        Timber.tag(TAG).d("Parse LEFT statement")
        consume(TokenType.TOKEN_LEFT, "Expect LEFT keyword.")
        val amount = parseExpression()
        return LeftStatement(amount)
    }

    private fun parseBlockStatement(): Statement {
        Timber.tag(TAG).d("Parse Block statement")

        val statements = mutableListOf<Statement>()

        consume(TokenType.TOKEN_OPEN_BRACE, "Expect close brace } at the start of block.")

        while (isAtEnd().not() && peek().type != TokenType.TOKEN_CLOSE_BRACE) {
            statements.add(parseStatement())
        }

        consume(TokenType.TOKEN_CLOSE_BRACE, "Expect close brace { at the end of block.")
        return BlockStatement(statements)
    }

    private fun parseExpressionStatement(): Statement {
        val expression = parseExpression()
        return ExpressionStatement(expression)
    }

    private fun parseExpression(): Expression {
        return parsePrimaryExpression()
    }

    private fun parsePrimaryExpression(): Expression {
        return when (peek().type) {
            TokenType.TOKEN_TRUE, TokenType.TOKEN_FALSE -> {
                val value = peek().type == TokenType.TOKEN_TRUE
                advance()
                BooleanExpression(value)
            }
            TokenType.TOKEN_NUMBER -> {
                val value = peek().literal.toFloat()
                advance()
                NumberExpression(value)
            }
            TokenType.TOKEN_IDENTIFIER -> {
                val name = peek().literal
                advance()
                VariableExpression(name)
            }
            else -> {
                Timber.tag(TAG).d("Unexpected primary expression")
                reportParserError(peek().position, "Unexpected primary expression")
                throw Exception("Unexpected primary expression")
            }
        }
    }

    private fun consume(type: TokenType, message: String): Token {
        if (peek().type == type) {
            val token = peek()
            advance()
            return token
        }
        reportParserError(peek().position, message)
        throw Exception(message)
    }

    private fun reportParserError(position: TokenPosition, message: String) {
        diagnostics.reportError(position, message)
    }

    private fun advance(): Token {
        if (isAtEnd().not()) currentIndex++
        return previous()
    }

    private fun checkPeek(type : TokenType) : Boolean {
        if (peek().type == type) {
            advance()
            return true
        }
        return false
    }

    private fun peek(): Token {
        return tokens[currentIndex]
    }

    private fun previous(): Token {
        return tokens[currentIndex - 1]
    }

    private fun isAtEnd(): Boolean {
        return peek().type == TokenType.TOKEN_END_OF_INPUT
    }
}