package cn.ac.ict.cana.modules.sound;


import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ac.ict.cana.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestingFragment extends Fragment {

    @BindView(R.id.btn_start_recorder)
    Button mBtnStartRecorder;

    @BindView(R.id.tv_quston)
    TextView mQueston;
    private String[] questons;
    int currentQueston = 0;

//    @BindView(R.id.ttv_tapper)
//    TickTockView ttv;
    @BindView(R.id.iv_sound_mic)
    ImageView iv_mic;
    private SoundMainActivity mActivity;
    private AnimationDrawable recordingTransition;

    public TestingFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sound_testing, container, false);
        ButterKnife.bind(this, view);
        mActivity = (SoundMainActivity) getActivity();
        iv_mic.setBackgroundResource(R.drawable.mic_animation);
        recordingTransition = (AnimationDrawable)iv_mic.getBackground();
        recordingTransition.start();
        mActivity.prepareRecorder();
//        initTickTockView();
//
        questons = getResources().getStringArray(R.array.SoundQueston);
        mQueston.setText(questons[0]);
        return view;
    }

//    boolean isStartRecorder;
//    private MyCount myCount;
    @OnClick(R.id.btn_start_recorder)
    public void click() {
       /* if (mBtnStartRecorder.isEnabled()) {
            mActivity.prepareRecorder(fileName + "_", "second_");
        }
        setBtnText();*/
        ++ currentQueston;
        if (currentQueston == questons.length - 1) {
            mBtnStartRecorder.setText(mActivity.getString(R.string.sound_complete));
        }
        if (currentQueston < questons.length) {
            mQueston.setText(questons[currentQueston]);
        } else {
            mActivity.finishTesting();
          //  ttv.stop();
        }
    }
//    private void initTickTockView()
//    {
//        Calendar start = Calendar.getInstance();
//        start.add(Calendar.SECOND,-1);
//        Calendar end = Calendar.getInstance();
//        end.add(Calendar.SECOND,60);
//        ttv.setOnTickListener(new TickTockView.OnTickListener() {
//            @Override
//            public String getText(long timeRemainingInMillis) {
//                if(timeRemainingInMillis<=0)
//                {
//                    mActivity.finishTesting();
//                }
//                return timeRemainingInMillis/1000+1+"秒";
//            }
//        });
//        ttv.start(start,end);
//    }
//    private void setBtnText() {
//        isStartRecorder = !isStartRecorder;
//        if (isStartRecorder) {
//            myCount = new MyCount(30000, 1000);
//            myCount.start();
//            mBtnStartRecorder.setText("结束！");
//            mBtnStartRecorder.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.freebie_2));
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
//        mBtnStartRecorder.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.freebie_4));
//        mBtnStartRecorder.setText(getString(R.string.btn_start_record));
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
//        //    mBtnStartRecorder.setBackgroundColor(Color.parseColor(""));
//           // mBtnStartRecorder.setText(String.format(Locale.CHINA, getString(R.string.sound_count_down), (l / 1000)));
//        }
//
//        @Override
//        public void onFinish() {
//            finishRecord();
//        }
//    }

//    private void finishRecord() {
//        release();
//        mActivity.showDialog(false);
//    }


}
