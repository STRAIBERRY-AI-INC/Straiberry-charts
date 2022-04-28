package com.straiberry.android.common.extensions

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.lang.reflect.Field

// Live Data
inline fun <T> LiveData<T>.subscribe(owner: LifecycleOwner, crossinline onDataReceived:(T)->Unit)=
        observe(owner, Observer { onDataReceived(it) })

/**
 * Gets a field from the project's BuildConfig. This is useful when, for example, flavors
 * are used at the project level to set custom fields.
 * @param context       Used to find the correct file
 * @param fieldName     The name of the field-to-access
 * @return              The value of the field, or `null` if the field is not found.
 */
fun Context.getBuildConfigValue(fieldName: String): Any? {
        try {
                val clazz = Class.forName(this.packageName.toString() + ".BuildConfig")
                val field: Field = clazz.getField(fieldName)
                return field.get(null)
        } catch (e: ClassNotFoundException) {
                e.printStackTrace()
        } catch (e: NoSuchFieldException) {
                e.printStackTrace()
        } catch (e: IllegalAccessException) {
                e.printStackTrace()
        }
        return null
}