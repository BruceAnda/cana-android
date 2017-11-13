package cn.ac.ict.cana.features.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast

import com.gigamole.navigationtabbar.ntb.NavigationTabBar

import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask

import cn.ac.ict.cana.features.adapter.MainAdapter
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.base.AudioBaseActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 程序的主界面
 */
open class MainActivity : AudioBaseActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var timer: Timer
    internal var back = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            init()
        }
    }

    fun init() {
        timer = Timer(false)
        vp_horizontal_ntb.adapter = MainAdapter(supportFragmentManager)

        val color = resources.getColor(R.color.mainColor2)

        val models = ArrayList<NavigationTabBar.Model>()
        models.add(
                NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_test),
                        color
                ).title(resources.getString(R.string.page_exam))
                        .build()
        )
        models.add(
                NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_user),
                        color
                ).title(resources.getString(R.string.page_user))
                        .build()
        )
        models.add(
                NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_history),
                        color
                ).title(resources.getString(R.string.page_history))
                        .build()
        )
        models.add(
                NavigationTabBar.Model.Builder(
                        ContextCompat.getDrawable(this, R.drawable.ic_settings),
                        color
                ).title(resources.getString(R.string.page_setting))
                        .build()
        )
        ntb.models = models
        ntb.setViewPager(vp_horizontal_ntb, 0)
        ntb.bgColor = Color.parseColor("#F3F5F7")
        ntb.titleSize = 40f
        if (intent.extras != null) {
            val int = intent.extras.getInt("page", 0)
            vp_horizontal_ntb.currentItem = int
        }
    }

    override fun onBackPressed() {
        Log.d(TAG, back.toString() + "Function entered")
        if (back == 1) {
            Toast.makeText(this@MainActivity, getString(R.string.second_back), Toast.LENGTH_SHORT).show()
            back++
            val tt = object : TimerTask() {
                override fun run() {
                    back = 1
                }
            }
            timer.schedule(tt, 2000)
        } else {
            finish()
        }
    }

}
