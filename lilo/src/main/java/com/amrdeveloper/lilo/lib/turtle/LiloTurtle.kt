package com.amrdeveloper.lilo.lib.turtle

import com.amrdeveloper.lilo.objects.LiloModule

private const val MODULE_NAME = "turtle"

val liloTurtleModule = LiloModule(name = MODULE_NAME).also {
    it.setAttr(name = "Turtle", value = liloTurtleType)
}
