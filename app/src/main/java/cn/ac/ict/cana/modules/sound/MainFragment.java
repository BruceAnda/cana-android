package cn.ac.ict.cana.modules.sound;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ac.ict.cana.R;

import static cn.ac.ict.cana.R.id.btn_start_recorder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    @BindView(btn_start_recorder)
    Button mBtnStartRecorder;
    SoundMainActivity mActivity;
    MediaPlayer mp;
//    private MyCount myCount;

    public MainFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sound_main, container, false);
        ButterKnife.bind(this, view);
        mActivity = (SoundMainActivity) getActivity();
        mp = MediaPlayer.create(mActivity.getApplicationContext(), R.raw.sound_guide);
        mp.start();
        return view;
    }
//    boolean isStartRecorder;
    @OnClick(btn_start_recorder)
    public void click() {
        if(mp!=null)
        {
            mp.stop();
            mp.release();
            mp=null;

        }
       // if (mBtnStartRecorder.isEnabled()) {
        mActivity.prepareRecorder();
        getFragmentManager().beginTransaction().replace(R.id.content, new TestingFragment()).commit();
//        setBtnText();

        //setEnable(false);
       // setBtnText();
    }

    @Override
    public void onPause() {
        if(mp!=null)
        {
            mp.stop();
            mp.release();
            mp=null;

        }
        //mActivity.finish();
        super.onPause();
    }

//  @Overrideprivate void setBtnText() {
//        mBtnStartRecorder.setText("开始");
//        mBtnStartRecorder.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.freebie_2));
//        isStartRecorder = !isStartRecorder;
//        if (isStartRecorder) {
//            myCount = new MyCount(30000, 1000);
//            myCount.start();
//            mBtnStartRecorder.setText("结束！");
//
//        } else {
//            myCount.cancel();
//            finishRecord();
//            mBtnStartRecorder.setText("开始录音！");
//            mBtnStartRecorder.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.freebie_4));
//        }
//    }


//    private void setEnable(boolean enabled) {
//        mBtnStartRecorder.setEnabled(enabled);
//    }

    /**
     * 恢复状态并释放资源
     */
//    private void release() {
//        setEnable(true);
//        mActivity.releaseRecorder();
//    }

//    class MyCount extends CountDownTimer {
//
//        public MyCount(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//        }
//
//        @Override
//        public void onTick(long l) {
//            mBtnStartRecorder.setBackgroundColor(ContextCompat.getColor(mActivity.getApplicationContext(), R.color.freebie_2));
//          //  mBtnStartRecorder.setText(String.format(Locale.CHINA, getString(R.string.sound_count_down), (l / 1000)));
//        }
//
//        @Override
//        public void onFinish() {
//            finishRecord();
//        }
//    }

    /**
     * 完成录音
     */
//    private void finishRecord() {
//        release();
//        mActivity.showDialog(true);
//    }


}
