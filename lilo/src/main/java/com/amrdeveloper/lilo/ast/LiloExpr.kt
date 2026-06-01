package com.amrdeveloper.lilo.ast

import com.amrdeveloper.lilo.parser.LiloToken

sealed interface LiloExpr : LiloNode

data class LambdaExpr(val parameters: List<Parameter>, val body: LiloStmt) : LiloExpr

data class CallExpr(val callee: LiloExpr, val args: List<LiloExpr>) : LiloExpr

data class GetExpr(val obj: LiloExpr, val name: NameExpr) : LiloExpr

data class GetItemExpr(val obj: LiloExpr, val index: LiloExpr) : LiloExpr

data class IfExpr(val condition: LiloExpr, val thenValue: LiloExpr, val elseValue: LiloExpr) : LiloExpr

enum class BinaryOp {
    PLUS,
    MINUS,
    MUL,
    DIV,
    MOD
}

data class BinaryOpExpr(val lhs: LiloExpr, val op: BinaryOp, val rhs: LiloExpr) : LiloExpr

enum class ComparisonOp {
    EQ,
    NE,
    GT,
    GE,
    LT,
    LE,
}

data class ComparisonOpExpr(val lhs: LiloExpr, val op: ComparisonOp, val rhs: LiloExpr) : LiloExpr

enum class BoolOp {
    AND,
    OR
}

data class BoolOpExpr(val lhs: LiloExpr, val op: BoolOp, val rhs: LiloExpr) : LiloExpr

enum class UnaryOp {
    PLUS,
    MINUS
}

data class UnaryOpExpr(val op: UnaryOp, val operand: LiloExpr) : LiloExpr

data class GroupExpr(val expr: LiloExpr) : LiloExpr

data class ListExpr(val values: List<LiloExpr>) : LiloExpr

data class SetExpr(val values: List<LiloExpr>) : LiloExpr

data class DictExpr(val values: List<Pair<LiloExpr, LiloExpr>>) : LiloExpr

data class TupleExpr(val values: List<LiloExpr>) : LiloExpr

data class NameExpr(val value: LiloToken) : LiloExpr

data class StrExpr(val value: LiloToken) : LiloExpr

data class IntExpr(val value: LiloToken) : LiloExpr

data class FloatExpr(val value: LiloToken) : LiloExpr

data class ComplexExpr(val value: LiloToken) : LiloExpr

data class BoolExpr(val value: LiloToken) : LiloExpr

data class NoneExpr(val value: LiloToken) : LiloExpr
