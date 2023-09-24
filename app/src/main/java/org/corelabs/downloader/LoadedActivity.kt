package org.corelabs.downloader

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import org.corelabs.downloader.Utils.Companion.apiRequest
import java.io.File

class LoadedActivity : AppCompatActivity() {
    @SuppressLint("UnspecifiedRegisterReceiverFlag", "CutPasteId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loaded)

        // Fix api calls
        val policy = ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val installedApps = intent.getStringExtra("data")?.split(':')

        val youtube = installedApps?.get(0).toBoolean()
        val youtubeInfo = apiRequest("app/youtube")

        val youtubeMusic = installedApps?.get(0).toBoolean()
        val youtubeMusicInfo = apiRequest("app/youtubeMusic")

        processInstalled(youtube, youtubeMusic)

        val tabLayout = findViewById<TabLayout>(R.id.tablayout)

        val appPreview = findViewById<ConstraintLayout>(R.id.app_preview)

        val installButton = findViewById<Button>(R.id.install_button)
        val settingsButton = findViewById<Button>(R.id.settingsButton)

        val appImage = findViewById<ImageView>(R.id.app_logo)

        val info = findViewById<TextView>(R.id.info)
        info.alpha = 0f

        val appVersion = findViewById<TextView>(R.id.app_version)
        appVersion.text = "v" + youtubeInfo["version"].toString()

        Handler(Looper.getMainLooper()).postDelayed({
            Utils.Slide(appPreview, 2000,-400f)
        }, 100)

        Handler(Looper.getMainLooper()).postDelayed({
            val appLogo = findViewById<ImageView>(R.id.app_logo)

            appLogo.visibility = View.VISIBLE
            Utils.animateOpacity(appLogo, 500)

        }, 1300)

        val appPreviewText = findViewById<TextView>(R.id.app_preview_name)

        Handler(Looper.getMainLooper()).postDelayed({
            Utils.animateCharacterByCharacter(appPreviewText, "Corsend Youtube", 50)
        }, 1300 + 500)

        val appDescription = findViewById<TextView>(R.id.app_description)

        Utils.animateCharacterByCharacter(appDescription, youtubeInfo["description"].toString(), 40)

        // Animate app description
        Utils.Slide(appDescription, 1500, 50f)

        Handler(Looper.getMainLooper()).postDelayed({
            // Animate application version
            Utils.animateOpacity(appVersion, 1000)

            // Animate settings button
            Utils.animateOpacity(settingsButton, 500)


            // Animate device info
            Utils.animateOpacity(info, 200)

            // Info about phone
            info.animate()
                .translationY(-50f)
                .setDuration(1500)
                .setInterpolator(LinearOutSlowInInterpolator())
                .start()

            val androidVersion = Build.VERSION.RELEASE
            val androidSdkVersion = Build.VERSION.SDK_INT

            val deviceManufacturer = Build.MANUFACTURER
            val deviceBrand = Build.BRAND
            val deviceCodeName = Build.DEVICE


            info.text = """Android $androidVersion (SDK $androidSdkVersion)
$deviceManufacturer $deviceBrand ($deviceCodeName)"""
        }, 1300 + 600)

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.text.toString() == "Corsend Youtube") {
                    processInstalled(youtube, youtubeMusic)

                    Utils.animateCharacterByCharacter(appPreviewText, getString(R.string.youtube), 35)
                    appImage.setImageResource(R.drawable.mmt)
                    Utils.animateCharacterByCharacter(appDescription, youtubeInfo["description"].toString(), 40)
                } else {
                    processInstalled(youtube, youtubeMusic)

                    Utils.animateCharacterByCharacter(appPreviewText, getString(R.string.youtube_music), 35)
                    appImage.setImageResource(R.drawable.mmt_music)
                    Utils.animateCharacterByCharacter(appDescription, youtubeMusicInfo["description"].toString(), 40)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Bind button
        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        installButton.setOnClickListener {
            val currentTabText = tabLayout.getTabAt(tabLayout.selectedTabPosition)?.text

            Toast.makeText(
                applicationContext,
                "Downloading $currentTabText, check your status bar, and click on notification after download!",
                Toast.LENGTH_SHORT
            ).show()

            val link = if (currentTabText == "Corsend Youtube") {
                "http://corsend.vidomnia.xyz:9999/install/youtube"
            } else {
                "http://corsend.vidomnia.xyz:9999/install/youtube_music"
            }

            val request = DownloadManager.Request(Uri.parse(link))
                .setTitle("Corsend Downloader")
                .setDescription("Downloading $currentTabText")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setMimeType("application/vnd.android.package-archive")
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    currentTabText.toString() + ".apk"
                )

            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            dm.enqueue(request)

            val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(ctxt: Context, intent: Intent) {
                    Toast.makeText(applicationContext, "App Downloaded!", Toast.LENGTH_LONG).show()

                    val builder = VmPolicy.Builder()
                    StrictMode.setVmPolicy(builder.build())

                    val installIntent = Intent(Intent.ACTION_VIEW)
                    installIntent.setDataAndType(
                        Uri.fromFile(
                            File(
                                Environment.DIRECTORY_DOWNLOADS,
                                currentTabText.toString() + ".apk"
                            )
                        ), "application/vnd.android.package-archive"
                    )
                    installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(installIntent)
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