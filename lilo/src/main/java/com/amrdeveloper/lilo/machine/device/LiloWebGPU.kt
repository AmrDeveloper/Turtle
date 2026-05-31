package com.amrdeveloper.lilo.machine.device

import androidx.webgpu.BufferUsage
import androidx.webgpu.GPUBindGroupDescriptor
import androidx.webgpu.GPUBindGroupEntry
import androidx.webgpu.GPUBuffer
import androidx.webgpu.GPUBufferDescriptor
import androidx.webgpu.GPUComputePipelineDescriptor
import androidx.webgpu.GPUComputeState
import androidx.webgpu.GPUDevice
import androidx.webgpu.GPUShaderModuleDescriptor
import androidx.webgpu.GPUShaderSourceWGSL
import androidx.webgpu.MapMode
import androidx.webgpu.helper.WebGpu
import androidx.webgpu.helper.createWebGpu
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.lib.gpu.LiloConfiguredKernal
import com.amrdeveloper.lilo.objects.LiloFloat
import com.amrdeveloper.lilo.objects.LiloInt
import com.amrdeveloper.lilo.objects.LiloList
import com.amrdeveloper.lilo.objects.LiloNone
import com.amrdeveloper.lilo.objects.LiloObject
import java.nio.ByteOrder

class LiloWebGPU : LiloAbstractGPU {

    private lateinit var webGpu: WebGpu
    private lateinit var device: GPUDevice

    override suspend fun initWebGPU() {
        webGpu = createWebGpu()
        device = webGpu.device
    }

    override suspend fun launchKernal(
        gpuCode: String,
        kernal: LiloConfiguredKernal,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val device = webGpu.device
        val module = device.createShaderModule(
            GPUShaderModuleDescriptor(
                shaderSourceWGSL = GPUShaderSourceWGSL(code = gpuCode)
            )
        )

        val computePipeline = device.createComputePipeline(
            GPUComputePipelineDescriptor(
                compute = GPUComputeState(module = module, entryPoint = "main")
            )
        )

        val buffers = mutableListOf<Triple<Int, GPUBuffer, Long>>()
        val stagingBuffers = mutableListOf<GPUBuffer>()

        // Prepare buffers
        args.forEachIndexed { index, arg ->
            // TODO: This part should be converted to function to calculate type
            val data = when (arg) {
                is LiloList -> arg.values.map { (it as? LiloFloat)?.value?.toFloat() ?: 0f }
                    .toFloatArray()
                is LiloFloat -> floatArrayOf(arg.value.toFloat())
                is LiloInt -> floatArrayOf(arg.value.toFloat())
                else -> floatArrayOf(0f)
            }

            val byteSize = data.size * 4L

            val usage = BufferUsage.Storage or BufferUsage.CopySrc or BufferUsage.CopyDst
            val buffer = device.createBuffer(GPUBufferDescriptor(size = byteSize, usage = usage))

            // Write initial data
            val stagingBuffer = device.createBuffer(
                GPUBufferDescriptor(
                    size = byteSize,
                    usage = BufferUsage.MapWrite or BufferUsage.CopySrc,
                    mappedAtCreation = true
                )
            )

            val mappedRange = stagingBuffer.getMappedRange()
            mappedRange.order(ByteOrder.nativeOrder())
            val floatBuffer = mappedRange.asFloatBuffer()
            floatBuffer.put(data)
            stagingBuffer.unmap()

            device.createCommandEncoder().use { encoder ->
                encoder.copyBufferToBuffer(stagingBuffer, 0, buffer, 0, byteSize)
                device.queue.submit(arrayOf(encoder.finish()))
            }

            buffers.add(Triple(index, buffer, byteSize))
            stagingBuffers.add(stagingBuffer)
        }

        // Create Bind Group
        val bindGroup = device.createBindGroup(
            GPUBindGroupDescriptor(
                layout = computePipeline.getBindGroupLayout(0),
                entries = buffers.map { (index, buffer, _) ->
                    GPUBindGroupEntry(binding = index, buffer = buffer)
                }.toTypedArray()
            )
        )

        // Dispatch
        val launchConfig = kernal.config
        val launchConfigBlocksDim3 = launchConfig.blocksDim.dim
        device.createCommandEncoder().use { dispatchEncoder ->
            dispatchEncoder.beginComputePass().use { pass ->
                pass.setPipeline(computePipeline)
                pass.setBindGroup(0, bindGroup)
                pass.dispatchWorkgroups(
                    workgroupCountX = launchConfigBlocksDim3.x,
                    workgroupCountY = launchConfigBlocksDim3.y,
                    workgroupCountZ = launchConfigBlocksDim3.z
                )
                pass.end()
            }
            device.queue.submit(arrayOf(dispatchEncoder.finish()))
        }

        // Read back results
        buffers.forEach { (argIndex, buffer, byteSize) ->
            val readStagingBuffer = device.createBuffer(
                GPUBufferDescriptor(
                    size = byteSize,
                    usage = BufferUsage.MapRead or BufferUsage.CopyDst
                )
            )

            device.createCommandEncoder().use { readEncoder ->
                readEncoder.copyBufferToBuffer(buffer, 0, readStagingBuffer, 0, byteSize)
                device.queue.submit(arrayOf(readEncoder.finish()))
            }

            readStagingBuffer.mapAndAwait(MapMode.Read, 0, byteSize)

            val mappedRange = readStagingBuffer.getConstMappedRange(0, byteSize)
            mappedRange.order(ByteOrder.nativeOrder())
            val floatBuffer = mappedRange.asFloatBuffer()

            val resultData = FloatArray((byteSize / 4).toInt())
            floatBuffer.get(resultData)

            readStagingBuffer.unmap()

            // Update LiloList parameter if it has `out` keyword
            if (kernal.definition.parameters[argIndex].isOut) {
                val liloList = args[argIndex] as LiloList
                resultData.forEachIndexed { i, value ->
                    if (i < liloList.values.size) {
                        liloList.values[i] = LiloFloat(value.toDouble())
                    }
                }
            }

            readStagingBuffer.close()
        }

        // Clean up
        buffers.forEach { it.second.close() }
        stagingBuffers.forEach { it.close() }
        module.close()
        computePipeline.close()
        bindGroup.close()

        return LiloResult.Success(data = LiloNone)
    }

    override fun deinitWebGPU() {
        if (!::webGpu.isInitialized) return
        webGpu.close()
    }

    override fun getWebGPU() = webGpu
    override fun getGPUDevice() = device
}
