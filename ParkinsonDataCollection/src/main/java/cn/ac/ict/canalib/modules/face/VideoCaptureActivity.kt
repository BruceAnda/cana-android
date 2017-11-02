/*
 *  Copyright 2016 Jeroen Mols
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cn.ac.ict.canalib.modules.face

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.MediaStore.Video.Thumbnails
import android.util.Log
import android.view.Display
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import java.io.File
import java.util.Timer
import java.util.TimerTask
import java.util.UUID

import cn.ac.ict.canalib.helpers.ModuleHelper
import cn.ac.ict.canalib.activities.ScoreActivity
import cn.ac.ict.canalib.modules.face.camera.CameraWrapper
import cn.ac.ict.canalib.modules.face.camera.NativeCamera
import cn.ac.ict.canalib.modules.face.configuration.CaptureConfiguration
import cn.ac.ict.canalib.modules.face.recorder.AlreadyUsedException
import cn.ac.ict.canalib.modules.face.recorder.VideoRecorder
import cn.ac.ict.canalib.modules.face.recorder.VideoRecorderInterface
import cn.ac.ict.canalib.modules.face.view.RecordingButtonInterface
import cn.ac.ict.canalib.modules.face.view.VideoCaptureView
import cn.ac.ict.canalib.R
import cn.ac.ict.canalib.db.bean.HistoryData
import cn.ac.ict.canalib.db.database
import cn.ac.ict.canalib.modules.guide.ModelGuideActivity7
import cn.ac.ict.canalib.utils.FileUtils
import com.lovearthstudio.duasdk.Dua
import org.jetbrains.anko.doAsync

class VideoCaptureActivity : Activity(), RecordingButtonInterface, VideoRecorderInterface {

    private var mVideoRecorded = false
    private var mVideoFile: VideoFile? = null
    private var mCaptureConfiguration: CaptureConfiguration? = null

    private var mVideoCaptureView: VideoCaptureView? = null
    private var mVideoRecorder: VideoRecorder? = null
    private var tvHint: TextView? = null
    private var mp1: MediaPlayer? = null
    private var mp2: MediaPlayer? = null
    private var mp3: MediaPlayer? = null
    internal var timer: Timer? = null
    internal lateinit var mHandler: Handler
    private lateinit var tt: TimerTask
    internal var i = 0
    internal lateinit var iv_bt: ImageView

    val videoThumbnail: Bitmap?
        get() {
            val thumbnail = ThumbnailUtils.createVideoThumbnail(mVideoFile?.fullPath, Thumbnails.FULL_SCREEN_KIND)
            if (thumbnail == null) {
                CLog.d(CLog.ACTIVITY, "Failed to generate video preview")
            }
            return thumbnail
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CLog.toggleLogging(this)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_face_videocapture)

        initializeCaptureConfiguration(savedInstanceState)

        mVideoCaptureView = findViewById<View>(R.id.videocapture_videocaptureview_vcv) as VideoCaptureView
        iv_bt = mVideoCaptureView!!.findViewById<View>(R.id.videocapture_recordbtn_iv) as ImageView
        mHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    5 -> {
                        timer!!.cancel()
                        mp2!!.start()
                        tvHint!!.text = getString(R.string.face_task_2)
                    }
                    10 -> {
                        timer!!.cancel()
                        mp3!!.start()
                        tvHint!!.text = getString(R.string.face_task_3)
                    }
                    15 -> {
                        timer!!.cancel()
                        iv_bt.performClick()
                    }
                    else -> {
                    }
                }
            }
        }
        tt = object : TimerTask() {
            override fun run() {
                mHandler.sendEmptyMessage(i)
                i++
            }
        }

        mp1 = MediaPlayer.create(applicationContext, R.raw.face_hint_1)
        mp1?.setOnCompletionListener {
            try {
                iv_bt.performClick()
                timer = Timer(true)
                tt = object : TimerTask() {
                    override fun run() {
                        mHandler.sendEmptyMessage(i)
                        i++
                    }
                }
                timer!!.schedule(tt, 0, 1000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mp2 = MediaPlayer.create(applicationContext, R.raw.face_hint_2)
        mp2?.setOnCompletionListener {
            timer = Timer(true)
            tt = object : TimerTask() {
                override fun run() {
                    mHandler.sendEmptyMessage(i)
                    i++
                }
            }
            timer!!.schedule(tt, 0, 1000)
        }
        mp3 = MediaPlayer.create(applicationContext, R.raw.face_hint_3)
        mp3?.setOnCompletionListener {
            timer = Timer(true)
            tt = object : TimerTask() {
                override fun run() {
                    mHandler.sendEmptyMessage(i)
                    i++
                }
            }
            timer!!.schedule(tt, 0, 1000)
        }
        initializeRecordingUI()

        tvHint = findViewById<View>(R.id.tv_hint) as TextView
        //   if (mVideoCaptureView == null) return; // Wrong orientation

        tvHint?.text = getString(R.string.face_task_1)
        tvHint?.background?.alpha = 100
        iv_bt.visibility = View.INVISIBLE
        Handler().postDelayed({ mp1!!.start() }, 1000)


    }

    private fun initializeCaptureConfiguration(savedInstanceState: Bundle?) {
        mCaptureConfiguration = generateCaptureConfiguration()
        mVideoRecorded = generateVideoRecorded(savedInstanceState)
        mVideoFile = generateOutputFile(savedInstanceState)
    }

    private fun initializeRecordingUI() {
        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        mVideoRecorder = VideoRecorder(this, mCaptureConfiguration, mVideoFile, CameraWrapper(NativeCamera(), display.rotation),
                mVideoCaptureView!!.previewSurfaceHolder)
        mVideoCaptureView!!.setRecordingButtonInterface(this)
        val showTimer = this.intent.getBooleanExtra(EXTRA_SHOW_TIMER, true)
        mVideoCaptureView!!.showTimer(showTimer)
        //        if (mVideoRecorded) {
        //            mVideoCaptureView.updateUIRecordingFinished(getVideoThumbnail());
        //        } else {
        //            mVideoCaptureView.updateUINotRecording();
        //        }
        mVideoCaptureView!!.showTimer(mCaptureConfiguration!!.showTimer)

    }

    override fun onPause() {
        super.onPause()
        //        if (mVideoRecorder != null) {
        //            mVideoRecorder.stopRecording(null);
        //        }
        if (mp1 != null) {
            mp1!!.stop()
            mp1 = null
        }
        if (mp2 != null) {
            mp2!!.stop()
            mp2 = null
        }
        if (mp3 != null) {
            mp3!!.stop()
            mp3 = null
        }
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
        releaseAllResources()
        //iv_bt.performClick();
        finishCancelled()
    }

    override fun onRecordButtonClicked() {
        Log.d("ddd", "recordbtn")
        try {
            mVideoRecorder!!.toggleRecording()
        } catch (e: AlreadyUsedException) {
            CLog.d(CLog.ACTIVITY, "Cannot toggle recording after cleaning up all resources")
        }

    }

    override fun onAcceptButtonClicked() {
        Log.d("ddd", "onAcceptButtonClicked")
        finishCompleted()
    }

    override fun onDeclineButtonClicked() {
        Log.d("ddd", "onDeclineButtonClicked: ")
        finishCancelled()
    }

    override fun onRecordingStarted() {
        mVideoCaptureView!!.updateUIRecordingOngoing()
    }

    override fun onRecordingStopped(message: String?) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        mVideoCaptureView!!.updateUIRecordingFinished(videoThumbnail)
        releaseAllResources()
    }

    override fun onRecordingSuccess() {
        mVideoRecorded = true
    }

    override fun onRecordingFailed(message: String) {
        finishError(message)
    }

    fun finishCompleted() {
        saveToStorage()
        /*Intent intent = new Intent(VideoCaptureActivity.this, ModuleHelper.getActivityAfterExam());
        startActivity(intent);
        finish();*/

        val intent = Intent(this@VideoCaptureActivity, ScoreActivity::class.java)
        intent.putExtra("modelName", ModuleHelper.MODULE_FACE)
        startActivity(intent)
        finish()
    }

    private fun finishCancelled() {
        //this.setResult(RESULT_CANCELED);
        if (mVideoFile != null) {
            mVideoFile!!.delete()
        }
        finish()

    }

    private fun finishError(message: String) {
        Toast.makeText(applicationContext, getString(R.string.face_finish_error) + message, Toast.LENGTH_LONG).show()

        val result = Intent()
        result.putExtra(EXTRA_ERROR_MESSAGE, message)
        this.setResult(RESULT_ERROR, result)
        finish()
    }

    private fun releaseAllResources() {
        if (mVideoRecorder != null) {
            Log.d("releaseAllResources", "In VideoCapture")
            mVideoRecorder!!.releaseAllResources()
        }
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        //        savedInstanceState.putBoolean(SAVED_RECORDED_BOOLEAN, mVideoRecorded);
        //        savedInstanceState.putString(SAVED_OUTPUT_FILENAME, mVideoFile.getFullPath());
        super.onSaveInstanceState(savedInstanceState)
    }

    protected fun generateCaptureConfiguration(): CaptureConfiguration {
        var returnConfiguration: CaptureConfiguration? = this.intent.getParcelableExtra(EXTRA_CAPTURE_CONFIGURATION)
        if (returnConfiguration == null) {
            returnConfiguration = CaptureConfiguration()
            CLog.d(CLog.ACTIVITY, "No captureconfiguration passed - using default configuration")
        }
        return returnConfiguration
    }

    private fun generateVideoRecorded(savedInstanceState: Bundle?): Boolean {
        return savedInstanceState?.getBoolean(SAVED_RECORDED_BOOLEAN, false) ?: false
    }

    protected fun generateOutputFile(savedInstanceState: Bundle?): VideoFile {
        val returnFile: VideoFile
        if (savedInstanceState != null) {
            returnFile = VideoFile(this, savedInstanceState.getString(SAVED_OUTPUT_FILENAME))
        } else {
            returnFile = VideoFile(this, this.intent.getStringExtra(EXTRA_OUTPUT_FILENAME))
        }
        // TODO: add checks to see if outputfile is writeable
        return returnFile
    }

    lateinit var path: String

    fun saveToStorage() {
        // SharedPreferences sharedPreferences = getSharedPreferences("Cana", Context.MODE_PRIVATE);
        //String filePath = HistoryData.getFilePath(this, ModuleHelper.MODULE_FACE);
        // mVideoFile.saveTo(FileUtils.INSTANCE.getFilePath());
        path = "${filesDir}${File.separator}${UUID.randomUUID()}.mp4"
        mVideoFile!!.saveTo(path)
        writeData()


        // SharedPreferences.Editor editor = sharedPreferences.edit();
        // editor.putString("HistoryFilePath", filePath);
        // editor.apply();
    }

    /**
     * 把数据写入文件
     */
    private fun writeData() {
        doAsync {
            val historyData = HistoryData(FileUtils.batch, "${Dua.getInstance().currentDuaId}", "$path", "0", ModuleHelper.MODULE_DATATYPE_FACE, "", "")
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

    override fun onBackPressed() {
        startActivity(Intent(this, ModelGuideActivity7::class.java))
        finish()
    }

    companion object {

        val RESULT_ERROR = 753245

        val EXTRA_OUTPUT_FILENAME = "com.jmolsmobile.extraoutputfilename"
        val EXTRA_CAPTURE_CONFIGURATION = "com.jmolsmobile.extracaptureconfiguration"
        val EXTRA_ERROR_MESSAGE = "com.jmolsmobile.extraerrormessage"
        val EXTRA_SHOW_TIMER = "com.jmolsmobile.extrashowtimer"

        private val SAVED_RECORDED_BOOLEAN = "com.jmolsmobile.savedrecordedboolean"
        protected val SAVED_OUTPUT_FILENAME = "com.jmolsmobile.savedoutputfilename"
    }
}
