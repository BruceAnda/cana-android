package cn.ac.ict.canalib.base

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle

open class ModelGuideBaseActivity : BaseActivity() {

    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var sharedPreference: SharedPreferences
    private var audio_is_open: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreference = getSharedPreferences("setting", Context.MODE_PRIVATE)
        audio_is_open = sharedPreference.getBoolean("audio_is_open", true)
    }

    fun createMediaPlayer(resId: Int) {
        mMediaPlayer = MediaPlayer.create(this, resId)
    }

    fun play() {
        if (audio_is_open) {
            mMediaPlayer.start()
        }
    }

    fun pasue() {
        if (audio_is_open) {
            mMediaPlayer.pause()
        }
    }

    fun stop() {
        if (audio_is_open) {
            mMediaPlayer.stop()
        }
    }

    fun release() {
        if (audio_is_open) {
            mMediaPlayer.release()
        }
    }

    override fun onResume() {
        super.onResume()

        play()
    }

    override fun onPause() {
        super.onPause()

        pasue()
    }

    override fun onStop() {
        super.onStop()

        stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }
}
