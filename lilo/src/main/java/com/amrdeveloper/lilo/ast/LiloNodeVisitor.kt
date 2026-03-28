package com.amrdeveloper.lilo.ast

import com.amrdeveloper.lilo.ast.ArithExpr
import com.amrdeveloper.lilo.ast.BoolExpr
import com.amrdeveloper.lilo.ast.CallExpr
import com.amrdeveloper.lilo.ast.FloatExpr
import com.amrdeveloper.lilo.ast.GroupExpr
import com.amrdeveloper.lilo.ast.IntExpr
import com.amrdeveloper.lilo.ast.LiloExpr
import com.amrdeveloper.lilo.ast.ListExpr
import com.amrdeveloper.lilo.ast.SymbolExpr

interface LiloStmtVisitor<T> {
    fun visit(stmt: LiloStmt): T = when (stmt) {
        is ExprStmt -> visitExprStmt(stmt)
        is AssignStmt -> visitAssignStmt(stmt)
    }

    fun visitExprStmt(stmt: ExprStmt): T
    fun visitAssignStmt(stmt: AssignStmt): T
}

interface LiloExprVisitor<T> {
    fun visit(expr: LiloExpr): T = when (expr) {
        is CallExpr -> visitCallExpr(expr)
        is ArithExpr -> visitArithExpr(expr)
        is GroupExpr -> visitGroupExpr(expr)
        is ListExpr -> visitListExpr(expr)
        is SymbolExpr -> visitSymbolExpr(expr)
        is IntExpr -> visitIntExpr(expr)
        is FloatExpr -> visitFloatExpr(expr)
        is BoolExpr -> visitBoolExpr(expr)
    }

    fun visitCallExpr(expr: CallExpr): T
    fun visitArithExpr(expr: ArithExpr): T
    fun visitGroupExpr(expr: GroupExpr): T
    fun visitListExpr(expr: ListExpr): T
    fun visitSymbolExpr(expr: SymbolExpr): T
    fun visitIntExpr(expr: IntExpr): T
    fun visitFloatExpr(expr: FloatExpr): T
    fun visitBoolExpr(expr: BoolExpr): T
}

interface LiloTreeVisitor<S, E> : LiloStmtVisitor<S>, LiloExprVisitor<E> {
    fun visitProgram(program: LiloProgram) {
        val nodes = program.nodes
        for (node in nodes) {
            visit(node)
        }
    }
}