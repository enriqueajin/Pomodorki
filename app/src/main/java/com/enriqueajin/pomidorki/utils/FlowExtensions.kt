package com.enriqueajin.pomidorki.utils

import kotlinx.coroutines.flow.MutableStateFlow

fun <T> MutableStateFlow<T>.updateState(transform: (T) -> T) {
    this.value = transform(this.value)
}
