package com.straiberry.android.common.helper

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.core.content.FileProvider
import com.straiberry.android.common.R
import com.straiberry.android.common.extensions.takeScreenshot
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.String
import kotlin.apply
import kotlin.toString


class ShareScreenshotHelper {
    /**
     * Get an screen shot of the view and share the result
     */
    fun shareResult(view: View, activity: Activity) {
        // Get the screenshot
        val screenShot = view.takeScreenshot()

        //Get application folder address
        val mediaStorageDir =
            File(activity.externalCacheDir.toString() + "Image.png")

        // Save image inside application folder and convert it to jpg
        try {
            val outputStream = FileOutputStream(String.valueOf(mediaStorageDir))
            screenShot.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // Check if the image dir exists
        if (mediaStorageDir.exists()) {
            // Get the uri of saved bitmap
            val imageUri: Uri = FileProvider.getUriForFile(
                activity,
                activity.packageName + ".provider",
                mediaStorageDir
            )
            val chooser = Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
            }, activity.getString(R.string.share_with))

            val resInfoList: List<ResolveInfo> = activity.packageManager
                .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)

            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                activity.grantUriPermission(
                    packageName,
                    imageUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            // Start sharing
            activity.startActivity(chooser)
        }
    }
}
