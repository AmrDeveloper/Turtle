package com.amrdeveloper.lilo.parser

import com.amrdeveloper.lilo.ast.AssertStmt
import com.amrdeveloper.lilo.ast.AssignStmt
import com.amrdeveloper.lilo.ast.BinaryExpr
import com.amrdeveloper.lilo.ast.BinaryOp
import com.amrdeveloper.lilo.ast.BlockStmt
import com.amrdeveloper.lilo.ast.BoolExpr
import com.amrdeveloper.lilo.ast.BreakStmt
import com.amrdeveloper.lilo.ast.CallExpr
import com.amrdeveloper.lilo.ast.ComparisonExpr
import com.amrdeveloper.lilo.ast.ComparisonOp
import com.amrdeveloper.lilo.ast.ComplexExpr
import com.amrdeveloper.lilo.ast.ContinueStmt
import com.amrdeveloper.lilo.ast.DictExpr
import com.amrdeveloper.lilo.ast.ExprStmt
import com.amrdeveloper.lilo.ast.FloatExpr
import com.amrdeveloper.lilo.ast.ForStmt
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
import com.amrdeveloper.lilo.ast.RaiseStmt
import com.amrdeveloper.lilo.ast.ReturnStmt
import com.amrdeveloper.lilo.ast.SetExpr
import com.amrdeveloper.lilo.ast.StrExpr
import com.amrdeveloper.lilo.ast.SymbolExpr
import com.amrdeveloper.lilo.ast.TupleExpr
import com.amrdeveloper.lilo.ast.UnaryExpr
import com.amrdeveloper.lilo.ast.WhileStmt
import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.valueOr

/// Parser for the Lilo Programming Language
///
/// Reference:
///   - https://docs.python.org/3.15/reference/grammar.html
///
class LiloParser(val tokens: List<LiloToken>) {

    private var currentPos: Int = 0

    // statements: statement+
    fun parse(): LiloResult<LiloProgram> {
        val nodes = mutableListOf<LiloStmt>()
        while (!isAtEnd()) {
            match(kind = LiloTokenKind.NEW_LINE)
            val stmt = parseStmt().valueOr { return it.toFailure() }
            nodes.add(stmt)
        }
        return LiloResult.Success(data = LiloProgram(nodes))
    }

    private fun parseStmt(): LiloResult<LiloStmt> {
        return parseCompoundStmt()
    }

    // simple_stmt:
    //    | function_def
    //    | if_stmt
    //    | for_stmt
    //    | while_stmt
    private fun parseCompoundStmt() : LiloResult<LiloStmt> {
        return when (peek().kind) {
            LiloTokenKind.DEF_KEYWORD -> parseFunctionDefStmt()
            LiloTokenKind.IF_KEYWORD -> parseIfStmt()
            LiloTokenKind.FOR_KEYWORD -> parseForStmt()
            LiloTokenKind.WHILE_KEYWORD -> parseWhileStmt()
            else -> parseSimpleStmt()
        }
    }

    // simple_stmt:
    //    | assignment
    //    | import_stmt
    //    | return_stmt
    //    | raise_stmt
    //    | pass_stmt
    //    | yield_stmt
    //    | assert_stmt
    //    | break_stmt
    //    | continue_stmt
    //    | global_stmt
    //    | nonlocal_stmt
    private fun parseSimpleStmt() : LiloResult<LiloStmt> {
        val simpleStmtRes =  when (peek().kind) {
            LiloTokenKind.FROM_KEYWORD -> parseFromImportStmt()
            LiloTokenKind.IMPORT_KEYWORD -> parseImportStmt()
            LiloTokenKind.RETURN_KEYWORD -> parseReturnStmt()
            LiloTokenKind.PASS_KEYWORD -> parsePassStmt()

            LiloTokenKind.RAISE_KEYWORD -> parseRaiseStmt()
            LiloTokenKind.ASSERT_KEYWORD -> parseAssertStmt()

            LiloTokenKind.BREAK_KEYWORD -> parseBreakStmt()
            LiloTokenKind.CONTINUE_KEYWORD -> parseContinueStmt()

            LiloTokenKind.GLOBAL_KEYWORD -> parseGlobalStmt()
            LiloTokenKind.NON_LOCAL_KEYWORD -> parseNonLocalStmt()
            else -> parseAssignmentStmt()
        }
        if (simpleStmtRes.isFailure()) return simpleStmtRes
        consumeSemiOrNewline()
        return simpleStmtRes
    }

    private fun parseDotConnectedNames(): LiloResult<List<String>> {
        val names = mutableListOf<String>()

        do {
            val name = expectAndConsume(
                kind = LiloTokenKind.NAME,
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

        val hasOpenParentheses = match(kind = LiloTokenKind.L_PAR)

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

            consumeOptionalSemi()
            val fromImportStmt = FromImportStmt(module = moduleName)
            return LiloResult.Success(data = fromImportStmt)
        }

        val importedSymbols = mutableListOf<Pair<String, String?>>()
        do {
            // Parse module name
            val name =
                expectAndConsume(
                    kind = LiloTokenKind.NAME,
                    message = "Expect symbol name after 'import'"
                ).valueOr { return it.toFailure() }

            var alias: String? = null
            if (match(kind = LiloTokenKind.AS_KEYWORD)) {
                // Consume alias name
                alias =
                    expectAndConsume(
                        kind = LiloTokenKind.NAME,
                        message = "Expect symbol after `as`"
                    ).valueOr { return it.toFailure() }.lexeme
            }

            importedSymbols.add(Pair(name.lexeme!!, alias))
        } while (match(kind = LiloTokenKind.COMMA))

        if (hasOpenParentheses) {
            expectAndConsume(
                kind = LiloTokenKind.R_PAR,
                message = "Expect `)` after imported symbols"
            ).valueOr { return it.toFailure() }
        }

        consumeOptionalSemi()

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
                    kind = LiloTokenKind.NAME,
                    message = "Expect symbol after `as`"
                ).valueOr { return it.toFailure() }.lexeme
            }

            modules.add(Pair(names, alias))
        } while (match(kind = LiloTokenKind.COMMA))

        consumeOptionalSemi()
        return LiloResult.Success(data = ImportStmt(modules))
    }

    private fun parseFunctionDefStmt(): LiloResult<FunctionStmt> {
        // Advance 'def' keyword
        advance()

        val name = expectAndConsume(
            kind = LiloTokenKind.NAME,
            message = "Expect function name"
        ).valueOr { return it.toFailure() }

        expectAndConsume(
            kind = LiloTokenKind.L_PAR,
            message = "Expect `(` after function name"
        ).valueOr { return it.toFailure() }

        val nodes = mutableListOf<String>()
        loop@ while (!isAtEnd() && isPeek(kind = LiloTokenKind.R_PAR).not()) {
            val parameter = expectAndConsume(
                kind = LiloTokenKind.NAME,
                message = "Expect parameter name"
            ).valueOr { return it.toFailure() }
            nodes.add(parameter.lexeme!!)

            consumeCommaOr { break@loop }
        }

        expectAndConsume(
            kind = LiloTokenKind.R_PAR,
            message = "Expect `)` after parameters"
        ).valueOr { return it.toFailure() }

        consumeOr(kind = LiloTokenKind.COLON) {
            return createDiagnostic(peek().loc, message = "Expected `:` after function parameters")
        }

        val block = parseBlockStmt().valueOr { return it.toFailure() }
        val functionStmt = FunctionStmt(name = name.lexeme!!, params = nodes, body = block)
        return LiloResult.Success(data = functionStmt)
    }

    private fun parseGlobalStmt(): LiloResult<GlobalStmt> {
        // Advance `global` keyword
        advance()

        val names = mutableListOf<String>()
        loop@ while (!isAtEnd()) {
            val name = expectAndConsume(
                kind = LiloTokenKind.NAME,
                message = "Expect `Name` after global"
            ).valueOr { return it.toFailure() }.lexeme!!
            names.add(name)

            consumeCommaOr { break@loop }
        }
        consumeOptionalSemi()
        return LiloResult.Success(data = GlobalStmt(names))
    }

    private fun parseNonLocalStmt(): LiloResult<NonLocalStmt> {
        // Advance `nonlocal` keyword
        advance()

        val names = mutableListOf<String>()
        loop@ while (!isAtEnd()) {
            val name = expectAndConsume(
                kind = LiloTokenKind.NAME,
                message = "Expect `Name` after nonlocal"
            ).valueOr { return it.toFailure() }.lexeme!!
            names.add(name)

            consumeCommaOr { break@loop }
        }
        consumeOptionalSemi()
        return LiloResult.Success(data = NonLocalStmt(names))
    }

    // if_stmt:
    //    | 'if' named_expression ':' block elif_stmt
    //    | 'if' named_expression ':' block [else_block]
    // elif_stmt:
    //    | 'elif' named_expression ':' block elif_stmt
    //    | 'elif' named_expression ':' block [else_block]
    //  else_block:
    //    | 'else' ':' block
    private fun parseIfStmt(): LiloResult<IfStmt> {
        // Advance 'if' keyword
        advance()

        val condition = parseExpr().valueOr { return it.toFailure() }
        consumeOr(kind = LiloTokenKind.COLON) {
            return createDiagnostic(peek().loc, message = "Expected `:` after if condition")
        }
        val body = parseBlockStmt().valueOr { return it.toFailure() }

        val ifs = mutableListOf<Pair<LiloExpr, LiloStmt>>()
        ifs.add(condition to body)

        // Parse zero or multiples else if statements
        while (match(kind = LiloTokenKind.ELIF_KEYWORD)) {
            val elifCondition = parseExpr().valueOr { return it.toFailure() }
            consumeOr(kind = LiloTokenKind.COLON) {
                return createDiagnostic(peek().loc, message = "Expected `:` after elif condition")
            }

            val elifBody = parseBlockStmt().valueOr { return it.toFailure() }
            ifs.add(elifCondition to elifBody)
        }

        // Parse `else` body
        var elseBlock: LiloStmt? = null
        if (match(kind = LiloTokenKind.ELSE_KEYWORD)) {
            consumeOr(kind = LiloTokenKind.COLON) {
                return createDiagnostic(peek().loc, message = "Expected `:` after else")
            }
            elseBlock = parseBlockStmt().valueOr { return it.toFailure() }
        }

        return LiloResult.Success(data = IfStmt(ifs, elseBlock))
    }

    // | 'for' star_targets 'in' ~ star_expressions ':' block [else_block]
    private fun parseForStmt() : LiloResult<ForStmt> {
        // Advance 'for' keyword
        advance()

        // TODO: Support multiple targets
        val target = parseExpr().valueOr { return it.toFailure() }

        expectAndConsume(
            kind = LiloTokenKind.IN_KEYWORD,
            message = "Expect `in` after for target"
        ).valueOr { return it.toFailure() }

        val iter = parseExpr().valueOr { return it.toFailure() }
        consumeOr(kind = LiloTokenKind.COLON) {
            return createDiagnostic(peek().loc, message = "Expected `:` after for iter")
        }

        val body = parseBlockStmt().valueOr { return it.toFailure() }

        var elseBlock: LiloStmt? = null
        if (match(kind = LiloTokenKind.ELSE_KEYWORD)) {
            consumeOr(kind = LiloTokenKind.COLON) {
                return createDiagnostic(peek().loc, message = "Expected `:` after for-else")
            }
            elseBlock = parseBlockStmt().valueOr { return it.toFailure() }
        }

        return LiloResult.Success(data = ForStmt(target, iter, body, elseBlock))
    }

    // while_stmt:
    //    | 'while' named_expression ':' block [else_block]
    private fun parseWhileStmt(): LiloResult<WhileStmt> {
        // Advance 'while' keyword
        advance()

        val condition = parseExpr().valueOr { return it.toFailure() }

        consumeOr(kind = LiloTokenKind.COLON) {
            return createDiagnostic(peek().loc, message = "Expected `:` after while condition")
        }

        val body = parseBlockStmt().valueOr { return it.toFailure() }

        var elseBlock: LiloStmt? = null
        if (match(kind = LiloTokenKind.ELSE_KEYWORD)) {
            consumeOr(kind = LiloTokenKind.COLON) {
                return createDiagnostic(peek().loc, message = "Expected `:` after function parameters")
            }

            elseBlock = parseBlockStmt().valueOr { return it.toFailure() }
        }

        return LiloResult.Success(data = WhileStmt(condition, body, elseBlock))
    }

    // block:
    //    | NEWLINE INDENT statements DEDENT
    //    | simple_stmts
    private fun parseBlockStmt(): LiloResult<LiloStmt> {
        // simple_stmts
        if (match(kind = LiloTokenKind.NEW_LINE).not()) {
            return parseSimpleStmt()
        }

        expectAndConsume(
            kind = LiloTokenKind.INDENT,
            message = "expected 'indent' before block"
        ).valueOr { return it.toFailure() }

        val nodes = mutableListOf<LiloStmt>()
        while (!isAtEnd() && peek().kind != LiloTokenKind.DEDENT) {
            val stmt = parseStmt().valueOr { return it.toFailure() }
            nodes.add(stmt)
        }

        if (!isPeek(kind = LiloTokenKind.DEDENT) && !isPeek(kind = LiloTokenKind.END_MARKER)) {
            return createDiagnostic(peek().loc, message = "expected 'DEDENT' at end of block")
        }

        // Consume `DEDENT` or `END_MARKER`
        advance()
        return LiloResult.Success(data = BlockStmt(nodes = nodes))
    }

    private fun parseReturnStmt(): LiloResult<ReturnStmt> {
        // Advance 'return' keyword
        advance()

        if (isPeek(kind = LiloTokenKind.SEMI)
            || isPeek(kind = LiloTokenKind.NEW_LINE)
            || isPeek(kind = LiloTokenKind.END_MARKER)) {
            advance()
            return LiloResult.Success(data = ReturnStmt())
        }

        val returnValue = parseCommaSeparatedExpr().valueOr { return it.toFailure() }
        consumeOptionalSemi()
        return LiloResult.Success(data = ReturnStmt(value = returnValue))
    }

    // | 'raise' expression 'from' expression
    // | 'raise' expression
    private fun parseRaiseStmt(): LiloResult<RaiseStmt> {
        // Advance 'raise' keyword
        advance()
        val exc = parseExpr().valueOr { return it.toFailure() }
        var cause : LiloExpr? = null
        if (match(kind = LiloTokenKind.FROM_KEYWORD)) {
            cause = parseExpr().valueOr { return it.toFailure() }
        }
        consumeOptionalSemi()
        return LiloResult.Success(data = RaiseStmt(exc, cause))
    }

    private fun parseAssertStmt(): LiloResult<AssertStmt> {
        // Advance 'assert' keyword
        advance()

        val test = parseExpr().valueOr { return it.toFailure() }
        var msg: LiloExpr? = null
        if (match(kind = LiloTokenKind.COMMA)) msg = parseExpr().valueOr { return it.toFailure() }
        consumeOptionalSemi()
        return LiloResult.Success(data = AssertStmt(test, msg))
    }

    private fun parseBreakStmt() : LiloResult<BreakStmt> {
        // Advance 'break' keyword
        advance()
        consumeOptionalSemi()
        return LiloResult.Success(data = BreakStmt())
    }

    private fun parseContinueStmt() : LiloResult<ContinueStmt> {
        // Advance 'Continue' keyword
        advance()
        consumeOptionalSemi()
        return LiloResult.Success(data = ContinueStmt())
    }

    private fun parsePassStmt() : LiloResult<PassStmt> {
        // Advance 'pass' keyword
        advance()
        consumeOptionalSemi()
        return LiloResult.Success(data = PassStmt())
    }

    private fun parseAssignmentStmt(): LiloResult<LiloStmt> {
        val lhs = parseExpr().valueOr { return it.toFailure() }
        if (match(kind = LiloTokenKind.EQ)) {
            val value = parseExpr().valueOr { return it.toFailure() }
            consumeOptionalSemi()
            return LiloResult.Success(data = AssignStmt(lValue = lhs, rValue = value))
        }
        consumeOptionalSemi()
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
            LiloTokenKind.PERCENT -> BinaryOp.MOD
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
            if (match(kind = LiloTokenKind.L_PAR)) {
                val args = mutableListOf<LiloExpr>()
                loop@ while (!isAtEnd() && isPeek(LiloTokenKind.R_PAR).not()) {
                    val expr = parseExpr().valueOr { return it.toFailure() }
                    args.add(expr)

                    consumeCommaOr { break@loop }
                }

                expectAndConsume(
                    kind = LiloTokenKind.R_PAR,
                    message = "expected ')' at end of call"
                ).valueOr { return it.toFailure() }

                expr = CallExpr(callee = expr, args = args)
                continue
            }

            if (match(kind = LiloTokenKind.L_SQB)) {
                val slice = parseCommaSeparatedExpr().valueOr { return it.toFailure() }

                expectAndConsume(
                    kind = LiloTokenKind.R_SQB,
                    message = "expected ']' after index value"
                ).valueOr { return it.toFailure() }

                expr = GetItemExpr(obj = expr, index = slice)
                continue
            }

            if (match(kind = LiloTokenKind.DOT)) {
                val callNAME = expectAndConsume(
                    kind = LiloTokenKind.NAME,
                    message = "expected symbol after `.` operator"
                ).valueOr { return it.toFailure() }

                expr = GetExpr(obj = expr, name = SymbolExpr(value = callNAME))
                continue
            }

            break
        }

        return LiloResult.Success(data = expr)
    }

    private fun parsePrimaryExpr(): LiloResult<LiloExpr> {
        val token = peek()
        return when (token.kind) {
            LiloTokenKind.NAME -> {
                LiloResult.Success(data = SymbolExpr(value = advance()))
            }

            LiloTokenKind.STRING -> {
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
            LiloTokenKind.L_SQB -> parseListExpr()
            LiloTokenKind.L_PAR -> parseGroupOrTupleExpr()

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
        loop@ while (!isAtEnd() && isPeek(kind = LiloTokenKind.R_SQB).not()) {
            val expr = parseExpr().valueOr { return it.toFailure() }
            list.add(expr)
            consumeCommaOr { break@loop }
        }

        expectAndConsume(
            kind = LiloTokenKind.R_SQB,
            message = "expected ']' at end of list"
        ).valueOr { return it.toFailure() }

        return LiloResult.Success(data = ListExpr(values = list))
    }

    private fun parseGroupOrTupleExpr(): LiloResult<LiloExpr> {
        // Advance '('
        advance()

        var hasComma = false
        val values = mutableListOf<LiloExpr>()
        loop@ while (!isAtEnd() && isPeek(kind = LiloTokenKind.R_PAR).not()) {
            val expr = parseExpr().valueOr { return it.toFailure() }
            values.add(expr)

            hasComma = consumeCommaOr { break@loop }
        }

        expectAndConsume(
            kind = LiloTokenKind.R_PAR,
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
        loop@ while (!isAtEnd() && isPeek(kind = LiloTokenKind.COLON).not()) {
            val parameter = expectAndConsume(
                kind = LiloTokenKind.NAME,
                message = "expected 'symbol' as lambda parameter name"
            ).valueOr { return it.toFailure() }
            parameters.add(parameter.lexeme!!)

            consumeCommaOr { break@loop }
        }

        expectAndConsume(
            kind = LiloTokenKind.COLON,
            message = "expected ':' after lambda parameters"
        ).valueOr { return it.toFailure() }

        val body = parseExpr().valueOr { return it.toFailure() }

        consumeOptionalSemi()

        val returnStmt = ReturnStmt(value = body)
        return LiloResult.Success(data = LambdaExpr(params = parameters, body = returnStmt))
    }

    private fun parseSetOrDictionaryExpr(): LiloResult<LiloExpr> {
        // Advance '{'
        advance()

        var isDictionary = false

        val setList = mutableListOf<LiloExpr>()
        val dictPairs = mutableListOf<Pair<LiloExpr, LiloExpr>>()
        loop@ while (!isAtEnd() && isPeek(kind = LiloTokenKind.R_BRACE).not()) {
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

            consumeCommaOr { break@loop }
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

    private fun consumeOptionalSemi(): LiloToken? =
        peek().takeIf { it.kind == LiloTokenKind.SEMI }?.also { advance() }

    private inline fun consumeOr(kind: LiloTokenKind, or: () -> Nothing): Boolean {
        if (match(kind = kind)) {
            return true
        }
        or()
    }

    private inline fun consumeCommaOr(or: () -> Nothing): Boolean {
        return consumeOr(kind = LiloTokenKind.COMMA) { or() }
    }

    private fun consumeSemiOrNewline(): LiloResult<Unit> {
        if (match(kind = LiloTokenKind.SEMI) || match(kind = LiloTokenKind.NEW_LINE)) {
            return LiloResult.Success(data = Unit)
        }
        return LiloResult.Failure(error = LiloDiagnostic(peek().loc, "Expected `;` or `newline`"))
    }

    private fun match(kind: LiloTokenKind): Boolean {
        if (!isAtEnd() && isPeek(kind)) {
            advance()
            return true
        }
        return false
    }

    private fun peek(): LiloToken = tokens[currentPos]

    private fun isPeek(kind: LiloTokenKind) = peek().kind == kind

    private fun previous(): LiloToken = tokens[currentPos - 1]

    private fun isAtEnd() = peek().kind.isEOF()
}
