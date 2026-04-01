package com.amrdeveloper.lilo.runtime

import com.amrdeveloper.lilo.ast.ArithExpr
import com.amrdeveloper.lilo.ast.BoolExpr
import com.amrdeveloper.lilo.ast.CallExpr
import com.amrdeveloper.lilo.ast.FloatExpr
import com.amrdeveloper.lilo.ast.IntExpr
import com.amrdeveloper.lilo.ast.GroupExpr
import com.amrdeveloper.lilo.ast.AssignStmt
import com.amrdeveloper.lilo.ast.BlockStmt
import com.amrdeveloper.lilo.ast.GetExpr
import com.amrdeveloper.lilo.ast.ExprStmt
import com.amrdeveloper.lilo.ast.FromImportStmt
import com.amrdeveloper.lilo.ast.FunctionStmt
import com.amrdeveloper.lilo.ast.ImportStmt
import com.amrdeveloper.lilo.ast.LiloProgram
import com.amrdeveloper.lilo.ast.LiloTreeVisitor
import com.amrdeveloper.lilo.ast.ListExpr
import com.amrdeveloper.lilo.ast.NoneExpr
import com.amrdeveloper.lilo.ast.StrExpr
import com.amrdeveloper.lilo.ast.SymbolExpr
import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.parser.LiloTokenKind
import com.amrdeveloper.lilo.std.supportedLiloStdlib
import com.amrdeveloper.lilo.`object`.LiloBool
import com.amrdeveloper.lilo.`object`.LiloCallable
import com.amrdeveloper.lilo.`object`.LiloFloat
import com.amrdeveloper.lilo.`object`.LiloFunction
import com.amrdeveloper.lilo.`object`.LiloInt
import com.amrdeveloper.lilo.`object`.LiloList
import com.amrdeveloper.lilo.`object`.LiloModule
import com.amrdeveloper.lilo.`object`.LiloNone
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.`object`.LiloStr

class LiloInterpreter(val liloHost: LiloHost) :
    LiloTreeVisitor<LiloResult<Unit>, LiloResult<LiloObject>> {

    private val TRUE = LiloBool(value = true)
    private val FALSE = LiloBool(value = false)

    val environment = LiloEnvironment(enclosing = null)

    private val liloStdlib = supportedLiloStdlib()

    fun evaluate(program: LiloProgram): LiloResult<Unit> {
        return visitProgram(program)
    }

    override fun visitProgram(program: LiloProgram): LiloResult<Unit> {
        val nodes = program.nodes
        for (node in nodes) {
            val result = visit(stmt = node)
            if (result.isFailure()) return result.toFailure()
        }
        return LiloResult.Success(data = Unit)
    }

    override fun visitFromImportStmt(stmt: FromImportStmt): LiloResult<Unit> {
        val liloStdModule =
            liloStdlib[stmt.module]
                ?: return runtimeException("No module named `$stmt.module`")
        if (liloStdModule !is LiloModule) return runtimeException("`${stmt.module}` is not module")
        for ((symbolName, alias) in stmt.symbols) {
            val symbol = liloStdModule.lookup(symbolName)
                ?: return runtimeException("No element named `$symbolName` in module `${stmt.module}`")
            environment.define(name = alias ?: symbolName, value = symbol)
        }
        return LiloResult.Success(data = Unit)
    }

    override fun visitImportStmt(stmt: ImportStmt): LiloResult<Unit> {
        for ((moduleName, alias) in stmt.modules) {
            val liloStdModule =
                liloStdlib.get(moduleName)
                    ?: return runtimeException("No module named `$moduleName`")
            if (liloStdModule !is LiloModule) return runtimeException("`$moduleName` is not module")
            environment.define(name = alias ?: moduleName, value = liloStdModule)
        }
        return LiloResult.Success(data = Unit)
    }

    override fun visitFunctionStmt(stmt: FunctionStmt): LiloResult<Unit> {
        val function = LiloFunction(params = stmt.params, body = stmt.body)
        environment.define(name = stmt.name, value = function)
        return LiloResult.Success(data = Unit)
    }

    override fun visitBlockStmt(stmt: BlockStmt): LiloResult<Unit> {
        for (node in stmt.nodes) {
            val result = visit(stmt = node)
            if (result.isFailure()) return result.toFailure()
        }
        return LiloResult.Success(data = Unit)
    }

    override fun visitExprStmt(stmt: ExprStmt): LiloResult<Unit> {
        val result = visit(stmt.expr)
        if (result.isFailure()) return result.toFailure()
        return LiloResult.Success(data = Unit)
    }

    override fun visitAssignStmt(stmt: AssignStmt): LiloResult<Unit> {
        val valueResult = visit(expr = stmt.value)
        if (valueResult.isFailure()) return valueResult.toFailure()
        val value = valueResult.toSuccessData()
        environment.define(name = stmt.name, value = value)
        return LiloResult.Success(data = Unit)
    }

    override fun visitGetExpr(expr: GetExpr): LiloResult<LiloObject> {
        val objResult = visit(expr.obj)
        if (objResult.isFailure()) return objResult.toFailure()
        val liloObj = objResult.toSuccessData()
        val attribute = expr.name.value.lexeme!!
        val liloAttribute = liloObj.lookup(name = attribute)
        if (liloAttribute != null) return runtimeObject(obj = liloAttribute)
        return runtimeException("Invalid `.`` expression on lhs")
    }

    override fun visitCallExpr(expr: CallExpr): LiloResult<LiloObject> {
        val calleeResult = visit(expr.callee)
        if (calleeResult.isFailure()) return calleeResult

        val callee = calleeResult.toSuccessData()
        if (callee is LiloCallable) {
            val args = mutableListOf<LiloObject>()
            for (arg in expr.args) {
                val valueResult = visit(expr = arg)
                if (valueResult.isFailure()) return valueResult.toFailure()
                val value = valueResult.toSuccessData()
                args.add(value)
            }
            return callee.invoke(interpreter = this, args)
        }

        return runtimeException("`$callee` is not callable")
    }

    override fun visitArithExpr(expr: ArithExpr): LiloResult<LiloObject> {
        val lhsResult = visit(expr.lhs)
        if (lhsResult.isFailure()) return lhsResult.toFailure()

        val rhsResult = visit(expr.rhs)
        if (rhsResult.isFailure()) return rhsResult.toFailure()

        val lhs = lhsResult.toSuccessData()
        val rhs = rhsResult.toSuccessData()

        val methodName = when (expr.op.kind) {
            LiloTokenKind.PLUS -> LiloMagicMethod.ADD
            LiloTokenKind.MINUS -> LiloMagicMethod.SUB
            LiloTokenKind.STAR -> LiloMagicMethod.MUL
            LiloTokenKind.SLASH -> LiloMagicMethod.DIV
            LiloTokenKind.MODULO -> LiloMagicMethod.MOD
            else -> null
        }

        if (methodName == null)
            return runtimeException("Op `${expr.op.kind.name}` is unsupported between ${lhs.type} & ${rhs.type}")

        val method = lhs.lookup(methodName)
            ?: return runtimeException("Method `${methodName}` unsupported between ${lhs.type} & ${rhs.type}")

        if (method !is LiloCallable)
            return runtimeException("Op `${lhs.type}` has no ${methodName} attribute")

        return method.invoke(interpreter = this, args = listOf(lhs, rhs))
    }

    override fun visitGroupExpr(expr: GroupExpr): LiloResult<LiloObject> {
        return visit(expr.expr)
    }

    override fun visitListExpr(expr: ListExpr): LiloResult<LiloObject> {
        val list = mutableListOf<LiloObject>()
        for (value in expr.values) {
            val elementResult = visit(expr = value)
            if (elementResult.isFailure()) return elementResult
            val element = elementResult.toSuccessData()
            list.add(element)
        }
        return runtimeObject(obj = LiloList(values = list))
    }

    override fun visitSymbolExpr(expr: SymbolExpr): LiloResult<LiloObject> {
        val symbolName = expr.value.lexeme!!
        val value = environment.get(symbolName)
        if (value != null) return runtimeObject(obj = value)

        val builtin = liloStdlib[symbolName]
        if (builtin != null) return runtimeObject(obj = builtin)

        return runtimeException("Undefined variable `${expr.value.lexeme}`")
    }

    override fun visitStrExpr(expr: StrExpr): LiloResult<LiloObject> {
        return runtimeObject(obj = LiloStr(value = expr.value.lexeme!!))
    }

    override fun visitIntExpr(expr: IntExpr): LiloResult<LiloObject> {
        val value = LiloInt(value = expr.value.lexeme!!.toInt())
        return runtimeObject(obj = value)
    }

    override fun visitFloatExpr(expr: FloatExpr): LiloResult<LiloObject> {
        val value = LiloFloat(value = expr.value.lexeme!!.toFloat())
        return runtimeObject(obj = value)
    }

    override fun visitBoolExpr(expr: BoolExpr): LiloResult<LiloObject> {
        val value = if (expr.value.kind == LiloTokenKind.TRUE_KEYWORD) TRUE else FALSE
        return runtimeObject(obj = value)
    }

    override fun visitNoneExpr(expr: NoneExpr): LiloResult<LiloObject> {
        return runtimeObject(obj = LiloNone())
    }

    private fun runtimeObject(obj: LiloObject): LiloResult.Success<LiloObject> {
        return LiloResult.Success(data = obj)
    }

    private fun runtimeException(message: String): LiloResult.Failure<LiloException> {
        return LiloResult.Failure(error = LiloException(message))
    }
}