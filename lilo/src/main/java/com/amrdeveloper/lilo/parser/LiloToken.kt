package com.amrdeveloper.lilo.parser

enum class LiloTokenKind {
    FROM_KEYWORD,
    IMPORT_KEYWORD,
    AS_KEYWORD,
    DEF_KEYWORD,
    RETURN_KEYWORD,
    IF_KEYWORD,
    ELIF_KEYWORD,
    ELSE_KEYWORD,
    LAMBDA_KEYWORD,

    TRUE_KEYWORD,
    FALSE_KEYWORD,
    NONE_KEYWORD,

    PLUS,
    MINUS,
    STAR,
    SLASH,
    MODULO,

    LPAR,               // (
    RPAR,               // )
    L_BRACKET,          // [
    R_BRACKET,          // ]
    L_BRACE,            // {
    R_BRACE,            // }

    DOT,
    COMMA,
    COLON,
    SEMICOLON,

    EQ,
    EQ_EQ,
    BANG,
    BANG_EQ,
    GT,
    GE,
    LT,
    LE,

    SYMBOL,
    STR_LITERAL,
    INT_LITERAL,
    FLOAT_LITERAL,
    COMPLEX_LITERAL,

    END_OF_FILE,
}

data class LiloLoc(val line: Int, val start: Int, val end: Int)

data class LiloToken(val kind: LiloTokenKind, val loc: LiloLoc, val lexeme: String?)

fun getLiloKeywordsMap() = mapOf(
    "from" to LiloTokenKind.FROM_KEYWORD,
    "import" to LiloTokenKind.IMPORT_KEYWORD,
    "as" to LiloTokenKind.AS_KEYWORD,
    "def" to LiloTokenKind.DEF_KEYWORD,
    "return" to LiloTokenKind.RETURN_KEYWORD,
    "if" to LiloTokenKind.IF_KEYWORD,
    "elif" to LiloTokenKind.ELIF_KEYWORD,
    "else" to LiloTokenKind.ELSE_KEYWORD,
    "lambda" to LiloTokenKind.LAMBDA_KEYWORD,

    "True" to LiloTokenKind.TRUE_KEYWORD,
    "False" to LiloTokenKind.FALSE_KEYWORD,
    "None" to LiloTokenKind.NONE_KEYWORD,
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
    '{' to LiloTokenKind.L_BRACE,
    '}' to LiloTokenKind.R_BRACE,

    '.' to LiloTokenKind.DOT,
    ',' to LiloTokenKind.COMMA,
    ':' to LiloTokenKind.COLON,
    ';' to LiloTokenKind.SEMICOLON,
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

fun LiloTokenKind.isUnaryOperator() = this in listOf(
    LiloTokenKind.PLUS,
    LiloTokenKind.MINUS,
)

fun LiloTokenKind.isEqualityOperator() = this in listOf(
    LiloTokenKind.EQ_EQ,
    LiloTokenKind.BANG_EQ,
)

fun LiloTokenKind.isComparisonOperator() = this in listOf(
    LiloTokenKind.GT,
    LiloTokenKind.GE,
    LiloTokenKind.LT,
    LiloTokenKind.LE,
)

fun LiloTokenKind.isEOF() = this == LiloTokenKind.END_OF_FILE
