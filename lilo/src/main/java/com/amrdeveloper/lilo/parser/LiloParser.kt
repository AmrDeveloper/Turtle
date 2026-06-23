package com.amrdeveloper.lilo.parser

import com.amrdeveloper.lilo.ast.AnnAssignStmt
import com.amrdeveloper.lilo.ast.AssertStmt
import com.amrdeveloper.lilo.ast.AssignStmt
import com.amrdeveloper.lilo.ast.BinaryOpExpr
import com.amrdeveloper.lilo.ast.BinaryOp
import com.amrdeveloper.lilo.ast.BlockStmt
import com.amrdeveloper.lilo.ast.BoolExpr
import com.amrdeveloper.lilo.ast.BoolOp
import com.amrdeveloper.lilo.ast.BoolOpExpr
import com.amrdeveloper.lilo.ast.BreakStmt
import com.amrdeveloper.lilo.ast.CallExpr
import com.amrdeveloper.lilo.ast.ComparisonOpExpr
import com.amrdeveloper.lilo.ast.ComparisonOp
import com.amrdeveloper.lilo.ast.ComplexExpr
import com.amrdeveloper.lilo.ast.ContinueStmt
import com.amrdeveloper.lilo.ast.DelStmt
import com.amrdeveloper.lilo.ast.DictCompExpr
import com.amrdeveloper.lilo.ast.DictExpr
import com.amrdeveloper.lilo.ast.ExceptHandler
import com.amrdeveloper.lilo.ast.ExprStmt
import com.amrdeveloper.lilo.ast.FloatExpr
import com.amrdeveloper.lilo.ast.ForIfClause
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
import com.amrdeveloper.lilo.ast.ListCompExpr
import com.amrdeveloper.lilo.ast.ListExpr
import com.amrdeveloper.lilo.ast.NonLocalStmt
import com.amrdeveloper.lilo.ast.NoneExpr
import com.amrdeveloper.lilo.ast.PassStmt
import com.amrdeveloper.lilo.ast.RaiseStmt
import com.amrdeveloper.lilo.ast.ReturnStmt
import com.amrdeveloper.lilo.ast.SetExpr
import com.amrdeveloper.lilo.ast.StrExpr
import com.amrdeveloper.lilo.ast.NameExpr
import com.amrdeveloper.lilo.ast.Parameter
import com.amrdeveloper.lilo.ast.SetCompExpr
import com.amrdeveloper.lilo.ast.TryStmt
import com.amrdeveloper.lilo.ast.TupleExpr
import com.amrdeveloper.lilo.ast.UnaryOp
import com.amrdeveloper.lilo.ast.UnaryOpExpr
import com.amrdeveloper.lilo.ast.WhileStmt
import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.toFailureError
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
            if (isPeek(kind = LiloTokenKind.NEW_LINE)
                || isPeek(kind = LiloTokenKind.INDENT)
                || isPeek(kind = LiloTokenKind.DEDENT)
            ) {
                advance()
                continue
            }
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
    //    | try_stmt
    //    | while_stmt
    private fun parseCompoundStmt(): LiloResult<LiloStmt> {
        return when (peek().kind) {
            LiloTokenKind.AT -> {
                val decorators = parseDecorators().valueOr { return it.toFailure() }
                if (!isPeek(kind = LiloTokenKind.DEF_KEYWORD)) {
                    return createDiagnostic(
                        loc = peek().loc,
                        message = "Expect `definition` after decorator"
                    )
                }
                parseFunctionDefStmt(decorators)
            }

            LiloTokenKind.DEF_KEYWORD -> parseFunctionDefStmt()
            LiloTokenKind.IF_KEYWORD -> parseIfStmt()
            LiloTokenKind.FOR_KEYWORD -> parseForStmt()
            LiloTokenKind.TRY_KEYWORD -> parseTryStmt()
            LiloTokenKind.WHILE_KEYWORD -> parseWhileStmt()
            else -> parseSimpleStmt()
        }
    }

    private fun parseDecorators(): LiloResult<List<LiloExpr>> {
        val decorators = mutableListOf<LiloExpr>()
        while (match(kind = LiloTokenKind.AT)) {
            val name = parseExpr().valueOr { return it.toFailure() }
            if (name !is NameExpr) {
                return createDiagnostic(
                    loc = peek().loc,
                    message = "Expect `Name` as decorator name"
                )
            }

            expectAndConsume(
                kind = LiloTokenKind.NEW_LINE,
                message = "Expected new line at end of decorator"
            ).valueOr { return it.toFailure() }
            decorators.add(name)
        }
        return LiloResult.Success(data = decorators)
    }

    // simple_stmt:
    //    | assignment
    //    | import_stmt
    //    | return_stmt
    //    | raise_stmt
    //    | pass_stmt
    //    | del_stmt
    //    | yield_stmt
    //    | assert_stmt
    //    | break_stmt
    //    | continue_stmt
    //    | global_stmt
    //    | nonlocal_stmt
    private fun parseSimpleStmt(): LiloResult<LiloStmt> {
        val simpleStmtRes = when (peek().kind) {
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
            LiloTokenKind.DEL_KEYWORD -> parseDelStmt()
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
            ).valueOr { return it.toFailure() }.lexeme!!
            names.add(name)
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

    // function_def_raw:
    //    | 'def' NAME [type_params] '(' [params] ')' ['->' expression ] ':'  block
    private fun parseFunctionDefStmt(decorators: List<LiloExpr> = emptyList()): LiloResult<FunctionStmt> {
        // Advance 'def' keyword
        advance()

        val name = expectAndConsume(
            kind = LiloTokenKind.NAME,
            message = "Expect function name"
        ).valueOr { return it.toFailure() }.lexeme!!

        expectAndConsume(
            kind = LiloTokenKind.L_PAR,
            message = "Expect `(` after function name"
        ).valueOr { return it.toFailure() }

        val parameters = mutableListOf<Parameter>()
        loop@ while (isPeek(kind = LiloTokenKind.R_PAR).not()) {
            val isOut = match(kind = LiloTokenKind.OUT_KEYWORD)

            val name = expectAndConsume(
                kind = LiloTokenKind.NAME,
                message = "Expect parameter name"
            ).valueOr { return it.toFailure() }.lexeme!!

            var type: LiloExpr? = null
            if (match(kind = LiloTokenKind.COLON)) {
                type = parseExpr().valueOr { return it.toFailure() }
            }

            parameters.add(Parameter(name, type, isOut))
            consumeCommaOr { break@loop }
        }

        expectAndConsume(
            kind = LiloTokenKind.R_PAR,
            message = "Expect `)` after parameters"
        ).valueOr { return it.toFailure() }

        // -> <Expr>
        var returns : LiloExpr? = null
        if (match(kind = LiloTokenKind.R_ARROW)) {
            returns = parseExpr().valueOr { return it.toFailure() }
        }

        consumeOr(kind = LiloTokenKind.COLON) {
            return createDiagnostic(peek().loc, message = "Expected `:` after function parameters")
        }

        val body = parseBlockStmt().valueOr { return it.toFailure() }
        val functionStmt = FunctionStmt(name, parameters, body, decorators, returns)
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

    private fun parseDelStmt() : LiloResult<DelStmt> {
        // Advance `del` keyword
        advance()

        val names = mutableListOf<String>()
        loop@ while (!isAtEnd()) {
            val name = expectAndConsume(
                kind = LiloTokenKind.NAME,
                message = "Expect `Name` after del statement"
            ).valueOr { return it.toFailure() }.lexeme!!
            names.add(name)

            consumeCommaOr { break@loop }
        }
        consumeOptionalSemi()
        return LiloResult.Success(data = DelStmt(names))
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

    // for_stmt
    //    | 'for' star_targets 'in' ~ star_expressions ':' block [else_block]
    private fun parseForStmt(): LiloResult<ForStmt> {
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

    // try_stmt:
    //    | 'try' ':' block finally_block
    //    | 'try' ':' block except_block+ [else_block] [finally_block]
    //    | 'try' ':' block except_star_block+ [else_block] [finally_block]
    // except_block:
    //    | 'except' expression ':' block
    //    | 'except' expression 'as' NAME ':' block
    //    | 'except' expressions ':' block
    //    | 'except' ':' block
    // finally_block:
    //    | 'finally' ':' block
    private fun parseTryStmt(): LiloResult<TryStmt> {
        // Advance 'try' keyword
        advance()

        // Advance ':'
        expectAndConsume(
            kind = LiloTokenKind.COLON,
            message = "expected ':' after `try` keyword"
        ).valueOr { return it.toFailure() }

        val tryBlock = parseBlockStmt().valueOr { return it.toFailureError() }
        val handlers = parseExceptHandlers().valueOr { return it.toFailure() }

        // Else block
        var elseBlock : LiloStmt? = null
        if (match(kind = LiloTokenKind.ELSE_KEYWORD)) {
            // Advance ':'
            expectAndConsume(
                kind = LiloTokenKind.COLON,
                message = "expected ':' after `finally` keyword"
            ).valueOr { return it.toFailure() }
            elseBlock = parseBlockStmt().valueOr { return it.toFailureError() }
        }

        // Finally block
        var finallyBody : LiloStmt? = null
        if (match(kind = LiloTokenKind.FINALLY_KEYWORD)) {
            // Advance ':'
            expectAndConsume(
                kind = LiloTokenKind.COLON,
                message = "expected ':' after `finally` keyword"
            ).valueOr { return it.toFailure() }
            finallyBody = parseBlockStmt().valueOr { return it.toFailureError() }
        }

        return LiloResult.Success(data = TryStmt(tryBlock, handlers, elseBlock = elseBlock, finallyBody))
    }

    // except_block:
    //    | 'except' expression ':' block
    //    | 'except' expression 'as' NAME ':' block
    //    | 'except' expressions ':' block
    //    | 'except' ':' block
    private fun parseExceptHandlers(): LiloResult<List<ExceptHandler>> {
        val handlers = mutableListOf<ExceptHandler>()
        var hasCatchAllHandler = false
        while (match(kind = LiloTokenKind.EXCEPT_KEYWORD)) {
           // Catch all
            if (match(kind = LiloTokenKind.COLON)) {
                if (hasCatchAllHandler) return createDiagnostic(peek().loc, message = "Only one catch-all except clause allowed")
                hasCatchAllHandler = true
                val catchAllHandler = parseBlockStmt().valueOr { return it.toFailure() }
                handlers.add(ExceptHandler(body = catchAllHandler))
                continue
            }

            // Catch handler with type, body and optional name alias
            val type = parseExpr().valueOr { return it.toFailure() }
            var name : String? = null
            if (match(kind = LiloTokenKind.AS_KEYWORD)) {
                name = expectAndConsume(
                    kind = LiloTokenKind.NAME,
                    message = "expected 'name' after except `as` keyword"
                ).valueOr { return it.toFailure() }.lexeme!!
            }

            expectAndConsume(
                kind = LiloTokenKind.COLON,
                message = "expected ':' before `except` body"
            ).valueOr { return it.toFailure() }

            val handlerBody = parseBlockStmt().valueOr { return it.toFailure() }
            handlers.add(ExceptHandler(type, name, handlerBody))
        }
        return LiloResult.Success(data = handlers)
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
                return createDiagnostic(
                    peek().loc,
                    message = "Expected `:` after function parameters"
                )
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

    // return_stmt:
    //    | 'return' [star_expressions]
    private fun parseReturnStmt(): LiloResult<ReturnStmt> {
        // Advance 'return' keyword
        advance()

        if (isPeek(kind = LiloTokenKind.SEMI)
            || isPeek(kind = LiloTokenKind.NEW_LINE)
            || isPeek(kind = LiloTokenKind.DEDENT)
            || isPeek(kind = LiloTokenKind.END_MARKER)
        ) {
            advance()
            return LiloResult.Success(data = ReturnStmt())
        }

        val returnValue = parseCommaSeparatedExpr().valueOr { return it.toFailure() }
        consumeOptionalSemi()
        return LiloResult.Success(data = ReturnStmt(value = returnValue))
    }

    //  raise_stmt:
    //    | 'raise' expression 'from' expression
    //    | 'raise' expression
    //    | 'raise'
    private fun parseRaiseStmt(): LiloResult<RaiseStmt> {
        // Advance 'raise' keyword
        advance()

        if (isPeek(kind = LiloTokenKind.SEMI)
            || isPeek(kind = LiloTokenKind.NEW_LINE)
            || isPeek(kind = LiloTokenKind.DEDENT)
            || isPeek(kind = LiloTokenKind.END_MARKER)
        ) {
            advance()
            return LiloResult.Success(data = RaiseStmt())
        }

        val exc = parseExpr().valueOr { return it.toFailure() }
        var cause: LiloExpr? = null
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

    private fun parseBreakStmt(): LiloResult<BreakStmt> {
        // Advance 'break' keyword
        advance()
        consumeOptionalSemi()
        return LiloResult.Success(data = BreakStmt())
    }

    private fun parseContinueStmt(): LiloResult<ContinueStmt> {
        // Advance 'Continue' keyword
        advance()
        consumeOptionalSemi()
        return LiloResult.Success(data = ContinueStmt())
    }

    private fun parsePassStmt(): LiloResult<PassStmt> {
        // Advance 'pass' keyword
        advance()

        if (isPeek(kind = LiloTokenKind.SEMI)
            || isPeek(kind = LiloTokenKind.NEW_LINE)
            || isPeek(kind = LiloTokenKind.DEDENT)
            || isPeek(kind = LiloTokenKind.END_MARKER)
        ) {
            advance()
        }

        return LiloResult.Success(data = PassStmt())
    }

    //  assign_stmt:
    //    | NAME '=' expression
    //    | NAME ':' expression '=' expression
    private fun parseAssignmentStmt(): LiloResult<LiloStmt> {
        val lhs = parseExpr().valueOr { return it.toFailure() }

        // Annotated assignment
        if (match(kind = LiloTokenKind.COLON)) {
            val annotation = parseExpr().valueOr { return it.toFailure() }
            expectAndConsume(
                kind = LiloTokenKind.EQ,
                message = "expected '=' after annotated variable"
            ).valueOr { return it.toFailure() }

            val value = parseExpr().valueOr { return it.toFailure() }
            consumeOptionalSemi()
            return LiloResult.Success(data = AnnAssignStmt(target = lhs, annotation, value = value))
        }

        // Assignment
        if (match(kind = LiloTokenKind.EQ)) {
            val value = parseExpr().valueOr { return it.toFailure() }
            consumeOptionalSemi()
            return LiloResult.Success(data = AssignStmt(target = lhs, value = value))
        }

        consumeOptionalSemi()
        return LiloResult.Success(data = ExprStmt(expr = lhs))
    }

    private fun parseExpr(): LiloResult<LiloExpr> {
        return parseIfExpr()
    }

    // | expr (',' expr )+ [',']
    // | expr
    private fun parseCommaSeparatedExpr(): LiloResult<LiloExpr> {
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
        val expr = parseDisjunctionExpr()

        if (match(kind = LiloTokenKind.IF_KEYWORD)) {
            val thenValue = expr.valueOr { return it.toFailure() }
            val condition = parseDisjunctionExpr().valueOr { return it.toFailure() }

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

    // disjunction:
    //    | conjunction ('or' conjunction )+
    //    | conjunction
    private fun parseDisjunctionExpr(): LiloResult<LiloExpr> {
        var expr = parseConjunctionExpr()
        while (match(kind = LiloTokenKind.OR_KEYWORD)) {
            val lhs = expr.valueOr { return it.toFailure() }
            val rhs = parseConjunctionExpr().valueOr { return it.toFailure() }
            expr = LiloResult.Success(data = BoolOpExpr(lhs, op = BoolOp.OR, rhs))
        }
        return expr
    }

    // conjunction:
    //    | inversion ('and' inversion )+
    //    | inversion
    private fun parseConjunctionExpr(): LiloResult<LiloExpr> {
        var expr = parseInversion()
        while (match(kind = LiloTokenKind.AND_KEYWORD)) {
            val lhs = expr.valueOr { return it.toFailure() }
            val rhs = parseInversion().valueOr { return it.toFailure() }
            expr = LiloResult.Success(data = BoolOpExpr(lhs, op = BoolOp.AND, rhs))
        }
        return expr
    }

    // inversion:
    //    | 'not' inversion
    //    | comparison
    private fun parseInversion() : LiloResult<LiloExpr> {
        if (match(kind = LiloTokenKind.NOT_KEYWORD)) {
            val operand = parseEqualityExpr().valueOr { return it.toFailure() }
            return LiloResult.Success(data = UnaryOpExpr(op = UnaryOp.NOT, operand = operand))
        }
        return parseEqualityExpr()
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
            val compOp = comparisonOpFromTokenKind(op.kind)
            lhs = ComparisonOpExpr(lhs = lhs, op = compOp, rhs = rhs)
        }
        return LiloResult.Success(data = lhs)
    }

    private fun parseComparisonsExpr(): LiloResult<LiloExpr> {
        var lhs = parseBitwiseOrExpr().valueOr { return it.toFailure() }
        while (!isAtEnd() && peek().kind.isComparisonOperator()) {
            val op = advance()
            val rhs = parseBitwiseOrExpr().valueOr { return it.toFailure() }
            val compOp = comparisonOpFromTokenKind(op.kind)
            lhs = ComparisonOpExpr(lhs = lhs, op = compOp, rhs = rhs)
        }
        return LiloResult.Success(data = lhs)
    }

    private fun binaryOpFromTokenKind(kind: LiloTokenKind): BinaryOp {
        return when (kind) {
            LiloTokenKind.PLUS -> BinaryOp.PLUS
            LiloTokenKind.MINUS -> BinaryOp.MINUS
            LiloTokenKind.STAR -> BinaryOp.MUL
            LiloTokenKind.DOUBLE_STAR -> BinaryOp.POW
            LiloTokenKind.SLASH -> BinaryOp.TRUE_DIV
            LiloTokenKind.DOUBLE_SLASH -> BinaryOp.FLOOR_DIV
            LiloTokenKind.PERCENT -> BinaryOp.MOD
            LiloTokenKind.AT -> BinaryOp.MAT_MUL
            LiloTokenKind.RIGHT_SHIFT -> BinaryOp.RIGHT_SHIFT
            LiloTokenKind.LEFT_SHIFT -> BinaryOp.LEFT_SHIFT
            LiloTokenKind.AMPER -> BinaryOp.BIT_AND
            LiloTokenKind.V_BAR -> BinaryOp.BIT_OR
            LiloTokenKind.CIRCUMFLEX -> BinaryOp.BIT_XOR
            else -> TODO(reason = "Unreachable BinaryOp")
        }
    }

    // bitwise_or:
    //    | bitwise_or '|' bitwise_xor
    //    | bitwise_xor
    private fun parseBitwiseOrExpr() :  LiloResult<LiloExpr> {
        var lhs = parseBitwiseXorExpr().valueOr { return it.toFailure() }
        while (isPeek(kind = LiloTokenKind.V_BAR)) {
            val op = binaryOpFromTokenKind(advance().kind)
            val rhs = parseBitwiseXorExpr().valueOr { return it.toFailure() }
            lhs = BinaryOpExpr(lhs = lhs, op = op, rhs = rhs)
        }
        return LiloResult.Success(data = lhs)
    }

    // bitwise_xor:
    //    | bitwise_xor '^' bitwise_and
    //    | bitwise_and
    private fun parseBitwiseXorExpr() :  LiloResult<LiloExpr> {
        var lhs = parseBitwiseAndExpr().valueOr { return it.toFailure() }
        while (isPeek(kind = LiloTokenKind.CIRCUMFLEX)) {
            val op = binaryOpFromTokenKind(advance().kind)
            val rhs = parseBitwiseAndExpr().valueOr { return it.toFailure() }
            lhs = BinaryOpExpr(lhs = lhs, op = op, rhs = rhs)
        }
        return LiloResult.Success(data = lhs)
    }

    // bitwise_and:
    //    | bitwise_and '&' shift_expr
    //    | shift_expr
    private fun parseBitwiseAndExpr() :  LiloResult<LiloExpr> {
        var lhs = parseShiftExpr().valueOr { return it.toFailure() }
        while (isPeek(kind = LiloTokenKind.AMPER)) {
            val op = binaryOpFromTokenKind(advance().kind)
            val rhs = parseShiftExpr().valueOr { return it.toFailure() }
            lhs = BinaryOpExpr(lhs = lhs, op = op, rhs = rhs)
        }
        return LiloResult.Success(data = lhs)
    }

    // shift_expr:
    //    | shift_expr '<<' sum
    //    | shift_expr '>>' sum
    //    | sum
    private fun parseShiftExpr() :  LiloResult<LiloExpr> {
        var lhs = parseSumExpr().valueOr { return it.toFailure() }
        while (!isAtEnd() && peek().kind.isShiftOperator()) {
            val op = binaryOpFromTokenKind(advance().kind)
            val rhs = parseSumExpr().valueOr { return it.toFailure() }
            lhs = BinaryOpExpr(lhs = lhs, op = op, rhs = rhs)
        }
        return LiloResult.Success(data = lhs)
    }

    // sum:
    //    | sum '+' term
    //    | sum '-' term
    //    | term
    private fun parseSumExpr(): LiloResult<LiloExpr> {
        var lhs = parseInvertExpr().valueOr { return it.toFailure() }
        while (!isAtEnd() && peek().kind.isSumOperator()) {
            val op = binaryOpFromTokenKind(advance().kind)
            val rhs = parseInvertExpr().valueOr { return it.toFailure() }
            lhs = BinaryOpExpr(lhs = lhs, op = op, rhs = rhs)
        }
        return LiloResult.Success(data = lhs)
    }

    private fun parseInvertExpr(): LiloResult<LiloExpr> {
        if (match(kind = LiloTokenKind.TILDE)) {
            val op = unaryOpFromTokenKind(previous().kind)
            val operand = parseTermExpr().valueOr { return it.toFailure() }
            return LiloResult.Success(data = UnaryOpExpr(op = op, operand = operand))
        }
        return parseTermExpr()
    }

    //  term:
    //    | term '*' factor
    //    | term '/' factor
    //    | term '//' factor
    //    | term '%' factor
    //    | term '@' factor
    //    | factor
    private fun parseTermExpr(): LiloResult<LiloExpr> {
        var lhs = parseUnaryExpr().valueOr { return it.toFailure() }
        while (!isAtEnd() && peek().kind.isTermOperator()) {
            val op = binaryOpFromTokenKind(advance().kind)
            val rhs = parseUnaryExpr().valueOr { return it.toFailure() }
            lhs = BinaryOpExpr(lhs = lhs, op = op, rhs = rhs)
        }
        return LiloResult.Success(data = lhs)
    }

    private fun unaryOpFromTokenKind(kind: LiloTokenKind): UnaryOp {
        return when (kind) {
            LiloTokenKind.PLUS -> UnaryOp.PLUS
            LiloTokenKind.MINUS -> UnaryOp.MINUS
            else -> TODO(reason = "Unreachable BinaryOp")
        }
    }

    // factor:
    //    | '+' factor
    //    | '-' factor
    //    | '~' factor
    //    | power
    private fun parseUnaryExpr(): LiloResult<LiloExpr> {
        if (peek().kind.isUnaryOperator()) {
            val op = unaryOpFromTokenKind(advance().kind)
            val expr = parsePowExpr().valueOr { return it.toFailure() }
            return LiloResult.Success(data = UnaryOpExpr(op = op, operand = expr))
        }
        return parsePowExpr()
    }

    // power:
    //    | primary '**' factor
    //    | primary
    private fun parsePowExpr() : LiloResult<LiloExpr> {
        var lhs = parseCallOrGetExpr().valueOr { return it.toFailure() }
        while (isPeek(kind = LiloTokenKind.DOUBLE_STAR)) {
            val op = binaryOpFromTokenKind(advance().kind)
            val rhs = parseUnaryExpr().valueOr { return it.toFailure() }
            lhs = BinaryOpExpr(lhs = lhs, op = op, rhs = rhs)
        }
        return LiloResult.Success(data = lhs)
    }

    private fun parseCallOrGetExpr(): LiloResult<LiloExpr> {
        var expr = parsePrimaryExpr().valueOr { return it.toFailure() }

        while (true) {
            if (match(kind = LiloTokenKind.L_PAR)) {
                val args = mutableListOf<LiloExpr>()
                loop@ while (isPeek(LiloTokenKind.R_PAR).not()) {
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

                expr = GetExpr(obj = expr, name = NameExpr(value = callNAME))
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
                LiloResult.Success(data = NameExpr(value = advance()))
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

    // for_if_clause
    //   'for' star_targets 'in' ~ disjunction ('if' disjunction )*
    private fun parseForIfClauses() : LiloResult<List<ForIfClause>> {
        if (isPeek(kind = LiloTokenKind.FOR_KEYWORD).not()) {
            return createDiagnostic(peek().loc, "Expected `for` at start of ForIfClause")
        }

        val forIfClauses = mutableListOf<ForIfClause>()
        while (isPeek(kind = LiloTokenKind.FOR_KEYWORD)) {
            // Advance 'for'
            advance()

            // Parse for target
            val target = parseExpr().valueOr { return it.toFailure() }

            expectAndConsume(
                kind = LiloTokenKind.IN_KEYWORD,
                message = "expected 'in' after `for` target"
            ).valueOr { return it.toFailure() }

            // Parse for iterator
            val iter = parseDisjunctionExpr().valueOr { return it.toFailure() }

            // If <condition>
            var filter : LiloExpr? = null
            if (match(kind = LiloTokenKind.IF_KEYWORD)) {
                filter = parseDisjunctionExpr().valueOr { return it.toFailure() }
            }

            val forIfClause = ForIfClause(target, iter, filter)
            forIfClauses.add(forIfClause)
        }

        return LiloResult.Success(data = forIfClauses)
    }

    private fun parseListExpr(): LiloResult<LiloExpr> {
        // Advance '['
        advance()

        // Parse first element if exists
        val list = mutableListOf<LiloExpr>()
        if (isPeek(kind = LiloTokenKind.R_SQB).not()) {
            list.add(parseExpr().valueOr { return it.toFailure() })
        }

        // List comprehension

        // listcomp:
        //   | '[' star_named_expression for_if_clauses ']'
        if (list.isNotEmpty() && isPeek(kind = LiloTokenKind.FOR_KEYWORD)) {
            val forIfClauses = parseForIfClauses().valueOr { return it.toFailure() }

            expectAndConsume(
                kind = LiloTokenKind.R_SQB,
                message = "expected ']' at end of list"
            ).valueOr { return it.toFailure() }

            val listCmp = ListCompExpr(elt = list[0], generator = forIfClauses)
            return LiloResult.Success(data = listCmp)
        }

        if (isPeek(kind = LiloTokenKind.R_SQB).not()) {
            expectAndConsume(
                kind = LiloTokenKind.COMMA,
                message = "expected ',' between list elements"
            ).valueOr { return it.toFailure() }
        }

        loop@ while (isPeek(kind = LiloTokenKind.R_SQB).not()) {
            list.add(parseExpr().valueOr { return it.toFailure() })
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
        loop@ while (isPeek(kind = LiloTokenKind.R_PAR).not()) {
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

        val parameters = mutableListOf<Parameter>()
        loop@ while (isPeek(kind = LiloTokenKind.COLON).not()) {
            if (isPeek(kind = LiloTokenKind.OUT_KEYWORD)) {
                return createDiagnostic(
                    peek().loc,
                    message = "`out` parameter is not supported with lambda"
                )
            }

            val name = expectAndConsume(
                kind = LiloTokenKind.NAME,
                message = "expected 'symbol' as lambda parameter name"
            ).valueOr { return it.toFailure() }.lexeme!!

            // lambda expressions do not support inline parameter type annotations
            parameters.add(Parameter(name, type = null, isOut = false))
            consumeCommaOr { break@loop }
        }

        expectAndConsume(
            kind = LiloTokenKind.COLON,
            message = "expected ':' after lambda parameters"
        ).valueOr { return it.toFailure() }

        val body = parseExpr().valueOr { return it.toFailure() }

        consumeOptionalSemi()

        val returnStmt = ReturnStmt(value = body)
        return LiloResult.Success(data = LambdaExpr(parameters = parameters, body = returnStmt))
    }

    private fun parseSetOrDictionaryExpr(): LiloResult<LiloExpr> {
        // Advance '{'
        advance()

        var isDictionary = false

        val setList = mutableListOf<LiloExpr>()
        val dictPairs = mutableListOf<Pair<LiloExpr, LiloExpr>>()

        // Parse first element if exists
        if (isPeek(kind = LiloTokenKind.R_BRACE).not()) {
            val key = parseExpr().valueOr { return it.toFailure() }

            // Parse map key and value pairs
            if (match(kind = LiloTokenKind.COLON)) {
                isDictionary = true
                val value = parseExpr().valueOr { return it.toFailure() }
                dictPairs.add(key to value)
            }

            // In case it's not dictionary, register it as set value
            if (isDictionary.not()) setList.add(key)
        }

        val hasAtLestOneElement = setList.isNotEmpty() || dictPairs.isNotEmpty()
        if (hasAtLestOneElement && isPeek(kind = LiloTokenKind.FOR_KEYWORD)) {
            val forIfClauses = parseForIfClauses().valueOr { return it.toFailure() }

            expectAndConsume(
                kind = LiloTokenKind.R_BRACE,
                message = "expected ']' at end of list"
            ).valueOr { return it.toFailure() }

            val compExpr = if (isDictionary) {
                DictCompExpr(elt = dictPairs.first(), generator = forIfClauses)
            } else {
                SetCompExpr(elt = setList.first(), generator = forIfClauses)
            }
            return LiloResult.Success(data = compExpr)
        }

        if (isPeek(kind = LiloTokenKind.R_BRACE).not()) {
            expectAndConsume(
                kind = LiloTokenKind.COMMA,
                message = "expected ',' between set/dict elements"
            ).valueOr { return it.toFailure() }
        }

        loop@ while (isPeek(kind = LiloTokenKind.R_BRACE).not()) {
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
