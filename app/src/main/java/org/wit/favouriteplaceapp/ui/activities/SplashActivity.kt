package org.wit.favouriteplaceapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import org.wit.favouriteplaceapp.databinding.ActivitySplashBinding

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    private lateinit var splashLayout : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashLayout = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashLayout.root)

        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
     window.insetsController?.hide(WindowInsets.Type.statusBars())
        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000) // 3000 is the delayed time in milliseconds.
    }
}