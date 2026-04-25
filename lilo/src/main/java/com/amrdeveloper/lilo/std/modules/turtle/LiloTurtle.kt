package com.amrdeveloper.lilo.std.modules.turtle

import com.amrdeveloper.lilo.`object`.LiloModule

private const val MODULE_NAME = "turtle"

val liloTurtleModule = LiloModule(name = MODULE_NAME).also {
    it.setAttr(name = "Turtle", value = liloTurtleType)
}
