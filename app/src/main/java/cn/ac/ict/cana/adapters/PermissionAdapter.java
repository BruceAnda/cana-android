package cn.ac.ict.cana.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.ToastManager;
import cn.ac.ict.cana.models.Permission;

/**
 * Author: saukymo
 * Date: 9/12/16
 * Reference: https://github.com/excilys/androidannotations/wiki/Adapters-and-lists
 */

@EBean
public class PermissionAdapter extends BaseAdapter {

    @Bean ToastManager toastManager;
    @RootContext Context mContext;

    private List<Permission> mPermissionSet;

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final PermissionView permissionView;
        if (view == null) {
            permissionView = PermissionView_.build(mContext);
        } else {
            permissionView = (PermissionView) view;
        }


        Permission permission = getItem(position);
        permissionView.bind(permission);

        Button btnPermissionName = (Button) permissionView.findViewById(R.id.btn_permission_name);

        if (!permission.permissionStatus) {
            btnPermissionName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("CheckedChanged", String.valueOf(mPermissionSet.size()) + "," + String.valueOf(position));
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{mPermissionSet.get(position).permissionName}, position);
                }
            });
        } else {
            btnPermissionName.setEnabled(false);
            btnPermissionName.setBackgroundColor(mContext.getResources().getColor(R.color.freebie_9));
            btnPermissionName.setTextColor(mContext.getResources().getColor(R.color.freebie_6));
        }

        return permissionView;

    }

    public void setList(ArrayList<Permission> permissionSet) {
        mPermissionSet = permissionSet;
    }
    @Override
    public int getCount() {
        return mPermissionSet.size();
    }

    @Override
    public Permission getItem(int position) {
        return mPermissionSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(Permission permission){
        mPermissionSet.add(permission);
    }

    public void deleteItem(int position) {
        mPermissionSet.remove(position);
    }


}
