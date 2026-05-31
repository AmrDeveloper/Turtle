package com.amrdeveloper.turtle.common

data class LazyValue<T>(
    var data: T,
    var isLoading: Boolean
)
