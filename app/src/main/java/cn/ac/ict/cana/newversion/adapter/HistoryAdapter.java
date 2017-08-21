package cn.ac.ict.cana.newversion.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lovearthstudio.duasdk.Dua;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import cn.ac.ict.cana.R;
import cn.ac.ict.cana.events.CheckedItemChangedEvent;
import cn.ac.ict.cana.events.NewHistoryEvent;
import cn.ac.ict.cana.events.ResponseEvent;
import cn.ac.ict.cana.helpers.DataBaseHelper;
import cn.ac.ict.cana.helpers.ModuleHelper;
import cn.ac.ict.cana.newversion.mode.History;
import cn.ac.ict.cana.providers.UserProvider;
import cn.ac.ict.cana.widget.BaseTreeViewAdapter;
import cn.ac.ict.cana.widget.TreeView;

/**
 * Author: saukymo
 * Date: 9/13/16
 */

public class HistoryAdapter extends BaseTreeViewAdapter {
    private  ArrayList<String> mGroups;
    private ArrayList<ArrayList<History>> mChildren;
    private final Set<ContentValues> mCheckedItems = new HashSet<>();
    private final Set<ContentValues> mUnuploadedItems = new HashSet<>();
    private Context mContext;
    private UserProvider userProvider;
    public HistoryAdapter(Context context, TreeView treeView, ArrayList<String> mGroups, ArrayList<ArrayList<History>> mChildren) {
        super(treeView);
        this.mGroups = mGroups;
        this.mChildren = mChildren;
        mContext = context;
        this.userProvider = new UserProvider(DataBaseHelper.getInstance(context));
        EventBus.getDefault().register(this);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildren.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildren.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private class ChildHolder {
        TextView tvHistoryUserName;
        TextView tvHistoryIsUploaded;
        TextView tvHistoryCreatedTime;
        CheckBox cbHistory;

        private ChildHolder(View view) {
            tvHistoryUserName = (TextView) view.findViewById(R.id.tv_history_user_name);
            tvHistoryIsUploaded = (TextView) view. findViewById(R.id.tv_history_is_upload);
            tvHistoryCreatedTime = (TextView) view.findViewById(R.id.tv_history_created_time);
            cbHistory = (CheckBox) view.findViewById(R.id.cb_history);
        }
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.adapter_history, null);
        }

        History history = mChildren.get(groupPosition).get(childPosition);
        ChildHolder holder = getChildHolder(convertView);

        // In android Studio, these have to extract a new variable;
        final long id = history.id;
        final boolean isUploaded = history.isUpload;
        holder.tvHistoryUserName.setText("用户: " + Dua.getInstance().getCurrentDuaUser().name.replace("dua:", ""));

        if (isUploaded) {
            holder.tvHistoryIsUploaded.setText("已上传");
            holder.tvHistoryIsUploaded.setTextColor(ContextCompat.getColor(mContext, R.color.freebie_2));
        } else {
            holder.tvHistoryIsUploaded.setText("未上传");
            holder.tvHistoryIsUploaded.setTextColor(ContextCompat.getColor(mContext, R.color.freebie_4));
        }
        holder.tvHistoryCreatedTime.setText("测试时间: " + history.createdTime);

        final ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("groupPosition", groupPosition);
        contentValues.put("childPosition", childPosition);
        holder.cbHistory.setChecked(mCheckedItems.contains(contentValues));
        holder.cbHistory.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final CheckBox cb = (CheckBox) v;
                if (cb.isChecked()) {
                    mCheckedItems.add(contentValues);
                } else {
                    mCheckedItems.remove(contentValues);
                }
                EventBus.getDefault().post(new CheckedItemChangedEvent(mCheckedItems.size()));
            }
        });

        return convertView;
    }

    public Set<ContentValues>getUnUploadIds() {
        ContentValues contentValues;
        for(int group = 0; group<getGroupCount();group++){
            for(int child =0;child<getChildrenCount(group);child++){
                History history = mChildren.get(group).get(child);
                if(history.isUpload==false){
                    contentValues= new ContentValues();
                    contentValues.put("id", history.id);
                    contentValues.put("groupPosition", group);
                    contentValues.put("childPosition", child);
                    mUnuploadedItems.add(contentValues);
                }
            }


        }
        return mUnuploadedItems;
    }

    public Set<ContentValues> getCheckedIds() {
        return mCheckedItems;
    }

    private ChildHolder getChildHolder(final View view) {
        ChildHolder holder = (ChildHolder) view.getTag();
        if (null == holder) {
            holder = new ChildHolder(view);
            view.setTag(holder);
        }
        return holder;
    }


    private class GroupHolder {
        TextView name;
        ImageView indicator;
        TextView onlineNum;

        private GroupHolder(View view) {
            name = (TextView) view.findViewById(R.id.group_name);
            indicator = (ImageView) view.findViewById(R.id.group_indicator);
            onlineNum = (TextView) view.findViewById(R.id.online_count);
        }
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.adapter_group_history, null);
        }

        GroupHolder holder = getGroupHolder(convertView);

        holder.name.setText(ModuleHelper.getName(mContext, mGroups.get(groupPosition)));
        holder.onlineNum.setText(String.format(Locale.CHINA, "%s", getChildrenCount(groupPosition) + "/" + getChildrenCount(groupPosition)));
        if (isExpanded) {
            holder.indicator.setImageResource(R.drawable.ic_expanded);
        } else {
            holder.indicator.setImageResource(R.drawable.ic_unexpanded);
        }

        return convertView;
    }

    private GroupHolder getGroupHolder(final View view) {
        GroupHolder holder = (GroupHolder) view.getTag();
        if (null == holder) {
            holder = new GroupHolder(view);
            view.setTag(holder);
        }
        return holder;
    }


    @Override
    public void updateHeader(View header, int groupPosition, int childPosition, int alpha) {
        ((TextView) header.findViewById(R.id.group_name)).setText(mGroups.get(groupPosition));
        ((TextView) header.findViewById(R.id.online_count)).setText(String.format(Locale.CHINA, "%s", getChildrenCount
                (groupPosition) + "/" + getChildrenCount(groupPosition)));
        header.setAlpha(alpha);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(ResponseEvent event){
        Log.d("History adapter", String.valueOf(event.id) + "g:" + event.groupPosition + " c:" + event.childPosition);
        History history = (History) getChild(event.groupPosition, event.childPosition);
        history.isUpload |= event.success;
        if (event.success) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", event.id);
            contentValues.put("groupPosition", event.groupPosition);
            contentValues.put("childPosition", event.childPosition);
            mCheckedItems.remove(contentValues);
        }
        notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void insert(NewHistoryEvent event){
       /* Log.d("HistroyAdapter", event.toString());
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("Cana", Context.MODE_PRIVATE);
        long historyId = sharedPreferences.getLong("HistoryId", 0);

        HistoryProvider historyProvider = new HistoryProvider(DataBaseHelper.getInstance(mContext));
        History history = historyProvider.getHistory(historyId);

        insertItem(history);
        notifyDataSetChanged();*/
    }

    public void removeItems(ArrayList<ContentValues> Items){
        /*
         * First cache histories to delete. And then delete them all.
         * This way is really slow and redundant.
         */

        SparseArray<ArrayList<History>> pending = new SparseArray<ArrayList<History>>(){{
            for (int i=0; i<ModuleHelper.ModuleList.size(); i++) {
                put(i, new ArrayList<History>());
            }}
        };

        for (ContentValues item: Items) {
            int groupPosition = (int) item.get("groupPosition");
            int childPosition = (int) item.get("childPosition");
            Log.d("HistoryAdapter", String.format("groupPosition: %d, childPosition: %d", groupPosition, childPosition));
            pending.get(groupPosition).add(mChildren.get(groupPosition).get(childPosition));
        }

        mCheckedItems.clear();
        EventBus.getDefault().post(new CheckedItemChangedEvent(mCheckedItems.size()));

        for (int i=0; i<ModuleHelper.ModuleList.size(); i++) {
            for (History history: pending.get(i)) {
                mChildren.get(i).remove(history);
            }
        }

        notifyDataSetChanged();
    }

    public void insertItem(History history) {
        Log.d("HistoryAdapter", "insertItem: " + history.toString());
        int groupPosition = ModuleHelper.ModuleList.indexOf(history.type);
        mChildren.get(groupPosition).add(history);
        notifyDataSetChanged();
    }

}
