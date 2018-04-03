package com.danertu.adapter;

import java.util.List;
import java.util.Map;

import com.danertu.dianping.CategoryProductActivity;
import com.danertu.dianping.R;
import com.danertu.entity.SecondCategory;
import com.danertu.entity.ThreeCategory;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseExpandableAdapter extends BaseExpandableListAdapter {

    private List<SecondCategory> parentList;
    private Map<String, List<ThreeCategory>> childList;
    private Context context;

    String path = "http://img.danertu.com/catoryImg/";
    ImageLoader imageLoader = null;

    public BaseExpandableAdapter(Context context, List<SecondCategory> parentList, Map<String, List<ThreeCategory>> childList) {
        for (int i = 0; i < parentList.size(); i++) {//三级分类若没有内容则隐藏标题
            SecondCategory secItem = parentList.get(i);
            List<ThreeCategory> value = childList.get(secItem.GetCategoryName());
            if (value.size() <= 0) {
                parentList.remove(i);
                i--;
            }
        }
        this.context = context;
        this.childList = childList;
        this.parentList = parentList;
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(parentList.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    private class ChildViewHolder {
        ChildGridView gridView;
        FollowListView gridChild;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View v, ViewGroup parent) {
        ChildViewHolder vh = null;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.childitem, null);
            FollowListView gridchild = (FollowListView) v.findViewById(R.id.GridView_toolbar);
            ChildGridView gridview = new ChildGridView(context);
            vh = new ChildViewHolder();
            vh.gridChild = gridchild;
            vh.gridView = gridview;
            v.setTag(vh);
        } else {
            vh = (ChildViewHolder) v.getTag();
        }

        final List<ThreeCategory> value = childList.get(parentList.get(groupPosition).GetCategoryName());
        vh.gridView.setValue(value);
        vh.gridChild.setAdapter(vh.gridView);
        vh.gridChild.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String threeID = value.get(arg2).getThreeID();
                String threeName = value.get(arg2).getCategoryName();
                Intent intent = new Intent();
                intent.putExtra("threeID", threeID);
                intent.putExtra("name", threeName);
                intent.setClass(context, CategoryProductActivity.class);
                context.startActivity(intent);
                /*
                 * Toast.makeText(context, value.get(arg2).GetCategoryName(),
				 * 1000) .show();
				 */
            }
        });
        return v;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
//		 return childList.get(parentList.get(groupPosition)).size();
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return parentList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View v, ViewGroup parent) {
        if (v == null)
            v = LayoutInflater.from(context).inflate(R.layout.parentitem, null);
        TextView textview = (TextView) v.findViewById(R.id.text_parent);
        textview.setText(parentList.get(groupPosition).GetCategoryName());
        return v;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ChildGridView extends BaseAdapter {
        private Context context;
        private List<ThreeCategory> value;

        ChildGridView(Context context) {
            this.context = context;
        }

        public List<ThreeCategory> getValue() {
            return value;
        }

        public void setValue(List<ThreeCategory> value) {
            this.value = value;
        }

        @Override
        public int getCount() {
            return value.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ViewHolder viewHolder;
            if (v == null) {
                v = LayoutInflater.from(context).inflate(R.layout.gradchilditem, null);
                viewHolder = new ViewHolder(v);
                v.setTag(viewHolder);
            } else {
                viewHolder = ((ViewHolder) v.getTag());
            }

            String uri = path + value.get(position).getPhoneImage();
            imageLoader.displayImage(uri, viewHolder.iv);
            viewHolder.textview.setText(value.get(position).getCategoryName());
            return v;
        }

        class ViewHolder {
            TextView textview;
            ImageView iv;

            public ViewHolder(View view) {
                textview = (TextView) view.findViewById(R.id.textchild);
                iv = (ImageView) view.findViewById(R.id.iv_cateThree_child);
            }
        }
    }
}
