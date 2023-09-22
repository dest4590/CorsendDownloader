package org.corelabs

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.PackageManagerCompat
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.io.File


class LoadedActivity : AppCompatActivity() {
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loaded)

        val installedApps = intent.getStringExtra("data")?.split(':')

        val youtube = installedApps?.get(0).toBoolean()
        val youtubeMusic = installedApps?.get(0).toBoolean()

        val tabLayout = findViewById<TabLayout>(R.id.tablayout)

        val frameLayout = findViewById<ConstraintLayout>(R.id.app_preview)

        val installButton = findViewById<Button>(R.id.install_button)

        val appImage = findViewById<ImageView>(R.id.app_logo)

        frameLayout.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.up))

        Handler(Looper.getMainLooper()).postDelayed({
            val applogo = findViewById<ImageView>(R.id.app_logo)
            applogo.visibility = View.VISIBLE
            applogo.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.from_left))
        }, 1600)

        val appPreviewText = findViewById<TextView>(R.id.app_preview_name)

        Handler(Looper.getMainLooper()).postDelayed({
            Utils.animateCharacterByCharacter(appPreviewText, "Corsend Youtube", 50)
        }, 1600+500)

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.text.toString() == "Corsend Youtube") {
                    processInstalled(youtube, youtubeMusic)

                    Utils.animateCharacterByCharacter(appPreviewText, getString(R.string.youtube), 50)
                    appImage.setImageResource(R.drawable.mmt)
                }

                else {
                    processInstalled(youtube, youtubeMusic)

                    Utils.animateCharacterByCharacter(appPreviewText, getString(R.string.youtube_music), 50)
                    appImage.setImageResource(R.drawable.mmt_music)
                }
             }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        installButton.setOnClickListener {

            var currentTabText = tabLayout.getTabAt(tabLayout.selectedTabPosition)?.text

            Toast.makeText(applicationContext, "Downloading $currentTabText, check your status bar!", Toast.LENGTH_SHORT).show()

            var link = if (currentTabText == "Corsend Youtube") {
                "http://146.59.16.97:9999/corsend_youtube_latest.apk"
            } else {
                "http://146.59.16.97:9999/corsend_music_latest.apk"
            }

            var request = DownloadManager.Request(Uri.parse(link))
                .setTitle("Corsend Downloader")
                .setDescription("Downloading $currentTabText")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setMimeType("application/vnd.android.package-archive")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, currentTabText.toString() + ".apk")

            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            dm.enqueue(request)

            val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(ctxt: Context, intent: Intent) {
                    Toast.makeText(applicationContext, "App Downloaded!", Toast.LENGTH_LONG).show()

                }
            }

            registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }

    @SuppressLint("SetTextI18n")
    fun changeInstalledText(installed: Boolean) {
        val appInstalledText = findViewById<TextView>(R.id.installed_text)

        if (installed) {
            appInstalledText.text = "Installed"
            appInstalledText.setTextColor(Color.parseColor("#8BC34A")) // green color
        }

        else {
            appInstalledText.text = "Not installed"
            appInstalledText.setTextColor(Color.parseColor("#FFF44336")) // red color
        }
    }

    fun processInstalled(youtube: Boolean, youtubeMusic: Boolean) {
        if (youtube) {
            changeInstalledText(true)
        }
        else {
            changeInstalledText(false)
        }

        if (youtubeMusic) {
            changeInstalledText(true)
        }
        else {
            changeInstalledText(false)
        }
    }
}