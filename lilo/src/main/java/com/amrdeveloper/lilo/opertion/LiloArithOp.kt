package com.amrdeveloper.lilo.opertion

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloException
import com.amrdeveloper.lilo.`object`.LiloFloat
import com.amrdeveloper.lilo.`object`.LiloInt
import com.amrdeveloper.lilo.`object`.LiloNumber
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.`object`.asFloat
import com.amrdeveloper.lilo.`object`.asInt
import com.amrdeveloper.lilo.`object`.isFloat

class LiloAddOp(val lhs: LiloObject, val rhs: LiloObject) : LiloOperation<LiloObject> {
    override fun run(): LiloResult<LiloObject> {
        if (lhs is LiloNumber && rhs is LiloNumber) {
            val isFloatAdd = lhs.isFloat() || rhs.isFloat()
            val value = if (isFloatAdd) LiloFloat(value = lhs.asFloat() + rhs.asFloat())
            else LiloInt(value = lhs.asInt() + rhs.asInt())
            return LiloResult.Success(data = value)
        }
        return LiloResult.Failure(error = LiloException("Op `+` is unsupported between lhs & rhs"))
    }
}

class LiloSubOp(val lhs: LiloObject, val rhs: LiloObject) : LiloOperation<LiloObject> {
    override fun run(): LiloResult<LiloObject> {
        if (lhs is LiloNumber && rhs is LiloNumber) {
            val isFloatAdd = lhs.isFloat() || rhs.isFloat()
            val value = if (isFloatAdd) LiloFloat(value = lhs.asFloat() - rhs.asFloat())
            else LiloInt(value = lhs.asInt() - rhs.asInt())
            return LiloResult.Success(data = value)
        }
        return LiloResult.Failure(error = LiloException("Op `-` is unsupported between lhs & rhs"))
    }
}

class LiloMulOp(val lhs: LiloObject, val rhs: LiloObject) : LiloOperation<LiloObject> {
    override fun run(): LiloResult<LiloObject> {
        if (lhs is LiloNumber && rhs is LiloNumber) {
            val isFloatAdd = lhs.isFloat() || rhs.isFloat()
            val value = if (isFloatAdd) LiloFloat(value = lhs.asFloat() * rhs.asFloat())
            else LiloInt(value = lhs.asInt() * rhs.asInt())
            return LiloResult.Success(data = value)
        }
        return LiloResult.Failure(error = LiloException("Op `*` is unsupported between lhs & rhs"))
    }
}

class LiloDivOp(val lhs: LiloObject, val rhs: LiloObject) : LiloOperation<LiloObject> {
    override fun run(): LiloResult<LiloObject> {
        if (lhs is LiloNumber && rhs is LiloNumber) {
            val isFloatAdd = lhs.isFloat() || rhs.isFloat()
            val value = if (isFloatAdd) LiloFloat(value = lhs.asFloat() / rhs.asFloat())
            else LiloInt(value = lhs.asInt() / rhs.asInt())
            return LiloResult.Success(data = value)
        }
        return LiloResult.Failure(error = LiloException("Op `/` is unsupported between lhs & rhs"))
    }
}

class LiloModOp(val lhs: LiloObject, val rhs: LiloObject) : LiloOperation<LiloObject> {
    override fun run(): LiloResult<LiloObject> {
        if (lhs is LiloNumber && rhs is LiloNumber) {
            val isFloatAdd = lhs.isFloat() || rhs.isFloat()
            val value = if (isFloatAdd) LiloFloat(value = lhs.asFloat() % rhs.asFloat())
            else LiloInt(value = lhs.asInt() % rhs.asInt())
            return LiloResult.Success(data = value)
        }
        return LiloResult.Failure(error = LiloException("Op `%` is unsupported between lhs & rhs"))
    }
}
