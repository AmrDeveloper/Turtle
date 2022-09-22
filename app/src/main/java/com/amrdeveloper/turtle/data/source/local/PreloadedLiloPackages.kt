package com.amrdeveloper.turtle.data.source.local

import com.amrdeveloper.turtle.data.LiloPackage

/**
 * A list of preloaded lilo packages that inserted into the database once it created
 */
val preloadedLiloPackages = listOf (

    /**
     * A lilo package to draw a Fractal Tree with green color
     */
    LiloPackage("Fractal Tree", """
        move 375, 400
        let angle = 30
        color GREEN
        rotate 90
        fun draw(size, level) {
           if level > 0 {
              forward size
              rotate angle
              draw(0.8 * size, level - 1)
              rotate -2 * angle
              draw(0.8 * size, level - 1)
              rotate angle
              forward -size
           }
        }
        draw(80, 7)
    """.trimIndent()),

    /**
     * A Lilo Package to draw nested stars
     */
    LiloPackage("Nested Stars", """
        move 150, 500
        color GREEN

        fun drawStar(size) {
           if size > 10 {
              repeat 5 {
                 forward size
                 drawStar(size / 3)
                 rotate 216
              }
           }
        }

        drawStar(360)
    """.trimIndent()),

    /**
     * A Lilo Package to draw a red sun
     */
    LiloPackage("Nested Stars", """
        move 100, 500
        color RED
        repeat 50 {
            forward 500
            rotate170
        }
    """.trimIndent())
)