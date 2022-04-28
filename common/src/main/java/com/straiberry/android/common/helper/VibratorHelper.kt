package com.straiberry.android.common.helper

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator



class VibratorHelper {
    private lateinit var vibrator: Vibrator

    fun vibrateForEditText(activity:Activity){
        vibrator=activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    VIBRATE_EDIT_TEXT_TIME,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator.vibrate(VIBRATE_EDIT_TEXT_TIME)
        }
    }

    fun vibrateForDetectingModel(activity:Activity){
        vibrator=activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    VibratorDurationForDetectionModel,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator.vibrate(VibratorDurationForDetectionModel)
        }
    }

    companion object{
        const val VIBRATE_EDIT_TEXT_TIME=200L
        const val VibratorDurationForDetectionModel=500L
    }
}