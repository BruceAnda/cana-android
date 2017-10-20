package cn.refactor.lib.colordialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ac.ict.canalib.R;
import cn.refactor.lib.colordialog.util.DisplayUtil;


/**
 * 作者 : andy
 * 日期 : 15/11/7 17:26
 * 邮箱 : andyxialm@gmail.com
 * 描述 : Dialog
 */
public class ColorDialogPermissionDead extends Dialog implements View.OnClickListener {

    private TextView mBtnPositive, mBtnNagative;
    
    private ImageView mContentIv;

    private Bitmap mContentBitmap;

    private View mBtnGroupView, mDividerView, mBkgView, mDialogView;

    private TextView mTitleTv, mContentTv, mPositiveBtn, mNegativeBtn;

    private Drawable mDrawable;

    private AnimationSet mAnimIn, mAnimOut;

    private int mResId, mBackgroundColor, mTitleTextColor, mContentTextColor;

    private OnPositiveListener mPositiveListener;

    private OnNegativeListener mNegativeListener;

    private CharSequence mTitleText, mContentText, mPositiveText, mNegativeText;

    private float mTitleTextSize, mContentTextSize, mPositiveTextSize, mNegativeTextSize;

    private boolean mIsShowAnim;

    public ColorDialogPermissionDead(Context context) {
        this(context, 0);
    }

    public ColorDialogPermissionDead(Context context, int theme) {
        super(context, R.style.color_dialog);
        init();
    }

    private void callDismiss() {
        super.dismiss();
    }

    private void init() {
        mAnimIn = AnimationLoader.getInAnimation(getContext());
        mAnimOut = AnimationLoader.getOutAnimation(getContext());
        initAnimListener();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitleText = title;
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getContext().getText(titleId));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = View.inflate(getContext(), R.layout.layout_colordialog_permission_dead, null);
        setContentView(contentView);

        mBtnPositive = (TextView) findViewById(R.id.btn_negative);
        mBtnNagative = (TextView) findViewById(R.id.btn_positive);
        mBtnPositive.setOnClickListener(this);
        mBtnNagative.setOnClickListener(this);
        


        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mBkgView = contentView.findViewById(R.id.llBkg);
        mTitleTv = (TextView) contentView.findViewById(R.id.tvTitle);
        mContentTv = (TextView) contentView.findViewById(R.id.tvContent);
        mContentIv = (ImageView) contentView.findViewById(R.id.ivContent);

        mPositiveBtn = (TextView) contentView.findViewById(R.id.btnPositive);
        mNegativeBtn = (TextView) contentView.findViewById(R.id.btnNegative);

        mDividerView = contentView.findViewById(R.id.divider);
        mBtnGroupView = contentView.findViewById(R.id.llBtnGroup);

        mPositiveBtn.setOnClickListener(this);
        mNegativeBtn.setOnClickListener(this);

        mTitleTv.setText(mTitleText);
        //mTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTitleTextSize);
        mTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize);
        mContentTv.setText(mContentText);
        //mContentTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mContentTextSize);
        mContentTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContentTextSize);
        mPositiveBtn.setText(mPositiveText);
        //mPositiveBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, mPositiveTextSize);
        mPositiveBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mPositiveTextSize);
        mNegativeBtn.setText(mNegativeText);
        //mNegativeBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, mNegativeTextSize);
        mNegativeBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mNegativeTextSize);

        if (null == mPositiveListener && null == mNegativeListener) {
            mBtnGroupView.setVisibility(View.GONE);
        } else if (null == mPositiveListener && null != mNegativeListener) {
            mPositiveBtn.setVisibility(View.GONE);
            mDividerView.setVisibility(View.GONE);
            mNegativeBtn.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.sel_def_gray));
        } else if (null != mPositiveListener && null == mNegativeListener) {
            mNegativeBtn.setVisibility(View.GONE);
            mDividerView.setVisibility(View.GONE);
            mPositiveBtn.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.sel_def_gray));
        }

        if (null != mDrawable) {
            mContentIv.setBackgroundDrawable(mDrawable);
        }

        if (null != mContentBitmap) {
            mContentIv.setImageBitmap(mContentBitmap);
        }

        if (0 != mResId) {
            mContentIv.setBackgroundResource(mResId);
        }

        setTextColor();

        setBackgroundColor();

        setContentMode();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startWithAnimation(mIsShowAnim);
    }

    @Override
    public void dismiss() {
        dismissWithAnimation(mIsShowAnim);
    }

    private void startWithAnimation(boolean showInAnimation) {
        if (showInAnimation) {
            mDialogView.startAnimation(mAnimIn);
        }
    }

    private void dismissWithAnimation(boolean showOutAnimation) {
        if (showOutAnimation) {
            mDialogView.startAnimation(mAnimOut);
        } else {
            super.dismiss();
        }
    }

    private void initAnimListener() {
        mAnimOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.post(new Runnable() {
                    @Override
                    public void run() {
                        callDismiss();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void setBackgroundColor() {

        if (0 == mBackgroundColor) {
            return;
        }

        int radius = DisplayUtil.dp2px(getContext(), 6);
        float[] outerRadii = new float[]{radius, radius, radius, radius, 0, 0, 0, 0};
        RoundRectShape roundRectShape = new RoundRectShape(outerRadii, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
        shapeDrawable.getPaint().setColor(mBackgroundColor);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        mBkgView.setBackgroundDrawable(shapeDrawable);
    }

    private void setTextColor() {

        if (0 != mTitleTextColor) {
            mTitleTv.setTextColor(mTitleTextColor);
        }

        if (0 != mContentTextColor) {
            mContentTv.setTextColor(mContentTextColor);
        }

    }

    private void setContentMode() {
        boolean isImageMode = (null != mDrawable | null != mContentBitmap | 0 != mResId);
        boolean isTextMode = (!TextUtils.isEmpty(mContentText));

        if (isImageMode && isTextMode) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mContentTv.getLayoutParams();
            params.gravity = Gravity.BOTTOM;
            mContentTv.setLayoutParams(params);
            mContentTv.setBackgroundColor(Color.BLACK);
            mContentTv.getBackground().setAlpha(0x28);
            mContentTv.setVisibility(View.VISIBLE);
            mContentIv.setVisibility(View.VISIBLE);
            return;
        }

        if (isTextMode) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mContentTv.getLayoutParams();
            params.gravity = Gravity.NO_GRAVITY;
            mContentTv.setLayoutParams(params);
            mContentIv.setVisibility(View.GONE);
            mContentTv.setVisibility(View.VISIBLE);
            return;
        }

        if (isImageMode) {
            mContentTv.setVisibility(View.GONE);
            mContentIv.setVisibility(View.VISIBLE);
            return;
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (R.id.btnPositive == id) {
            mPositiveListener.onClick(this);
        } else if (R.id.btnNegative == id) {
            mNegativeListener.onClick(this);
        } else if (R.id.btn_negative == id) {
            listener.cancel(this);
        } else if (R.id.btn_positive == id) {
            listener.determine(this);
        } else {

        }
    }

    public ColorDialogPermissionDead setAnimationEnable(boolean enable) {
        mIsShowAnim = enable;
        return this;
    }

    public ColorDialogPermissionDead setAnimationIn(AnimationSet animIn) {
        mAnimIn = animIn;
        return this;
    }

    public ColorDialogPermissionDead setAnimationOut(AnimationSet animOut) {
        mAnimOut = animOut;
        initAnimListener();
        return this;
    }

    public ColorDialogPermissionDead setColor(int color) {
        mBackgroundColor = color;
        return this;
    }

    public ColorDialogPermissionDead setColor(String color) {
        try {
            setColor(Color.parseColor(color));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ColorDialogPermissionDead setTitleTextColor(int color) {
        mTitleTextColor = color;
        return this;
    }

    public ColorDialogPermissionDead setTitleTextColor(String color) {
        try {
            setTitleTextColor(Color.parseColor(color));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ColorDialogPermissionDead setContentTextColor(int color) {
        mContentTextColor = color;
        return this;
    }

    public ColorDialogPermissionDead setContentTextColor(String color) {
        try {
            setContentTextColor(Color.parseColor(color));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return this;
    }


    public ColorDialogPermissionDead setPositiveListener(CharSequence text, OnPositiveListener l) {
        mPositiveText = text;
        mPositiveListener = l;
        return this;
    }

    public ColorDialogPermissionDead setPositiveListener(int textId, OnPositiveListener l) {
        return setPositiveListener(getContext().getText(textId), l);
    }

    public ColorDialogPermissionDead setNegativeListener(CharSequence text, OnNegativeListener l) {
        mNegativeText = text;
        mNegativeListener = l;
        return this;
    }

    public ColorDialogPermissionDead setNegativeListener(int textId, OnNegativeListener l) {
        return setNegativeListener(getContext().getText(textId), l);
    }

    public ColorDialogPermissionDead setContentText(CharSequence text) {
        mContentText = text;
        return this;
    }

    public ColorDialogPermissionDead setContentTextSize(float size) {
        mContentTextSize = size;
        return this;
    }

    public ColorDialogPermissionDead setTitleTextSize(float size) {
        mTitleTextSize = size;
        return this;
    }

    public ColorDialogPermissionDead setPositiveTextSize(float size) {
        mPositiveTextSize = size;
        return this;
    }

    public ColorDialogPermissionDead setNegativeTextSize(float size) {
        mNegativeTextSize = size;
        return this;
    }


    public ColorDialogPermissionDead setContentText(int textId) {
        return setContentText(getContext().getText(textId));
    }

    public ColorDialogPermissionDead setContentImage(Drawable drawable) {
        mDrawable = drawable;
        return this;
    }

    public ColorDialogPermissionDead setContentImage(Bitmap bitmap) {
        mContentBitmap = bitmap;
        return this;
    }

    public ColorDialogPermissionDead setContentImage(int resId) {
        mResId = resId;
        return this;
    }

    public CharSequence getContentText() {
        return mContentText;
    }

    public CharSequence getTitleText() {
        return mTitleText;
    }

    public CharSequence getPositiveText() {
        return mPositiveText;
    }

    public CharSequence getNegativeText() {
        return mNegativeText;
    }

    public interface OnPositiveListener {
        void onClick(ColorDialogPermissionDead dialog);
    }

    public interface OnNegativeListener {
        void onClick(ColorDialogPermissionDead dialog);
    }

    private ImageView ivClose;
    private Button btnStart;
    private OnClickListener listener;

    public ColorDialogPermissionDead setListener(OnClickListener listener) {
        this.listener = listener;
        return this;
    }

    public interface OnClickListener {
        void cancel(ColorDialogPermissionDead dialog);

        void determine(ColorDialogPermissionDead dialog);
    }
}
