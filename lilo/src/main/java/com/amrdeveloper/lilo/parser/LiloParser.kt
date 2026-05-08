package com.amrdeveloper.lilo.parser

import com.amrdeveloper.lilo.ast.AssertStmt
import com.amrdeveloper.lilo.ast.AssignStmt
import com.amrdeveloper.lilo.ast.BinaryExpr
import com.amrdeveloper.lilo.ast.BinaryOp
import com.amrdeveloper.lilo.ast.BlockStmt
import com.amrdeveloper.lilo.ast.BoolExpr
import com.amrdeveloper.lilo.ast.CallExpr
import com.amrdeveloper.lilo.ast.ComparisonExpr
import com.amrdeveloper.lilo.ast.ComparisonOp
import com.amrdeveloper.lilo.ast.ComplexExpr
import com.amrdeveloper.lilo.ast.DictExpr
import com.amrdeveloper.lilo.ast.ExprStmt
import com.amrdeveloper.lilo.ast.FloatExpr
import com.amrdeveloper.lilo.ast.FromImportStmt
import com.amrdeveloper.lilo.ast.FunctionStmt
import com.amrdeveloper.lilo.ast.GetExpr
import com.amrdeveloper.lilo.ast.GetItemExpr
import com.amrdeveloper.lilo.ast.GlobalStmt
import com.amrdeveloper.lilo.ast.GroupExpr
import com.amrdeveloper.lilo.ast.IfExpr
import com.amrdeveloper.lilo.ast.IfStmt
import com.amrdeveloper.lilo.ast.ImportStmt
import com.amrdeveloper.lilo.ast.IntExpr
import com.amrdeveloper.lilo.ast.LambdaExpr
import com.amrdeveloper.lilo.ast.LiloExpr
import com.amrdeveloper.lilo.ast.LiloProgram
import com.amrdeveloper.lilo.ast.LiloStmt
import com.amrdeveloper.lilo.ast.ListExpr
import com.amrdeveloper.lilo.ast.NonLocalStmt
import com.amrdeveloper.lilo.ast.NoneExpr
import com.amrdeveloper.lilo.ast.PassStmt
import com.amrdeveloper.lilo.ast.ReturnStmt
import com.amrdeveloper.lilo.ast.SetExpr
import com.amrdeveloper.lilo.ast.StrExpr
import com.amrdeveloper.lilo.ast.SymbolExpr
import com.amrdeveloper.lilo.ast.TupleExpr
import com.amrdeveloper.lilo.ast.UnaryExpr
import com.amrdeveloper.lilo.ast.WhileStmt
import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.valueOr

class LiloParser(val tokens: List<LiloToken>) {

    private var currentPos: Int = 0

    fun parse(): LiloResult<LiloProgram> {
        val nodes = mutableListOf<LiloStmt>()
        while (!isAtEnd()) {
            val stmt = parseStmt().valueOr { return it.toFailure() }
            nodes.add(stmt)
        }
        return LiloResult.Success(data = LiloProgram(nodes))
    }

    private fun parseStmt(): LiloResult<LiloStmt> {
        return when (peek().kind) {
            LiloTokenKind.FROM_KEYWORD -> parseFromImportStmt()
            LiloTokenKind.IMPORT_KEYWORD -> parseImportStmt()
            LiloTokenKind.DEF_KEYWORD -> parseFunctionStmt()
            LiloTokenKind.GLOBAL_KEYWORD -> parseGlobalStmt()
            LiloTokenKind.NON_LOCAL_KEYWORD -> parseNonLocalStmt()
            LiloTokenKind.IF_KEYWORD -> parseIfStmt()
            LiloTokenKind.WHILE_KEYWORD -> parseWhileStmt()
            LiloTokenKind.L_BRACE -> parseBlockStmt()
            LiloTokenKind.RETURN_KEYWORD -> parseReturnStmt()
            LiloTokenKind.ASSERT_KEYWORD -> parseAssertStmt()
            LiloTokenKind.PASS_KEYWORD -> parsePassStmt()
            else -> parseAssignmentStmt()
        }
    }

    private fun parseDotConnectedNames(): LiloResult<List<String>> {
        val names = mutableListOf<String>()

        do {
            val name = expectAndConsume(
                kind = LiloTokenKind.SYMBOL,
                message = "Expect module name"
            ).valueOr { return it.toFailure() }
            names.add(name.lexeme!!)
        } while (match(kind = LiloTokenKind.DOT))

        return LiloResult.Success(data = names)
    }

    private fun parseFromImportStmt(): LiloResult<FromImportStmt> {
        // Advance 'from' keyword
        advance()

        // Consume module name
        val moduleName = parseDotConnectedNames().valueOr { return it.toFailure() }

        // Consume `import` keyword
        expectAndConsume(
            kind = LiloTokenKind.IMPORT_KEYWORD,
            message = "Expect `import` keyword `from module`"
        ).valueOr { return it.toFailure() }

        val hasOpenParentheses = match(kind = LiloTokenKind.LPAR)

        // from <module> import *
        if (isPeek(kind = LiloTokenKind.STAR)) {
            if (hasOpenParentheses) {
                return createDiagnostic(
                    loc = peek().loc,
                    message = "`(`, `)` can't be used with import *"
                )
            }

            // Advance `*`
            advance()

            consumeOptional(kind = LiloTokenKind.SEMICOLON)
            val fromImportStmt = FromImportStmt(module = moduleName)
            return LiloResult.Success(data = fromImportStmt)
        }

        val importedSymbols = mutableListOf<Pair<String, String?>>()
        do {
            // Parse module name
            val name =
                expectAndConsume(
                    kind = LiloTokenKind.SYMBOL,
                    message = "Expect symbol name after 'import'"
                ).valueOr { return it.toFailure() }

            var alias: String? = null
            if (match(kind = LiloTokenKind.AS_KEYWORD)) {
                // Consume alias name
                alias =
                    expectAndConsume(
                        kind = LiloTokenKind.SYMBOL,
                        message = "Expect symbol after `as`"
                    ).valueOr { return it.toFailure() }.lexeme
            }

            importedSymbols.add(Pair(name.lexeme!!, alias))
        } while (match(kind = LiloTokenKind.COMMA))

        if (hasOpenParentheses) {
            expectAndConsume(
                kind = LiloTokenKind.RPAR,
                message = "Expect `)` after imported symbols"
            ).valueOr { return it.toFailure() }
        }

        consumeOptional(kind = LiloTokenKind.SEMICOLON)

        val fromImportStmt = FromImportStmt(module = moduleName, symbols = importedSymbols)
        return LiloResult.Success(data = fromImportStmt)
    }

    private fun parseImportStmt(): LiloResult<ImportStmt> {
        // Advance 'import' keyword
        advance()

        val modules = mutableListOf<Pair<List<String>, String?>>()
        do {
            // Parse module names
            val names = parseDotConnectedNames().valueOr { return it.toFailure() }

            var alias: String? = null
            if (match(kind = LiloTokenKind.AS_KEYWORD)) {
                // Consume alias name
                alias = expectAndConsume(
                    kind = LiloTokenKind.SYMBOL,
                    message = "Expect symbol after `as`"
                ).valueOr { return it.toFailure() }.lexeme
            }

            modules.add(Pair(names, alias))
        } while (match(kind = LiloTokenKind.COMMA))

        consumeOptional(kind = LiloTokenKind.SEMICOLON)
        return LiloResult.Success(data = ImportStmt(modules))
    }

    private fun parseFunctionStmt(): LiloResult<FunctionStmt> {
        // Advance 'def' keyword
        advance()

        val name = expectAndConsume(
            kind = LiloTokenKind.SYMBOL,
            message = "Expect function name"
        ).valueOr { return it.toFailure() }

        expectAndConsume(
            kind = LiloTokenKind.LPAR,
            message = "Expect `(` after function name"
        ).valueOr { return it.toFailure() }

        val nodes = mutableListOf<String>()
        while (!isAtEnd() && isPeek(kind = LiloTokenKind.RPAR).not()) {
            val parameter = expectAndConsume(
                kind = LiloTokenKind.SYMBOL,
                message = "Expect parameter name"
            ).valueOr { return it.toFailure() }

            nodes.add(parameter.lexeme!!)

            consumeCommaOr { break }
        }

        expectAndConsume(
            kind = LiloTokenKind.RPAR,
            message = "Expect `)` after parameters"
        ).valueOr { return it.toFailure() }

        val block = parseBlockStmt().valueOr { return it.toFailure() }
        val functionStmt = FunctionStmt(name = name.lexeme!!, params = nodes, body = block.nodes)
        return LiloResult.Success(data = functionStmt)
    }

    private fun parseGlobalStmt(): LiloResult<GlobalStmt> {
        // Advance `global` keyword
        advance()

        val names = mutableListOf<String>()
        while (!isAtEnd()) {
            val name = expectAndConsume(
                kind = LiloTokenKind.SYMBOL,
                message = "Expect `Name` after global"
            ).valueOr { return it.toFailure() }.lexeme!!
            names.add(name)

            consumeCommaOr { break }
        }
        return LiloResult.Success(data = GlobalStmt(names))
    }

    private fun parseNonLocalStmt(): LiloResult<NonLocalStmt> {
        // Advance `nonlocal` keyword
        advance()

        val names = mutableListOf<String>()
        while (!isAtEnd()) {
            val name = expectAndConsume(
                kind = LiloTokenKind.SYMBOL,
                message = "Expect `Name` after nonlocal"
            ).valueOr { return it.toFailure() }.lexeme!!
            names.add(name)

            consumeCommaOr { break }
        }
        return LiloResult.Success(data = NonLocalStmt(names))
    }

    private fun parseIfStmt(): LiloResult<IfStmt> {
        // Advance 'if' keyword
        advance()

        val condition = parseExpr().valueOr { return it.toFailure() }
        val body = parseStmt().valueOr { return it.toFailure() }

        val ifs = mutableListOf<Pair<LiloExpr, LiloStmt>>()
        ifs.add(condition to body)

        // Parse zero or multiples else if statements
        while (match(kind = LiloTokenKind.ELIF_KEYWORD)) {
            val elifCondition = parseExpr().valueOr { return it.toFailure() }
            val elifBody = parseStmt().valueOr { return it.toFailure() }
            ifs.add(elifCondition to elifBody)
        }

        // Parse `else` body
        var elseBlock: LiloStmt? = null
        if (match(kind = LiloTokenKind.ELSE_KEYWORD)) {
            elseBlock = parseStmt().valueOr { return it.toFailure() }
        }

        return LiloResult.Success(data = IfStmt(ifs, elseBlock))
    }

    private fun parseWhileStmt(): LiloResult<WhileStmt> {
        // Advance 'while' keyword
        advance()

        val condition = parseExpr().valueOr { return it.toFailure() }
        val body = parseStmt().valueOr { return it.toFailure() }

        var elseBlock: LiloStmt? = null
        if (match(kind = LiloTokenKind.ELSE_KEYWORD)) {
            elseBlock = parseStmt().valueOr { return it.toFailure() }
        }

        return LiloResult.Success(data = WhileStmt(condition, body, elseBlock))
    }

    private fun parseBlockStmt(): LiloResult<BlockStmt> {
        // Advance '{'
        advance()

        val nodes = mutableListOf<LiloStmt>()
        while (!isAtEnd() && peek().kind != LiloTokenKind.R_BRACE) {
            val stmt = parseStmt().valueOr { return it.toFailure() }
            nodes.add(stmt)
        }

        expectAndConsume(
            kind = LiloTokenKind.R_BRACE,
            message = "expected ']' at end of block"
        ).valueOr { return it.toFailure() }

        return LiloResult.Success(data = BlockStmt(nodes = nodes))
    }

    private fun parseReturnStmt(): LiloResult<ReturnStmt> {
        // Advance 'return' keyword
        advance()

        if (match(kind = LiloTokenKind.SEMICOLON)) {
            return LiloResult.Success(data = ReturnStmt())
        }

        if (isPeek(kind = LiloTokenKind.R_BRACE)) {
            return LiloResult.Success(data = ReturnStmt())
        }

        val returnValue = parseCommaSeparatedExpr().valueOr { return it.toFailure() }
        consumeOptional(kind = LiloTokenKind.SEMICOLON)
        return LiloResult.Success(data = ReturnStmt(value = returnValue))
    }

    private fun parseAssertStmt(): LiloResult<AssertStmt> {
        // Advance 'assert' keyword
        advance()

        val test = parseExpr().valueOr { return it.toFailure() }
        var msg: LiloExpr? = null
        if (match(kind = LiloTokenKind.COMMA)) msg = parseExpr().valueOr { return it.toFailure() }
        consumeOptional(kind = LiloTokenKind.SEMICOLON)
        return LiloResult.Success(data = AssertStmt(test, msg))
    }

    private fun parsePassStmt() : LiloResult<PassStmt> {
        // Advance 'pass' keyword
        advance()
        return LiloResult.Success(data = PassStmt())
    }

    private fun parseAssignmentStmt(): LiloResult<LiloStmt> {
        val lhs = parseExpr().valueOr { return it.toFailure() }
        if (match(kind = LiloTokenKind.EQ)) {
            val value = parseExpr().valueOr { return it.toFailure() }
            return LiloResult.Success(data = AssignStmt(lValue = lhs, rValue = value))
        }
        return LiloResult.Success(data = ExprStmt(expr = lhs))
    }

    private fun parseExpr(): LiloResult<LiloExpr> {
        return parseIfExpr()
    }

    // | expr (',' expr )+ [',']
    // | expr
    private fun parseCommaSeparatedExpr() : LiloResult<LiloExpr> {
        val elements = mutableListOf<LiloExpr>()
        val expr = parseExpr().valueOr { return it.toFailure() }
        elements.add(expr)

        while (match(kind = LiloTokenKind.COMMA)) {
            val expr = parseExpr().valueOr { return it.toFailure() }
            elements.add(expr)
        }

        return if (elements.size == 1) {
            LiloResult.Success(data = elements[0])
        } else {
            LiloResult.Success(data = TupleExpr(values = elements))
        }
    }

    private fun parseIfExpr(): LiloResult<LiloExpr> {
        val expr = parseEqualityExpr()

        if (match(kind = LiloTokenKind.IF_KEYWORD)) {
            val thenValue = expr.valueOr { return it.toFailure() }
            val condition = parseIfExpr().valueOr { return it.toFailure() }

            // Advance `else`
            expectAndConsume(
                kind = LiloTokenKind.ELSE_KEYWORD,
                message = "Expect `else` after `if` value"
            ).valueOr { return it.toFailure() }

            val elseValue = parseIfExpr().valueOr { return it.toFailure() }
            return LiloResult.Success(data = IfExpr(condition, thenValue, elseValue))
        }

        return expr
    }

    private fun comparisonOpFromTokenKind(kind: LiloTokenKind): ComparisonOp {
        return when (kind) {
            LiloTokenKind.EQ_EQ -> ComparisonOp.EQ
            LiloTokenKind.BANG_EQ -> ComparisonOp.NE
            LiloTokenKind.GT -> ComparisonOp.GT
            LiloTokenKind.GE -> ComparisonOp.GE
            LiloTokenKind.LT -> ComparisonOp.LT
            LiloTokenKind.LE -> ComparisonOp.LE
            else -> TODO(reason = "Unreachable ComparisonOp")
        }
    }

    private fun parseEqualityExpr(): LiloResult<LiloExpr> {
        var lhs = parseComparisonsExpr().valueOr { return it.toFailure() }
        while (!isAtEnd() && peek().kind.isEqualityOperator()) {
            val op = advance()
            val rhs = parseComparisonsExpr().valueOr { return it.toFailure() }
            val binOp = comparisonOpFromTokenKind(op.kind)
            lhs = ComparisonExpr(lhs = lhs, op = binOp, rhs = rhs)
        }
        return LiloResult.Success(data = lhs)
    }

    private fun parseComparisonsExpr(): LiloResult<LiloExpr> {
        var lhs = parseAdditiveExpr().valueOr { return it.toFailure() }
        while (!isAtEnd() && peek().kind.isComparisonOperator()) {
            val op = advance()
            val rhs = parseAdditiveExpr().valueOr { return it.toFailure() }
            val binOp = comparisonOpFromTokenKind(op.kind)
            lhs = ComparisonExpr(lhs = lhs, op = binOp, rhs = rhs)
        }
        return LiloResult.Success(data = lhs)
    }

    private fun binaryOpFromTokenKind(kind: LiloTokenKind): BinaryOp {
        return when (kind) {
            LiloTokenKind.PLUS -> BinaryOp.PLUS
            LiloTokenKind.MINUS -> BinaryOp.MINUS
            LiloTokenKind.STAR -> BinaryOp.MUL
            LiloTokenKind.SLASH -> BinaryOp.DIV
            LiloTokenKind.MODULO -> BinaryOp.MOD
            else -> TODO(reason = "Unreachable BinaryOp")
        }
    }

    private fun parseAdditiveExpr(): LiloResult<LiloExpr> {
        var lhs = parseMultiplicativeExpr().valueOr { return it.toFailure() }
        while (!isAtEnd() && peek().kind.isTermOperator()) {
            val op = advance()
            val rhs = parseMultiplicativeExpr().valueOr { return it.toFailure() }
            val binOp = binaryOpFromTokenKind(op.kind)
            lhs = BinaryExpr(lhs = lhs, op = binOp, rhs = rhs)
        }
        return LiloResult.Success(data = lhs)
    }

    private fun parseMultiplicativeExpr(): LiloResult<LiloExpr> {
        var lhs = parseUnaryExpr().valueOr { return it.toFailure() }
        while (!isAtEnd() && peek().kind.isFactorOperator()) {
            val op = advance()
            val rhs = parseUnaryExpr().valueOr { return it.toFailure() }
            val binOp = binaryOpFromTokenKind(op.kind)
            lhs = BinaryExpr(lhs = lhs, op = binOp, rhs = rhs)
        }
        return LiloResult.Success(data = lhs)
    }

    private fun parseUnaryExpr(): LiloResult<LiloExpr> {
        if (peek().kind.isUnaryOperator()) {
            val op = advance()
            val expr = parseUnaryExpr().valueOr { return it.toFailure() }
            return LiloResult.Success(data = UnaryExpr(op = op, operand = expr))
        }

        return parseCallOrGetExpr()
    }

    private fun parseCallOrGetExpr(): LiloResult<LiloExpr> {
        var expr = parsePrimaryExpr().valueOr { return it.toFailure() }

        while (true) {
            if (match(kind = LiloTokenKind.LPAR)) {
                val args = mutableListOf<LiloExpr>()
                while (!isAtEnd() && isPeek(LiloTokenKind.RPAR).not()) {
                    val expr = parseExpr().valueOr { return it.toFailure() }
                    args.add(expr)

                    consumeCommaOr { break }
                }

                expectAndConsume(
                    kind = LiloTokenKind.RPAR,
                    message = "expected ')' at end of call"
                ).valueOr { return it.toFailure() }

                expr = CallExpr(callee = expr, args = args)
                continue
            }

            if (match(kind = LiloTokenKind.L_BRACKET)) {
                val slice = parseCommaSeparatedExpr().valueOr { return it.toFailure() }

                expectAndConsume(
                    kind = LiloTokenKind.R_BRACKET,
                    message = "expected ']' after index value"
                ).valueOr { return it.toFailure() }

                expr = GetItemExpr(obj = expr, index = slice)
                continue
            }

            if (match(kind = LiloTokenKind.DOT)) {
                val callSymbol = expectAndConsume(
                    kind = LiloTokenKind.SYMBOL,
                    message = "expected symbol after `.` operator"
                ).valueOr { return it.toFailure() }

                expr = GetExpr(obj = expr, name = SymbolExpr(value = callSymbol))
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
                LiloResult.Success(data = SymbolExpr(value = advance()))
            }

            LiloTokenKind.STR_LITERAL -> {
                LiloResult.Success(data = StrExpr(value = advance()))
            }

            LiloTokenKind.INT_LITERAL -> {
                LiloResult.Success(data = IntExpr(value = advance()))
            }

            LiloTokenKind.FLOAT_LITERAL -> {
                LiloResult.Success(data = FloatExpr(value = advance()))
            }

            LiloTokenKind.COMPLEX_LITERAL -> {
                LiloResult.Success(data = ComplexExpr(value = advance()))
            }

            LiloTokenKind.TRUE_KEYWORD, LiloTokenKind.FALSE_KEYWORD -> {
                LiloResult.Success(data = BoolExpr(value = advance()))
            }

            LiloTokenKind.NONE_KEYWORD -> {
                LiloResult.Success(data = NoneExpr(value = advance()))
            }

            LiloTokenKind.LAMBDA_KEYWORD -> parseLambdaExpr()
            LiloTokenKind.L_BRACE -> parseSetOrDictionaryExpr()
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
            val expr = parseExpr().valueOr { return it.toFailure() }
            list.add(expr)

            consumeCommaOr { break }
        }

        expectAndConsume(
            kind = LiloTokenKind.R_BRACKET,
            message = "expected ']' at end of list"
        ).valueOr { return it.toFailure() }

        return LiloResult.Success(data = ListExpr(values = list))
    }

    private fun parseGroupOrTupleExpr(): LiloResult<LiloExpr> {
        // Advance '('
        advance()

        var hasComma = false
        val values = mutableListOf<LiloExpr>()
        while (!isAtEnd() && isPeek(kind = LiloTokenKind.RPAR).not()) {
            val expr = parseExpr().valueOr { return it.toFailure() }
            values.add(expr)

            hasComma = consumeCommaOr { break }
        }

        expectAndConsume(
            kind = LiloTokenKind.RPAR,
            message = "expected ')' after group or tuple expr"
        ).valueOr { return it.toFailure() }

        val expr =
            if (values.size == 1 && !hasComma) GroupExpr(expr = values[0]) else TupleExpr(values = values)
        return LiloResult.Success(data = expr)
    }

    private fun parseLambdaExpr(): LiloResult<LambdaExpr> {
        // Advance 'lambda'
        advance()

        val parameters = mutableListOf<String>()
        while (!isAtEnd() && isPeek(kind = LiloTokenKind.COLON).not()) {
            val parameter = expectAndConsume(
                kind = LiloTokenKind.SYMBOL,
                message = "expected 'symbol' as lambda parameter name"
            ).valueOr { return it.toFailure() }
            parameters.add(parameter.lexeme!!)

            consumeCommaOr { break }
        }

        expectAndConsume(
            kind = LiloTokenKind.COLON,
            message = "expected ':' after lambda parameters"
        ).valueOr { return it.toFailure() }

        val body = parseExpr().valueOr { return it.toFailure() }

        consumeOptional(kind = LiloTokenKind.SEMICOLON)

        val returnStmt = ReturnStmt(value = body)
        return LiloResult.Success(data = LambdaExpr(params = parameters, body = returnStmt))
    }

    private fun parseSetOrDictionaryExpr(): LiloResult<LiloExpr> {
        // Advance '{'
        advance()

        var isDictionary = false

        val setList = mutableListOf<LiloExpr>()
        val dictPairs = mutableListOf<Pair<LiloExpr, LiloExpr>>()
        while (!isAtEnd() && isPeek(kind = LiloTokenKind.R_BRACE).not()) {
            val key = parseExpr().valueOr { return it.toFailure() }

            // Parse map key and value pairs
            if (match(kind = LiloTokenKind.COLON)) {
                isDictionary = true
                val value = parseExpr().valueOr { return it.toFailure() }
                dictPairs.add(key to value)
            } else if (isDictionary) {
                return createDiagnostic(
                    loc = peek().loc,
                    message = "Expected `:` between the key and value of map element"
                )
            }

            // In case it's not dictionary, register it as set value
            if (isDictionary.not()) setList.add(key)

            consumeCommaOr { break }
        }

        expectAndConsume(
            kind = LiloTokenKind.R_BRACE,
            message = "expected '}' at end of set or dict"
        ).valueOr { return it.toFailure() }

        return if (isDictionary) LiloResult.Success(data = DictExpr(values = dictPairs))
        else LiloResult.Success(data = SetExpr(values = setList))
    }

    private fun expectAndConsume(kind: LiloTokenKind, message: String): LiloResult<LiloToken> {
        return if (match(kind)) LiloResult.Success(data = previous())
        else createDiagnostic(peek().loc, message)
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

    private fun consumeOptional(kind: LiloTokenKind): LiloToken? =
        peek().takeIf { it.kind == kind }?.also { advance() }

    private inline fun consumeCommaOr(or: () -> Nothing): Boolean {
        if (match(kind = LiloTokenKind.COMMA)) {
            return true
        }
        or()
    }

    private fun match(kind: LiloTokenKind): Boolean {
        if (!isAtEnd() && isPeek(kind)) {
            advance()
            return true
        }
        return false
    }

    private fun peek(): LiloToken {
        return tokens[currentPos]
    }

    private fun isPeek(kind: LiloTokenKind) = peek().kind == kind

    private fun previous(): LiloToken {
        return tokens[currentPos - 1]
    }

    private fun isAtEnd() = peek().kind.isEOF()
}
