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

package cn.ac.ict.cana.modules.face;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.models.History;
import cn.ac.ict.cana.modules.face.camera.CameraWrapper;
import cn.ac.ict.cana.modules.face.camera.NativeCamera;
import cn.ac.ict.cana.modules.face.configuration.CaptureConfiguration;
import cn.ac.ict.cana.modules.face.recorder.AlreadyUsedException;
import cn.ac.ict.cana.modules.face.recorder.VideoRecorder;
import cn.ac.ict.cana.modules.face.recorder.VideoRecorderInterface;
import cn.ac.ict.cana.modules.face.view.RecordingButtonInterface;
import cn.ac.ict.cana.modules.face.view.VideoCaptureView;

public class VideoCaptureActivity extends Activity implements RecordingButtonInterface, VideoRecorderInterface {

    public static final int RESULT_ERROR = 753245;

    public static final String EXTRA_OUTPUT_FILENAME = "com.jmolsmobile.extraoutputfilename";
    public static final String EXTRA_CAPTURE_CONFIGURATION = "com.jmolsmobile.extracaptureconfiguration";
    public static final String EXTRA_ERROR_MESSAGE = "com.jmolsmobile.extraerrormessage";
    public static final String EXTRA_SHOW_TIMER = "com.jmolsmobile.extrashowtimer";

    private static final String SAVED_RECORDED_BOOLEAN = "com.jmolsmobile.savedrecordedboolean";
    protected static final String SAVED_OUTPUT_FILENAME = "com.jmolsmobile.savedoutputfilename";

    private boolean mVideoRecorded = false;
    VideoFile mVideoFile = null;
    private CaptureConfiguration mCaptureConfiguration;

    private VideoCaptureView mVideoCaptureView;
    private VideoRecorder mVideoRecorder;
    private TextView tvHint;
    private MediaPlayer mp1;
    private MediaPlayer mp2;
    private MediaPlayer mp3;
    Timer timer;
    Handler mHandler;
    TimerTask tt;
    int i = 0;
    ImageView iv_bt;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CLog.toggleLogging(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_face_videocapture);

        initializeCaptureConfiguration(savedInstanceState);

        mVideoCaptureView = (VideoCaptureView) findViewById(R.id.videocapture_videocaptureview_vcv);
        iv_bt = (ImageView) mVideoCaptureView.findViewById(R.id.videocapture_recordbtn_iv);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 5:
                        timer.cancel();
                        mp2.start();
                        tvHint.setText(getString(R.string.face_task_2));
                        break;
                    case 10:
                        timer.cancel();
                        mp3.start();
                        tvHint.setText(getString(R.string.face_task_3));
                        break;
                    case 15:
                        timer.cancel();
                        iv_bt.performClick();
                    default:
                        break;
                }
            }
        };
        tt = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(i);
                i++;
            }
        };

        mp1 = MediaPlayer.create(getApplicationContext(), R.raw.face_hint_1);
        mp1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    iv_bt.performClick();
                    timer = new Timer(true);
                    tt = new TimerTask() {
                        @Override
                        public void run() {
                            mHandler.sendEmptyMessage(i);
                            i++;
                        }
                    };
                    timer.schedule(tt, 0, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mp2 = MediaPlayer.create(getApplicationContext(), R.raw.face_hint_2);
        mp2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                timer = new Timer(true);
                tt = new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(i);
                        i++;
                    }
                };
                timer.schedule(tt, 0, 1000);
            }
        });
        mp3 = MediaPlayer.create(getApplicationContext(), R.raw.face_hint_3);
        mp3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                timer = new Timer(true);
                tt = new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(i);
                        i++;
                    }
                };
                timer.schedule(tt, 0, 1000);
            }
        });
        initializeRecordingUI();

        tvHint = (TextView) findViewById(R.id.tv_hint);
        //   if (mVideoCaptureView == null) return; // Wrong orientation

        tvHint.setText(getString(R.string.face_task_1));
        tvHint.getBackground().setAlpha(100);
        iv_bt.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mp1.start();
            }
        }, 1000);


    }

    private void initializeCaptureConfiguration(final Bundle savedInstanceState) {
        mCaptureConfiguration = generateCaptureConfiguration();
        mVideoRecorded = generateVideoRecorded(savedInstanceState);
        mVideoFile = generateOutputFile(savedInstanceState);
    }

    private void initializeRecordingUI() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        mVideoRecorder = new VideoRecorder(this, mCaptureConfiguration, mVideoFile, new CameraWrapper(new NativeCamera(), display.getRotation()),
                mVideoCaptureView.getPreviewSurfaceHolder());
        mVideoCaptureView.setRecordingButtonInterface(this);
        boolean showTimer = this.getIntent().getBooleanExtra(EXTRA_SHOW_TIMER, true);
        mVideoCaptureView.showTimer(showTimer);
//        if (mVideoRecorded) {
//            mVideoCaptureView.updateUIRecordingFinished(getVideoThumbnail());
//        } else {
//            mVideoCaptureView.updateUINotRecording();
//        }
        mVideoCaptureView.showTimer(mCaptureConfiguration.getShowTimer());

    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (mVideoRecorder != null) {
//            mVideoRecorder.stopRecording(null);
//        }
        if(mp1!=null)
        {
            mp1.stop();
            mp1 = null;
        }
        if(mp2!=null)
        {
            mp2.stop();
            mp2 = null;
        }
        if(mp3!=null)
        {
            mp3.stop();
            mp3 = null;
        }
        if(timer!=null)
        {
            timer.cancel();
            timer = null;
        }
        releaseAllResources();
        //iv_bt.performClick();
        finishCancelled();
    }

    @Override
    public void onRecordButtonClicked() {
        Log.d("ddd","recordbtn");
        try {
            mVideoRecorder.toggleRecording();
        } catch (AlreadyUsedException e) {
            CLog.d(CLog.ACTIVITY, "Cannot toggle recording after cleaning up all resources");
        }
    }

    @Override
    public void onAcceptButtonClicked() {
        Log.d("ddd", "onAcceptButtonClicked");
        finishCompleted();
    }

    @Override
    public void onDeclineButtonClicked() {
        Log.d("ddd", "onDeclineButtonClicked: ");
        finishCancelled();
    }

    @Override
    public void onRecordingStarted() {
        mVideoCaptureView.updateUIRecordingOngoing();
    }

    @Override
    public void onRecordingStopped(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

        mVideoCaptureView.updateUIRecordingFinished(getVideoThumbnail());
        releaseAllResources();
    }

    @Override
    public void onRecordingSuccess() {
        mVideoRecorded = true;
    }

    @Override
    public void onRecordingFailed(String message) {
        finishError(message);
    }

    public void finishCompleted() {
        saveToStorage();
        Intent intent = new Intent(VideoCaptureActivity.this, ModuleHelper.getActivityAfterExam());
        startActivity(intent);
        finish();
    }

    private void finishCancelled() {
        //this.setResult(RESULT_CANCELED);
        if(mVideoFile!=null)
        {
            mVideoFile.delete();
        }
        finish();

    }

    private void finishError(final String message) {
        Toast.makeText(getApplicationContext(), getString(R.string.face_finish_error) + message, Toast.LENGTH_LONG).show();

        final Intent result = new Intent();
        result.putExtra(EXTRA_ERROR_MESSAGE, message);
        this.setResult(RESULT_ERROR, result);
        finish();
    }

    private void releaseAllResources() {
        if (mVideoRecorder != null) {
            Log.d("releaseAllResources", "In VideoCapture");
            mVideoRecorder.releaseAllResources();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putBoolean(SAVED_RECORDED_BOOLEAN, mVideoRecorded);
//        savedInstanceState.putString(SAVED_OUTPUT_FILENAME, mVideoFile.getFullPath());
        super.onSaveInstanceState(savedInstanceState);
    }

    protected CaptureConfiguration generateCaptureConfiguration() {
        CaptureConfiguration returnConfiguration = this.getIntent().getParcelableExtra(EXTRA_CAPTURE_CONFIGURATION);
        if (returnConfiguration == null) {
            returnConfiguration = new CaptureConfiguration();
            CLog.d(CLog.ACTIVITY, "No captureconfiguration passed - using default configuration");
        }
        return returnConfiguration;
    }

    private boolean generateVideoRecorded(final Bundle savedInstanceState) {
        if (savedInstanceState == null) return false;
        return savedInstanceState.getBoolean(SAVED_RECORDED_BOOLEAN, false);
    }

    protected VideoFile generateOutputFile(Bundle savedInstanceState) {
        VideoFile returnFile;
        if (savedInstanceState != null) {
            returnFile = new VideoFile(this, savedInstanceState.getString(SAVED_OUTPUT_FILENAME));
        } else {
            returnFile = new VideoFile(this, this.getIntent().getStringExtra(EXTRA_OUTPUT_FILENAME));
        }
        // TODO: add checks to see if outputfile is writeable
        return returnFile;
    }

    public Bitmap getVideoThumbnail() {
        final Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(mVideoFile.getFullPath(),
                Thumbnails.FULL_SCREEN_KIND);
        if (thumbnail == null) {
            CLog.d(CLog.ACTIVITY, "Failed to generate video preview");
        }
        return thumbnail;
    }

    public void saveToStorage() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cana", Context.MODE_PRIVATE);
        String filePath = History.getFilePath(this, ModuleHelper.MODULE_FACE);
        mVideoFile.saveTo(filePath);


        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("HistoryFilePath", filePath);
        editor.apply();
    }
}
