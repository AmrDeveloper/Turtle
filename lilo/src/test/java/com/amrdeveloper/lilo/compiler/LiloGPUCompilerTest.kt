package com.amrdeveloper.lilo.compiler

import com.amrdeveloper.lilo.common.LiloDiagnostic
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.isSuccess
import com.amrdeveloper.lilo.common.toFailureError
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.lib.gpu.LiloGPUDim
import com.amrdeveloper.lilo.lib.gpu.LiloLaunchConfig
import com.amrdeveloper.lilo.machine.device.LiloConfigDim3
import com.amrdeveloper.lilo.parser.LiloLexer
import com.amrdeveloper.lilo.parser.LiloParser
import org.junit.Assert.assertTrue
import org.junit.Test

class LiloGPUCompilerTest {

    @Test
    fun compile_lilo_to_webgpu_test() {
        val sourceCodes = mutableListOf(
            """
            def empty_function():
                pass
            """,
            """
            def vec_add(a, b, out c):
                i = gpu.global_id.x
                c[i] = a[i] + b[i]
            """,
            """
            def vec_add(a, b, out c):
                i = 0 if gpu.global_id.x < 4 else gpu.global_id.x
                c[i] = a[i] + b[i]
            """,
        )

        val expectedResults = mutableListOf(
            """
            const block_dim = vec3<u32>(2u, 1u, 1u);
            @compute @workgroup_size(2, 1, 1)
            fn main(
              @builtin(workgroup_id) block_idx: vec3<u32>,
              @builtin(local_invocation_id) thread_idx: vec3<u32>,
              @builtin(global_invocation_id) global_id: vec3<u32>
            )
            {
              return;
            }
            """.trimIndent(),
            """
            @group(0) @binding(0) var<storage, read> a: array<f32>;
            @group(0) @binding(1) var<storage, read> b: array<f32>;
            @group(0) @binding(2) var<storage, read_write> c: array<f32>;

            const block_dim = vec3<u32>(2u, 1u, 1u);
            @compute @workgroup_size(2, 1, 1)
            fn main(
              @builtin(workgroup_id) block_idx: vec3<u32>,
              @builtin(local_invocation_id) thread_idx: vec3<u32>,
              @builtin(global_invocation_id) global_id: vec3<u32>
            )
            {
              var i = global_id.x;
              c[i] = a[i] + b[i];
            }
            """.trimIndent(),
            """
            @group(0) @binding(0) var<storage, read> a: array<f32>;
            @group(0) @binding(1) var<storage, read> b: array<f32>;
            @group(0) @binding(2) var<storage, read_write> c: array<f32>;

            const block_dim = vec3<u32>(2u, 1u, 1u);
            @compute @workgroup_size(2, 1, 1)
            fn main(
              @builtin(workgroup_id) block_idx: vec3<u32>,
              @builtin(local_invocation_id) thread_idx: vec3<u32>,
              @builtin(global_invocation_id) global_id: vec3<u32>
            )
            {
              var i = select(0, global_id.x, global_id.x < 4);
              c[i] = a[i] + b[i];
            }
            """.trimIndent()
        )

        val dim3 = LiloGPUDim(LiloConfigDim3(x = 2, y = 1, z = 1))
        val config = LiloLaunchConfig(blocksDim = dim3, threadsDim = dim3)
        for ((index, sourceCode) in sourceCodes.withIndex()) {
            val lexerResult = LiloLexer(source = sourceCode).tokenize()
            if (lexerResult.isFailure()) {
                println("Error[Lexer]: " + lexerResult.toFailureError<LiloDiagnostic>().message)
            }
            assertTrue("Lexer error", lexerResult.isSuccess())

            val parseResult = LiloParser(tokens = lexerResult.toSuccessData()).parse()
            if (parseResult.isFailure()) {
                println("Error[Parser]: " + parseResult.toFailureError<LiloDiagnostic>().message)
            }
            assertTrue("Parser error", parseResult.isSuccess())
            val result = parseResult.toSuccessData()
            val gpuCodeResult = LiloGPUCompiler(config).visitProgram(result)
            if (gpuCodeResult.isFailure()) {
                println("Error[GPUCompiler]: " + gpuCodeResult.toFailureError<LiloDiagnostic>().message)
            }

            assertTrue(
                "GPU Compiler results",
                expectedResults[index] == gpuCodeResult.toSuccessData().trimIndent()
            )
        }
    }
}
