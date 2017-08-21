package cn.ac.ict.cana.adapters;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.models.Permission;

/**
 * Author: saukymo
 * Date: 9/12/16
 * Reference: https://github.com/excilys/androidannotations/wiki/Adapters-and-lists
 */
@EViewGroup(R.layout.adapter_permission)
public class PermissionView extends LinearLayout {

    @ViewById(R.id.btn_permission_name) public TextView btnPermissionName;

    public PermissionView(Context context) {
        super(context);
    }

    public void bind(Permission permission) {
        btnPermissionName.setText(permission.permissionName);
    }
}
