package com.lovearthstudio.duaui.util;

import android.content.Context;

import com.lovearthstudio.duaui.R;

import cn.refactor.lib.colordialog.ColorDialog;

/**
 * Created by zhaoliang on 2017/4/13.
 */

public class InfoUtil {

    public static void showDialog(Context context, String title, String content, String buttonText) {
        ColorDialog dialog = new ColorDialog(context);
        dialog.setColor(context.getResources().getColor(R.color.colorAccent));
        dialog.setContentTextSize(context.getResources().getDimension(R.dimen.info_content_text_size));
        dialog.setTitleTextSize(context.getResources().getDimension(R.dimen.info_title_text_size));
        dialog.setNegativeTextSize(context.getResources().getDimension(R.dimen.info_button_text_size));
        dialog.setPositiveTextSize(context.getResources().getDimension(R.dimen.info_button_text_size));
        dialog.setAnimationEnable(true);
        dialog.setTitle(title);
        dialog.setContentText(content);
        dialog.setPositiveListener(buttonText, new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }
}
