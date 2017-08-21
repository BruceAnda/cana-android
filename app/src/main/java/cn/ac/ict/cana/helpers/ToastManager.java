package cn.ac.ict.cana.helpers;

import android.content.Context;
import android.widget.Toast;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
/**
 * Author: saukymo
 * Date: 9/12/16
 */
@EBean(scope = EBean.Scope.Singleton)
public class ToastManager {

    @RootContext
    Context context;

    public ToastManager(Context ctx) {
        context = ctx;
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void show(CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    public void show(int textResId) {
        Toast.makeText(context, textResId, Toast.LENGTH_SHORT).show();
    }
}