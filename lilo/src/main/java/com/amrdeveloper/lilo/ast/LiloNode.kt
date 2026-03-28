package com.amrdeveloper.lilo.ast

interface LiloNode

data class LiloProgram(val nodes: List<LiloStmt>) : LiloNode
