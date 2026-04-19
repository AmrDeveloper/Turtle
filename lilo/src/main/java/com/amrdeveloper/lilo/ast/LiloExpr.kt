package com.amrdeveloper.lilo.ast

import com.amrdeveloper.lilo.parser.LiloToken

sealed interface LiloExpr : LiloNode

data class LambdaExpr(val params: List<String>, val body: LiloStmt) : LiloExpr

data class CallExpr(val callee: LiloExpr, val args: List<LiloExpr>) : LiloExpr

data class GetExpr(val obj: LiloExpr, val name: SymbolExpr) : LiloExpr

data class GetItemExpr(val obj: LiloExpr, val index: LiloExpr) : LiloExpr

data class IfExpr(val condition: LiloExpr, val thenValue: LiloExpr, val elseValue: LiloExpr) : LiloExpr

data class BinaryExpr(val lhs: LiloExpr, val op: LiloToken, val rhs: LiloExpr) : LiloExpr

data class UnaryExpr(val op: LiloToken, val operand: LiloExpr) : LiloExpr

data class GroupExpr(val expr: LiloExpr) : LiloExpr

data class ListExpr(val values: List<LiloExpr>) : LiloExpr

data class SetExpr(val values: List<LiloExpr>) : LiloExpr

data class TupleExpr(val values: List<LiloExpr>) : LiloExpr

data class SymbolExpr(val value: LiloToken) : LiloExpr

data class StrExpr(val value: LiloToken) : LiloExpr

data class IntExpr(val value: LiloToken) : LiloExpr

data class FloatExpr(val value: LiloToken) : LiloExpr

data class ComplexExpr(val value: LiloToken) : LiloExpr

data class BoolExpr(val value: LiloToken) : LiloExpr

data class NoneExpr(val value: LiloToken) : LiloExpr
