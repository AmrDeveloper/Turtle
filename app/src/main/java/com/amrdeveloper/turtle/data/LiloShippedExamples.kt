package com.amrdeveloper.turtle.data

val liloShippedExamples = listOf(
    // Lilo Examples
    LiloFileEntity(
        name = "HelloWorld",
        sourceCode = """
        print("Hello, World")
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "ForElseStmt",
        sourceCode = """
        for i in range(0):
            print(i)
        else:
            print("Else")
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "TupleIterators",
        sourceCode = """
        for i in (1, 2, 3):
            print(i)
            
        for i in 1, 2, 3:
            print(i)
            
       for i in reversed((1, 2, 3)):
            print(i) 
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "Inspect",
        sourceCode = """
        import inspect
        
        print(inspect.ismodule(inspect))
        print(inspect.isfunction(list.append))
        print(inspect.ismethod([1].append))
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "ListComprehension",
        sourceCode = """
        l = [x for x in range(3)]
        print(l)
        
        l2 = [x for x in range(3) for x in range(3)]
        print(l2)
        
        l3 = [x for x in range(3) if x > 0]
        print(l3)
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "SetComprehension",
        sourceCode = """
        s1 = {x for x in range(3)}
        print(s1)
        
        s2 = {x for x in range(3) for x in range(3)}
        print(s2)
        
        s3 = {x for x in range(3) if x > 0}
        print(s3)
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "ListIterator",
        sourceCode = """
        print("Iterator")
        for i in iter(range(10)):
            print(i)
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "ListIteratorAndReversed",
        sourceCode = """
        print("Iterator")
        for i in iter(range(10)):
            print(i)
            
        print("Reversed")
        for i in reversed(range(10)):
            print(i)
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
        name = "TurtleStarWhile",
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
        name = "TurtleStarFor",
        sourceCode = """
        import turtle

        t = turtle.Turtle()
        for i in range(5):
            t.forward(200.0)
            t.right(144.0)
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
    LiloFileEntity(
        name = "TurtleMandelbrot",
        sourceCode = """
        import turtle
        
        t = turtle.Turtle()
        t.hideturtle()
        
        max_iter = 30
        zoom = 200.0
        move_x = -0.75
        move_y = 0.0
        screen_x = -400
        while screen_x < 400:
            screen_y = -400
            while screen_y < 400:
                cx = screen_x / zoom + move_x
                cy = screen_y / zoom + move_y
                zx = 0.0
                zy = 0.0
                iteration = 0
                while iteration < max_iter:
                    new_zx = zx * zx - zy * zy + cx
                    new_zy = 2 * zx * zy + cy
                    zx = new_zx
                    zy = new_zy
                    if zx * zx + zy * zy > 4:
                        break
                    iteration = iteration + 1
                if iteration < max_iter:
                    # Color based on iteration count
                    r = (iteration * 10) % 256
                    g = (iteration * 15) % 256
                    b = (iteration * 20) % 256
                    t.pencolor((r, g, b))
                else:
                    # Points inside the set (the main body)
                    t.pencolor((0, 0, 0))
                t.penup()
                t.goto(screen_x, screen_y)
                t.pendown()
                t.circle(2)
                screen_y = screen_y + 4
            screen_x = screen_x + 4
        """.trimIndent()
    ),
    // GPU Examples
    LiloFileEntity(
        name = "GPU_VecAdd_Global_id",
        sourceCode = """
        from gpu import (
            gpu,
            Dim,
            LaunchConfig,
            ConfiguredKernal
        )

        @gpu
        def vec_add(a, b, out c):
          i = 0 if gpu.global_id.x < 4 else gpu.global_id.x
          c[i] = a[i] + b[i]
        
        a = [1.0, 2.0, 3.0, 4.0]
        b = [5.0, 6.0, 7.0, 8.0]
        c = [0.0, 0.0, 0.0, 0.0]
        
        blocks = Dim(1, 1, 1)
        threads = Dim(4, 1, 1)
        config = LaunchConfig(blocks, threads)
        kernal = ConfiguredKernal(vec_add, config)
        kernal(a, b, c)
        print(c)
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "GPU_VecAdd",
        sourceCode = """
        from gpu import (
            gpu,
            Dim,
            LaunchConfig,
            ConfiguredKernal
        )
        
        @gpu
        def vec_add(a, b, out c):
          i = gpu.block_dim.x * gpu.block_idx.x + gpu.thread_idx.x
          c[i] = a[i] + b[i]
        
        a = [1.0, 2.0, 3.0, 4.0]
        b = [5.0, 6.0, 7.0, 8.0]
        c = [0.0, 0.0, 0.0, 0.0]
        
        blocks = Dim(1, 1, 1)
        threads = Dim(4, 1, 1)
        config = LaunchConfig(blocks, threads)
        kernal = ConfiguredKernal(vec_add, config)
        kernal(a, b, c)
        print(c)
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "GPU_VecAddSugar",
        sourceCode = """
        from gpu import (gpu, Dim)
                
        @gpu
        def vec_add(a, b, out c):
          i = gpu.block_dim.x * gpu.block_idx.x + gpu.thread_idx.x
          c[i] = a[i] + b[i]
        
        a = [1.0, 2.0, 3.0, 4.0]
        b = [5.0, 6.0, 7.0, 8.0]
        c = [0.0, 0.0, 0.0, 0.0]
                
        blocks = Dim(1, 1, 1)
        threads = Dim(4, 1, 1)
        vec_add[blocks, threads](a, b, c)
        print(c)
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "GPU_VecAddSugarWithPow",
        sourceCode = """
        from gpu import (gpu, Dim)
                
        @gpu
        def vec_add(a, b, out c):
          i = gpu.block_dim.x * gpu.block_idx.x + gpu.thread_idx.x
          c[i] = a[i] + b[i] ** 2
        
        a = [1.0, 2.0, 3.0, 4.0]
        b = [5.0, 6.0, 7.0, 8.0]
        c = [0.0, 0.0, 0.0, 0.0]
                
        blocks = Dim(1, 1, 1)
        threads = Dim(4, 1, 1)
        vec_add[blocks, threads](a, b, c)
        print(c)
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "GPU_DeviceInfo",
        sourceCode = """
        import gpu
                
        print("Max Threads Dim: ", gpu.max_threads_dim())
        print("Max Threads per block: ", gpu.max_threads_per_block())
        print("Wrap size: ", gpu.wrap_size())
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "GPUvsCPU",
        sourceCode = """
        from gpu import (gpu, Dim, LaunchConfig, ConfiguredKernal)
        import time
                        
        @gpu
        def vec_add(a, b, out c):
          i = gpu.block_dim.x * gpu.block_idx.x + gpu.thread_idx.x
          c[i] = a[i] + b[i] ** 2
          
        def normal_vec_add(a, b, c):
            size = len(a)
            for i in range(size):
                c[i] = a[i] + b[i] ** 2
        
        a = [1.0 for x in range(1048576)]
        b = [1.0 for x in range(1048576)]
        c = [0.0 for x in range(1048576)]
                
        blocks = Dim(4096, 1, 1)
        threads = Dim(256, 1, 1)
        
        start = time.time()
        vec_add[blocks, threads](a, b, c)
        print("GPU Time ", time.time() - start)
        
        start2 = time.time()
        normal_vec_add(a, b, c)
        print("CPU Time ", time.time() - start2)    
        """.trimIndent()
    ),
    LiloFileEntity(
        name = "Exception",
        sourceCode = """
         try:
            raise NameError
         except NameError:
            print(2)
         else:
            print(3)
        finally:
            print(4)
        """.trimIndent()
    ),
)
