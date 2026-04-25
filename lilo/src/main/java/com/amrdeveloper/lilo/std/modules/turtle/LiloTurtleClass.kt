package com.amrdeveloper.lilo.std.modules.turtle

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.machine.screen.LiloScreen
import com.amrdeveloper.lilo.`object`.LiloFloat
import com.amrdeveloper.lilo.`object`.LiloNone
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloException
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.liloMethodType

data class LiloTurtle(val id: Int = 0) : LiloObject(liloTurtleType) {

    init {
        setAttr(name = "goto", value = TurtleGoto)
    }

    override fun toString() = "<turtle.Turtle object at idx ${id}>"
}

private object TurtleGoto : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 3 || args[1] !is LiloFloat || args[2] !is LiloFloat) {
            return LiloResult.Failure(error = LiloException("`turtle.goto` expect 2 floats as x and y"))
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val x = (args[1] as LiloFloat).value
        val y = (args[2] as LiloFloat).value

        val pointer = screen.getPointerAt(idx = self.id)!!
        pointer.x = x
        pointer.y = y

        return LiloResult.Success(data = LiloNone())
    }
}
