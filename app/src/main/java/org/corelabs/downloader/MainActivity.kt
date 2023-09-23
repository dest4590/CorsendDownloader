package org.corelabs.downloader

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Utils.animateCharacterByCharacter(findViewById(R.id.textHeader), "CorsendDownloader", 50L)
        Utils.animateOpacity(findViewById(R.id.textVersion), 2000L)

        Handler(Looper.getMainLooper()).postDelayed(
            {
                Utils.animateOpacity(findViewById(R.id.loading), 1000L)

                Utils.animateOpacity(findViewById(R.id.collectInfoText), 1000L)
                Utils.animateCharacterByCharacter(findViewById(R.id.collectInfoText), "Collecting info...", 50L)

                // Collect data
                val info = Utils.collectInfo(this.applicationContext)

                // Check server status
                val serverStatusView = findViewById<TextView>(R.id.serverStatus)
                Utils.animateOpacity(serverStatusView, 1000L)

                if (info[2]) {
                    serverStatusView.text = getString(R.string.serverOnline)
                } else {
                    serverStatusView.text = getString(R.string.serverOffline)
                }

                // Start net intent
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, LoadedActivity::class.java)
                    intent.putExtra("data", info.joinToString(separator = ":" ))
                    startActivity(intent)
                    finish()
                }, 2000)
            },
            1000
        )
    }
}
