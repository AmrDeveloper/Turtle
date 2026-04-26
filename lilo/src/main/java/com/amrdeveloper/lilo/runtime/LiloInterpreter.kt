package com.amrdeveloper.lilo.runtime

import com.amrdeveloper.lilo.ast.AssignStmt
import com.amrdeveloper.lilo.ast.BinaryExpr
import com.amrdeveloper.lilo.ast.BlockStmt
import com.amrdeveloper.lilo.ast.BoolExpr
import com.amrdeveloper.lilo.ast.CallExpr
import com.amrdeveloper.lilo.ast.ComplexExpr
import com.amrdeveloper.lilo.ast.ExprStmt
import com.amrdeveloper.lilo.ast.FloatExpr
import com.amrdeveloper.lilo.ast.FromImportStmt
import com.amrdeveloper.lilo.ast.FunctionStmt
import com.amrdeveloper.lilo.ast.GetExpr
import com.amrdeveloper.lilo.ast.GetItemExpr
import com.amrdeveloper.lilo.ast.GroupExpr
import com.amrdeveloper.lilo.ast.IfExpr
import com.amrdeveloper.lilo.ast.IfStmt
import com.amrdeveloper.lilo.ast.ImportStmt
import com.amrdeveloper.lilo.ast.IntExpr
import com.amrdeveloper.lilo.ast.LambdaExpr
import com.amrdeveloper.lilo.ast.LiloProgram
import com.amrdeveloper.lilo.ast.LiloTreeVisitor
import com.amrdeveloper.lilo.ast.ListExpr
import com.amrdeveloper.lilo.ast.NoneExpr
import com.amrdeveloper.lilo.ast.ReturnStmt
import com.amrdeveloper.lilo.ast.SetExpr
import com.amrdeveloper.lilo.ast.StrExpr
import com.amrdeveloper.lilo.ast.SymbolExpr
import com.amrdeveloper.lilo.ast.TupleExpr
import com.amrdeveloper.lilo.ast.UnaryExpr
import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.machine.LiloAbstractMachine
import com.amrdeveloper.lilo.`object`.LiloBool
import com.amrdeveloper.lilo.`object`.LiloComplex
import com.amrdeveloper.lilo.`object`.LiloFloat
import com.amrdeveloper.lilo.`object`.LiloFunction
import com.amrdeveloper.lilo.`object`.LiloInt
import com.amrdeveloper.lilo.`object`.LiloList
import com.amrdeveloper.lilo.`object`.LiloMethod
import com.amrdeveloper.lilo.`object`.LiloModule
import com.amrdeveloper.lilo.`object`.LiloNone
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.`object`.LiloSet
import com.amrdeveloper.lilo.`object`.LiloStr
import com.amrdeveloper.lilo.`object`.LiloTuple
import com.amrdeveloper.lilo.parser.LiloTokenKind
import com.amrdeveloper.lilo.runtime.signal.LiloReturnSignal
import com.amrdeveloper.lilo.std.registerLiloStandardLibrary
import com.amrdeveloper.lilo.type.LiloType
import com.amrdeveloper.lilo.type.liloMethodType

class LiloInterpreter(val liloMachine: LiloAbstractMachine) :
    LiloTreeVisitor<LiloResult<Unit>, LiloResult<LiloObject>> {

    private val TRUE = LiloBool(value = true)
    private val FALSE = LiloBool(value = false)

    val environment = LiloEnvironment(enclosing = null).also {
        registerLiloStandardLibrary(environment = it)
    }

    fun evaluate(program: LiloProgram): LiloResult<Unit> {
        return visitProgram(program)
    }

    override fun visitProgram(program: LiloProgram): LiloResult<Unit> {
        val nodes = program.nodes
        for (node in nodes) {
            val result = visit(stmt = node)
            if (result.isFailure()) return result.toFailure()
        }
        return LiloResult.Success(data = Unit)
    }

    override fun visitFromImportStmt(stmt: FromImportStmt): LiloResult<Unit> {
        val liloStdModule =
            environment.get(stmt.module)
                ?: return runtimeException("No module named `$stmt.module`")
        if (liloStdModule !is LiloModule) return runtimeException("`${stmt.module}` is not module")

        if (stmt.symbols != null) {
            for ((symbolName, alias) in stmt.symbols) {
                val symbol = liloStdModule.getAttr(symbolName)
                    ?: return runtimeException("No element named `$symbolName` in module `${stmt.module}`")
                environment.define(name = alias ?: symbolName, value = symbol)
            }
            return LiloResult.Success(data = Unit)
        }

        // From <module> import *
        for ((name, value) in liloStdModule.dict) {
            environment.define(name = name, value = value)
        }

        return LiloResult.Success(data = Unit)
    }

    override fun visitImportStmt(stmt: ImportStmt): LiloResult<Unit> {
        for ((moduleName, alias) in stmt.modules) {
            val liloStdModule =
                environment.get(moduleName)
                    ?: return runtimeException("No module named `$moduleName`")
            if (liloStdModule !is LiloModule) return runtimeException("`$moduleName` is not module")
            environment.define(name = alias ?: moduleName, value = liloStdModule)
        }
        return LiloResult.Success(data = Unit)
    }

    override fun visitFunctionStmt(stmt: FunctionStmt): LiloResult<Unit> {
        val function = LiloFunction(params = stmt.params, body = stmt.body)
        environment.define(name = stmt.name, value = function)
        return LiloResult.Success(data = Unit)
    }

    override fun visitIfStmt(stmt: IfStmt): LiloResult<Unit> {
        for ((expr, body) in stmt.ifs) {
            val conditionRes = visit(expr = expr)
            if (conditionRes.isFailure()) return conditionRes.toFailure()
            val condition = conditionRes.toSuccessData()
            val isTruthRes = isLiloObjectEvalToTrue(obj = condition)
            if (isTruthRes.isFailure()) return isTruthRes.toFailure()
            val isTruth = isTruthRes.toSuccessData()
            if (isTruth) {
                val evalStmt = visit(stmt = body)
                if (evalStmt.isFailure()) return evalStmt.toFailure()
                return LiloResult.Success(data = Unit)
            }
        }

        // Execute the else block if it exists
        if (stmt.elseBlock != null) {
            val evalStmt = visit(stmt = stmt.elseBlock)
            if (evalStmt.isFailure()) return evalStmt.toFailure()
            return LiloResult.Success(data = Unit)
        }

        return LiloResult.Success(data = Unit)
    }

    override fun visitBlockStmt(stmt: BlockStmt): LiloResult<Unit> {
        for (node in stmt.nodes) {
            val result = visit(stmt = node)
            if (result.isFailure()) return result.toFailure()
        }
        return LiloResult.Success(data = Unit)
    }

    override fun visitExprStmt(stmt: ExprStmt): LiloResult<Unit> {
        val result = visit(stmt.expr)
        if (result.isFailure()) return result.toFailure()
        return LiloResult.Success(data = Unit)
    }

    override fun visitAssignStmt(stmt: AssignStmt): LiloResult<Unit> {
        val valueResult = visit(expr = stmt.value)
        if (valueResult.isFailure()) return valueResult.toFailure()
        val value = valueResult.toSuccessData()
        environment.define(name = stmt.name, value = value)
        return LiloResult.Success(data = Unit)
    }

    override fun visitReturnStmt(stmt: ReturnStmt): LiloResult<Unit> {
        if (stmt.value != null) {
            val valueResult = visit(expr = stmt.value)
            if (valueResult.isFailure()) return valueResult.toFailure()
            val value = valueResult.toSuccessData()
            throw LiloReturnSignal(value = value)
        }
        throw LiloReturnSignal()
    }

    override fun visitLambdaExpr(expr: LambdaExpr): LiloResult<LiloObject> {
        val lambdaName = "Function"
        val function = LiloFunction(params = expr.params, body = listOf(expr.body))
        environment.define(name = lambdaName, value = function)
        return LiloResult.Success(data = function)
    }

    override fun visitGetExpr(expr: GetExpr): LiloResult<LiloObject> {
        val objResult = visit(expr.obj)
        if (objResult.isFailure()) return objResult.toFailure()
        val liloObj = objResult.toSuccessData()
        val attribute = expr.name.value.lexeme!!
        val liloAttribute = liloObj.getAttr(name = attribute)
        if (liloAttribute != null) {
            val methodOrAttribute = if (liloAttribute.type == liloMethodType)
                LiloMethod(self = liloObj, method = liloAttribute)
            else liloAttribute
            return runtimeObject(obj = methodOrAttribute)
        }
        return runtimeException("Invalid `.`` expression on lhs")
    }

    override fun visitIfExpr(expr: IfExpr): LiloResult<LiloObject> {
        val conditionResult = visit(expr.condition)
        if (conditionResult.isFailure()) return conditionResult.toFailure()
        val condition = conditionResult.toSuccessData()
        val isTruthRes = isLiloObjectEvalToTrue(obj = condition)
        if (isTruthRes.isFailure()) return isTruthRes.toFailure()
        val isTruth = isTruthRes.toSuccessData()
        if (isTruth) {
            val thenValueResult = visit(expr.thenValue)
            if (thenValueResult.isFailure()) return thenValueResult.toFailure()
            return thenValueResult
        }

        val elseValueResult = visit(expr.elseValue)
        if (elseValueResult.isFailure()) return elseValueResult.toFailure()
        return elseValueResult
    }

    override fun visitGetItemExpr(expr: GetItemExpr): LiloResult<LiloObject> {
        val objResult = visit(expr.obj)
        if (objResult.isFailure()) return objResult.toFailure()

        val indexResult = visit(expr.index)
        if (indexResult.isFailure()) return indexResult.toFailure()

        val liloObj = objResult.toSuccessData()
        val index = indexResult.toSuccessData()

        val liloGetItemMethod = liloObj.getAttr(name = LiloMagicMethod.GET_ITEM)
        if (liloGetItemMethod == null || liloGetItemMethod !is LiloCallable) {
            return runtimeException("`${liloObj}` object is not subscriptable")
        }

        return liloGetItemMethod.invoke(interpreter = this, args = listOf(liloObj, index))
    }

    override fun visitCallExpr(expr: CallExpr): LiloResult<LiloObject> {
        val calleeResult = visit(expr.callee)
        if (calleeResult.isFailure()) return calleeResult

        val args = ArrayList<LiloObject>(expr.args.size)
        for (arg in expr.args) {
            val valueResult = visit(expr = arg)
            if (valueResult.isFailure()) return valueResult.toFailure()
            val value = valueResult.toSuccessData()
            args.add(value)
        }

        // Call `__init__` if the callee is LiloType
        val callee = calleeResult.toSuccessData()
        if (callee is LiloType) {
            val initFunction = callee.getAttr(name = LiloMagicMethod.INIT)
            if (initFunction == null || initFunction !is LiloCallable) {
                return runtimeException("`${callee.type}` has no `__init__` attribute")
            }
            return initFunction.invoke(interpreter = this, args = args)
        }

        // In case of function call
        if (callee is LiloCallable) return callee.invoke(interpreter = this, args)
        return runtimeException("`$callee` is not callable")
    }

    override fun visitBinaryExpr(expr: BinaryExpr): LiloResult<LiloObject> {
        val lhsResult = visit(expr.lhs)
        if (lhsResult.isFailure()) return lhsResult.toFailure()

        val rhsResult = visit(expr.rhs)
        if (rhsResult.isFailure()) return rhsResult.toFailure()

        val lhs = lhsResult.toSuccessData()
        val rhs = rhsResult.toSuccessData()

        val methodName = when (expr.op.kind) {
            LiloTokenKind.PLUS -> LiloMagicMethod.ADD
            LiloTokenKind.MINUS -> LiloMagicMethod.SUB
            LiloTokenKind.STAR -> LiloMagicMethod.MUL
            LiloTokenKind.SLASH -> LiloMagicMethod.DIV
            LiloTokenKind.MODULO -> LiloMagicMethod.MOD
            else -> null
        }

        if (methodName == null)
            return runtimeException("Op `${expr.op.kind.name}` is unsupported between ${lhs.type} & ${rhs.type}")

        val method = lhs.getAttr(methodName)
            ?: return runtimeException("Method `${methodName}` unsupported between ${lhs.type} & ${rhs.type}")

        if (method !is LiloCallable)
            return runtimeException("Op `${lhs.type}` has no $methodName attribute")

        return method.invoke(interpreter = this, args = listOf(lhs, rhs))
    }

    override fun visitUnaryExpr(expr: UnaryExpr): LiloResult<LiloObject> {
        val operandResult = visit(expr.operand)
        if (operandResult.isFailure()) return operandResult
        val operand = operandResult.toSuccessData()

        val methodName = when (expr.op.kind) {
            LiloTokenKind.PLUS -> LiloMagicMethod.POS
            LiloTokenKind.MINUS -> LiloMagicMethod.NEG
            else -> null
        }

        if (methodName == null)
            return runtimeException("Op `${expr.op.kind.name}` is unsupported on ${operand.type}")

        val method = operand.getAttr(methodName)
            ?: return runtimeException("Method `${methodName}` unsupported from ${operand.type}")

        if (method !is LiloCallable)
            return runtimeException("Op `${operand.type}` has no $methodName attribute")

        return method.invoke(interpreter = this, args = listOf(operand))
    }

    override fun visitGroupExpr(expr: GroupExpr): LiloResult<LiloObject> {
        return visit(expr.expr)
    }

    override fun visitListExpr(expr: ListExpr): LiloResult<LiloObject> {
        val list = ArrayList<LiloObject>(expr.values.size)
        for (value in expr.values) {
            val elementResult = visit(expr = value)
            if (elementResult.isFailure()) return elementResult
            val element = elementResult.toSuccessData()
            list.add(element)
        }
        return runtimeObject(obj = LiloList(values = list))
    }

    override fun visitSetExpr(expr: SetExpr): LiloResult<LiloObject> {
        val set = mutableSetOf<LiloObject>()
        for (value in expr.values) {
            val elementResult = visit(expr = value)
            if (elementResult.isFailure()) return elementResult
            val element = elementResult.toSuccessData()
            set.add(element)
        }
        return runtimeObject(obj = LiloSet(values = set))
    }

    override fun visitTupleExpr(expr: TupleExpr): LiloResult<LiloObject> {
        val list = ArrayList<LiloObject>(expr.values.size)
        for (value in expr.values) {
            val elementResult = visit(expr = value)
            if (elementResult.isFailure()) return elementResult
            val element = elementResult.toSuccessData()
            list.add(element)
        }
        return runtimeObject(obj = LiloTuple(values = list))
    }

    override fun visitSymbolExpr(expr: SymbolExpr): LiloResult<LiloObject> {
        val symbolName = expr.value.lexeme!!
        val value = environment.get(symbolName)
        if (value != null) return runtimeObject(obj = value)
        return runtimeException("Undefined variable `${expr.value.lexeme}`")
    }

    override fun visitStrExpr(expr: StrExpr): LiloResult<LiloObject> {
        return runtimeObject(obj = LiloStr(value = expr.value.lexeme!!))
    }

    override fun visitIntExpr(expr: IntExpr): LiloResult<LiloObject> {
        val value = LiloInt(value = expr.value.lexeme!!.toInt())
        return runtimeObject(obj = value)
    }

    override fun visitFloatExpr(expr: FloatExpr): LiloResult<LiloObject> {
        val value = LiloFloat(value = expr.value.lexeme!!.toFloat())
        return runtimeObject(obj = value)
    }

    override fun visitComplexExpr(expr: ComplexExpr): LiloResult<LiloObject> {
        val complexImag = expr.value.lexeme!!.toFloat()
        val complex = LiloComplex(real = 0f, imag = complexImag)
        return runtimeObject(obj = complex)
    }

    override fun visitBoolExpr(expr: BoolExpr): LiloResult<LiloObject> {
        val value = if (expr.value.kind == LiloTokenKind.TRUE_KEYWORD) TRUE else FALSE
        return runtimeObject(obj = value)
    }

    override fun visitNoneExpr(expr: NoneExpr): LiloResult<LiloObject> {
        return runtimeObject(obj = LiloNone())
    }

    private fun isLiloObjectEvalToTrue(obj: LiloObject): LiloResult<Boolean> {
        val magicMethod = obj.getAttr(name = LiloMagicMethod.BOOL)
        // If object has no __bool__, we will assume it has content and can eval to true
        if (magicMethod == null) {
            return LiloResult.Success(data = true)
        }

        // __bool__ must be callable
        if (magicMethod !is LiloCallable) {
            return runtimeException("`${obj}` object has no attribute '__bool__'")
        }

        // Call `obj.__bool__` and make sure result is boolean
        val callable = magicMethod as LiloCallable
        val boolResult = callable.invoke(interpreter = this, args = listOf(obj))
        if (boolResult.isFailure()) return boolResult.toFailure()
        val condBool = boolResult.toSuccessData()
        if (condBool !is LiloBool) {
            return runtimeException("Expects bool from calling `__bool__`")
        }

        return LiloResult.Success(data = condBool.value)
    }

    private fun runtimeObject(obj: LiloObject): LiloResult.Success<LiloObject> {
        return LiloResult.Success(data = obj)
    }

    private fun runtimeException(message: String): LiloResult.Failure<LiloException> {
        return LiloResult.Failure(error = LiloException(message))
    }
}
