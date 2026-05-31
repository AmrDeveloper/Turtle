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

class LiloDecoratorTest {

    @Test
    fun compile_lilo_to_webgpu_test() {
        val sourceCodes = mutableListOf(
            """
            def empty_function():
                pass
            """,
        )

        val expectedResults = mutableListOf(
            """
            @compute @workgroup_size(2, 1, 1)
            fn main(@builtin(global_invocation_id) global_id: vec3<u32>)
            {
              return;
            }
            """.trimIndent()
        )

        val dim3 = LiloGPUDim(LiloConfigDim3(2, 1, 1))
        val config = LiloLaunchConfig(dim3, dim3)
        val gpuCompiler = LiloGPUCompiler(config)
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
            val gpuCodeResult = gpuCompiler.visitProgram(result)
            if (gpuCodeResult.isFailure()) {
                println("Error[GPUCompiler]: " + gpuCodeResult.toFailureError<LiloDiagnostic>().message)
            }
            assertTrue("GPU Compiler results", expectedResults[index] == gpuCodeResult.toSuccessData().trimIndent())
        }
    }
}
