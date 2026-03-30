package com.amrdeveloper.lilo.ast

interface LiloStmtVisitor<T> {
    fun visit(stmt: LiloStmt): T = when (stmt) {
        is ImportStmt -> visitImportStmt(stmt)
        is FunctionStmt -> visitFunctionStmt(stmt)
        is BlockStmt -> visitBlockStmt(stmt)
        is ExprStmt -> visitExprStmt(stmt)
        is AssignStmt -> visitAssignStmt(stmt)
    }

    fun visitImportStmt(stmt : ImportStmt) : T
    fun visitFunctionStmt(stmt: FunctionStmt): T
    fun visitBlockStmt(stmt: BlockStmt): T
    fun visitExprStmt(stmt: ExprStmt): T
    fun visitAssignStmt(stmt: AssignStmt): T
}

interface LiloExprVisitor<T> {
    fun visit(expr: LiloExpr): T = when (expr) {
        is DotExpr -> visitDotExpr(expr)
        is CallExpr -> visitCallExpr(expr)
        is ArithExpr -> visitArithExpr(expr)
        is GroupExpr -> visitGroupExpr(expr)
        is ListExpr -> visitListExpr(expr)
        is SymbolExpr -> visitSymbolExpr(expr)
        is IntExpr -> visitIntExpr(expr)
        is FloatExpr -> visitFloatExpr(expr)
        is BoolExpr -> visitBoolExpr(expr)
    }

    fun visitDotExpr(expr: DotExpr): T
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
    fun visitProgram(program: LiloProgram) : S
}