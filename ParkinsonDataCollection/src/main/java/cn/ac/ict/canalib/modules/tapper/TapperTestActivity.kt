package cn.ac.ict.canalib.modules.tapper

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.activities.ScoreActivity
import cn.ac.ict.canalib.base.BaseActivity
import cn.ac.ict.canalib.modules.guide.ModelGuideActivity6
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.db.database
import cn.ac.ict.canalib.common.Tapping
import cn.ac.ict.canalib.common.TappingData
import cn.ac.ict.canalib.utils.FileUtils
import com.alibaba.fastjson.JSON
import com.lovearthstudio.duasdk.Dua
import kotlinx.android.synthetic.main.activity_tapper_test.*
import org.jetbrains.anko.doAsync
import java.io.File
import java.util.*

/**
 * 手指灵敏测试
 */
class TapperTestActivity : BaseActivity() {
    private val TAG = TapperTestActivity::class.java.simpleName
    val tips = arrayOf("右手", "预备", "开始", "左手", "预备", "开始")
    var index = 0
    private var rightCount = 0
    private var leftCount = 0
    var isRight = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tapper_test)

        TipCountDonwTimer(3000, 1000).start()
    }

    // 提示语倒计时
    inner class TipCountDonwTimer(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            Log.i(TAG, "onFinish")
            enAbleCircleButton(true)
            tv_anim.visibility = View.GONE
            // 右手提示语结束后开始测试右手
            TestCountDownTimer(15000, 1000).start()
        }

        override fun onTick(p0: Long) {
            tv_anim.visibility = View.VISIBLE
            tv_anim.text = tips[index++]
        }
    }

    // 测试倒计时
    inner class TestCountDownTimer(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            enAbleCircleButton(false)

            tv_count_down.text = "0"
            if (isRight) {
                isRight = false
                rightCount = 0
                leftCount = 0
                tv_left_count.text = (rightCount).toString()
                tv_right_count.text = (leftCount).toString()
                TipCountDonwTimer(3000, 1000).start()
            } else {
                // 测试完成
                btn_finish.isEnabled = true
                btn_finish.text = "完成"
            }
        }

        override fun onTick(p0: Long) {
            tv_count_down.text = (p0 / 1000).toString()
        }
    }

    /**
     * 修改圆按钮的可点击状态
     */
    private fun enAbleCircleButton(isEnable: Boolean) {
        btn_left.isEnabled = isEnable
        btn_right.isEnabled = isEnable
    }

    /**
     * 左边的圆圈点击
     */
    fun clickLeft(view: View) {
        Log.i(TAG, "right")
        tv_left_count.text = (++rightCount).toString()
        if (isRight) {
            FileUtils.tappingR.data.add(TappingData("R", System.currentTimeMillis()))
            //FileUtils.tapperRDatas.add(TapperData(System.currentTimeMillis(), "R"))
        } else {
            // FileUtils.tapperLDatas.add(TapperData(System.currentTimeMillis(), "R"))
            FileUtils.tappingL.data.add(TappingData("R", System.currentTimeMillis()))
        }
    }

    /**
     * 右边的圆圈点击
     */
    fun clickRight(view: View) {
        Log.i(TAG, "left")
        tv_right_count.text = (++leftCount).toString()
        if (isRight) {
            FileUtils.tappingR.data.add(TappingData("L", System.currentTimeMillis()))
            //  FileUtils.tapperRDatas.add(TapperData(System.currentTimeMillis(), "L"))
        } else {
            FileUtils.tappingL.data.add(TappingData("L", System.currentTimeMillis()))
            // FileUtils.tapperLDatas.add(TapperData(System.currentTimeMillis(), "L"))
        }
    }

    /**
     * 写入Stride数据
     */
    private fun writeTapperData() {
        writeData(FileUtils.tappingL)
        writeData(FileUtils.tappingR)
    }

    /**
     * 把数据写入文件
     */
    private fun writeData(tapper: Tapping) {
        doAsync {
            val historyData = HistoryData(FileUtils.batch, "${Dua.getInstance().currentDuaId}", "${filesDir}${File.separator}${UUID.randomUUID()}.txt", "0", tapper.type, "")
            val data = JSON.toJSONString(tapper)
            Log.i("Tapper", data)
            FileUtils.writeToFile(data, historyData.filePath)
            insertDB(historyData)
        }
    }

    /**
     * 把数据文件路径插入到数据库
     */
    private fun insertDB(historyData: HistoryData) {
        database.use {
            // 历史数据
            val values = ContentValues()
            values.put(HistoryData.BATCH, historyData.batch)
            values.put(HistoryData.USERID, historyData.userID)
            values.put(HistoryData.TYPE, historyData.type)
            values.put(HistoryData.FILEPATH, historyData.filePath)
            values.put(HistoryData.MARK, historyData.mark)
            values.put(HistoryData.ISUPLOAD, historyData.isUpload)
            // 插入数据库
            insert(HistoryData.TABLE_NAME, null, values)
        }
    }

    fun toScore(view: View) {
        writeTapperData()
        val intent = Intent(this, ScoreActivity::class.java)
        intent.putExtra("modelName", ModuleHelper.MODULE_TAPPER)
        startActivity(intent)
        finish()
    }

    /**
     * 返回
     */
    override fun onBackPressed() {
        startActivity(Intent(this@TapperTestActivity, ModelGuideActivity6::class.java))
        finish()
    }
}

