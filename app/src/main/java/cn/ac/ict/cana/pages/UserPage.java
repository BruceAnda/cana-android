package cn.ac.ict.cana.pages;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.helpers.DataBaseHelper;
import cn.ac.ict.cana.models.User;
import cn.ac.ict.cana.providers.UserProvider;

/**
 * Author: saukymo
 * Date: 9/13/16
 */
public class UserPage{

    static public View InitialUserPageView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.pageview_user, null, false);
        final UserProvider userProvider = new UserProvider(DataBaseHelper.getInstance(context));

        final ArrayList<User> userList = userProvider.getUsers();
        Log.d("MainAdapter", userList.toString());
      /*  final ListView lvUser = (ListView) view.findViewById(R.id.lv_user);
        final UserAdapter userAdapter = new UserAdapter(context);

        userAdapter.setList(userList);
        lvUser.setAdapter(userAdapter);
*/
        return view;
    }
}
