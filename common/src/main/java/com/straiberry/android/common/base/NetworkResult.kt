package com.straiberry.android.common.base

/**
 * This sealed class is responsible for
 * getting our response data. if response has success we get
 * data and if response have failed we get message and error code.
 */
sealed class Loadable <out T:Any>
data class Success<out T:Any>(val data:T) : Loadable<T>()
data class Failure(val throwable: Throwable) : Loadable<Nothing>()
object Loading: Loadable<Nothing>()
object NotLoading: Loadable<Nothing>()

sealed class HttpError
data class HttpErrorWithMessage(val code: Int, val message: String): HttpError()
object UnknownHttpError: HttpError()

