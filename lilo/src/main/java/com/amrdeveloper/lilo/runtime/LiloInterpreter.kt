package com.amrdeveloper.lilo.runtime

import com.amrdeveloper.lilo.ast.AnnAssignStmt
import com.amrdeveloper.lilo.ast.AssertStmt
import com.amrdeveloper.lilo.ast.AssignStmt
import com.amrdeveloper.lilo.ast.BinaryOpExpr
import com.amrdeveloper.lilo.ast.BinaryOp
import com.amrdeveloper.lilo.ast.BlockStmt
import com.amrdeveloper.lilo.ast.BoolExpr
import com.amrdeveloper.lilo.ast.BoolOp
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
import com.amrdeveloper.lilo.ast.ListCompExpr
import com.amrdeveloper.lilo.ast.ListExpr
import com.amrdeveloper.lilo.ast.NonLocalStmt
import com.amrdeveloper.lilo.ast.NoneExpr
import com.amrdeveloper.lilo.ast.PassStmt
import com.amrdeveloper.lilo.ast.RaiseStmt
import com.amrdeveloper.lilo.ast.ReturnStmt
import com.amrdeveloper.lilo.ast.SetExpr
import com.amrdeveloper.lilo.ast.StrExpr
import com.amrdeveloper.lilo.ast.NameExpr
import com.amrdeveloper.lilo.ast.TupleExpr
import com.amrdeveloper.lilo.ast.UnaryOp
import com.amrdeveloper.lilo.ast.UnaryOpExpr
import com.amrdeveloper.lilo.ast.WhileStmt
import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.common.valueOr
import com.amrdeveloper.lilo.machine.LiloAbstractMachine
import com.amrdeveloper.lilo.objects.EXCEPTION_CAUSE_FIELD
import com.amrdeveloper.lilo.objects.LiloBool
import com.amrdeveloper.lilo.objects.LiloComplex
import com.amrdeveloper.lilo.objects.LiloDict
import com.amrdeveloper.lilo.objects.LiloFloat
import com.amrdeveloper.lilo.objects.LiloFunction
import com.amrdeveloper.lilo.objects.LiloInt
import com.amrdeveloper.lilo.objects.LiloList
import com.amrdeveloper.lilo.objects.LiloMethod
import com.amrdeveloper.lilo.objects.LiloModule
import com.amrdeveloper.lilo.objects.LiloNone
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.LiloSet
import com.amrdeveloper.lilo.objects.LiloStr
import com.amrdeveloper.lilo.objects.LiloTuple
import com.amrdeveloper.lilo.objects.liloAssertionErrorType
import com.amrdeveloper.lilo.objects.liloBaseExceptionType
import com.amrdeveloper.lilo.objects.liloRuntimeErrorType
import com.amrdeveloper.lilo.parser.LiloTokenKind
import com.amrdeveloper.lilo.runtime.signal.LiloBreakSignal
import com.amrdeveloper.lilo.runtime.signal.LiloContinueSignal
import com.amrdeveloper.lilo.runtime.signal.LiloReturnSignal
import com.amrdeveloper.lilo.lib.registerLiloAutoImportedModule
import com.amrdeveloper.lilo.lib.registerLiloStandardLibrary
import com.amrdeveloper.lilo.objects.LiloType
import com.amrdeveloper.lilo.objects.createLiloException
import com.amrdeveloper.lilo.objects.isTrue
import com.amrdeveloper.lilo.objects.liloAttributeErrorType
import com.amrdeveloper.lilo.objects.liloImportErrorType
import com.amrdeveloper.lilo.objects.liloModuleNotFoundErrorType
import com.amrdeveloper.lilo.objects.liloModuleType
import com.amrdeveloper.lilo.objects.liloNameErrorType
import com.amrdeveloper.lilo.objects.liloNotImplementedError
import com.amrdeveloper.lilo.objects.liloStopIterationType
import com.amrdeveloper.lilo.objects.liloSyntaxErrorType
import com.amrdeveloper.lilo.objects.liloTypeErrorType
import com.amrdeveloper.lilo.objects.str
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
        try {
            visitProgram(program).valueOr { return it.toFailure() }
        } catch (e: LiloRaise) {
            val exceptionMessage = e.exception.str(this).valueOr { return it.toFailure() }
            return LiloResult.Failure(error = LiloExceptionMessage(exceptionMessage))
        }
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
        names: List<String>,
        alias: String? = null,
        shouldDefine: Boolean = false
    ): LiloResult<LiloObject> {
        var liloModule = LiloEnvironment.builtins[names[0]]
        if (liloModule == null)
            throw createLiloException(liloModuleNotFoundErrorType, "No module named '${names[0]}'")

        if (liloModule !is LiloModule)
            throw createLiloException(liloModuleNotFoundErrorType, "No module named '${names[0]}'")
        if (shouldDefine && (names.size == 1 || alias == null)) {
            environment.set(name = alias ?: names[0], value = liloModule)
        }

        for (name in names.drop(n = 1)) {
            liloModule = liloModule?.getAttr(name)
                ?: throw createLiloException(liloModuleNotFoundErrorType, "No module named `${name}` inside ${names[0]}")
            if (liloModule !is LiloModule) {
                throw createLiloException(liloModuleNotFoundErrorType, "No module named '${names[0]}'")
            }
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
                    ?: throw createLiloException(liloImportErrorType, "cannot import name '$symbolName' from '${stmt.module}'")
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
        var function : LiloObject = LiloFunction(definition = stmt)

        // Decorators applied in reverse order
        for (decorator in stmt.decorators.reversed()) {
            val decoratorObject = visit(expr = decorator).valueOr { return it.toFailure() }
            if (decoratorObject !is LiloCallable) {
                throw createLiloException(liloTypeErrorType, "Decorator `$decorator` must be callable")
            }
            function = decoratorObject.invoke(interpreter = this, args = listOf(function))
                .valueOr { return it.toFailure() }
        }

        environment.setGlobal(name = stmt.name, value = function)
        return LiloResult.Success(data = Unit)
    }

    override fun visitGlobalStmt(stmt: GlobalStmt): LiloResult<Unit> {
        for (name in stmt.names) environment.markGlobal(name)
        return LiloResult.Success(data = Unit)
    }

    override fun visitNonLocalStmt(stmt: NonLocalStmt): LiloResult<Unit> {
        // FIXME: NonLocal Statement Not yet implemented
        throw createLiloException(liloNotImplementedError, "NonLocal Statement Not yet implemented")
    }

    override fun visitIfStmt(stmt: IfStmt): LiloResult<Unit> {
        for ((expr, body) in stmt.ifs) {
            val condition = visit(expr = expr).valueOr { return it.toFailure() }
            val isTruth = condition.isTrue(this).valueOr { return it.toFailure() }
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
        val target = (stmt.target as NameExpr).value.lexeme!!
        val iter = visit(expr = stmt.iter).valueOr { return it.toFailure() }

        // Technically, an iterator is any object that implements two special methods: .__iter__()
        // which returns the iterator object itself, and .__next__() which returns the next value
        // and raises a StopIteration exception when there are no more items to return.
        val iteratorFunc = iter.getAttr(name = LiloMagicMethod.ITER)
        if (iteratorFunc == null || iteratorFunc !is LiloCallable) {
            throw createLiloException(liloTypeErrorType, "`${iter.type}` is not iterable")
        }

        val iterator =
            iteratorFunc.invoke(interpreter = this, args = listOf(iter)).valueOr { return it.toFailure() }
        val nextFunc = iterator.getAttr(name = LiloMagicMethod.NEXT)
        if (nextFunc == null || nextFunc !is LiloCallable) {
            throw createLiloException(liloTypeErrorType, "${iter.type}' object is not an iterator")
        }

        while (true) {
            try {
                val value = nextFunc.invoke(interpreter = this, args = listOf(iterator))
                    .valueOr { return it.toFailure() }
                environment.set(target, value)
                visit(stmt = stmt.body).valueOr { return it.toFailure() }
            } catch (e: LiloRaise) {
                if (liloStopIterationType == e.exception || liloStopIterationType == e.exception.type) {
                    break
                }
                throw e
            }
        }

        return LiloResult.Success(data = Unit)
    }

    override fun visitWhileStmt(stmt: WhileStmt): LiloResult<Unit> {
        val condition = visit(expr = stmt.condition).valueOr { return it.toFailure() }
        var isTruth = condition.isTrue(this).valueOr { return it.toFailure() }
        if (!isTruth && stmt.elseBlock != null) {
            visit(stmt = stmt.elseBlock).valueOr { return it.toFailure() }
            return LiloResult.Success(data = Unit)
        }

        try {
            do {
                try {
                    visit(stmt = stmt.body).valueOr { return it.toFailure() }
                } catch (_: LiloContinueSignal) {

                }

                // Execute the condition for the next run
                val condition = visit(expr = stmt.condition).valueOr { return it.toFailure() }
                isTruth = condition.isTrue(this).valueOr { return it.toFailure() }
            } while (isTruth)
        } catch (_: LiloBreakSignal) {
            return LiloResult.Success(data = Unit)
        }
        return LiloResult.Success(data = Unit)
    }

    override fun visitBlockStmt(stmt: BlockStmt): LiloResult<Unit> {
        for (node in stmt.nodes) {
            visit(stmt = node).valueOr { return it.toFailure() }
        }
        return LiloResult.Success(data = Unit)
    }

    override fun visitExprStmt(stmt: ExprStmt): LiloResult<Unit> {
        visit(stmt.expr).valueOr { return it.toFailure() }
        return LiloResult.Success(data = Unit)
    }

    override fun visitAnnotatedAssignStmt(stmt: AnnAssignStmt): LiloResult<Unit> {
        // Annotation will be ignored in the interpreter, but it will be used in the GPU Compiler
        val target = stmt.target
        val value = visit(expr = stmt.value).valueOr { return it.toFailure() }
        return when (target) {
            is NameExpr -> {
                environment.set(name = target.value.lexeme!!, value = value)
                LiloResult.Success(data = Unit)
            }

            is GetItemExpr -> {
                val obj = visit(expr = target.obj).valueOr { return it.toFailure() }
                val index = visit(expr = target.index).valueOr { return it.toFailure() }

                val liloSetItemMethod = obj.getAttr(name = LiloMagicMethod.SET_ITEM)
                if (liloSetItemMethod == null || liloSetItemMethod !is LiloCallable) {
                    throw createLiloException(liloTypeErrorType, "Object $obj doesn't support item assignment")
                }

                val invokeResult =
                    liloSetItemMethod.invoke(interpreter = this, args = listOf(obj, index, value))
                if (invokeResult.isFailure()) return invokeResult.toFailure()
                LiloResult.Success(data = Unit)
            }

            else -> throw createLiloException(liloSyntaxErrorType, "cannot assign to literal here. Maybe you meant '==' instead of '='?")
        }
    }

    override fun visitAssignStmt(stmt: AssignStmt): LiloResult<Unit> {
        val target = stmt.target
        val value = visit(expr = stmt.value).valueOr { return it.toFailure() }
        return when (target) {
            is NameExpr -> {
                environment.set(name = target.value.lexeme!!, value = value)
                LiloResult.Success(data = Unit)
            }

            is GetItemExpr -> {
                val obj = visit(expr = target.obj).valueOr { return it.toFailure() }
                val index = visit(expr = target.index).valueOr { return it.toFailure() }

                val liloSetItemMethod = obj.getAttr(name = LiloMagicMethod.SET_ITEM)
                if (liloSetItemMethod == null || liloSetItemMethod !is LiloCallable) {
                    throw createLiloException(liloTypeErrorType, "Object $obj doesn't support item assignment")
                }

                val invokeResult =
                    liloSetItemMethod.invoke(interpreter = this, args = listOf(obj, index, value))
                if (invokeResult.isFailure()) return invokeResult.toFailure()
                LiloResult.Success(data = Unit)
            }

            else -> throw createLiloException(liloSyntaxErrorType, "cannot assign to literal here. Maybe you meant '==' instead of '='?")
        }
    }

    override fun visitRaiseStmt(stmt: RaiseStmt): LiloResult<Unit> {
        if (stmt.exc == null) {
            // FIXME: Implement tracking for Active Exception
            throw createLiloException(liloRuntimeErrorType, "No active exception to reraise")
        }

        val exc = visit(expr = stmt.exc).valueOr { return it.toFailure() }
        val type = exc as? LiloType ?: exc.type
        if (type?.isSubclass(parent = liloBaseExceptionType) == false) {
            throw createLiloException(liloTypeErrorType, "exceptions must derive from BaseException")
        }

        val exceptionArgs = mutableListOf<LiloObject>()
        exceptionArgs.add(LiloStr(value = type!!.name))

        var cause: LiloObject? = null
        stmt.cause?.let {
            cause = visit(expr = stmt.cause).valueOr { return it.toFailure() }
            val isCauseType = cause is LiloType
            val causeType = cause as? LiloType ?: cause.type
            if (causeType?.isSubclass(parent = liloBaseExceptionType) == false) {
                throw createLiloException(liloTypeErrorType, "exceptions cause must derive from BaseException")
            }

            if (isCauseType) {
                cause = LiloObject(type = cause)
                exceptionArgs.add(LiloStr(value = " from ${causeType!!.name}"))
            } else {
                exceptionArgs.add(LiloStr(value = " from ${causeType!!.type?.name}"))
            }
        }

        var exception = exc
        if (exc is LiloType) exception = LiloObject(type = exc)
        if (cause != null) exception.setAttr(name = EXCEPTION_CAUSE_FIELD, value = cause)
        val argsTuple = LiloTuple(values = exceptionArgs)
        exception.setAttr(name = "args", value = argsTuple)
        throw createLiloException(exceptionOjb = exception)
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
        val isTruth = condition.isTrue(this).valueOr { return it.toFailure() }
        if (isTruth) return LiloResult.Success(data = Unit)
        val args = mutableListOf<LiloObject>()
        if (stmt.msg != null) {
            val assertMsg = visit(expr = stmt.msg).valueOr { return it.toFailure() }
            args.add(assertMsg)
        }
        throw createLiloException(liloAssertionErrorType, *args.toTypedArray())
    }

    override fun visitBreakStmt(stmt: BreakStmt): LiloResult<Unit> {
        throw LiloBreakSignal()
    }

    override fun visitContinueStmt(stmt: ContinueStmt): LiloResult<Unit> {
        throw LiloContinueSignal()
    }

    override fun visitPassStmt(stmt: PassStmt): LiloResult<Unit> {
        return LiloResult.Success(data = Unit)
    }

    override fun visitLambdaExpr(expr: LambdaExpr): LiloResult<LiloObject> {
        val definition = FunctionStmt("Lambda", expr.parameters, expr.body)
        val function = LiloFunction(definition)
        return LiloResult.Success(data = function)
    }

    override fun visitGetExpr(expr: GetExpr): LiloResult<LiloObject> {
        val liloObj = visit(expr.obj).valueOr { return it.toFailure() }
        val attribute = expr.name.value.lexeme!!

        // If the attribute found on the instance, return it directly
        val instanceAttr = liloObj.dict[attribute]
        if (instanceAttr != null) return runtimeObject(obj = instanceAttr)

        // If it's a callable on an instance, bind it
        val typeAttr = liloObj.type?.getAttr(name = attribute)
        if (typeAttr != null) {
            if (liloObj !is LiloType && liloObj.type != liloModuleType && typeAttr is LiloCallable) {
                return runtimeObject(obj = LiloMethod(self = liloObj, method = typeAttr))
            }
            return runtimeObject(obj = typeAttr)
        }

        throw createLiloException(liloAttributeErrorType, "'${liloObj.type}' object has no attribute '${attribute}'")
    }

    override fun visitIfExpr(expr: IfExpr): LiloResult<LiloObject> {
        val condition = visit(expr.condition).valueOr { return it.toFailure() }
        val isTruth = condition.isTrue(this).valueOr { return it.toFailure() }
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
            throw createLiloException(liloTypeErrorType, "'${liloObj.type}' object is not subscriptable")
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
                throw createLiloException(liloTypeErrorType, "`${callee.type}` has no `__init__` attribute")
            }
            return initFunction.invoke(interpreter = this, args = args)
        }

        // Call `__call__` if the callee is LiloObject
        val callFunction = callee.getAttr(name = LiloMagicMethod.CALL)
        if (callFunction != null && callFunction is LiloCallable) {
            val fullArgs = listOf(callee) + args
            return callFunction.invoke(interpreter = this, args = fullArgs)
        }

        // In case of function call
        if (callee is LiloCallable) return callee.invoke(interpreter = this, args)
        throw createLiloException(liloTypeErrorType, "`${callee.type}` is not callable")
    }

    override fun visitBinaryExpr(expr: BinaryOpExpr): LiloResult<LiloObject> {
        val lhs = visit(expr.lhs).valueOr { return it.toFailure() }
        val rhs = visit(expr.rhs).valueOr { return it.toFailure() }
        val methodName = when (expr.op) {
            BinaryOp.PLUS -> LiloMagicMethod.ADD
            BinaryOp.MINUS -> LiloMagicMethod.SUB
            BinaryOp.MUL -> LiloMagicMethod.MUL
            BinaryOp.TRUE_DIV -> LiloMagicMethod.TRUE_DIV
            BinaryOp.FLOOR_DIV -> LiloMagicMethod.FLOOR_DIV
            BinaryOp.MOD -> LiloMagicMethod.MOD
            BinaryOp.POW -> LiloMagicMethod.POW
            BinaryOp.RIGHT_SHIFT -> LiloMagicMethod.RIGHT_SHIFT
            BinaryOp.LEFT_SHIFT -> LiloMagicMethod.LEFT_SHIFT
            BinaryOp.BIT_AND -> LiloMagicMethod.BIT_AND
            BinaryOp.BIT_OR -> LiloMagicMethod.BIT_OR
            BinaryOp.BIT_XOR -> LiloMagicMethod.BIT_XOR
        }

        val method = lhs.getAttr(methodName)
            ?: throw createLiloException(liloTypeErrorType, "unsupported operand type(s) for ${methodName}: '${lhs.type}' and '${rhs.type}'")

        if (method !is LiloCallable)
            throw createLiloException(liloTypeErrorType, "$method in `${lhs.type}` must be a callable")

        return method.invoke(interpreter = this, args = listOf(lhs, rhs))
    }

    override fun visitComparisonExpr(expr: ComparisonOpExpr): LiloResult<LiloObject> {
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
            ?: throw createLiloException(liloTypeErrorType, "unsupported operand type(s) for ${methodName}: '${lhs.type}' and '${rhs.type}'")

        if (method !is LiloCallable)
            throw createLiloException(liloTypeErrorType, "$method in `${lhs.type}` must be a callable")

        return method.invoke(interpreter = this, args = listOf(lhs, rhs))
    }

    override fun visitBoolOpExpr(expr: BoolOpExpr): LiloResult<LiloObject> {
        val lhs = visit(expr.lhs).valueOr { return it.toFailure() }
        val isLhsTrue = lhs.isTrue(this).valueOr { return it.toFailure() }

        // Short-circuit evaluation
        val isOrOperand = expr.op == BoolOp.OR
        // Tautology v Any -> Tautology
        if (isOrOperand && isLhsTrue) return LiloResult.Success(data = TRUE)
        // Inconsistent ^ Any -> Inconsistent
        if (!isOrOperand && !isLhsTrue) return LiloResult.Success(data = FALSE)

        val rhs = visit(expr.rhs).valueOr { return it.toFailure() }
        val isRhsTrue = rhs.isTrue(this).valueOr { return it.toFailure() }
        return LiloResult.Success(data = LiloBool(value = isRhsTrue))
    }

    override fun visitUnaryExpr(expr: UnaryOpExpr): LiloResult<LiloObject> {
        val operand = visit(expr.operand).valueOr { return it.toFailure() }
        val methodName = when (expr.op) {
            UnaryOp.PLUS -> LiloMagicMethod.POS
            UnaryOp.MINUS -> LiloMagicMethod.NEG
            UnaryOp.NOT -> LiloMagicMethod.NOT
            UnaryOp.INVERT -> LiloMagicMethod.INVERT
        }

        val method = operand.getAttr(methodName)
            ?: throw createLiloException(liloTypeErrorType, "unsupported operand type(s) for ${methodName}: '${operand.type}'")

        if (method !is LiloCallable)
            throw createLiloException(liloTypeErrorType, "$method in `${operand}` must be a callable")

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

    override fun visitListCompExpr(expr: ListCompExpr): LiloResult<LiloObject> {
        val list = mutableListOf<LiloObject>()
        for (forIfClause in expr.generator) {
            val target = (forIfClause.target as NameExpr).value.lexeme!!
            val iter = visit(expr = forIfClause.iter).valueOr { return it.toFailure() }
            val iteratorFunc = iter.getAttr(name = LiloMagicMethod.ITER)
            if (iteratorFunc == null || iteratorFunc !is LiloCallable) {
                throw createLiloException(liloTypeErrorType, "'${iter.type}' object is not iterable")
            }

            val iterator =
                iteratorFunc.invoke(interpreter = this, args = listOf(iter)).valueOr { return it.toFailure() }
            val nextFunc = iterator.getAttr(name = LiloMagicMethod.NEXT)
            if (nextFunc == null || nextFunc !is LiloCallable) {
                throw createLiloException(liloTypeErrorType, "${iter.type}' object is not an iterator")
            }

            val previous = environment
            environment = LiloEnvironment(enclosing = environment)

            while (true) {
                try {
                    val value = nextFunc.invoke(interpreter = this, args = listOf(iterator))
                        .valueOr { return it.toFailure() }
                    environment.set(name = target, value = value)
                    if (forIfClause.filter != null) {
                        val condition = visit(expr = forIfClause.filter).valueOr { return it.toFailure() }
                        val isTruth = condition.isTrue(this).valueOr { return it.toFailure() }
                        if (!isTruth) continue
                    }

                    val element = visit(expr.elt).valueOr { return it.toFailure() }
                    list.add(element)
                } catch (e: LiloRaise) {
                    if (liloStopIterationType == e.exception || liloStopIterationType == e.exception.type) {
                        break
                    }
                    environment = previous
                    throw e
                }
            }
            environment = previous
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

    override fun visitNameExpr(expr: NameExpr): LiloResult<LiloObject> {
        val name = expr.value.lexeme!!
        val value = environment.get(name)
        if (value != null) return runtimeObject(obj = value)
        val builtin = LiloEnvironment.builtins[name]
        if (builtin != null) return runtimeObject(obj = builtin)
        throw createLiloException(liloNameErrorType, "Name '${name}' is not defined")
    }

    override fun visitStrExpr(expr: StrExpr): LiloResult<LiloObject> {
        return runtimeObject(obj = LiloStr(value = expr.value.lexeme!!))
    }

    override fun visitIntExpr(expr: IntExpr): LiloResult<LiloObject> {
        val value = LiloInt(value = expr.value.lexeme!!.toInt())
        return runtimeObject(obj = value)
    }

    override fun visitFloatExpr(expr: FloatExpr): LiloResult<LiloObject> {
        val value = LiloFloat(value = expr.value.lexeme!!.toDouble())
        return runtimeObject(obj = value)
    }

    override fun visitComplexExpr(expr: ComplexExpr): LiloResult<LiloObject> {
        val complexImag = expr.value.lexeme!!.toDouble()
        val complex = LiloComplex(real = 0.0, imag = complexImag)
        return runtimeObject(obj = complex)
    }

    override fun visitBoolExpr(expr: BoolExpr): LiloResult<LiloObject> {
        val value = if (expr.value.kind == LiloTokenKind.TRUE_KEYWORD) TRUE else FALSE
        return runtimeObject(obj = value)
    }

    override fun visitNoneExpr(expr: NoneExpr): LiloResult<LiloObject> {
        return runtimeObject(obj = LiloNone)
    }

    private fun runtimeObject(obj: LiloObject): LiloResult.Success<LiloObject> {
        return LiloResult.Success(data = obj)
    }
}
