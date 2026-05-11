package com.amrdeveloper.lilo.common

object LiloMagicMethod {
    const val INIT = "__init__"

    // Binary expr
    const val ADD = "__add__"
    const val SUB = "__sub__"
    const val MUL = "__mul__"
    const val DIV = "__truediv__"
    const val MOD = "__mod__"

    // Comparison expr
    const val EQ = "__eq__"
    const val NE = "__ne__"
    const val GT = "__gt__"
    const val GE = "__ge__"
    const val LT = "__lt__"
    const val LE = "__le__"

    // Unary expr
    const val POS = "__pos__"
    const val NEG = "__neg__"

    const val SET_ITEM = "__setitem__"
    const val GET_ITEM = "__getitem__"

    const val BOOL = "__bool__"

    const val CALL = "__call__"

    const val LEN = "__len__"
    const val STR = "__str__"
}
