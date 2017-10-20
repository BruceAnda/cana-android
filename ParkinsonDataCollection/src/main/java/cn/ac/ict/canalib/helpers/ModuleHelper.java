package cn.ac.ict.canalib.helpers;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;

import cn.ac.ict.canalib.R;

/**
 * Author: saukymo
 * Date: 9/19/16
 */
public class ModuleHelper {
    public final static String MODULE_COUNT = "Count";
    public final static String MODULE_FACE = "Face";
    public final static String MODULE_SOUND = "Sound";
    public final static String MODULE_STAND = "Stand";
    public final static String MODULE_STRIDE = "Stride";
    public final static String MODULE_TAPPER = "Tapping";
    public final static String MODULE_TREMOR = "Tremor";
    public final static String MODULE_ARM_DROOP = "ArmDroop";

    /**
     * 数据类型
     */
    public final static String MODULE_DATATYPE_MEMORY = "Memory";
    public final static String MODULE_DATATYPE_TREMOR_LR = "Tremor_LR";
    public final static String MODULE_DATATYPE_TREMOR_LP = "Tremor_LP";
    public final static String MODULE_DATATYPE_TREMOR_RR = "Tremor_RR";
    public final static String MODULE_DATATYPE_TREMOR_RP = "Tremor_RP";
    public final static String MODULE_DATATYPE_STRIDE = "Stride";
    public final static String MODULE_DATATYPE_STAND_L = "Stand_L";
    public final static String MODULE_DATATYPE_STAND_R = "Stand_R";
    public final static String MODULE_DATATYPE_TAPPING_L = "Tapping_L";
    public final static String MODULE_DATATYPE_TAPPING_R = "Tapping_R";
    public final static String MODULE_DATATYPE_SOUND = "Sound";
    public final static String MODULE_DATATYPE_FACE = "Face";
    public final static String MODULE_DATATYPE_ARMDROOP_L = "ArmDroop_L";
    public final static String MODULE_DATATYPE_ARMDROOP_R = "ArmDroop_R";


    public static String getName(Context context, String moduleName) {
        if (null == moduleName) {
            return "";
        }
        switch (moduleName) {
            case MODULE_COUNT:
                return context.getString(R.string.module_count);
            case MODULE_TREMOR:
                return context.getString(R.string.module_face);
            case MODULE_SOUND:
                return context.getString(R.string.module_sound);
            case MODULE_STAND:
                return context.getString(R.string.module_stand);
            case MODULE_STRIDE:
                return context.getString(R.string.module_stride);
            case MODULE_TAPPER:
                return context.getString(R.string.module_tapper);
            case MODULE_FACE:
                return "面部表情";
            case MODULE_ARM_DROOP:
                return "手臂下垂";
            default:
                return "";
        }
    }

    public static String getEvaluationGuide(Context context, String moduleName) {
        String tips = context.getString(R.string.evaluation_guide_none);
        if (null == moduleName) {
            return tips;
        }
        switch (moduleName) {
            case MODULE_COUNT:
                tips = context.getString(R.string.evaluation_guide_count);
                break;
            case MODULE_STRIDE:
                tips = context.getString(R.string.evaluation_guide_stride);
                break;
            case MODULE_STAND:
                tips = context.getString(R.string.evaluation_guide_stand);
                break;
            case MODULE_FACE:
                tips = context.getString(R.string.evaluation_guide_face);
                break;
            case MODULE_TAPPER:
                tips = context.getString(R.string.evaluation_guide_tapper);
                break;
            case MODULE_SOUND:
                tips = context.getString(R.string.evaluation_guide_sound);
                break;
            default:
                tips = context.getString(R.string.evaluation_guide_none);
        }
        return tips;
    }
}
