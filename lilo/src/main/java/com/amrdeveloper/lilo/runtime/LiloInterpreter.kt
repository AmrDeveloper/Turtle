package com.amrdeveloper.lilo.runtime

import com.amrdeveloper.lilo.ast.AssertStmt
import com.amrdeveloper.lilo.ast.AssignStmt
import com.amrdeveloper.lilo.ast.BinaryExpr
import com.amrdeveloper.lilo.ast.BinaryOp
import com.amrdeveloper.lilo.ast.BlockStmt
import com.amrdeveloper.lilo.ast.BoolExpr
import com.amrdeveloper.lilo.ast.BreakStmt
import com.amrdeveloper.lilo.ast.CallExpr
import com.amrdeveloper.lilo.ast.ComparisonExpr
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
import com.amrdeveloper.lilo.ast.NonLocalStmt
import com.amrdeveloper.lilo.ast.NoneExpr
import com.amrdeveloper.lilo.ast.PassStmt
import com.amrdeveloper.lilo.ast.RaiseStmt
import com.amrdeveloper.lilo.ast.ReturnStmt
import com.amrdeveloper.lilo.ast.SetExpr
import com.amrdeveloper.lilo.ast.StrExpr
import com.amrdeveloper.lilo.ast.SymbolExpr
import com.amrdeveloper.lilo.ast.TupleExpr
import com.amrdeveloper.lilo.ast.UnaryExpr
import com.amrdeveloper.lilo.ast.WhileStmt
import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.common.valueOr
import com.amrdeveloper.lilo.machine.LiloAbstractMachine
import com.amrdeveloper.lilo.`object`.EXCEPTION_CAUSE_FIELD
import com.amrdeveloper.lilo.`object`.LiloBool
import com.amrdeveloper.lilo.`object`.LiloComplex
import com.amrdeveloper.lilo.`object`.LiloDict
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
import com.amrdeveloper.lilo.`object`.liloAssertionErrorType
import com.amrdeveloper.lilo.`object`.liloBaseExceptionType
import com.amrdeveloper.lilo.parser.LiloTokenKind
import com.amrdeveloper.lilo.runtime.signal.LiloBreakSignal
import com.amrdeveloper.lilo.runtime.signal.LiloContinueSignal
import com.amrdeveloper.lilo.runtime.signal.LiloReturnSignal
import com.amrdeveloper.lilo.std.registerLiloAutoImportedModule
import com.amrdeveloper.lilo.std.registerLiloStandardLibrary
import com.amrdeveloper.lilo.type.LiloType
import com.amrdeveloper.lilo.type.liloMethodType
import kotlin.collections.set

class LiloInterpreter(val liloMachine: LiloAbstractMachine) :
    LiloTreeVisitor<LiloResult<Unit>, LiloResult<LiloObject>> {

    private val TRUE = LiloBool(value = true)
    private val FALSE = LiloBool(value = false)

    val globals = LiloEnvironment().also {
        // Register builtins
        registerLiloAutoImportedModule()
        registerLiloStandardLibrary()
    }

    var environment = globals

    fun evaluate(program: LiloProgram): LiloResult<Unit> {
        try { visitProgram(program).valueOr { return it.toFailure() } }
        catch (e : LiloRaise) { return LiloResult.Failure(error = LiloExceptionMessage(e.toString())) }
        return LiloResult.Success(data = Unit)
    }

    override fun visitProgram(program: LiloProgram): LiloResult<Unit> {
        val nodes = program.nodes
        for (node in nodes) {
            visit(stmt = node).valueOr { return it.toFailure() }
        }
        return LiloResult.Success(data = Unit)
    }

    private fun resolveNestedModules(
        names : List<String>,
        alias : String? = null,
        shouldDefine : Boolean = false
    ) : LiloResult<LiloObject> {
        var liloModule = LiloEnvironment.builtins.get(names[0])
            ?: return runtimeException("No module named `${names[0]}`")
        if (liloModule !is LiloModule) return runtimeException("`${names[0]}` is not module")
        if (shouldDefine && (names.size == 1 || alias == null)) {
            environment.set(name = alias ?: names[0], value = liloModule)
        }

        for (name in names.drop(n = 1)) {
            liloModule = liloModule.getAttr(name)
                ?: return runtimeException("No module named `${name}` inside ${names[0]}")
            if (liloModule !is LiloModule) return runtimeException("`${name}` is not module")
        }

        if (shouldDefine && names.size > 1 && alias != null) {
            environment.set(name = alias, value = liloModule)
        }

        return LiloResult.Success(data = liloModule)
    }

    override fun visitFromImportStmt(stmt: FromImportStmt): LiloResult<Unit> {
        val module = resolveNestedModules(names = stmt.module, shouldDefine = false)
            .valueOr { return it.toFailure() }

        if (stmt.symbols != null) {
            for ((symbolName, alias) in stmt.symbols) {
                val symbol = module.getAttr(symbolName)
                    ?: return runtimeException("No element named `$symbolName` in module `${stmt.module}`")
                environment.set(name = alias ?: symbolName, value = symbol)
            }
            return LiloResult.Success(data = Unit)
        }

        // From <module> import *
        for ((name, value) in module.dict) {
            environment.set(name = name, value = value)
        }

        return LiloResult.Success(data = Unit)
    }

    override fun visitImportStmt(stmt: ImportStmt): LiloResult<Unit> {
        for ((moduleNames, alias) in stmt.modules) {
            resolveNestedModules(moduleNames, alias, shouldDefine = true)
                .valueOr { return it.toFailure() }
        }
        return LiloResult.Success(data = Unit)
    }

    override fun visitFunctionStmt(stmt: FunctionStmt): LiloResult<Unit> {
        val function = LiloFunction(params = stmt.params, body = stmt.body)
        environment.set(name = stmt.name, value = function)
        return LiloResult.Success(data = Unit)
    }

    override fun visitGlobalStmt(stmt: GlobalStmt): LiloResult<Unit> {
        for (name in stmt.names) environment.markGlobal(name)
        return LiloResult.Success(data = Unit)
    }

    override fun visitNonLocalStmt(stmt: NonLocalStmt): LiloResult<Unit> {
        // TODO: NonLocal Statement Not yet implemented
        return runtimeException("NonLocal statement Not yet implemented")
    }

    override fun visitIfStmt(stmt: IfStmt): LiloResult<Unit> {
        for ((expr, body) in stmt.ifs) {
            val condition = visit(expr = expr).valueOr { return it.toFailure() }
            val isTruth = isLiloObjectEvalToTrue(obj = condition).valueOr { return it.toFailure() }
            if (isTruth) {
                visit(stmt = body).valueOr { return it.toFailure() }
                return LiloResult.Success(data = Unit)
            }
        }

        // Execute the else block if it exists
        if (stmt.elseBlock != null) {
            visit(stmt = stmt.elseBlock).valueOr { return it.toFailure() }
            return LiloResult.Success(data = Unit)
        }

        return LiloResult.Success(data = Unit)
    }

    override fun visitForStmt(stmt: ForStmt): LiloResult<Unit> {
        // TODO: For Statement Not yet implemented
        return runtimeException("For statement Not yet implemented")
    }

    override fun visitWhileStmt(stmt: WhileStmt): LiloResult<Unit> {
        val condition = visit(expr = stmt.condition).valueOr { return it.toFailure() }
        var isTruth = isLiloObjectEvalToTrue(obj = condition).valueOr { return it.toFailure() }
        if (!isTruth && stmt.elseBlock != null) {
            visit(stmt = stmt.elseBlock).valueOr { return it.toFailure() }
            return LiloResult.Success(data = Unit)
        }

        try {
            do {
                try {
                    visit(stmt = stmt.body).valueOr { return it.toFailure() }
                }
                catch (_ : LiloContinueSignal) { }

                // Execute the condition for the next run
                val condition = visit(expr = stmt.condition).valueOr { return it.toFailure() }
                isTruth = isLiloObjectEvalToTrue(obj = condition).valueOr { return it.toFailure() }
            } while (isTruth)
        } catch (_ : LiloBreakSignal) { return LiloResult.Success(data = Unit) }
        return LiloResult.Success(data = Unit)
    }

    override fun visitBlockStmt(stmt: BlockStmt): LiloResult<Unit> {
        for (node in stmt.nodes) { visit(stmt = node).valueOr { return it.toFailure() } }
        return LiloResult.Success(data = Unit)
    }

    override fun visitExprStmt(stmt: ExprStmt): LiloResult<Unit> {
        visit(stmt.expr).valueOr { return it.toFailure() }
        return LiloResult.Success(data = Unit)
    }

    override fun visitAssignStmt(stmt: AssignStmt): LiloResult<Unit> {
        val lValue = stmt.lValue
        val value = visit(expr = stmt.rValue).valueOr { return it.toFailure() }
        return when (lValue) {
            is SymbolExpr -> {
                environment.set(name = lValue.value.lexeme!!, value = value)
                LiloResult.Success(data = Unit)
            }

            is GetItemExpr -> {
                val obj = visit(expr = lValue.obj).valueOr { return it.toFailure() }
                val index = visit(expr = lValue.index).valueOr { return it.toFailure() }

                val liloSetItemMethod = obj.getAttr(name = LiloMagicMethod.SET_ITEM)
                if (liloSetItemMethod == null || liloSetItemMethod !is LiloCallable) {
                    return runtimeException("`${obj}` support item assignment")
                }

                val invokeResult =
                    liloSetItemMethod.invoke(interpreter = this, args = listOf(obj, index, value))
                if (invokeResult.isFailure()) return invokeResult.toFailure()
                LiloResult.Success(data = Unit)
            }

            else -> runtimeException("Invalid `lvalue` for assign expr")
        }
    }

    override fun visitRaiseStmt(stmt: RaiseStmt): LiloResult<Unit> {
        val exc = visit(expr = stmt.exc).valueOr { return it.toFailure() }
        val type = exc as? LiloType ?: exc.type
        if (type?.isSubclass(parent = liloBaseExceptionType) == false) {
            return runtimeException("exceptions must derive from BaseException")
        }

        var cause : LiloObject? = null
        stmt.cause?.let {
            cause = visit(expr = stmt.cause).valueOr { return it.toFailure() }
            val isCauseType = cause is LiloType
            val causeType = cause as? LiloType ?: cause.type
            if (causeType?.isSubclass(parent = liloBaseExceptionType) == false) {
                return runtimeException("exceptions cause must derive from BaseException")
            }

            if (isCauseType) cause = LiloObject(type = cause)
        }

        var exception = exc
        if (exc is LiloType) exception = LiloObject(type = exc)
        if (cause != null) {
            exception.setAttr(name = EXCEPTION_CAUSE_FIELD, value = cause)
        }

        throw LiloRaise(exception = exception)
    }

    override fun visitReturnStmt(stmt: ReturnStmt): LiloResult<Unit> {
        if (stmt.value != null) {
            val value = visit(expr = stmt.value).valueOr { return it.toFailure() }
            throw LiloReturnSignal(value = value)
        }
        throw LiloReturnSignal()
    }

    override fun visitAssertStmt(stmt: AssertStmt): LiloResult<Unit> {
        val condition = visit(expr = stmt.test).valueOr { return it.toFailure() }
        val isTruth = isLiloObjectEvalToTrue(obj = condition).valueOr { return it.toFailure() }

        if (isTruth) return LiloResult.Success(data = Unit)
        val exception = LiloObject(type = liloAssertionErrorType)
        if (stmt.msg != null) {
            // TODO: Assert with message Not yet implemented
            return runtimeException("Assert message Not yet implemented")
        }
        throw LiloRaise(exception = exception)
    }

    override fun visitBreakStmt(stmt: BreakStmt): LiloResult<Unit> {
        throw LiloBreakSignal()
    }

    override fun visitContinueStmt(stmt: ContinueStmt): LiloResult<Unit> {
        throw LiloContinueSignal()
    }

    override fun visitPassStmt(stmt : PassStmt) : LiloResult<Unit> {
        return LiloResult.Success(data = Unit)
    }

    override fun visitLambdaExpr(expr: LambdaExpr): LiloResult<LiloObject> {
        val function = LiloFunction(params = expr.params, body = listOf(expr.body))
        return LiloResult.Success(data = function)
    }

    override fun visitGetExpr(expr: GetExpr): LiloResult<LiloObject> {
        val liloObj = visit(expr.obj).valueOr { return it.toFailure() }
        val attribute = expr.name.value.lexeme!!
        val liloAttribute = liloObj.getAttr(name = attribute)
        if (liloAttribute != null) {
            val methodOrAttribute = if (liloAttribute.type == liloMethodType)
                LiloMethod(self = liloObj, method = liloAttribute)
            else liloAttribute
            return runtimeObject(obj = methodOrAttribute)
        }
        return runtimeException("Invalid `.` expression on lhs")
    }

    override fun visitIfExpr(expr: IfExpr): LiloResult<LiloObject> {
        val condition = visit(expr.condition).valueOr { return it.toFailure() }
        val isTruth = isLiloObjectEvalToTrue(obj = condition).valueOr { return it.toFailure() }
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
        val liloObj = visit(expr.obj).valueOr { return it.toFailure() }
        val slice = visit(expr.index).valueOr { return it.toFailure() }

        val liloGetItemMethod = liloObj.getAttr(name = LiloMagicMethod.GET_ITEM)
        if (liloGetItemMethod == null || liloGetItemMethod !is LiloCallable) {
            return runtimeException("`${liloObj}` object is not subscriptable")
        }

        return liloGetItemMethod.invoke(interpreter = this, args = listOf(liloObj, slice))
    }

    override fun visitCallExpr(expr: CallExpr): LiloResult<LiloObject> {
        val callee = visit(expr.callee).valueOr { return it.toFailure() }

        val args = ArrayList<LiloObject>(expr.args.size)
        for (arg in expr.args) {
            val valueResult = visit(expr = arg)
            if (valueResult.isFailure()) return valueResult.toFailure()
            val value = valueResult.toSuccessData()
            args.add(value)
        }

        // Call `__init__` if the callee is LiloType
        if (callee is LiloType) {
            val initFunction = callee.getAttr(name = LiloMagicMethod.INIT)
            if (initFunction == null || initFunction !is LiloCallable) {
                return runtimeException("`${callee.type}` has no `__init__` attribute")
            }
            return initFunction.invoke(interpreter = this, args = args)
        }

        // Call `__call__` if the callee is LiloObject
        val callFunction = callee.getAttr(name = LiloMagicMethod.CALL)
        if (callFunction != null && callFunction is LiloCallable) {
            return callFunction.invoke(interpreter = this, args = args)
        }

        // In case of function call
        if (callee is LiloCallable) return callee.invoke(interpreter = this, args)
        return runtimeException("`$callee` is not callable")
    }

    override fun visitBinaryExpr(expr: BinaryExpr): LiloResult<LiloObject> {
        val lhs = visit(expr.lhs).valueOr { return it.toFailure() }
        val rhs = visit(expr.rhs).valueOr { return it.toFailure() }

        val methodName = when (expr.op) {
            BinaryOp.PLUS -> LiloMagicMethod.ADD
            BinaryOp.MINUS -> LiloMagicMethod.SUB
            BinaryOp.MUL -> LiloMagicMethod.MUL
            BinaryOp.DIV -> LiloMagicMethod.DIV
            BinaryOp.MOD -> LiloMagicMethod.MOD
        }

        val method = lhs.getAttr(methodName)
            ?: return runtimeException("Method `${methodName}` unsupported between ${lhs.type} & ${rhs.type}")

        if (method !is LiloCallable)
            return runtimeException("Op `${lhs.type}` has no $methodName attribute")

        return method.invoke(interpreter = this, args = listOf(lhs, rhs))
    }

    override fun visitComparisonExpr(expr: ComparisonExpr): LiloResult<LiloObject> {
        val lhs = visit(expr.lhs).valueOr { return it.toFailure() }
        val rhs = visit(expr.rhs).valueOr { return it.toFailure() }

        val methodName = when (expr.op) {
            ComparisonOp.EQ -> LiloMagicMethod.EQ
            ComparisonOp.NE -> LiloMagicMethod.NE
            ComparisonOp.GT -> LiloMagicMethod.GT
            ComparisonOp.GE -> LiloMagicMethod.GE
            ComparisonOp.LT -> LiloMagicMethod.LT
            ComparisonOp.LE -> LiloMagicMethod.LE
        }

        val method = lhs.getAttr(methodName)
            ?: return runtimeException("Method `${methodName}` unsupported between ${lhs.type} & ${rhs.type}")

        if (method !is LiloCallable)
            return runtimeException("Op `${lhs.type}` has no $methodName attribute")

        return method.invoke(interpreter = this, args = listOf(lhs, rhs))
    }

    override fun visitUnaryExpr(expr: UnaryExpr): LiloResult<LiloObject> {
        val operand = visit(expr.operand).valueOr { return it.toFailure() }

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
            val element = visit(expr = value).valueOr { return it.toFailure() }
            list.add(element)
        }
        return runtimeObject(obj = LiloList(values = list))
    }

    override fun visitSetExpr(expr: SetExpr): LiloResult<LiloObject> {
        val set = mutableSetOf<LiloObject>()
        for (value in expr.values) {
            val element = visit(expr = value).valueOr { return it.toFailure() }
            set.add(element)
        }
        return runtimeObject(obj = LiloSet(values = set))
    }

    override fun visitDictExpr(expr: DictExpr): LiloResult<LiloObject> {
        val map = mutableMapOf<LiloObject, LiloObject>()
        for ((key, value) in expr.values) {
            val key = visit(expr = key).valueOr { return it.toFailure() }
            val value = visit(expr = value).valueOr { return it.toFailure() }
            map[key] = value
        }
        return runtimeObject(obj = LiloDict(values = map))
    }

    override fun visitTupleExpr(expr: TupleExpr): LiloResult<LiloObject> {
        val list = ArrayList<LiloObject>(expr.values.size)
        for (value in expr.values) {
            val element = visit(expr = value).valueOr { return it.toFailure() }
            list.add(element)
        }
        return runtimeObject(obj = LiloTuple(values = list))
    }

    override fun visitSymbolExpr(expr: SymbolExpr): LiloResult<LiloObject> {
        val name = expr.value.lexeme!!
        val value = environment.get(name)
        if (value != null) return runtimeObject(obj = value)
        val builtin = LiloEnvironment.builtins[name]
        if (builtin != null) return runtimeObject(obj = builtin)
        return runtimeException("Name '${name}' is not defined")
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
        return runtimeObject(obj = LiloNone)
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
        val condBool = callable.invoke(interpreter = this, args = listOf(obj))
            .valueOr { return it.toFailure() }
        if (condBool !is LiloBool) {
            return runtimeException("Expects bool from calling `__bool__`")
        }

        return LiloResult.Success(data = condBool.value)
    }

    private fun runtimeObject(obj: LiloObject): LiloResult.Success<LiloObject> {
        return LiloResult.Success(data = obj)
    }

    private fun runtimeException(message: String): LiloResult.Failure<LiloExceptionMessage> {
        return LiloResult.Failure(error = LiloExceptionMessage(message))
    }
}
