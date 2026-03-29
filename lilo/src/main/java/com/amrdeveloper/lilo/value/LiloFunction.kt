package com.amrdeveloper.lilo.value

import com.amrdeveloper.lilo.ast.LiloStmt

class LiloFunction(val params: List<String>, val body: List<LiloStmt>) : LiloValue {
    override fun toString(): String {
        return "def (".plus(params.joinToString(", ")).plus(")")
    }
}