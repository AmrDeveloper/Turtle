package com.amrdeveloper.lilo.ast

sealed interface LiloStmt : LiloNode

data class ExprStmt(val expr: LiloExpr) : LiloStmt

data class AssignStmt(val name: String, val value: LiloExpr) : LiloStmt
