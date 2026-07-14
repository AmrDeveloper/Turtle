package com.amrdeveloper.lilo.parser

/// Representing the kind of Token in Lilo Programming Language.
/// Inspired from `cpython/pycore_token.h`
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
    WHILE_KEYWORD,
    FOR_KEYWORD,
    IN_KEYWORD,

    TRY_KEYWORD,
    EXCEPT_KEYWORD,
    FINALLY_KEYWORD,

    GLOBAL_KEYWORD,
    NON_LOCAL_KEYWORD,
    DEL_KEYWORD,

    RAISE_KEYWORD,
    ASSERT_KEYWORD,

    BREAK_KEYWORD,
    CONTINUE_KEYWORD,
    PASS_KEYWORD,

    OUT_KEYWORD,

    TRUE_KEYWORD,
    FALSE_KEYWORD,
    NONE_KEYWORD,

    AND_KEYWORD,
    OR_KEYWORD,
    NOT_KEYWORD,

    PLUS,                // +
    MINUS,               // -
    STAR,                // *
    SLASH,               // /
    PERCENT,             // %
    DOUBLE_STAR,         // **
    DOUBLE_SLASH,        // //

    RIGHT_SHIFT,         // >>
    LEFT_SHIFT,          // <<

    AMPER,               // &
    V_BAR,               // |
    CIRCUMFLEX,          // ^
    TILDE,               // ~

    L_PAR,               // (
    R_PAR,               // )
    L_SQB,               // [
    R_SQB,               // ]
    L_BRACE,             // {
    R_BRACE,             // }

    AT,                  // @
    DOT,                 // .
    COMMA,               // ,
    COLON,               // :
    SEMI,                // ;

    R_ARROW,             // ->

    EQ,
    COLON_EQ,
    EQ_EQ,
    BANG,
    BANG_EQ,
    GT,
    GE,
    LT,
    LE,

    NAME,
    STRING,
    F_STRING_START,
    F_STRING_MIDDLE,
    F_STRING_END,
    INT_LITERAL,
    FLOAT_LITERAL,
    COMPLEX_LITERAL,

    NEW_LINE,
    INDENT,
    DEDENT,

    END_MARKER,
}

data class LiloLoc(val line: Int, val start: Int, val end: Int)

data class LiloToken(val kind: LiloTokenKind, val loc: LiloLoc, val lexeme: String?)

val liloKeywordsMap = mapOf(
    "from" to LiloTokenKind.FROM_KEYWORD,
    "import" to LiloTokenKind.IMPORT_KEYWORD,
    "as" to LiloTokenKind.AS_KEYWORD,
    "def" to LiloTokenKind.DEF_KEYWORD,
    "return" to LiloTokenKind.RETURN_KEYWORD,
    "if" to LiloTokenKind.IF_KEYWORD,
    "elif" to LiloTokenKind.ELIF_KEYWORD,
    "else" to LiloTokenKind.ELSE_KEYWORD,
    "lambda" to LiloTokenKind.LAMBDA_KEYWORD,
    "while" to LiloTokenKind.WHILE_KEYWORD,
    "for" to LiloTokenKind.FOR_KEYWORD,
    "in" to LiloTokenKind.IN_KEYWORD,

    "try" to LiloTokenKind.TRY_KEYWORD,
    "except" to LiloTokenKind.EXCEPT_KEYWORD,
    "finally" to LiloTokenKind.FINALLY_KEYWORD,

    "global" to LiloTokenKind.GLOBAL_KEYWORD,
    "nonlocal" to LiloTokenKind.NON_LOCAL_KEYWORD,
    "del" to LiloTokenKind.DEL_KEYWORD,

    "raise" to LiloTokenKind.RAISE_KEYWORD,
    "assert" to LiloTokenKind.ASSERT_KEYWORD,

    "break" to LiloTokenKind.BREAK_KEYWORD,
    "continue" to LiloTokenKind.CONTINUE_KEYWORD,
    "pass" to LiloTokenKind.PASS_KEYWORD,

    "out" to LiloTokenKind.OUT_KEYWORD,

    "and" to LiloTokenKind.AND_KEYWORD,
    "or" to LiloTokenKind.OR_KEYWORD,
    "not" to LiloTokenKind.NOT_KEYWORD,

    "True" to LiloTokenKind.TRUE_KEYWORD,
    "False" to LiloTokenKind.FALSE_KEYWORD,
    "None" to LiloTokenKind.NONE_KEYWORD,
)

val liloOneCharTokenMap = mapOf(
    '+' to LiloTokenKind.PLUS,
    '-' to LiloTokenKind.MINUS,
    '*' to LiloTokenKind.STAR,
    '/' to LiloTokenKind.SLASH,
    '%' to LiloTokenKind.PERCENT,

    '&' to LiloTokenKind.AMPER,
    '|' to LiloTokenKind.V_BAR,
    '^' to LiloTokenKind.CIRCUMFLEX,
    '~' to LiloTokenKind.TILDE,

    '(' to LiloTokenKind.L_PAR,
    ')' to LiloTokenKind.R_PAR,
    '[' to LiloTokenKind.L_SQB,
    ']' to LiloTokenKind.R_SQB,
    '{' to LiloTokenKind.L_BRACE,
    '}' to LiloTokenKind.R_BRACE,

    '@' to LiloTokenKind.AT,
    '.' to LiloTokenKind.DOT,
    ',' to LiloTokenKind.COMMA,
    ':' to LiloTokenKind.COLON,
    ';' to LiloTokenKind.SEMI,
)

fun LiloTokenKind.isShiftOperator() = this in listOf(
    LiloTokenKind.RIGHT_SHIFT,
    LiloTokenKind.LEFT_SHIFT,
)

fun LiloTokenKind.isSumOperator() = this in listOf(
    LiloTokenKind.PLUS,
    LiloTokenKind.MINUS,
)

fun LiloTokenKind.isTermOperator() = this in listOf(
    LiloTokenKind.STAR,
    LiloTokenKind.SLASH,
    LiloTokenKind.DOUBLE_SLASH,
    LiloTokenKind.PERCENT,
    LiloTokenKind.AT,
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

fun LiloTokenKind.isEOF() = this == LiloTokenKind.END_MARKER
