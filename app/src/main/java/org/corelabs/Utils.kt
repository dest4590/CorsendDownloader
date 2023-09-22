package org.corelabs

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.TextView


class Utils {
    companion object {
        fun animateCharacterByCharacter(view: TextView, text: String, delay: Long) {
            val charAnimation = ValueAnimator.ofInt(0, text.length)

            charAnimation.apply {
                this.duration = delay * text.length.toLong()
                this.repeatCount = 0
                addUpdateListener {
                    val charCount = it.animatedValue as Int
                    val animatedText = text.substring(0, charCount)
                    view.text = animatedText
                }
            }

            charAnimation.start()
        }

        @SuppressLint("ObjectAnimatorBinding")
        fun animateOpacity(obj: Any?, durationMillis: Long) {
            val animator = ObjectAnimator.ofFloat(obj, "alpha", 0.0f, 1.0f)

            animator.duration = durationMillis

            animator.start()
        }


        @Suppress("DEPRECATION")
        fun isPackageInstalled(packageName: String, context: Context): Boolean {
            return try {
                context.packageManager.getApplicationInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }


        fun collectInfo(context: Context): Array<Boolean> {
            var youtubeInstalled = false
            var youtubeMusicInstalled = false
            if (isPackageInstalled("org.corelabs.corsend", context)) {
                youtubeInstalled = true
                Log.d("CorsendDownloader", "Corsend Youtube installed!")
            }

            if (isPackageInstalled("org.corelabs.corsend", context)) {
                Log.d("CorsendDownloader", "Corsend Youtube Music installed!")
                youtubeMusicInstalled = true
            }

            return arrayOf(youtubeInstalled, youtubeMusicInstalled)

        }
    }
}