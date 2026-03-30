package com.amrdeveloper.lilo.runtime

import com.amrdeveloper.lilo.ast.ArithExpr
import com.amrdeveloper.lilo.ast.BoolExpr
import com.amrdeveloper.lilo.ast.CallExpr
import com.amrdeveloper.lilo.ast.FloatExpr
import com.amrdeveloper.lilo.ast.IntExpr
import com.amrdeveloper.lilo.ast.GroupExpr
import com.amrdeveloper.lilo.ast.AssignStmt
import com.amrdeveloper.lilo.ast.BlockStmt
import com.amrdeveloper.lilo.ast.DotExpr
import com.amrdeveloper.lilo.ast.ExprStmt
import com.amrdeveloper.lilo.ast.FunctionStmt
import com.amrdeveloper.lilo.ast.ImportStmt
import com.amrdeveloper.lilo.ast.LiloProgram
import com.amrdeveloper.lilo.ast.LiloTreeVisitor
import com.amrdeveloper.lilo.ast.ListExpr
import com.amrdeveloper.lilo.ast.PrintCallExpr
import com.amrdeveloper.lilo.ast.SymbolExpr
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.opertion.LiloAddOp
import com.amrdeveloper.lilo.opertion.LiloDivOp
import com.amrdeveloper.lilo.opertion.LiloModOp
import com.amrdeveloper.lilo.opertion.LiloMulOp
import com.amrdeveloper.lilo.opertion.LiloSubOp
import com.amrdeveloper.lilo.parser.LiloTokenKind
import com.amrdeveloper.lilo.std.core.LiloStdModule
import com.amrdeveloper.lilo.std.supportedLiloStdlib
import com.amrdeveloper.lilo.value.LiloBool
import com.amrdeveloper.lilo.value.LiloBuiltinFunction
import com.amrdeveloper.lilo.value.LiloFloat
import com.amrdeveloper.lilo.value.LiloFunction
import com.amrdeveloper.lilo.value.LiloInt
import com.amrdeveloper.lilo.value.LiloList
import com.amrdeveloper.lilo.value.LiloModule
import com.amrdeveloper.lilo.value.LiloValue

class LiloInterpreter(private val liloHost: LiloHost) :
    LiloTreeVisitor<LiloResult<Unit>, LiloResult<LiloValue>> {

    private val TRUE = LiloBool(value = true)
    private val FALSE = LiloBool(value = false)

    private val environment = LiloEnvironment(enclosing = null)

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

    override fun visitImportStmt(stmt: ImportStmt): LiloResult<Unit> {
        val moduleName = stmt.moduleName
        val liloStdModule =
            liloStdlib.get(moduleName) ?: return runtimeException("No module named `$moduleName`")
        if (liloStdModule !is LiloStdModule) {
            return runtimeException("`$moduleName` is not module")
        }

        val module = LiloModule(name = stmt.moduleName)
        environment.define(name = moduleName, value = module)
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

    override fun visitDotExpr(expr: DotExpr): LiloResult<LiloValue> {
        val objResult = visit(expr.obj)
        if (objResult.isFailure()) return objResult.toFailure()
        val obj = objResult.toSuccessData()

        if (obj is LiloModule) {
            val moduleName = obj.name
            if (liloStdlib.containsKey(moduleName).not()) {
                return runtimeException("No module named `$moduleName`")
            }

            val liloModule = liloStdlib[moduleName]!! as LiloStdModule
            val liloFunction = liloModule.getStdFunction(obj.name)
            if (liloFunction != null) {
                return runtimeObject(obj = LiloBuiltinFunction(obj.name, liloFunction))
            }

            return runtimeException("No function named `${obj.name}`")
        }

        return runtimeException("Invalid Dot expression rhs")
    }

    override fun visitCallExpr(expr: CallExpr): LiloResult<LiloValue> {
        val calleeResult = visit(expr.callee)
        if (calleeResult.isFailure()) return calleeResult

        val callee = calleeResult.toSuccessData()
        if (callee is LiloFunction) {
            val function = callee
            for ((index, arg) in expr.args.withIndex()) {
                val valueResult = visit(arg)
                if (valueResult.isFailure()) return valueResult.toFailure()
                val value = valueResult.toSuccessData()
                environment.define(name = function.params[index], value = value)
            }

            for (stmt in function.body) {
                val result = visit(stmt)
                if (result.isFailure()) return result.toFailure()
            }

            return runtimeObject(obj = LiloInt(value = 0))
        }

        if (callee is LiloBuiltinFunction) {
            val args = mutableListOf<LiloValue>()

            for (arg in expr.args) {
                val valueResult = visit(arg)
                if (valueResult.isFailure()) return valueResult.toFailure()
                val value = valueResult.toSuccessData()
                args.add(value)
            }

            val callResult = callee.function.call(args = args)
            if (callResult.isFailure()) return callResult.toFailure()
            return runtimeObject(obj = callResult.toSuccessData())
        }

        return runtimeException("`$callee` is not callable")
    }

    override fun visitPrintCallExpr(expr: PrintCallExpr): LiloResult<LiloValue> {
        val values = mutableListOf<LiloValue>()
        for (arg in expr.args) {
            val value = visit(arg)
            if (value.isFailure()) return value.toFailure()
            values.add(value.toSuccessData())
        }

        val output = values.joinToString(separator = " ")
        liloHost.write(output)
        return runtimeObject(obj = LiloInt(value = 0))
    }

    override fun visitArithExpr(expr: ArithExpr): LiloResult<LiloValue> {
        val lhsResult = visit(expr.lhs)
        if (lhsResult.isFailure()) return lhsResult.toFailure()

        val rhsResult = visit(expr.rhs)
        if (rhsResult.isFailure()) return rhsResult.toFailure()

        val lhs = lhsResult.toSuccessData()
        val rhs = rhsResult.toSuccessData()

        return when (expr.op.kind) {
            LiloTokenKind.PLUS -> LiloAddOp(lhs = lhs, rhs = rhs).run()
            LiloTokenKind.MINUS -> LiloSubOp(lhs = lhs, rhs = rhs).run()
            LiloTokenKind.STAR -> LiloMulOp(lhs = lhs, rhs = rhs).run()
            LiloTokenKind.SLASH -> LiloDivOp(lhs = lhs, rhs = rhs).run()
            LiloTokenKind.MODULO -> LiloModOp(lhs = lhs, rhs = rhs).run()
            else -> runtimeException("Op `${expr.op.kind.name}` is unsupported between lhs & rhs")
        }
    }

    override fun visitGroupExpr(expr: GroupExpr): LiloResult<LiloValue> {
        return visit(expr.expr)
    }

    override fun visitListExpr(expr: ListExpr): LiloResult<LiloValue> {
        val list = mutableListOf<LiloValue>()

        for (value in expr.values) {
            val elemnetResult = visit(expr = value)
            if (elemnetResult.isFailure()) return elemnetResult
            val element = elemnetResult.toSuccessData()
            list.add(element)
        }

        return runtimeObject(obj = LiloList(values = list))
    }

    override fun visitSymbolExpr(expr: SymbolExpr): LiloResult<LiloValue> {
        val value = environment.get(expr.value.lexeme!!)
            ?: return runtimeException("Undefined variable `${expr.value.lexeme}`")
        return runtimeObject(obj = value)
    }

    override fun visitIntExpr(expr: IntExpr): LiloResult<LiloValue> {
        val value = LiloInt(value = expr.value.lexeme!!.toInt())
        return runtimeObject(obj = value)
    }

    override fun visitFloatExpr(expr: FloatExpr): LiloResult<LiloValue> {
        val value = LiloFloat(value = expr.value.lexeme!!.toFloat())
        return runtimeObject(obj = value)
    }

    override fun visitBoolExpr(expr: BoolExpr): LiloResult<LiloValue> {
        val value = if (expr.value.kind == LiloTokenKind.TRUE_KEYWORD) TRUE else FALSE
        return runtimeObject(obj = value)
    }

    private fun runtimeObject(obj: LiloValue): LiloResult.Success<LiloValue> {
        return LiloResult.Success(data = obj)
    }

    private fun runtimeException(message: String): LiloResult.Failure<LiloException> {
        return LiloResult.Failure(error = LiloException(message))
    }
}