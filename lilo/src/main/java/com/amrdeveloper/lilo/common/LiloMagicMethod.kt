package com.amrdeveloper.lilo.common

object LiloMagicMethod {
    const val INIT = "__init__"

    // Numeric conversion
    const val ROUND = "__round__"

    // Arithmetic Op
    const val ADD = "__add__"
    const val SUB = "__sub__"
    const val MUL = "__mul__"
    const val TRUE_DIV = "__truediv__"
    const val FLOOR_DIV = "__floordiv__"
    const val MOD = "__mod__"
    const val POW = "__pow__"
    const val MATRIX_MUL = "__matmul__"

    const val ABS = "__abs__"

    // Shift Op
    const val RIGHT_SHIFT = "__rshift__"
    const val LEFT_SHIFT = "__lshift__"

    // Bitwise Op
    const val BIT_AND = "__and__"
    const val BIT_OR = "__or__"
    const val BIT_XOR = "__xor__"

    // Comparison Op
    const val EQ = "__eq__"
    const val NE = "__ne__"
    const val GT = "__gt__"
    const val GE = "__ge__"
    const val LT = "__lt__"
    const val LE = "__le__"

    // Unary Op
    const val POS = "__pos__"
    const val NEG = "__neg__"
    const val NOT = "__not__"
    const val INVERT = "__invert__"

    const val SET_ITEM = "__setitem__"
    const val GET_ITEM = "__getitem__"

    // Iterator Op
    const val ITER = "__iter__"
    const val REVERSED = "__reversed__"
    const val NEXT = "__next__"

    const val BOOL = "__bool__"

    const val CALL = "__call__"

    const val LEN = "__len__"
    const val STR = "__str__"
}
