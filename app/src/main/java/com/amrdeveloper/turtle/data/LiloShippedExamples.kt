package com.amrdeveloper.turtle.data

val liloShippedExamples = listOf(
    LiloFileEntity(
        name = "HelloWorld",
        sourceCode = """
        print("Hello, World")
        """.trimIndent()
    ),
    // Turtle Examples
    LiloFileEntity(
        name = "TurtleSquare",
        sourceCode = """
        import turtle

        t = turtle.Turtle()
        i = 0
        while i < 4:
            t.forward(100.0)
            t.right(90.0)
            i = i + 1
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "TurtleTriangle",
        sourceCode = """
        import turtle

        t = turtle.Turtle()
        i = 0
        while i < 4:
            t.forward(120.0)
            t.left(120.0)
            i = i + 1
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "TurtlePentagon",
        sourceCode = """
        import turtle

        t = turtle.Turtle()
        i = 0
        while i < 5:
            t.forward(100.0)
            t.right(72.0)
            i = i + 1
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "TurtleHexagon",
        sourceCode = """
        import turtle

        t = turtle.Turtle()
        i = 0
        while i < 6:
            t.forward(80.0)
            t.right(60.0)
            i = i + 1
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "TurtleStar",
        sourceCode = """
        import turtle

        t = turtle.Turtle()
        i = 0
        while i < 5:
            t.forward(200.0)
            t.right(144.0)
            i = i + 1
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "TurtleCircle",
        sourceCode = """
        import turtle

        t = turtle.Turtle()
        t.circle(100.0)
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "TurtleCircle",
        sourceCode = """
        import turtle
        
        t = turtle.Turtle()
        i = 0
        while i < 200:
            t.forward(i * 4.0)
            t.right(91.0)
            i = i + 1
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "TurtleSquareSpiral",
        sourceCode = """
        import turtle
        
        t = turtle.Turtle()
        i = 0
        while i < 200:
            t.forward(i * 4.0)
            t.right(90.0)
            i = i + 1
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "TurtleExpandingPolygon",
        sourceCode = """
        import turtle
        
        t = turtle.Turtle()
        
        size = 20
        while size < 300:
            side = 0
            while side < 6:
                t.forward(size)
                t.right(60.0)
                side = side + 1
            t.right(10.0)
            size = size + 10
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "TurtleFlowerOfCircles",
        sourceCode = """
        import turtle
        
        t = turtle.Turtle()
        
        rotation = 0
        while rotation < 72:
            t.circle(120)
            t.left(5)
            rotation = rotation + 1
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "TurtleFractalTree",
        sourceCode = """
        import turtle
        
        t = turtle.Turtle()
        t.left(90)
        
        def branch(length):
            if length < 8:
                return
            t.forward(length)
            t.left(25)
            branch(length - 15)
            t.right(50)
            branch(length - 15)
            t.left(25)
            t.backward(length)
        
        branch(120)
        """.trimIndent()
    ),
)
