package com.amrdeveloper.lilo.runtime

import com.amrdeveloper.lilo.ast.ArithExpr
import com.amrdeveloper.lilo.ast.BoolExpr
import com.amrdeveloper.lilo.ast.CallExpr
import com.amrdeveloper.lilo.ast.FloatExpr
import com.amrdeveloper.lilo.ast.IntExpr
import com.amrdeveloper.lilo.ast.GroupExpr
import com.amrdeveloper.lilo.ast.AssignStmt
import com.amrdeveloper.lilo.ast.ExprStmt
import com.amrdeveloper.lilo.ast.LiloProgram
import com.amrdeveloper.lilo.ast.LiloTreeVisitor
import com.amrdeveloper.lilo.ast.ListExpr
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
import com.amrdeveloper.lilo.value.LiloBool
import com.amrdeveloper.lilo.value.LiloFloat
import com.amrdeveloper.lilo.value.LiloInt
import com.amrdeveloper.lilo.value.LiloList
import com.amrdeveloper.lilo.value.LiloValue
import kotlin.collections.iterator

class LiloInterpreter : LiloTreeVisitor<LiloResult<Unit>, LiloResult<LiloValue>> {

    private val TRUE = LiloBool(value = true)
    private val FALSE = LiloBool(value = false)

    private val scope = mutableMapOf<String, LiloValue>()

    fun evaluate(program: LiloProgram) {
        visitProgram(program)

        for ((key, value) in scope) {
            println("$key = $value")
        }
    }

    override fun visitExprStmt(stmt: ExprStmt): LiloResult<Unit> {
        visit(stmt.expr)
        return LiloResult.Success(data = Unit)
    }

    override fun visitAssignStmt(stmt: AssignStmt): LiloResult<Unit> {
        val valueResult = visit(stmt.value)
        if (valueResult.isFailure()) return valueResult.toFailure()
        val value = valueResult.toSuccessData()
        scope[stmt.name] = value
        return LiloResult.Success(data = Unit)
    }

    override fun visitCallExpr(expr: CallExpr): LiloResult<LiloValue> {
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
        val value = scope[expr.value.lexeme!!]!!
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