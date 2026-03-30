package com.amrdeveloper.lilo.value

import com.amrdeveloper.lilo.ast.LiloStmt
import com.amrdeveloper.lilo.std.core.LiloStdFunction

class LiloFunction(val params: List<String>, val body: List<LiloStmt>) : LiloValue {
    override fun toString(): String {
        return "def (".plus(params.joinToString(", ")).plus(")")
    }
}

class LiloBuiltinFunction(val name: String, val function: LiloStdFunction) : LiloValue {
    override fun toString(): String {
        return "_builtin_$name(...)"
    }
}