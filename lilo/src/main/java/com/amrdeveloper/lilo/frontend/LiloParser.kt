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

package com.amrdeveloper.lilo.frontend

import com.amrdeveloper.lilo.ast.*
import com.amrdeveloper.lilo.utils.LiloDiagnostics
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
                parameters.add(advance())
                if (peek().type == TokenType.TOKEN_COMMA) advance()
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
            TokenType.TOKEN_LET -> parseLetDeclaration()
            TokenType.TOKEN_IF -> parseIfStatement()
            TokenType.TOKEN_WHILE -> parseWhileStatement()
            TokenType.TOKEN_REPEAT -> parseRepeatStatement()
            TokenType.TOKEN_CUBE -> parseCubeStatement()
            TokenType.TOKEN_CIRCLE -> parseCircleStatement()
            TokenType.TOKEN_MOVE -> parseMoveStatement()
            TokenType.TOKEN_MOVE_X -> parseMoveXStatement()
            TokenType.TOKEN_MOVE_Y -> parseMoveYStatement()
            TokenType.TOKEN_COLOR -> parseColorStatement()
            TokenType.TOKEN_BACKGROUND -> parseBackgroundStatement()
            TokenType.TOKEN_SLEEP -> parseSleepStatement()
            TokenType.TOKEN_SPEED -> parseSpeedStatement()
            TokenType.TOKEN_POINTER_SHOW -> parsePointerShowStatement()
            TokenType.TOKEN_POINTER_HIDE -> parsePointerHideStatement()
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
        val keyword = consume(TokenType.TOKEN_IF, "Expect if keyword.")
        val condition = parseExpression()
        val statement = parseStatement()

        Timber.tag(TAG).d("Parse else if (elif) statements if exists")
        val alternatives = mutableListOf<IfStatement>()
        while (checkPeek(TokenType.TOKEN_ELIF)) {
            val token = previous()
            val alternativeCondition = parseExpression()
            val alternativeStatement = parseStatement()
            val elseIfStatement = IfStatement(token, alternativeCondition, alternativeStatement)
            alternatives.add(elseIfStatement)
        }

        Timber.tag(TAG).d("Parse else statements if exists")
        if (checkPeek(TokenType.TOKEN_ELSE)) {
            val token = previous()
            val alternativeStatement = parseStatement()
            val elseStatement = IfStatement(token, BooleanExpression(true), alternativeStatement)
            alternatives.add(elseStatement)
        }

        return IfStatement(keyword, condition, statement, alternatives)
    }

    private fun parseWhileStatement(): Statement {
        Timber.tag(TAG).d("Parse while statement")
        val keyword = consume(TokenType.TOKEN_WHILE, "Expect while keyword.")
        val condition = parseExpression()
        val statement = parseStatement()
        return WhileStatement(keyword, condition, statement)
    }

    private fun parseRepeatStatement(): Statement {
        Timber.tag(TAG).d("Parse repeat statement")
        val keyword = consume(TokenType.TOKEN_REPEAT, "Expect repeat keyword.")
        val condition = parseExpression()
        val statement = parseStatement()
        return RepeatStatement(keyword, condition, statement)
    }

    private fun parseCubeStatement(): Statement {
        Timber.tag(TAG).d("Parse Cube statement")
        val keyword = consume(TokenType.TOKEN_CUBE, "Expect Cube keyword.")
        val value =  parseExpression()
        return CubeStatement(keyword, value)
    }

    private fun parseCircleStatement(): Statement {
        Timber.tag(TAG).d("Parse Circle statement")
        val keyword = consume(TokenType.TOKEN_CIRCLE, "Expect Circle keyword.")
        val radius = parseExpression()
        return CircleStatement(keyword, radius)
    }

    private fun parseMoveStatement(): Statement {
        Timber.tag(TAG).d("Parse Move statement")
        val keyword = consume(TokenType.TOKEN_MOVE, "Expect Move keyword.")
        val moveXAmount = parseExpression()
        consume(TokenType.TOKEN_COMMA, "Expect , between 2 numbers of move instruction.")
        val moveYAmount = parseExpression()
        return MoveStatement(keyword, moveXAmount, moveYAmount)
    }

    private fun parseMoveXStatement(): Statement {
        Timber.tag(TAG).d("Parse Move X statement")
        val keyword = consume(TokenType.TOKEN_MOVE_X, "Expect Move X keyword.")
        val amount = parseExpression()
        return MoveXStatement(keyword, amount)
    }

    private fun parseMoveYStatement(): Statement {
        Timber.tag(TAG).d("Parse Move Y statement")
        val keyword = consume(TokenType.TOKEN_MOVE_Y, "Expect Move Y keyword.")
        val amount = parseExpression()
        return MoveYStatement(keyword, amount)
    }

    private fun parseColorStatement(): Statement {
        Timber.tag(TAG).d("Parse Color statement")
        val keyword = consume(TokenType.TOKEN_COLOR, "Expect Color keyword.")
        val colorName = parseExpression()
        return ColorStatement(keyword, colorName)
    }

    private fun parseBackgroundStatement(): Statement {
        Timber.tag(TAG).d("Parse background statement")
        val keyword = consume(TokenType.TOKEN_BACKGROUND, "Expect background keyword.")
        val colorName = parseExpression()
        return BackgroundStatement(keyword, colorName)
    }

    private fun parseSpeedStatement(): Statement {
        Timber.tag(TAG).d("Parse Speed statement")
        val keyword = consume(TokenType.TOKEN_SPEED, "Expect Speed keyword.")
        val amount = parseExpression()
        return SpeedStatement(keyword, amount)
    }

    private fun parseSleepStatement(): Statement {
        Timber.tag(TAG).d("Parse Sleep statement")
        val keyword = consume(TokenType.TOKEN_SLEEP, "Expect Sleep keyword.")
        val amount = parseExpression()
        return SleepStatement(keyword, amount)
    }

    private fun parsePointerShowStatement(): Statement {
        Timber.tag(TAG).d("Parse Pointer show statement")
        consume(TokenType.TOKEN_POINTER_SHOW, "Expect show keyword.")
        return ShowPointerStatement()
    }

    private fun parsePointerHideStatement(): Statement {
        Timber.tag(TAG).d("Parse Pointer hide statement")
        consume(TokenType.TOKEN_POINTER_HIDE, "Expect hide keyword.")
        return HidePointerStatement()
    }

    private fun parseStopStatement(): Statement {
        Timber.tag(TAG).d("Parse Stop statement")
        consume(TokenType.TOKEN_STOP, "Expect Stop keyword.")
        return StopStatement()
    }

    private fun parseRotateStatement(): Statement {
        Timber.tag(TAG).d("Parse Rotate statement")
        val keyword = consume(TokenType.TOKEN_ROTATE, "Expect Rotate keyword.")
        val amount = parseExpression()
        return RotateStatement(keyword, amount)
    }

    private fun parseForwardStatement(): Statement {
        Timber.tag(TAG).d("Parse Forward statement")
        val keyword = consume(TokenType.TOKEN_FORWARD, "Expect Forward keyword.")
        val amount = parseExpression()
        return ForwardStatement(keyword, amount)
    }

    private fun parseBackwardStatement(): Statement {
        Timber.tag(TAG).d("Parse Backward statement")
        val keyword = consume(TokenType.TOKEN_BACKWARD, "Expect Backward keyword.")
        val amount = parseExpression()
        return BackwardStatement(keyword, amount)
    }

    private fun parseRightStatement(): Statement {
        Timber.tag(TAG).d("Parse Right statement")
        val keyword = consume(TokenType.TOKEN_RIGHT, "Expect Right keyword.")
        val amount = parseExpression()
        return RightStatement(keyword, amount)
    }

    private fun parseLeftStatement(): Statement {
        Timber.tag(TAG).d("Parse LEFT statement")
        val keyword = consume(TokenType.TOKEN_LEFT, "Expect LEFT keyword.")
        val amount = parseExpression()
        return LeftStatement(keyword, amount)
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
        return parseAssignExpression()
    }

    private fun parseAssignExpression() : Expression {
        val expression = parseLogicalOrExpression()
        if (peek().type in assignOperators) {
            advance()
            val operator = previous()
            val value = parseAssignExpression()
            if (expression is VariableExpression || expression is IndexExpression) {
                return if (operator.type == TokenType.TOKEN_EQ) {
                    AssignExpression(operator, expression, value)
                } else {
                    // x += y  ----> x = x + y ----> Assign(x, Binary(x, +, y))
                    val binaryOperator = specialAssignToBinary[operator.type]!!
                    val binaryOperatorToken = operator.copy(type = binaryOperator)
                    val binaryExpression = BinaryExpression(expression, binaryOperatorToken, value)
                    AssignExpression(operator, expression, binaryExpression)
                }
            }
            reportParserError(operator.position, "Invalid assignment target.")
        }
        return expression
    }

    private fun parseLogicalOrExpression() : Expression {
        var expression = parseLogicalAndExpression()
        while (checkPeek(TokenType.TOKEN_LOGICAL_OR)) {
            val operator = previous()
            val right = parseLogicalAndExpression()
            expression = LogicalExpression(expression, operator, right)
        }
        return expression
    }

    private fun parseLogicalAndExpression() : Expression {
        var expression = parseEqualityExpression()
        while (checkPeek(TokenType.TOKEN_LOGICAL_OR)) {
            val operator = previous()
            val right = parseEqualityExpression()
            expression = LogicalExpression(expression, operator, right)
        }
        return expression
    }

    private fun parseEqualityExpression() : Expression {
        var expression = parseComparisonExpression()
        while (checkPeek(TokenType.TOKEN_EQ_EQ) || checkPeek(TokenType.TOKEN_BANG_EQ)) {
            val operator = previous()
            val right = parseComparisonExpression()
            expression = BinaryExpression(expression, operator, right)
        }
        return expression
    }

    private fun parseComparisonExpression() : Expression {
        var expression = parseTermExpression()
        while (checkPeek(TokenType.TOKEN_GT) || checkPeek(TokenType.TOKEN_GT_EQ) ||
            checkPeek(TokenType.TOKEN_LS) || checkPeek(TokenType.TOKEN_LS_EQ)) {
            val operator = previous()
            val right = parseTermExpression()
            expression = BinaryExpression(expression, operator, right)
        }
        return expression
    }

    private fun parseTermExpression() : Expression {
        var expression = parseFactorExpression()
        while (checkPeek(TokenType.TOKEN_PLUS) || checkPeek(TokenType.TOKEN_MINUS)) {
            val operator = previous()
            val right = parseFactorExpression()
            expression = BinaryExpression(expression, operator, right)
        }
        return expression
    }

    private fun parseFactorExpression() : Expression {
        var expression = parseUnaryExpression()
        while (checkPeek(TokenType.TOKEN_MUL) || checkPeek(TokenType.TOKEN_DIV) || checkPeek(
                TokenType.TOKEN_REMINDER)) {
            val operator = previous()
            val right = parseUnaryExpression()
            expression =  BinaryExpression(expression, operator, right)
        }
        return expression
    }

    private fun parseUnaryExpression() : Expression {
        if (checkPeek(TokenType.TOKEN_BANG) || checkPeek(TokenType.TOKEN_MINUS)) {
            val operator = previous()
            val right = parseUnaryExpression()
            return UnaryExpression(operator, right)
        }
        return parseCallExpression()
    }

    private fun parseCallExpression() : Expression {
        var expression = parsePrimaryExpression()
        while (true) {
            if (checkPeek(TokenType.TOKEN_OPEN_PAREN)) {
                val openParenToken = previous()
                val arguments = mutableListOf<Expression>()
                if (checkPeek(TokenType.TOKEN_CLOSE_PAREN).not()) {
                    do { arguments.add(parseExpression()) }
                    while (checkPeek(TokenType.TOKEN_COMMA))
                    consume(TokenType.TOKEN_CLOSE_PAREN, "Expect close paren ) after call arguments.")
                }
                expression = CallExpression(expression, openParenToken, arguments)
            }
            else if (checkPeek(TokenType.TOKEN_OPEN_BRACKET)) {
                val openBracketToken = previous()
                val index = parseExpression()
                consume(TokenType.TOKEN_CLOSE_BRACKET, "Expect close bracket [ after index value.")
                expression = IndexExpression(openBracketToken, expression, index)
            }
            else if (checkPeek(TokenType.TOKEN_DOT)) {
                val dotToken = previous()
                val statement = parseStatement()
                if (statement is TurtleStatement) {
                    expression = DotExpression(dotToken, expression, statement)
                } else {
                    reportParserError(dotToken.position, "Invalid Dot Expression")
                }
            }
            else {
                break
            }
        }
        return expression
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
                val name = peek()
                advance()
                VariableExpression(name)
            }
            TokenType.TOKEN_OPEN_PAREN -> {
                advance()
                val expression = parseExpression()
                consume(TokenType.TOKEN_CLOSE_PAREN, "Expect close paren ) at the end of group expression.")
                GroupExpression(expression)
            }
            TokenType.TOKEN_OPEN_BRACKET -> {
                advance()
                val values = mutableListOf<Expression>()
                while (isAtEnd().not() && peek().type != TokenType.TOKEN_CLOSE_BRACKET) {
                    val expression = parseExpression()
                    values.add(expression)
                    if (checkPeek(TokenType.TOKEN_COMMA).not()) break;
                }
                consume(TokenType.TOKEN_CLOSE_BRACKET, "Expect close bracket ] at the end of list expression.")
                ListExpression(values)
            }
            TokenType.TOKEN_NEW_TURTLE -> {
                advance()
                NewTurtleExpression()
            }
            TokenType.TOKEN_THIS -> {
                advance()
                ThieExpression()
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