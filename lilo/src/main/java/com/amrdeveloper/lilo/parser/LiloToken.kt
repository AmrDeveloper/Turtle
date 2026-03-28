package com.amrdeveloper.lilo.parser

enum class LiloTokenKind {
    TRUE_KEYWORD,
    FALSE_KEYWORD,

    PLUS,
    MINUS,
    STAR,
    SLASH,
    MODULO,

    LPAR,               // (
    RPAR,               // )
    L_BRACKET,          // [
    R_BRACKET,          // ]

    COMMA,

    EQ,

    SYMBOL,
    INT_LITERAL,
    FLOAT_LITERAL,

    END_OF_FILE,
}

data class LiloLoc(val line: Int, val start: Int, val end: Int)

data class LiloToken(val kind: LiloTokenKind, val loc: LiloLoc, val lexeme: String?)

fun getLiloKeywordsMap() = mapOf(
    "True" to LiloTokenKind.TRUE_KEYWORD,
    "False" to LiloTokenKind.FALSE_KEYWORD,
)

fun getLiloOneCharTokenMap() = mapOf(
    '+' to LiloTokenKind.PLUS,
    '-' to LiloTokenKind.MINUS,
    '*' to LiloTokenKind.STAR,
    '/' to LiloTokenKind.SLASH,
    '%' to LiloTokenKind.MODULO,

    '(' to LiloTokenKind.LPAR,
    ')' to LiloTokenKind.RPAR,
    '[' to LiloTokenKind.L_BRACKET,
    ']' to LiloTokenKind.R_BRACKET,

    '=' to LiloTokenKind.EQ,

    ',' to LiloTokenKind.COMMA,
)

fun LiloTokenKind.isTermOperator() = this in listOf(
    LiloTokenKind.PLUS,
    LiloTokenKind.MINUS,
)

fun LiloTokenKind.isFactorOperator() = this in listOf(
    LiloTokenKind.STAR,
    LiloTokenKind.SLASH,
    LiloTokenKind.MODULO
)

fun LiloTokenKind.isEOF() = this == LiloTokenKind.END_OF_FILE