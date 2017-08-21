package cn.ac.ict.cana.models;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import cn.ac.ict.cana.helpers.ModuleHelper;

/**
 * Author: saukymo
 * Date: 9/13/16
 */
public class Exam {
    public final String name;
    SharedPreferences settings;

    @Deprecated
    public Exam(String examName) {
        name = examName;
    }

    public Exam(Builder builder) {
        this.name = builder.name;
    }

    String[] perm = {Manifest.permission.RECORD_AUDIO};
    //TODO: This method shouldn't be here. Move it out.
    public void go(Context context) {
        /*if (ModuleHelper.MODULE_SOUND.equals(name)) {
            jump(context);
           *//* if (EasyPermissions.hasPermissions(context, perm)) {
                jump(context);
            } else {
                EasyPermissions.requestPermissions((Activity) context, "请求权限", 0x00, perm);
            }*//*
        } else {
            jump(context);
        }*/
        jump(context);

    }

    private void jump(Context context) {
        Intent intent = new Intent();

        settings = context.getSharedPreferences("Cana", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings .edit();

        editor.putString("ModuleName", name);
        editor.apply();

        intent.setClass(context, ModuleHelper.getModule(name));
        context.startActivity(intent);
    }

    public static class Builder {

        private String name;
        public Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Exam build() {
            return new Exam(this);
        }
    }
}
