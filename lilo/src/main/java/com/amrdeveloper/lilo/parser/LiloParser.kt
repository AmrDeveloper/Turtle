package com.amrdeveloper.lilo.parser

import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.ast.ArithExpr
import com.amrdeveloper.lilo.ast.AssignStmt
import com.amrdeveloper.lilo.ast.BoolExpr
import com.amrdeveloper.lilo.ast.CallExpr
import com.amrdeveloper.lilo.ast.ExprStmt
import com.amrdeveloper.lilo.ast.FloatExpr
import com.amrdeveloper.lilo.ast.IntExpr
import com.amrdeveloper.lilo.ast.GroupExpr
import com.amrdeveloper.lilo.ast.LiloExpr
import com.amrdeveloper.lilo.ast.LiloProgram
import com.amrdeveloper.lilo.ast.LiloStmt
import com.amrdeveloper.lilo.ast.ListExpr
import com.amrdeveloper.lilo.ast.SymbolExpr
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.toSuccessData

class LiloParser(val tokens: List<LiloToken>) {

    private var currentPos: Int = 0

    fun parse(): LiloResult<LiloProgram> {
        val nodes = mutableListOf<LiloStmt>()
        while (!isAtEnd()) {
            val nodeResult = parseDecl()
            if (nodeResult.isFailure()) return nodeResult.toFailure()
            nodes.add(nodeResult.toSuccessData())
        }
        return LiloResult.Success(data = LiloProgram(nodes))
    }

    private fun parseDecl(): LiloResult<LiloStmt> {
        return when (peek().kind) {
            else -> parseStmt()
        }
    }

    private fun parseStmt(): LiloResult<LiloStmt> {
        return when (peek().kind) {
            else -> parseAssignmentStmt()
        }
    }

    private fun parseAssignmentStmt(): LiloResult<LiloStmt> {
        if (peekNext().kind == LiloTokenKind.EQ) {
            val nameResult = expectAndConsume(kind = LiloTokenKind.SYMBOL, "Expect symbol on right side of `=`")
            if (nameResult.isFailure()) return nameResult.toFailure()
            val name = nameResult.toSuccessData()

            // Consume `=`
            advance()

            val valueResult = parseExpr()
            if (valueResult.isFailure()) return valueResult.toFailure()
            val value = valueResult.toSuccessData()
            return LiloResult.Success(data = AssignStmt(name.lexeme!!, value))
        }

        val expr = parseExpr()
        if (expr.isFailure()) return expr.toFailure()
        return LiloResult.Success(data = ExprStmt(expr = expr.toSuccessData()))
    }

    private fun parseExpr(): LiloResult<LiloExpr> {
        return parseAdditiveExpr()
    }

    private fun parseAdditiveExpr(): LiloResult<LiloExpr> {
        val lhsResult = parseMultiplicativeExpr()
        if (lhsResult.isFailure()) return lhsResult.toFailure()
        var lhs = lhsResult.toSuccessData()

        while (!isAtEnd() && peek().kind.isTermOperator()) {
            val op = advance()
            val rhsResult = parseMultiplicativeExpr()
            if (rhsResult.isFailure()) return rhsResult.toFailure()
            val rhs = rhsResult.toSuccessData()
            lhs = ArithExpr(lhs = lhs, op = op, rhs = rhs)
        }

        return LiloResult.Success(data = lhs)
    }

    private fun parseMultiplicativeExpr(): LiloResult<LiloExpr> {
        val lhsResult = visitCallExpr()
        if (lhsResult.isFailure()) return lhsResult.toFailure()
        var lhs = lhsResult.toSuccessData()

        while (!isAtEnd() && peek().kind.isFactorOperator()) {
            val op = advance()
            val rhsResult = visitCallExpr()
            if (rhsResult.isFailure()) return rhsResult.toFailure()

            val rhs = rhsResult.toSuccessData()
            lhs = ArithExpr(lhs = lhs, op = op, rhs = rhs)
        }

        return LiloResult.Success(data = lhs)
    }

    private fun visitCallExpr(): LiloResult<LiloExpr> {
        val calleeResult = parsePriamryExpr()
        if (calleeResult.isFailure()) return calleeResult.toFailure()
        if (peek().kind != LiloTokenKind.LPAR) {
            return calleeResult
        }

        val call = calleeResult.toSuccessData()
        if ((call is SymbolExpr).not()) {
            return createDiagnostic(previous().loc, "Expect literal as callee name")
        }

        val functionName = call.value.lexeme!!

        // (
        advance()

        val args = mutableListOf<LiloExpr>()
        while (!isAtEnd() && peek().kind != LiloTokenKind.RPAR) {
            val exprResult = parseExpr()
            if (exprResult.isFailure()) return exprResult.toFailure()
            args.add(exprResult.toSuccessData())

            if (peek().kind == LiloTokenKind.COMMA) {
                advance()
                continue
            }

            break
        }


        run {
            val consumeRes = expectAndConsume(kind = LiloTokenKind.RPAR, message = "expected ')' at end of call")
            if (consumeRes.isFailure()) return consumeRes.toFailure()
        }

        return LiloResult.Success(data = CallExpr(callee = functionName, args = args))
    }

    private fun parsePriamryExpr(): LiloResult<LiloExpr> {
        val token = peek()
        return when (token.kind) {
            LiloTokenKind.SYMBOL -> {
                advance()
                LiloResult.Success(data = SymbolExpr(value = token))
            }

            LiloTokenKind.INT_LITERAL -> {
                advance()
                LiloResult.Success(data = IntExpr(value = token))
            }

            LiloTokenKind.FLOAT_LITERAL -> {
                advance()
                LiloResult.Success(data = FloatExpr(value = token))
            }

            LiloTokenKind.TRUE_KEYWORD, LiloTokenKind.FALSE_KEYWORD -> {
                advance()
                LiloResult.Success(data = BoolExpr(value = token))
            }

            LiloTokenKind.L_BRACKET -> parseListExpr()
            LiloTokenKind.LPAR -> parseGroupExpr()
            else -> createDiagnostic(loc = token.loc, message = "Unexpected primary expr `${token.kind.name}`")
        }
    }

    private fun parseListExpr(): LiloResult<LiloExpr> {
        // Advance '['
        advance()

        val list = mutableListOf<LiloExpr>()
        while (!isAtEnd() && peek().kind != LiloTokenKind.R_BRACKET) {
            val exprResult = parseExpr()
            if (exprResult.isFailure()) return exprResult.toFailure()
            list.add(exprResult.toSuccessData())

            if (peek().kind == LiloTokenKind.COMMA) {
                advance()
                continue
            }

            break
        }

        run {
            val consumeRes = expectAndConsume(kind = LiloTokenKind.R_BRACKET, message = "expected ']' at end of list")
            if (consumeRes.isFailure()) return consumeRes.toFailure()
        }

        return LiloResult.Success(data = ListExpr(values = list))
    }

    private fun parseGroupExpr(): LiloResult<LiloExpr> {
        run {
            val consumeRes = expectAndConsume(kind = LiloTokenKind.LPAR, message = "expected '('")
            if (consumeRes.isFailure()) return consumeRes.toFailure()
        }

        val exprResult = parseExpr()
        if (exprResult.isFailure()) return exprResult.toFailure()
        val expr = exprResult.toSuccessData()

        run {
            val consumeRes = expectAndConsume(kind = LiloTokenKind.RPAR, message = "expected ')'")
            if (consumeRes.isFailure()) return consumeRes.toFailure()
        }

        return LiloResult.Success(data = GroupExpr(expr = expr))
    }

    private fun expectAndConsume(kind: LiloTokenKind, message: String): LiloResult<LiloToken> {
        if (peek().kind == kind) {
            advance()
            return LiloResult.Success(data = previous())
        }
        return createDiagnostic(peek().loc, message)
    }

    private fun createDiagnostic(loc: LiloLoc, message: String): LiloResult.Failure<LiloDiagnostic> {
        val disagnostic = LiloDiagnostic(loc, message)
        return LiloResult.Failure(error = disagnostic)
    }

    private fun advance(): LiloToken {
        if (isAtEnd().not()) currentPos++
        return previous()
    }

    private fun peek(): LiloToken {
        return tokens[currentPos]
    }

    private fun peekNext(): LiloToken {
        return tokens[currentPos + 1]
    }

    private fun previous(): LiloToken {
        return tokens[currentPos - 1]
    }

    private fun isAtEnd() = peek().kind.isEOF()
}