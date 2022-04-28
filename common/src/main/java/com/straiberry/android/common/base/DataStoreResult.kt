package com.straiberry.android.common.base

/**
 * This sealed class is responsible for
 * checking status of CRUD operations in database
 */
sealed class Readable <out T:Any>
data class ReadableSuccess<out T:Any>(val data:T) : Readable<T>()
data class ReadableFailure(val throwable: Throwable) : Readable<Nothing>()

