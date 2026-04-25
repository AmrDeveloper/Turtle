package com.amrdeveloper.lilo.std.modules.turtle

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.machine.screen.LiloScreen
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.LiloBaseType
import com.amrdeveloper.lilo.type.LiloType
import com.amrdeveloper.lilo.type.liloFunctionType

val liloTurtleType =
    LiloType(name = "turtle.Turtle", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE

        it.setAttr(name = LiloMagicMethod.INIT, value = TurtleInit)

    }

private object TurtleInit : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        return LiloResult.Success(data = LiloTurtle(id = screen.initPointer()))
    }
}
