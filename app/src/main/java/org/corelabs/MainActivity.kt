package org.corelabs

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
                var info = Utils.collectInfo(this.applicationContext)

                Handler(Looper.getMainLooper()).postDelayed({
                    var intent = Intent(this, LoadedActivity::class.java)
                    intent.putExtra("data", info.joinToString(separator = ":" ))
                    startActivity(intent)
                    finish()
                }, 2000)
            },
            1000
        )
    }
}
