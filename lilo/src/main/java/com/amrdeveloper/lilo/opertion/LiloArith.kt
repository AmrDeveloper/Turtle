package com.amrdeveloper.lilo.opertion

import runtime.LiloException
import common.LiloResult
import com.amrdeveloper.lilo.value.LiloFloat
import com.amrdeveloper.lilo.value.LiloInt
import com.amrdeveloper.lilo.value.LiloNumber
import com.amrdeveloper.lilo.value.LiloValue
import com.amrdeveloper.lilo.value.asFloat
import com.amrdeveloper.lilo.value.asInt
import com.amrdeveloper.lilo.value.isFloat

class LiloAddOp(val lhs: LiloValue, val rhs: LiloValue) : LiloOperation<LiloValue> {
    override fun run(): LiloResult<LiloValue> {
        if (lhs is LiloNumber && rhs is LiloNumber) {
            val isFloatAdd = lhs.isFloat() || rhs.isFloat()
            val value = if (isFloatAdd) LiloFloat(value = lhs.asFloat() + rhs.asFloat())
            else LiloInt(value = lhs.asInt() + rhs.asInt())
            return LiloResult.Success(data = value)
        }
        return LiloResult.Failure(error = LiloException("Op `+` is unsupported between lhs & rhs"))
    }
}

class LiloSubOp(val lhs: LiloValue, val rhs: LiloValue) : LiloOperation<LiloValue> {
    override fun run(): LiloResult<LiloValue> {
        if (lhs is LiloNumber && rhs is LiloNumber) {
            val isFloatAdd = lhs.isFloat() || rhs.isFloat()
            val value = if (isFloatAdd) LiloFloat(value = lhs.asFloat() - rhs.asFloat())
            else LiloInt(value = lhs.asInt() - rhs.asInt())
            return LiloResult.Success(data = value)
        }
        return LiloResult.Failure(error = LiloException("Op `-` is unsupported between lhs & rhs"))
    }
}

class LiloMulOp(val lhs: LiloValue, val rhs: LiloValue) : LiloOperation<LiloValue> {
    override fun run(): LiloResult<LiloValue> {
        if (lhs is LiloNumber && rhs is LiloNumber) {
            val isFloatAdd = lhs.isFloat() || rhs.isFloat()
            val value = if (isFloatAdd) LiloFloat(value = lhs.asFloat() * rhs.asFloat())
            else LiloInt(value = lhs.asInt() * rhs.asInt())
            return LiloResult.Success(data = value)
        }
        return LiloResult.Failure(error = LiloException("Op `-` is unsupported between lhs & rhs"))
    }
}

class LiloDivOp(val lhs: LiloValue, val rhs: LiloValue) : LiloOperation<LiloValue> {
    override fun run(): LiloResult<LiloValue> {
        if (lhs is LiloNumber && rhs is LiloNumber) {
            val isFloatAdd = lhs.isFloat() || rhs.isFloat()
            val value = if (isFloatAdd) LiloFloat(value = lhs.asFloat() / rhs.asFloat())
            else LiloInt(value = lhs.asInt() / rhs.asInt())
            return LiloResult.Success(data = value)
        }
        return LiloResult.Failure(error = LiloException("Op `-` is unsupported between lhs & rhs"))
    }
}

class LiloModOp(val lhs: LiloValue, val rhs: LiloValue) : LiloOperation<LiloValue> {
    override fun run(): LiloResult<LiloValue> {
        if (lhs is LiloNumber && rhs is LiloNumber) {
            val isFloatAdd = lhs.isFloat() || rhs.isFloat()
            val value = if (isFloatAdd) LiloFloat(value = lhs.asFloat() % rhs.asFloat())
            else LiloInt(value = lhs.asInt() % rhs.asInt())
            return LiloResult.Success(data = value)
        }
        return LiloResult.Failure(error = LiloException("Op `-` is unsupported between lhs & rhs"))
    }
}
