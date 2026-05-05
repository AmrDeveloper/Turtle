package com.amrdeveloper.lilo.ast

sealed interface LiloStmt : LiloNode

data class FromImportStmt(val module: String, val symbols: List<Pair<String, String?>>? = null) :
    LiloStmt

data class ImportStmt(val modules : List<Pair<String, String?>>) : LiloStmt

data class FunctionStmt(val name: String, val params: List<String>, val body: List<LiloStmt>) : LiloStmt

data class GlobalStmt(val names: List<String>) : LiloStmt

data class NonLocalStmt(val names: List<String>) : LiloStmt

data class IfStmt(val ifs: List<Pair<LiloExpr, LiloStmt>>, val elseBlock: LiloStmt? = null) :
    LiloStmt

data class BlockStmt(val nodes: List<LiloStmt>) : LiloStmt

data class ExprStmt(val expr: LiloExpr) : LiloStmt

data class AssignStmt(val lValue: LiloExpr, val rValue: LiloExpr) : LiloStmt

data class ReturnStmt(val value: LiloExpr? = null) : LiloStmt
