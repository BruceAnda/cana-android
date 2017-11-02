package cn.ac.ict.cana.features.pagers

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Process
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.ac.ict.cana.R
import cn.ac.ict.canalib.common.extensions.inflate
import com.lovearthstudio.duasdk.Dua
import kotlinx.android.synthetic.main.fragment_setting_page.*

/**
 * 设置界面
 */
class SettingPageFragment : Fragment() {

    private lateinit var sharedPreference: SharedPreferences
    private var audio_is_open: Boolean = true

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       return container?.inflate(R.layout.fragment_setting_page)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreference = context.getSharedPreferences("setting", Context.MODE_PRIVATE)

        audio_is_open = sharedPreference.getBoolean("audio_is_open", true)

        changeAudioText()
        btn_setting_audio.setOnClickListener {
            audio_is_open = !audio_is_open
            changeAudioText()
            sharedPreference.edit().putBoolean("audio_is_open", audio_is_open).commit()
        }

        btn_setting_support.setOnClickListener {
            Toast.makeText(context, getString(R.string.about), Toast.LENGTH_SHORT).show()
        }
        btn_setting_about.setOnClickListener {
            Toast.makeText(context, getString(R.string.support), Toast.LENGTH_SHORT).show()
        }
        btn_logout.setOnClickListener {
            Dua.getInstance().logout()
            Process.killProcess(Process.myPid())
        }
    }

    private fun changeAudioText() {
        if (audio_is_open) {
            btn_setting_audio.text = "关闭声音"
        } else {
            btn_setting_audio.text = "打开声音"
        }
    }
}// Required empty public constructor
