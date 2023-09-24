package org.corelabs.downloader

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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

        fun Slide(obj: View, durationMillis: Long, offset: Float) {
            obj.animate()
                .translationY(offset)
                .setDuration(1500)
                .setInterpolator(LinearOutSlowInInterpolator())
                .start()
        }


        @Suppress("DEPRECATION")
        private fun isPackageInstalled(packageName: String, context: Context): Boolean {
            return try {
                context.packageManager.getApplicationInfo(packageName, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }

        fun apiRequest(path: String): JSONObject {
            val url = URL("http://corsend.vidomnia.xyz:9999/$path")
            val response = StringBuilder()
            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                val reader = BufferedReader(InputStreamReader(connection.inputStream))

                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
            } catch (e: Exception) {
                Log.e("CorsendDownloader", e.stackTraceToString())
                return JSONObject("{'server': 'error'}")

            } finally {
                connection.disconnect()
            }
            return JSONObject(response.toString())
        }


        fun collectInfo(context: Context): Array<Boolean> {
            var youtubeInstalled = false
            var youtubeMusicInstalled = false
            if (isPackageInstalled("org.corelabs.corsend", context)) {
                youtubeInstalled = true
                Log.d("CorsendDownloader", "Corsend Youtube installed!")
            }

            if (isPackageInstalled("org.corelabs.corsend.music", context)) {
                Log.d("CorsendDownloader", "Corsend Youtube Music installed!")
                youtubeMusicInstalled = true
            }

            // Fix internet
            StrictMode.setThreadPolicy(ThreadPolicy.Builder().permitAll().build())

            val ping = apiRequest("ping")
            val online = ping["server"] == "pong"

            return arrayOf(youtubeInstalled, youtubeMusicInstalled, online)
        }
    }
}