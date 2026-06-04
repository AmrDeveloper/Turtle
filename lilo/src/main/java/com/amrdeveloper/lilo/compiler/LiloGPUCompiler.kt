package com.amrdeveloper.lilo.compiler

import com.amrdeveloper.lilo.ast.AnnAssignStmt
import com.amrdeveloper.lilo.ast.AssertStmt
import com.amrdeveloper.lilo.ast.AssignStmt
import com.amrdeveloper.lilo.ast.BinaryOpExpr
import com.amrdeveloper.lilo.ast.BinaryOp
import com.amrdeveloper.lilo.ast.BlockStmt
import com.amrdeveloper.lilo.ast.BoolExpr
import com.amrdeveloper.lilo.ast.BoolOpExpr
import com.amrdeveloper.lilo.ast.BreakStmt
import com.amrdeveloper.lilo.ast.CallExpr
import com.amrdeveloper.lilo.ast.ComparisonOpExpr
import com.amrdeveloper.lilo.ast.ComparisonOp
import com.amrdeveloper.lilo.ast.ComplexExpr
import com.amrdeveloper.lilo.ast.ContinueStmt
import com.amrdeveloper.lilo.ast.DictExpr
import com.amrdeveloper.lilo.ast.ExprStmt
import com.amrdeveloper.lilo.ast.FloatExpr
import com.amrdeveloper.lilo.ast.ForStmt
import com.amrdeveloper.lilo.ast.FromImportStmt
import com.amrdeveloper.lilo.ast.FunctionStmt
import com.amrdeveloper.lilo.ast.GetExpr
import com.amrdeveloper.lilo.ast.GetItemExpr
import com.amrdeveloper.lilo.ast.GlobalStmt
import com.amrdeveloper.lilo.ast.GroupExpr
import com.amrdeveloper.lilo.ast.IfExpr
import com.amrdeveloper.lilo.ast.IfStmt
import com.amrdeveloper.lilo.ast.ImportStmt
import com.amrdeveloper.lilo.ast.IntExpr
import com.amrdeveloper.lilo.ast.LambdaExpr
import com.amrdeveloper.lilo.ast.LiloProgram
import com.amrdeveloper.lilo.ast.LiloTreeVisitor
import com.amrdeveloper.lilo.ast.ListExpr
import com.amrdeveloper.lilo.ast.NameExpr
import com.amrdeveloper.lilo.ast.NonLocalStmt
import com.amrdeveloper.lilo.ast.NoneExpr
import com.amrdeveloper.lilo.ast.PassStmt
import com.amrdeveloper.lilo.ast.RaiseStmt
import com.amrdeveloper.lilo.ast.ReturnStmt
import com.amrdeveloper.lilo.ast.SetExpr
import com.amrdeveloper.lilo.ast.StrExpr
import com.amrdeveloper.lilo.ast.TupleExpr
import com.amrdeveloper.lilo.ast.UnaryOp
import com.amrdeveloper.lilo.ast.UnaryOpExpr
import com.amrdeveloper.lilo.ast.WhileStmt
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.valueOr
import com.amrdeveloper.lilo.lib.gpu.LiloLaunchConfig
import com.amrdeveloper.lilo.parser.LiloTokenKind

class LiloGPUCompiler(val config : LiloLaunchConfig) : LiloTreeVisitor<LiloResult<String>, LiloResult<String>> {

    private val definedVariables = mutableSetOf("gpu")

    override fun visitProgram(program: LiloProgram): LiloResult<String> {
        val builder = StringBuilder()
        for (node in program.nodes) {
            val result = visit(stmt = node).valueOr { return it.toFailure() }
            builder.append("$result\n")
        }
        return LiloResult.Success(data = builder.toString())
    }

    override fun visitFromImportStmt(stmt: FromImportStmt): LiloResult<String> {
        return LiloResult.Failure(error = "FromImport NYI on GPU")
    }

    override fun visitImportStmt(stmt: ImportStmt): LiloResult<String> {
        return LiloResult.Failure(error = "Import NYI on GPU")
    }

    override fun visitFunctionStmt(stmt: FunctionStmt): LiloResult<String> {
        val builder = StringBuilder()

        // Generate Bindings
        stmt.parameters.forEachIndexed { index, parameter ->
            val access = if (parameter.isOut) "read_write" else "read"
            definedVariables.add(parameter.name)
            // TODO: Convert type from LiloType -> WebGPU type
            val dummyType = "array<f32>"
            builder.append("@group(0) @binding($index) var<storage, $access> ${parameter.name}: $dummyType;\n")
        }
        builder.append("\n")

        // Generate Kernel Entry Point
        val threadConfigDim = config.threadsDim.dim
        builder.append("const block_dim = vec3<u32>(${threadConfigDim.x}u, ${threadConfigDim.y}u, ${threadConfigDim.z}u);\n")
        builder.append("@compute @workgroup_size(${threadConfigDim.x}, ${threadConfigDim.y}, ${threadConfigDim.z})\n")
        builder.append("fn main(\n")
        builder.append("  @builtin(workgroup_id) block_idx: vec3<u32>,\n")
        builder.append("  @builtin(local_invocation_id) thread_idx: vec3<u32>,\n")
        builder.append("  @builtin(global_invocation_id) global_id: vec3<u32>\n")
        builder.append(")\n")
        builder.append(visit(stmt.body).valueOr { return it.toFailure() })
        return LiloResult.Success(data = builder.toString())
    }

    override fun visitGlobalStmt(stmt: GlobalStmt): LiloResult<String> {
        return LiloResult.Failure(error = "Global NYI on GPU")
    }

    override fun visitNonLocalStmt(stmt: NonLocalStmt): LiloResult<String> {
        return LiloResult.Failure(error = "NonLocal NYI on GPU")
    }

    override fun visitIfStmt(stmt: IfStmt): LiloResult<String> {
        return LiloResult.Failure(error = "Import NYI on GPU")
    }

    override fun visitForStmt(stmt: ForStmt): LiloResult<String> {
        return LiloResult.Failure(error = "Import NYI on GPU")
    }

    override fun visitWhileStmt(stmt: WhileStmt): LiloResult<String> {
        return LiloResult.Failure(error = "While NYI on GPU")
    }

    override fun visitBlockStmt(stmt: BlockStmt): LiloResult<String> {
        val builder = StringBuilder()
        builder.append("{\n")
        for (node in stmt.nodes) {
            val result = visit(stmt = node).valueOr { return it.toFailure() }
            builder.append("  $result\n")
        }
        builder.append("}")
        return LiloResult.Success(data = builder.toString())
    }

    override fun visitExprStmt(stmt: ExprStmt): LiloResult<String> {
        val result = visit(stmt.expr).valueOr { return it.toFailure() }
        return LiloResult.Success(data = "$result;")
    }

    override fun visitAnnotatedAssignStmt(stmt: AnnAssignStmt): LiloResult<String> {
        val value = visit(stmt.value).valueOr { return it.toFailure() }
        if (stmt.target is NameExpr ) {
            val targetName = stmt.target.value.lexeme!!
            if (definedVariables.contains(targetName)) {
                return LiloResult.Success(data = "$targetName = $value;")
            }
            definedVariables.add(targetName)
            return LiloResult.Success(data = "var $targetName = $value;")
        }

        // Target is not NameExpr, can be `vec[i]` or any other expr else
        val target = visit(expr = stmt.target).valueOr { return it.toFailure() }
        return LiloResult.Success(data = "$target = $value;")
    }

    override fun visitAssignStmt(stmt: AssignStmt): LiloResult<String> {
        val value = visit(stmt.value).valueOr { return it.toFailure() }
        if (stmt.target is NameExpr) {
            val targetName = stmt.target.value.lexeme!!
            if (definedVariables.contains(targetName)) {
                return LiloResult.Success(data = "$targetName = $value;")
            }
            definedVariables.add(targetName)
            return LiloResult.Success(data = "var $targetName = $value;")
        }

        // Target is not NameExpr, can be `vec[i]` or any other expr else
        val target = visit(expr = stmt.target).valueOr { return it.toFailure() }
        return LiloResult.Success(data = "$target = $value;")
    }

    override fun visitRaiseStmt(stmt: RaiseStmt): LiloResult<String> {
        return LiloResult.Failure("Assert NYI on GPU")
    }

    override fun visitReturnStmt(stmt: ReturnStmt): LiloResult<String> {
        val value = if (stmt.value != null) {
            visit(expr = stmt.value).valueOr { return it.toFailure() }
        } else {
            ""
        }
        return LiloResult.Success(data = "return ${value};")
    }

    override fun visitAssertStmt(stmt: AssertStmt): LiloResult<String> {
        return LiloResult.Failure(error = "Assert NYI on GPU")
    }

    override fun visitBreakStmt(stmt: BreakStmt): LiloResult<String> {
        return LiloResult.Success(data = "break;")
    }

    override fun visitContinueStmt(stmt: ContinueStmt): LiloResult<String> {
        return LiloResult.Success(data ="continue;")
    }

    override fun visitPassStmt(stmt: PassStmt): LiloResult<String> {
        return LiloResult.Success(data = "return;")
    }

    override fun visitLambdaExpr(expr: LambdaExpr): LiloResult<String> {
        return LiloResult.Failure(error = "Lambda NYI on GPU")
    }

    override fun visitGetExpr(expr: GetExpr): LiloResult<String> {
        val exprObj = expr.obj
        val attr = (expr.name).value.lexeme
        if (exprObj is NameExpr && exprObj.value.lexeme == "gpu") {
            when (attr) {
                "block_dim" -> return LiloResult.Success(data = "block_dim")
                "block_idx" -> return LiloResult.Success(data = "block_idx")
                "thread_idx" -> return LiloResult.Success(data = "thread_idx")
                "global_id" -> return LiloResult.Success(data = "global_id")
            }
        }
        val obj = visit(expr.obj).valueOr { return it.toFailure() }
        return LiloResult.Success(data = "$obj.$attr")
    }

    override fun visitGetItemExpr(expr: GetItemExpr): LiloResult<String> {
        val obj = visit(expr.obj).valueOr { return it.toFailure() }
        val index = visit(expr.index).valueOr { return it.toFailure() }
        return LiloResult.Success("${obj}[${index}]")
    }

    override fun visitIfExpr(expr: IfExpr): LiloResult<String> {
        val cond = visit(expr.condition).valueOr { return it.toFailure() }
        val thenValue = visit(expr.thenValue).valueOr { return it.toFailure() }
        val elseValue = visit(expr.elseValue).valueOr { return it.toFailure() }
        return LiloResult.Success(data = "select($thenValue, $elseValue, $cond)")
    }

    override fun visitCallExpr(expr: CallExpr): LiloResult<String> {
        return LiloResult.Failure(error = "Call NYI on GPU")
    }

    override fun visitBinaryExpr(expr: BinaryOpExpr): LiloResult<String> {
        val lhs = visit(expr.lhs).valueOr { return it.toFailure() }
        val rhs = visit(expr.rhs).valueOr { return it.toFailure() }
        val result = when (expr.op) {
            BinaryOp.PLUS -> "$lhs + $rhs"
            BinaryOp.MINUS -> "$lhs - $rhs"
            BinaryOp.MUL -> "$lhs * $rhs"
            BinaryOp.TRUE_DIV -> "$lhs / $rhs"
            BinaryOp.FLOOR_DIV -> "floor($lhs / $rhs)"
            BinaryOp.MOD -> "$lhs % $rhs"
            BinaryOp.POW -> "pow($lhs, $rhs)"
            BinaryOp.RIGHT_SHIFT -> "$lhs >> $rhs"
            BinaryOp.LEFT_SHIFT -> "$lhs << $rhs"
        }
        return LiloResult.Success(data = result)
    }

    override fun visitComparisonExpr(expr: ComparisonOpExpr): LiloResult<String> {
        val op = when (expr.op) {
            ComparisonOp.EQ -> "=="
            ComparisonOp.NE -> "!="
            ComparisonOp.GT -> ">"
            ComparisonOp.GE -> ">="
            ComparisonOp.LT -> "<"
            ComparisonOp.LE -> "<="
        }
        val lhs = visit(expr.lhs).valueOr { return it.toFailure() }
        val rhs = visit(expr.rhs).valueOr { return it.toFailure() }
        return LiloResult.Success(data = "$lhs $op $rhs")
    }

    override fun visitBoolOpExpr(expr: BoolOpExpr): LiloResult<String> {
        return LiloResult.Failure(error = "BoolOp NYI on GPU")
    }

    override fun visitUnaryExpr(expr: UnaryOpExpr): LiloResult<String> {
        val op = when (expr.op) {
            UnaryOp.PLUS -> "+"
            UnaryOp.MINUS -> "-"
            UnaryOp.NOT -> "!"
        }
        val operand = visit(expr.operand).valueOr { return it.toFailure() }
        return LiloResult.Success(data = "$op${operand}")
    }

    override fun visitGroupExpr(expr: GroupExpr): LiloResult<String> {
        val result = visit(expr).valueOr { return it.toFailure() }
        return LiloResult.Success(data = "(${result})")
    }

    override fun visitListExpr(expr: ListExpr): LiloResult<String> {
        return LiloResult.Failure(error = "List NYI on GPU")
    }

    override fun visitSetExpr(expr: SetExpr): LiloResult<String> {
        return LiloResult.Failure(error = "Set NYI on GPU")
    }

    override fun visitDictExpr(expr: DictExpr): LiloResult<String> {
        return LiloResult.Failure(error = "Dict NYI on GPU")
    }

    override fun visitTupleExpr(expr: TupleExpr): LiloResult<String> {
        return LiloResult.Failure(error = "Tuple NYI on GPU")
    }

    override fun visitNameExpr(expr: NameExpr): LiloResult<String> {
        val name = expr.value.lexeme!!
        if (!definedVariables.contains(name)) {
            return LiloResult.Failure(error = "Name '${name}' is not defined")
        }
        return LiloResult.Success(data = name)
    }

    override fun visitStrExpr(expr: StrExpr): LiloResult<String> {
        return LiloResult.Success(data = "\"${expr.value.lexeme!!}\"")
    }

    override fun visitIntExpr(expr: IntExpr): LiloResult<String> {
        return LiloResult.Success(data = expr.value.lexeme!!)
    }

    override fun visitFloatExpr(expr: FloatExpr): LiloResult<String> {
        return LiloResult.Success(data = expr.value.lexeme!!)
    }

    override fun visitComplexExpr(expr: ComplexExpr): LiloResult<String> {
        return LiloResult.Failure(error = "ComplexType NYI on GPU")
    }

    override fun visitBoolExpr(expr: BoolExpr): LiloResult<String> {
        return LiloResult.Success(data = if (expr.value.kind == LiloTokenKind.TRUE_KEYWORD) "true" else "false")
    }

    override fun visitNoneExpr(expr: NoneExpr): LiloResult<String> {
        return LiloResult.Success(data = "null")
    }
}
