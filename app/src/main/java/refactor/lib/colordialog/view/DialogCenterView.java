package cn.refactor.lib.colordialog.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.ac.ict.canalib.R;


/**
 * Created by zhaoliang on 2017/4/28.
 */

public class DialogCenterView extends RelativeLayout {

    private ImageView mIcon;
    private TextView mTvTitle;
    private TextView mTvContent;
    private int iconResource;
    private String title;
    private String tvContent;

    public DialogCenterView(Context context) {
        this(context, null);
    }

    public DialogCenterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DialogCenterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.dialog_center_view, this, true);

        mIcon = (ImageView) findViewById(R.id.iv_icon);
        mTvTitle = (TextView) findViewById(R.id.tv_permission_title);
        mTvContent = (TextView) findViewById(R.id.tv_permission_content);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DialogCenterViewArrts);
        iconResource = typedArray.getResourceId(R.styleable.DialogCenterViewArrts_icon, 0);
        title = typedArray.getString(R.styleable.DialogCenterViewArrts_title);
        tvContent = typedArray.getString(R.styleable.DialogCenterViewArrts_content);
        typedArray.recycle();

        if (iconResource != 0) {
            mIcon.setImageResource(iconResource);
        }
        if (!TextUtils.isEmpty(title)) {
            mTvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(tvContent)) {
            mTvContent.setText(tvContent);
        }
    }

}
