package com.amrdeveloper.lilo.std

sealed interface LiloStdModule {
    fun getStdFunction(name: String): LiloStdFunction?
}

fun supportedLiloStdModules(): Map<String, LiloStdModule> {
    return mapOf(
        "random" to LiloRandomModule()
    )
}