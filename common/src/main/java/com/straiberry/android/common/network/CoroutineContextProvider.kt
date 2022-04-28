package com.straiberry.android.common.network

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

data class CoroutineContextProvider(
    val main: CoroutineContext = Dispatchers.Main,
    val io: CoroutineContext = Dispatchers.IO
)
