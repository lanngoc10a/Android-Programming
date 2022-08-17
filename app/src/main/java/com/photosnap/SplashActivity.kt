package com.photosnap

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.photosnap.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        fader()

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val myIntent = Intent(this, MainActivity::class.java)
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(myIntent)
                finish()
            }catch (e : Exception){
                Log.v("Splash",e.toString())
            }
        }, 3000)

    }

    // value range from [0 - 1] [invisible - visible]
    private fun fader() {
        val animator : ObjectAnimator = ObjectAnimator.ofFloat(
            findViewById(R.id.fullscreen_content), View.ALPHA,0f,1f)
        animator.duration = 1000
        animator.start()
    }

}