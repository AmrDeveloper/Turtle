package com.amrdeveloper.lilo.ast

sealed interface LiloStmt : LiloNode

data class FromImportStmt(
    val module: List<String>,
    val symbols: List<Pair<String, String?>>? = null
) : LiloStmt

data class ImportStmt(val modules: List<Pair<List<String>, String?>>) : LiloStmt

data class Parameter(
    val name: String,
    val type: LiloExpr? = null,
    val isOut: Boolean = false // Used for GPU kernal to mark parameter as output
)

data class FunctionStmt(
    val name: String,
    val parameters: List<Parameter>,
    val body: LiloStmt,
    val decorators: List<LiloExpr> = emptyList(),
    val returns: LiloExpr? = null
) : LiloStmt

data class GlobalStmt(val names: List<String>) : LiloStmt

data class NonLocalStmt(val names: List<String>) : LiloStmt

data class DelStmt(val names: List<String>) : LiloStmt

data class ForStmt(
    val target: LiloExpr,
    val iter: LiloExpr,
    val body: LiloStmt,
    val elseBlock: LiloStmt?
) : LiloStmt

data class WhileStmt(
    val condition: LiloExpr,
    val body: LiloStmt,
    val elseBlock: LiloStmt? = null
) : LiloStmt

data class IfStmt(
    val ifs: List<Pair<LiloExpr, LiloStmt>>,
    val elseBlock: LiloStmt? = null
) : LiloStmt

data class BlockStmt(val nodes: List<LiloStmt>) : LiloStmt

data class ExprStmt(val expr: LiloExpr) : LiloStmt

data class AssignStmt(
    val target: LiloExpr,
    val value: LiloExpr
) : LiloStmt

data class AnnAssignStmt(
    val target: LiloExpr,
    val annotation: LiloExpr,
    val value: LiloExpr
) : LiloStmt

data class ExceptHandler(
    val type: LiloExpr? = null,
    val name: String? = null,
    val body: LiloStmt,
)

data class TryStmt(
    val body: LiloStmt,
    val handlers: List<ExceptHandler>,
    val elseBlock: LiloStmt? = null,
    val finallyBody: LiloStmt? = null,
) : LiloStmt

data class RaiseStmt(val exc: LiloExpr? = null, val cause: LiloExpr? = null) : LiloStmt

data class ReturnStmt(val value: LiloExpr? = null) : LiloStmt

data class AssertStmt(val test: LiloExpr, val msg: LiloExpr? = null) : LiloStmt

class BreakStmt : LiloStmt

class ContinueStmt : LiloStmt

class PassStmt : LiloStmt
