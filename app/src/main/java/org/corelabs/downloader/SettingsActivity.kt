package org.corelabs.downloader

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Start video
        val destPicture = findViewById<VideoView>(R.id.destPicture)
        destPicture.setVideoPath("android.resource://" + packageName + "/" + R.raw.komaru)
        destPicture.setOnPreparedListener { mp -> mp.isLooping = true }
        destPicture.start()



        findViewById<FrameLayout>(R.id.destPictureFrame).clipToOutline = true

    }
}