package com.amrdeveloper.lilo.lib.turtle

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.machine.screen.LiloScreen
import com.amrdeveloper.lilo.objects.LiloBaseType
import com.amrdeveloper.lilo.objects.LiloBool
import com.amrdeveloper.lilo.objects.LiloFloat
import com.amrdeveloper.lilo.objects.LiloInt
import com.amrdeveloper.lilo.objects.LiloModule
import com.amrdeveloper.lilo.objects.LiloNone
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.LiloTuple
import com.amrdeveloper.lilo.objects.LiloType
import com.amrdeveloper.lilo.objects.createLiloException
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloTypeErrorType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import kotlin.math.cos
import kotlin.math.sin

private const val MODULE_NAME = "turtle"

val liloTurtleType =
    LiloType(name = "turtle.Turtle", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE

        it.setAttr(name = LiloMagicMethod.INIT, value = TurtleInit)

        // Draw shapes
        it.setAttr(name = "forward", value = TurtleForward)
        it.setAttr(name = "bf", value = TurtleForward)
        it.setAttr(name = "backward", value = TurtleBackward)
        it.setAttr(name = "bk", value = TurtleBackward)
        it.setAttr(name = "left", value = TurtleLeft)
        it.setAttr(name = "lt", value = TurtleLeft)
        it.setAttr(name = "right", value = TurtleRight)
        it.setAttr(name = "rt", value = TurtleRight)
        it.setAttr(name = "circle", value = TurtleCircle)

        // Pointer control
        it.setAttr(name = "showturtle", value = TurtleShowTurtle)
        it.setAttr(name = "st", value = TurtleShowTurtle)
        it.setAttr(name = "hideturtle", value = TurtleHideTurtle)
        it.setAttr(name = "ht", value = TurtleHideTurtle)
        it.setAttr(name = "isvisible", value = TurtleIsVisible)

        // Pen control
        it.setAttr(name = "penup", value = TurtlePenUp)
        it.setAttr(name = "up", value = TurtlePenUp)
        it.setAttr(name = "pu", value = TurtlePenUp)
        it.setAttr(name = "pendown", value = TurtlePenDown)
        it.setAttr(name = "down", value = TurtlePenDown)
        it.setAttr(name = "pd", value = TurtlePenDown)
        it.setAttr(name = "isdown", value = TurtleIsDown)
        it.setAttr(name = "pencolor", value = TurtlePenColor)

        it.setAttr(name = "goto", value = TurtleGoto)

        it.setAttr(name = "clear", value = TurtleClear)
        it.setAttr(name = "pos", value = TurtlePos)
    }

val liloTurtleModule = LiloModule(name = MODULE_NAME).also {
    it.setAttr(name = "Turtle", value = liloTurtleType)
}

data class LiloTurtle(val id: Int = 0) : LiloObject(liloTurtleType) {
    override fun toString() = "<turtle.Turtle object at idx ${id}>"
}

private object TurtleInit : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.isNotEmpty()) {
            throw createLiloException(liloTypeErrorType, "`turtle.__init__` Expect no arguments got ${args.size}")
        }
        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        return LiloResult.Success(data = LiloTurtle(id = screen.initPointer()))
    }
}

private object TurtleForward : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`turtle.forward` Expect 2 arguments got ${args.size}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`turtle.forward` Expect second argument expected number, got ${args[1].type}")
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val distance = when (val distance = args[1]) {
            is LiloFloat -> distance.value
            is LiloInt -> distance.value.toDouble()
            else -> 0.0
        }
        val pointer = screen.getPointerAt(idx = self.id)!!
        val radius = Math.toRadians(pointer.degree)
        val dstX = pointer.x + (distance * cos(x = radius)).toFloat()
        val dstY = pointer.y + (distance * sin(x = radius)).toFloat()

        if (pointer.penDown) {
            pointer.path().lineTo(x = dstX, y = dstY)
        } else {
            pointer.path().moveTo(x = dstX, y = dstY)
        }

        pointer.x = dstX
        pointer.y = dstY
        screen.updateScreen()
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleBackward : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`turtle.backward` Expect 2 arguments got ${args.size}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`turtle.backward` Expect second argument expected number, got ${args[1].type}")
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val distance = when (val distance = args[1]) {
            is LiloFloat -> distance.value
            is LiloInt -> distance.value.toDouble()
            else -> 0.0
        }

        val pointer = screen.getPointerAt(idx = self.id)!!
        val radius = Math.toRadians(pointer.degree)
        val dstX = pointer.x - (distance * cos(x = radius)).toFloat()
        val dstY = pointer.y - (distance * sin(x = radius)).toFloat()

        if (pointer.penDown) {
            pointer.path().lineTo(x = dstX, y = dstY)
        } else {
            pointer.path().moveTo(x = dstX, y = dstY)
        }

        pointer.x = dstX
        pointer.y = dstY
        screen.updateScreen()
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleLeft : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`turtle.left` Expect 2 arguments got ${args.size}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`turtle.left` Expect second argument expected number, got ${args[1].type}")
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val degree = when (val degree = args[1]) {
            is LiloFloat -> degree.value
            is LiloInt -> degree.value.toDouble()
            else -> 0.0
        }

        val pointer = screen.getPointerAt(idx = self.id)!!
        pointer.degree = (pointer.degree + degree) % 360
        screen.updateScreen()
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleRight : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`turtle.right` Expect 2 arguments got ${args.size}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`turtle.right` Expect second argument expected number, got ${args[1].type}")
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val degree = when (val degree = args[1]) {
            is LiloFloat -> degree.value
            is LiloInt -> degree.value.toDouble()
            else -> 0.0
        }

        val pointer = screen.getPointerAt(idx = self.id)!!
        pointer.degree = (pointer.degree - degree) % 360
        screen.updateScreen()
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleCircle : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`turtle.circle` Expect 2 arguments got ${args.size}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`turtle.circle` Expect second argument expected number, got ${args[1].type}")
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val radius = when (val radius = args[1]) {
            is LiloFloat -> radius.value
            is LiloInt -> radius.value.toDouble()
            else -> 0.0
        }

        val pointer = screen.getPointerAt(idx = self.id)!!
        if (pointer.penDown) {
            val angle = Math.toRadians(pointer.degree)
            // Calculate center so that the turtle is on the edge (Python style)
            // Center is 90 degrees to the "left" of the current heading
            val centerX = pointer.x + (radius * cos(angle + Math.PI / 2)).toFloat()
            val centerY = pointer.y + (radius * sin(angle + Math.PI / 2)).toFloat()

            pointer.path().addOval(
                Rect(
                    center = Offset(x = centerX, y = centerY),
                    radius = radius.toFloat()
                )
            )
        }
        screen.updateScreen()
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleShowTurtle : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`turtle.show` Expect 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloTurtle) {
            throw createLiloException(liloTypeErrorType, "`turtle.show` Expect argument to be Turtle, got ${args[0].type}")
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        pointer.visible = true
        screen.updateScreen()
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleHideTurtle : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`turtle.hide` Expect 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloTurtle) {
            throw createLiloException(liloTypeErrorType, "`turtle.hide` Expect argument to be Turtle, got ${args[0].type}")
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        pointer.visible = false
        screen.updateScreen()
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleIsVisible : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`turtle.visible` Expect 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloTurtle) {
            throw createLiloException(liloTypeErrorType, "`turtle.visible` Expect argument to be Turtle, got ${args[0].type}")
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        return LiloResult.Success(data = LiloBool(value = pointer.visible))
    }
}

private object TurtlePenUp : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`turtle.penup` Expect 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloTurtle) {
            throw createLiloException(liloTypeErrorType, "`turtle.penup` Expect argument to be Turtle, got ${args[0].type}")
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        pointer.penDown = false
        screen.updateScreen()
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtlePenDown : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`turtle.pendown` Expect 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloTurtle  ) {
            throw createLiloException(liloTypeErrorType, "`turtle.pendown` Expect argument to be Turtle, got ${args[0].type}")
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        pointer.penDown = true
        screen.updateScreen()
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleIsDown : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`turtle.isdown` Expect 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloTurtle  ) {
            throw createLiloException(liloTypeErrorType, "`turtle.isdown` Expect argument to be Turtle, got ${args[0].type}")
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        return LiloResult.Success(data = LiloBool(value = pointer.penDown))
    }
}

private object TurtlePos : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`turtle.pos` Expect 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloTurtle  ) {
            throw createLiloException(liloTypeErrorType, "`turtle.pos` Expect argument to be Turtle, got ${args[0].type}")
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        val position = listOf(
            LiloFloat(value = pointer.x.toDouble()),
            LiloFloat(value = pointer.y.toDouble())
        )
        return LiloResult.Success(data = LiloTuple(values = position))
    }
}

private object TurtleClear : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`turtle.clear` Expect 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloTurtle  ) {
            throw createLiloException(liloTypeErrorType, "`turtle.clear` Expect argument to be Turtle, got ${args[0].type}")
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle
        val pointer = screen.getPointerAt(idx = self.id)!!
        pointer.path().reset()
        screen.updateScreen()
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtleGoto : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        var x: Float
        var y: Float
        if (args.size == 2 && args[1] is LiloTuple) {
            val tuple = args[1] as LiloTuple
            if (tuple.values.size != 2 || tuple.values[0] !is LiloFloat || tuple.values[1] !is LiloFloat) {
                throw createLiloException(liloTypeErrorType, "`turtle.goto` expect floats x, y or (x, y)")
            }
            x = (tuple.values[0] as LiloFloat).value.toFloat()
            y = (tuple.values[1] as LiloFloat).value.toFloat()
        } else if (args.size == 3 && (args[1] is LiloFloat || args[1] is LiloInt) && (args[2] is LiloFloat || args[2] is LiloInt)) {
            x = when (val lhs = args[1]) {
                is LiloInt -> lhs.value.toFloat()
                is LiloFloat -> lhs.value.toFloat()
                else -> 0.0f
            }
            y = when (val rhs = args[2]) {
                is LiloInt -> rhs.value.toFloat()
                is LiloFloat -> rhs.value.toFloat()
                else -> 0.0f
            }
        } else {
            throw createLiloException(liloTypeErrorType, "`turtle.goto` expect floats x, y or (x, y)")
        }

        val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
        val self = args[0] as LiloTurtle

        val pointer = screen.getPointerAt(idx = self.id)!!
        if (pointer.penDown) {
            pointer.path().lineTo(x = x, y = y)
        } else {
            pointer.path().moveTo(x = x, y = y)
        }

        pointer.x = x
        pointer.y = y
        screen.updateScreen()
        return LiloResult.Success(data = LiloNone)
    }
}

private object TurtlePenColor : LiloObject(liloFunctionType), LiloCallable {
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
                throw createLiloException(liloTypeErrorType, "`turtle.pencolor` expects tuple of floats or ints as (r, g, b)")
            }

            val screen = interpreter.liloMachine.getScreen()!! as LiloScreen
            val self = args[0] as LiloTurtle
            val pointer = screen.getPointerAt(idx = self.id)!!

            val r = rgb.values[0]
            val g = rgb.values[1]
            val b = rgb.values[2]

            if ((r is LiloInt && g is LiloInt && b is LiloInt)) {
                pointer.setColor(Color(r.value, g.value, b.value))
                screen.updateScreen()
                return LiloResult.Success(data = LiloNone)
            }

            if (r is LiloFloat && g is LiloFloat && b is LiloFloat) {
                pointer.setColor(Color(r.value.toFloat(), g.value.toFloat(), b.value.toFloat()))
                screen.updateScreen()
                return LiloResult.Success(data = LiloNone)
            }

            throw createLiloException(liloTypeErrorType, "`turtle.pencolor` expects tuple of floats or ints as (r, g, b)")
        }

        throw createLiloException(liloTypeErrorType, "`turtle.pencolor` expects tuple of floats or ints as (r, g, b)")
    }
}
