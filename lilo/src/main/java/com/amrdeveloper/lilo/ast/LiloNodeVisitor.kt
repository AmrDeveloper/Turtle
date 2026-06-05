package com.amrdeveloper.lilo.ast

interface LiloStmtVisitor<T> {
    fun visit(stmt: LiloStmt): T = when (stmt) {
        is FromImportStmt -> visitFromImportStmt(stmt)
        is ImportStmt -> visitImportStmt(stmt)
        is FunctionStmt -> visitFunctionStmt(stmt)
        is GlobalStmt -> visitGlobalStmt(stmt)
        is NonLocalStmt -> visitNonLocalStmt(stmt)
        is IfStmt -> visitIfStmt(stmt)
        is ForStmt -> visitForStmt(stmt)
        is WhileStmt -> visitWhileStmt(stmt)
        is BlockStmt -> visitBlockStmt(stmt)
        is ExprStmt -> visitExprStmt(stmt)
        is AnnAssignStmt -> visitAnnotatedAssignStmt(stmt)
        is AssignStmt -> visitAssignStmt(stmt)
        is RaiseStmt -> visitRaiseStmt(stmt)
        is ReturnStmt -> visitReturnStmt(stmt)
        is AssertStmt -> visitAssertStmt(stmt)
        is BreakStmt -> visitBreakStmt(stmt)
        is ContinueStmt -> visitContinueStmt(stmt)
        is PassStmt -> visitPassStmt(stmt)
    }

    fun visitFromImportStmt(stmt: FromImportStmt): T
    fun visitImportStmt(stmt: ImportStmt): T
    fun visitFunctionStmt(stmt: FunctionStmt): T
    fun visitGlobalStmt(stmt: GlobalStmt): T
    fun visitNonLocalStmt(stmt: NonLocalStmt): T
    fun visitIfStmt(stmt: IfStmt): T
    fun visitForStmt(stmt: ForStmt): T
    fun visitWhileStmt(stmt: WhileStmt): T
    fun visitBlockStmt(stmt: BlockStmt): T
    fun visitExprStmt(stmt: ExprStmt): T
    fun visitAnnotatedAssignStmt(stmt: AnnAssignStmt): T
    fun visitAssignStmt(stmt: AssignStmt): T
    fun visitRaiseStmt(stmt: RaiseStmt): T
    fun visitReturnStmt(stmt: ReturnStmt): T
    fun visitAssertStmt(stmt: AssertStmt): T
    fun visitBreakStmt(stmt: BreakStmt): T
    fun visitContinueStmt(stmt: ContinueStmt): T
    fun visitPassStmt(stmt: PassStmt): T
}

interface LiloExprVisitor<T> {
    fun visit(expr: LiloExpr): T = when (expr) {
        is LambdaExpr -> visitLambdaExpr(expr)
        is GetExpr -> visitGetExpr(expr)
        is GetItemExpr -> visitGetItemExpr(expr)
        is IfExpr -> visitIfExpr(expr)
        is CallExpr -> visitCallExpr(expr)
        is BinaryOpExpr -> visitBinaryExpr(expr)
        is ComparisonOpExpr -> visitComparisonExpr(expr)
        is BoolOpExpr -> visitBoolOpExpr(expr)
        is UnaryOpExpr -> visitUnaryExpr(expr)
        is GroupExpr -> visitGroupExpr(expr)
        is ListCompExpr -> visitListCompExpr(expr)
        is ListExpr -> visitListExpr(expr)
        is SetExpr -> visitSetExpr(expr)
        is DictExpr -> visitDictExpr(expr)
        is TupleExpr -> visitTupleExpr(expr)
        is NameExpr -> visitNameExpr(expr)
        is StrExpr -> visitStrExpr(expr)
        is IntExpr -> visitIntExpr(expr)
        is FloatExpr -> visitFloatExpr(expr)
        is ComplexExpr -> visitComplexExpr(expr)
        is BoolExpr -> visitBoolExpr(expr)
        is NoneExpr -> visitNoneExpr(expr)
    }

    fun visitLambdaExpr(expr: LambdaExpr): T
    fun visitGetExpr(expr: GetExpr): T
    fun visitGetItemExpr(expr: GetItemExpr): T
    fun visitIfExpr(expr: IfExpr): T
    fun visitCallExpr(expr: CallExpr): T
    fun visitBinaryExpr(expr: BinaryOpExpr): T
    fun visitComparisonExpr(expr: ComparisonOpExpr): T
    fun visitBoolOpExpr(expr: BoolOpExpr): T
    fun visitUnaryExpr(expr: UnaryOpExpr): T
    fun visitGroupExpr(expr: GroupExpr): T
    fun visitListExpr(expr: ListExpr): T
    fun visitListCompExpr(expr: ListCompExpr): T
    fun visitSetExpr(expr: SetExpr): T
    fun visitDictExpr(expr: DictExpr): T
    fun visitTupleExpr(expr: TupleExpr): T
    fun visitNameExpr(expr: NameExpr): T
    fun visitStrExpr(expr: StrExpr): T
    fun visitIntExpr(expr: IntExpr): T
    fun visitFloatExpr(expr: FloatExpr): T
    fun visitComplexExpr(expr: ComplexExpr): T
    fun visitBoolExpr(expr: BoolExpr): T
    fun visitNoneExpr(expr: NoneExpr): T
}

interface LiloTreeVisitor<S, E> : LiloStmtVisitor<S>, LiloExprVisitor<E> {
    fun visitProgram(program: LiloProgram): S
}
