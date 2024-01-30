package com.example.jaago.screens.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.example.jaago.R
import com.example.jaago.screens.base.BaseActivity

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val tvRateUs = findViewById<TextView>(R.id.tv_rate_us)
        val llRateUs = findViewById<LinearLayout>(R.id.ll_rate_us)
        llRateUs.setOnClickListener {
            // Open Play Store for rating
            openPlayStoreForRating()
        }

        val tvShare = findViewById<TextView>(R.id.tv_share)
        val llShare = findViewById<LinearLayout>(R.id.ll_share)
        llShare.setOnClickListener {
            // Share the Play Store link
            shareApp()
        }
    }

    private fun openPlayStoreForRating() {
        val packageName = packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this awesome app: https://play.google.com/store/apps/details?id=$packageName")
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}