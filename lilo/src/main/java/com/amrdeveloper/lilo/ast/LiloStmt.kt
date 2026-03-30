package com.amrdeveloper.lilo.ast

sealed interface LiloStmt : LiloNode

data class ImportStmt(val modules : List<Pair<String, String?>>) : LiloStmt

data class FunctionStmt(val name: String, val params: List<String>, val body: List<LiloStmt>) : LiloStmt

data class BlockStmt(val nodes: List<LiloStmt>) : LiloStmt

data class ExprStmt(val expr: LiloExpr) : LiloStmt

data class AssignStmt(val name: String, val value: LiloExpr) : LiloStmt
