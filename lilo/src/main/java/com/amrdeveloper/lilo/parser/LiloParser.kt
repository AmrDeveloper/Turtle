package com.amrdeveloper.lilo.parser

import com.amrdeveloper.lilo.ast.AssignStmt
import com.amrdeveloper.lilo.ast.BinaryExpr
import com.amrdeveloper.lilo.ast.BlockStmt
import com.amrdeveloper.lilo.ast.BoolExpr
import com.amrdeveloper.lilo.ast.CallExpr
import com.amrdeveloper.lilo.ast.ExprStmt
import com.amrdeveloper.lilo.ast.FloatExpr
import com.amrdeveloper.lilo.ast.FromImportStmt
import com.amrdeveloper.lilo.ast.FunctionStmt
import com.amrdeveloper.lilo.ast.GetExpr
import com.amrdeveloper.lilo.ast.GetItemExpr
import com.amrdeveloper.lilo.ast.GroupExpr
import com.amrdeveloper.lilo.ast.IfExpr
import com.amrdeveloper.lilo.ast.ImportStmt
import com.amrdeveloper.lilo.ast.IntExpr
import com.amrdeveloper.lilo.ast.LiloExpr
import com.amrdeveloper.lilo.ast.LiloProgram
import com.amrdeveloper.lilo.ast.LiloStmt
import com.amrdeveloper.lilo.ast.ListExpr
import com.amrdeveloper.lilo.ast.NoneExpr
import com.amrdeveloper.lilo.ast.StrExpr
import com.amrdeveloper.lilo.ast.SymbolExpr
import com.amrdeveloper.lilo.ast.TupleExpr
import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.toSuccessData

class LiloParser(val tokens: List<LiloToken>) {

    private var currentPos: Int = 0

    fun parse(): LiloResult<LiloProgram> {
        val nodes = mutableListOf<LiloStmt>()
        while (!isAtEnd()) {
            val nodeResult = parseStmt()
            if (nodeResult.isFailure()) return nodeResult.toFailure()
            nodes.add(nodeResult.toSuccessData())
        }
        return LiloResult.Success(data = LiloProgram(nodes))
    }

    private fun parseStmt(): LiloResult<LiloStmt> {
        return when (peek().kind) {
            LiloTokenKind.FROM_KEYWORD -> parseFromImportStmt()
            LiloTokenKind.IMPORT_KEYWORD -> parseImportStmt()
            LiloTokenKind.DEF_KEYWORD -> parseFunctionStmt()
            LiloTokenKind.L_BRACE -> parseBlockStmt()
            else -> parseAssignmentStmt()
        }
    }

    private fun parseFromImportStmt(): LiloResult<LiloStmt> {
        // Advance 'from' keyword
        advance()

        val moduleNameResult =
            expectAndConsume(kind = LiloTokenKind.SYMBOL, "Expect module name after `from`")
        if (moduleNameResult.isFailure()) return moduleNameResult.toFailure()
        val moduleName = moduleNameResult.toSuccessData()

        val importResult = expectAndConsume(
            kind = LiloTokenKind.IMPORT_KEYWORD,
            "Expect `import` keyword `from module`"
        )
        if (importResult.isFailure()) return importResult.toFailure()

        val hasOpenParentheses = isPeek(kind = LiloTokenKind.LPAR)
        if (hasOpenParentheses) {
            // Advance `(`
            advance()
        }

        val importedSymbols = mutableListOf<Pair<String, String?>>()
        do {
            if (isPeek(kind = LiloTokenKind.COMMA)) {
                advance()
            }

            // Parse module name
            val nameResult =
                expectAndConsume(kind = LiloTokenKind.SYMBOL, "Expect symbol name after 'import'")
            if (nameResult.isFailure()) return nameResult.toFailure()
            val name = nameResult.toSuccessData()
            var alias: String? = null
            if (isPeek(kind = LiloTokenKind.AS_KEYWORD)) {
                // Advance 'as' keyword
                advance()

                // Consume alias name
                val aliasResult =
                    expectAndConsume(kind = LiloTokenKind.SYMBOL, "Expect symbol after `as`")
                if (aliasResult.isFailure()) return aliasResult.toFailure()
                alias = aliasResult.toSuccessData().lexeme
            }

            importedSymbols.add(Pair(name.lexeme!!, alias))
        } while (isPeek(kind = LiloTokenKind.COMMA))

        if (hasOpenParentheses) {
            val rightParResult =
                expectAndConsume(kind = LiloTokenKind.RPAR, "Expect `)` after imported symbols")
            if (rightParResult.isFailure()) return rightParResult.toFailure()
        }

        val fromImportStmt = FromImportStmt(module = moduleName.lexeme!!, symbols = importedSymbols)
        return LiloResult.Success(data = fromImportStmt)
    }

    private fun parseImportStmt(): LiloResult<LiloStmt> {
        // Advance 'import' keyword
        advance()

        val modules = mutableListOf<Pair<String, String?>>()
        do {
            if (isPeek(kind = LiloTokenKind.COMMA)) {
                advance()
            }

            // Parse module name
            val nameResult = expectAndConsume(kind = LiloTokenKind.SYMBOL, "Expect function name")
            if (nameResult.isFailure()) return nameResult.toFailure()
            val name = nameResult.toSuccessData()
            var alias: String? = null
            if (isPeek(kind = LiloTokenKind.AS_KEYWORD)) {
                // Advance 'as' keyword
                advance()

                // Consume alias name
                val aliasResult =
                    expectAndConsume(kind = LiloTokenKind.SYMBOL, "Expect symbol after `as`")
                if (aliasResult.isFailure()) return aliasResult.toFailure()
                alias = aliasResult.toSuccessData().lexeme
            }

            modules.add(Pair(name.lexeme!!, alias))
        } while (isPeek(kind = LiloTokenKind.COMMA))

        val importStmt = ImportStmt(modules)
        return LiloResult.Success(data = importStmt)
    }

    private fun parseFunctionStmt(): LiloResult<LiloStmt> {
        // Advance 'def' keyword
        advance()

        val nameResult = expectAndConsume(kind = LiloTokenKind.SYMBOL, "Expect function name")
        if (nameResult.isFailure()) return nameResult.toFailure()
        val name = nameResult.toSuccessData()

        val lParResult =
            expectAndConsume(kind = LiloTokenKind.LPAR, "Expect `(` after function name")
        if (lParResult.isFailure()) return lParResult.toFailure()

        val nodes = mutableListOf<String>()
        while (!isAtEnd() && isPeek(kind =LiloTokenKind.RPAR).not()) {
            val nameResult = expectAndConsume(kind = LiloTokenKind.SYMBOL, "Expect parameter name")
            if (nameResult.isFailure()) return nameResult.toFailure()
            val parameterName = nameResult.toSuccessData()
            nodes.add(parameterName.lexeme!!)

            if (isPeek(kind = LiloTokenKind.COMMA)) {
                advance()
                continue
            }

            break
        }

        val rParResult = expectAndConsume(kind = LiloTokenKind.RPAR, "Expect `)` after paramters")
        if (rParResult.isFailure()) return rParResult.toFailure()

        val bodyResult = parseBlockStmt()
        if (bodyResult.isFailure()) return bodyResult.toFailure()
        val block = bodyResult.toSuccessData() as BlockStmt

        val functionStmt = FunctionStmt(name = name.lexeme!!, params = nodes, body = block.nodes)
        return LiloResult.Success(data = functionStmt)
    }

    private fun parseBlockStmt(): LiloResult<LiloStmt> {
        // Advance '{'
        advance()

        val nodes = mutableListOf<LiloStmt>()
        while (!isAtEnd() && peek().kind != LiloTokenKind.R_BRACE) {
            val nodeResult = parseStmt()
            if (nodeResult.isFailure()) return nodeResult.toFailure()
            nodes.add(nodeResult.toSuccessData())
        }

        run {
            val consumeRes = expectAndConsume(
                kind = LiloTokenKind.R_BRACE,
                message = "expected ']' at end of list"
            )
            if (consumeRes.isFailure()) return consumeRes.toFailure()
        }

        return LiloResult.Success(data = BlockStmt(nodes = nodes))
    }

    private fun parseAssignmentStmt(): LiloResult<LiloStmt> {
        if (peekNext().kind == LiloTokenKind.EQ) {
            val nameResult =
                expectAndConsume(kind = LiloTokenKind.SYMBOL, "Expect symbol on right side of `=`")
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
        return parseIfExpr()
    }

    private fun parseIfExpr(): LiloResult<LiloExpr> {
        val expr = parseAdditiveExpr()
        if (expr.isFailure()) return expr.toFailure()

        if (isPeek(kind = LiloTokenKind.IF_KEYWORD)) {
            // Advance `if`
            advance()

            val conditionResult = parseIfExpr()
            if (conditionResult.isFailure()) return conditionResult.toFailure()

            // Advance `else`
            val expectResult = expectAndConsume(
                kind = LiloTokenKind.ELSE_KEYWORD,
                message = "Expect `else` after `if` value"
            )
            if (expectResult.isFailure()) return expectResult.toFailure()

            val elseResult = parseIfExpr()
            if (elseResult.isFailure()) return elseResult.toFailure()

            val conditionValue = conditionResult.toSuccessData()
            val thenValue = expr.toSuccessData()
            val elseValue = elseResult.toSuccessData()
            return LiloResult.Success(data = IfExpr(conditionValue, thenValue, elseValue))
        }

        return expr
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
            lhs = BinaryExpr(lhs = lhs, op = op, rhs = rhs)
        }

        return LiloResult.Success(data = lhs)
    }

    private fun parseMultiplicativeExpr(): LiloResult<LiloExpr> {
        val lhsResult = parseCallOrGetExpr()
        if (lhsResult.isFailure()) return lhsResult.toFailure()
        var lhs = lhsResult.toSuccessData()

        while (!isAtEnd() && peek().kind.isFactorOperator()) {
            val op = advance()
            val rhsResult = parseCallOrGetExpr()
            if (rhsResult.isFailure()) return rhsResult.toFailure()

            val rhs = rhsResult.toSuccessData()
            lhs = BinaryExpr(lhs = lhs, op = op, rhs = rhs)
        }

        return LiloResult.Success(data = lhs)
    }

    private fun parseCallOrGetExpr(): LiloResult<LiloExpr> {
        val calleeResult = parsePrimaryExpr()
        if (calleeResult.isFailure()) return calleeResult.toFailure()
        var expr = calleeResult.toSuccessData()

        while (true) {
            if (isPeek(kind =LiloTokenKind.LPAR)) {
                // (
                advance()

                val args = mutableListOf<LiloExpr>()
                while (!isAtEnd() && isPeek(LiloTokenKind.RPAR).not()) {
                    val exprResult = parseExpr()
                    if (exprResult.isFailure()) return exprResult.toFailure()
                    args.add(exprResult.toSuccessData())

                    if (isPeek(kind = LiloTokenKind.COMMA)) {
                        advance()
                        continue
                    }

                    break
                }

                run {
                    val consumeRes = expectAndConsume(kind = LiloTokenKind.RPAR, message = "expected ')' at end of call")
                    if (consumeRes.isFailure()) return consumeRes.toFailure()
                }

                expr = CallExpr(callee = expr, args = args)
                continue
            }

            if (isPeek(kind =LiloTokenKind.L_BRACKET)) {
                // (
                advance()

                val indexResult = parseExpr()
                if (indexResult.isFailure()) return indexResult.toFailure()
                val index = indexResult.toSuccessData()

                val consumeRes = expectAndConsume(kind = LiloTokenKind.R_BRACKET, message = "expected ']' after index value")
                if (consumeRes.isFailure()) return consumeRes.toFailure()

                expr = GetItemExpr(obj = expr, index = index)
                continue
            }

            if (isPeek(kind = LiloTokenKind.DOT)) {
                // `.`
                advance()

                // Symbol
                val callResult = expectAndConsume(kind = LiloTokenKind.SYMBOL, message = "expected symbol after `.` operator")
                if (callResult.isFailure()) return callResult.toFailure()

                expr = GetExpr(obj = expr, name = SymbolExpr(value = callResult.toSuccessData()))
                continue
            }

            break
        }

        return LiloResult.Success(data = expr)
    }

    private fun parsePrimaryExpr(): LiloResult<LiloExpr> {
        val token = peek()
        return when (token.kind) {
            LiloTokenKind.SYMBOL -> {
                advance()
                LiloResult.Success(data = SymbolExpr(value = token))
            }

            LiloTokenKind.STR_LITERAL -> {
                advance()
                LiloResult.Success(data = StrExpr(value = token))
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

            LiloTokenKind.NONE_KEYWORD -> {
                advance()
                LiloResult.Success(data = NoneExpr(value = token))
            }

            LiloTokenKind.L_BRACKET -> parseListExpr()
            LiloTokenKind.LPAR -> parseGroupOrTupleExpr()
            else -> createDiagnostic(
                loc = token.loc,
                message = "Unexpected primary expr `${token.kind.name}`"
            )
        }
    }

    private fun parseListExpr(): LiloResult<LiloExpr> {
        // Advance '['
        advance()

        val list = mutableListOf<LiloExpr>()
        while (!isAtEnd() && isPeek(kind = LiloTokenKind.R_BRACKET).not()) {
            val exprResult = parseExpr()
            if (exprResult.isFailure()) return exprResult.toFailure()
            list.add(exprResult.toSuccessData())

            if (isPeek(kind = LiloTokenKind.COMMA)) {
                advance()
                continue
            }

            break
        }

        run {
            val consumeRes = expectAndConsume(
                kind = LiloTokenKind.R_BRACKET,
                message = "expected ']' at end of list"
            )
            if (consumeRes.isFailure()) return consumeRes.toFailure()
        }

        return LiloResult.Success(data = ListExpr(values = list))
    }

    private fun parseGroupOrTupleExpr(): LiloResult<LiloExpr> {
        // Advance '('
        advance()

        var hasComma = false
        val values = mutableListOf<LiloExpr>()
        while (!isAtEnd() && isPeek(kind = LiloTokenKind.RPAR).not()) {
            val exprResult = parseExpr()
            if (exprResult.isFailure()) return exprResult.toFailure()
            val expr = exprResult.toSuccessData()
            values.add(expr)

            if (isPeek(kind = LiloTokenKind.COMMA)) {
                hasComma = true
                advance()
            } else {
                break
            }
        }

        val consumeRes = expectAndConsume(kind = LiloTokenKind.RPAR, message = "expected ')' after group or tuple expr")
        if (consumeRes.isFailure()) return consumeRes.toFailure()

        val expr =
            if (values.size == 1 && !hasComma) GroupExpr(expr = values[0]) else TupleExpr(values = values)
        return LiloResult.Success(data = expr)
    }

    private fun expectAndConsume(kind: LiloTokenKind, message: String): LiloResult<LiloToken> {
        if (peek().kind == kind) {
            advance()
            return LiloResult.Success(data = previous())
        }
        return createDiagnostic(peek().loc, message)
    }

    private fun createDiagnostic(
        loc: LiloLoc,
        message: String
    ): LiloResult.Failure<LiloDiagnostic> {
        val diagnostic = LiloDiagnostic(loc, message)
        return LiloResult.Failure(error = diagnostic)
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

    private fun isPeek(kind: LiloTokenKind) = peek().kind == kind

    private fun previous(): LiloToken {
        return tokens[currentPos - 1]
    }

    private fun isAtEnd() = peek().kind.isEOF()
}
