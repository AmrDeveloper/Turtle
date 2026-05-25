package com.amrdeveloper.lilo.std.modules.turtle

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.machine.screen.LiloScreen
import com.amrdeveloper.lilo.`object`.LiloBool
import com.amrdeveloper.lilo.`object`.LiloFloat
import com.amrdeveloper.lilo.`object`.LiloInt
import com.amrdeveloper.lilo.`object`.LiloNone
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.`object`.LiloTuple
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.liloMethodType
import kotlin.math.cos
import kotlin.math.sin

data class LiloTurtle(val id: Int = 0) : LiloObject(liloTurtleType) {

    init {
        // Draw shapes
        setAttr(name = "forward", value = TurtleForward)
        setAttr(name = "bf", value = TurtleForward)
        setAttr(name = "backward", value = TurtleBackword)
        setAttr(name = "bk", value = TurtleBackword)
        setAttr(name = "left", value = TurtleLeft)
        setAttr(name = "lt", value = TurtleLeft)
        setAttr(name = "right", value = TurtleRight)
        setAttr(name = "rt", value = TurtleRight)
        setAttr(name = "circle", value = TurtleCircle)

        // Pointer control
        setAttr(name = "showturtle", value = TurtleShowTurtle)
        setAttr(name = "st", value = TurtleShowTurtle)
        setAttr(name = "hideturtle", value = TurtleHideTurtle)
        setAttr(name = "ht", value = TurtleHideTurtle)
        setAttr(name = "isvisible", value = TurtleIsVisible)

        // Pen control
        setAttr(name = "penup", value = TurtlePenUp)
        setAttr(name = "up", value = TurtlePenUp)
        setAttr(name = "pu", value = TurtlePenUp)
        setAttr(name = "pendown", value = TurtlePenDown)
        setAttr(name = "down", value = TurtlePenDown)
        setAttr(name = "pd", value = TurtlePenDown)
        setAttr(name = "isdown", value = TurtleIsDown)
        setAttr(name = "pencolor", value = TurtlePenColor)

        setAttr(name = "goto", value = TurtleGoto)

        setAttr(name = "clear", value = TurtleClear)
        setAttr(name = "pos", value = TurtlePos)
    }

    override fun toString() = "<turtle.Turtle object at idx ${id}>"
}

private object TurtleForward : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2 || args[1] !is LiloFloat) {
            return LiloResult.Failure(error = LiloExceptionMessage("`turtle.forward` expect 1 floats as distance"))
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val distance = (args[1] as LiloFloat).value
        val pointer = screen.getPointerAt(idx = self.id)!!

        val radius = Math.toRadians(pointer.degree.toDouble())
        val dstX = pointer.x + (distance * cos(x = radius)).toFloat()
        val dstY = pointer.y + (distance * sin(x = radius)).toFloat()

        if (pointer.penDown) {
            pointer.path.lineTo(x = dstX, y = dstY)
        } else {
            pointer.path.moveTo(x = dstX, y = dstY)
        }

        pointer.x = dstX
        pointer.y = dstY
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleBackword : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2 || args[1] !is LiloFloat) {
            return LiloResult.Failure(error = LiloExceptionMessage("`turtle.backward` expect 1 floats as distance"))
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val distance = (args[1] as LiloFloat).value
        val pointer = screen.getPointerAt(idx = self.id)!!

        val radius = Math.toRadians(pointer.degree.toDouble())
        val dstX = pointer.x - (distance * cos(x = radius)).toFloat()
        val dstY = pointer.y - (distance * sin(x = radius)).toFloat()

        if (pointer.penDown) {
            pointer.path.lineTo(x = dstX, y = dstY)
        } else {
            pointer.path.moveTo(x = dstX, y = dstY)
        }

        pointer.x = dstX
        pointer.y = dstY

        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleLeft : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2 || args[1] !is LiloFloat) {
            return LiloResult.Failure(error = LiloExceptionMessage("`turtle.left` expect 1 floats as degree"))
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val degree = (args[1] as LiloFloat).value
        val pointer = screen.getPointerAt(idx = self.id)!!
        pointer.degree = (pointer.degree + degree) % 360
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleRight : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2 || args[1] !is LiloFloat) {
            return LiloResult.Failure(error = LiloExceptionMessage("`turtle.right` expect 1 floats as degree"))
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val degree = (args[1] as LiloFloat).value
        val pointer = screen.getPointerAt(idx = self.id)!!
        pointer.degree = (pointer.degree - degree) % 360
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleCircle : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2 || args[1] !is LiloFloat) {
            return LiloResult.Failure(error = LiloExceptionMessage("`turtle.circle` expect 1 floats as radius"))
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val radius = (args[1] as LiloFloat).value
        val pointer = screen.getPointerAt(idx = self.id)!!
        if (pointer.penDown) {
            pointer.path.addOval(
                Rect(
                    center = Offset(x = pointer.x, y = pointer.y),
                    radius = radius
                )
            )
        }
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleShowTurtle : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        pointer.visible = true
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleHideTurtle : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        pointer.visible = false
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleIsVisible : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        return LiloResult.Success(data = LiloBool(value = pointer.visible))
    }
}

private object TurtlePenUp : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        pointer.penDown = false
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtlePenDown : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        pointer.penDown = true
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleIsDown : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        return LiloResult.Success(data = LiloBool(value = pointer.penDown))
    }
}

private object TurtlePos : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        val position = listOf(
            LiloFloat(value = pointer.x),
            LiloFloat(value = pointer.y)
        )
        return LiloResult.Success(data = LiloTuple(values = position))
    }
}

private object TurtleClear : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        pointer.path.reset()
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleGoto : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 3 || args[1] !is LiloFloat || args[2] !is LiloFloat) {
            return LiloResult.Failure(error = LiloExceptionMessage("`turtle.goto` expect 2 floats as x and y"))
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val x = (args[1] as LiloFloat).value
        val y = (args[2] as LiloFloat).value

        val pointer = screen.getPointerAt(idx = self.id)!!
        if (pointer.penDown) {
            pointer.path.relativeLineTo(dx = x, dy = y)
        } else {
            pointer.path.moveTo(x = x, y = y)
        }

        pointer.x = x
        pointer.y = y

        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtlePenColor : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        //  - pencolor((r, g, b))
        //     Set pencolor to the RGB color represented by the tuple of
        //     r, g, and b.  Each of r, g, and b must be in the range
        //     0..colormode, where colormode is either 1.0 or 255
        if (args.size == 2 && args[1] is LiloTuple) {
            val rgb = args[1] as LiloTuple
            if (rgb.values.size != 3) {
                return LiloResult.Failure(error = LiloExceptionMessage("`turtle.pencolor` expects tuple of floats or ints as (r, g, b)"))
            }

            val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
            val self = args[0] as LiloTurtle
            val pointer = screen.getPointerAt(idx = self.id)!!

            val r = rgb.values[0]
            val g = rgb.values[1]
            val b = rgb.values[2]

            if ((r is LiloInt && g is LiloInt && b is LiloInt)) {
                pointer.color = Color(r.value, g.value, b.value)
                return LiloResult.Success(data = LiloNone)
            }

            if (r is LiloFloat && g is LiloFloat && b is LiloFloat) {
                pointer.color = Color(r.value, g.value, b.value)
                return LiloResult.Success(data = LiloNone)
            }

            return LiloResult.Failure(error = LiloExceptionMessage("`turtle.pencolor` expects tuple of floats or ints as (r, g, b)"))
        }

        return LiloResult.Failure(error = LiloExceptionMessage("`turtle.pencolor` expects tuple of floats or ints as (r, g, b)"))
    }
}
