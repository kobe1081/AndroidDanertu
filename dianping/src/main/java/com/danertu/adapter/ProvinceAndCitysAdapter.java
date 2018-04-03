package com.danertu.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class ProvinceAndCitysAdapter extends BaseExpandableListAdapter
		implements Filterable {

	private String[] groups;

	private String[][] childs;

	/*private String[] allGroups;

	private String[][] allChilds;*/

	private Context context;

	private CityFilter filter;

	private ExpandableListView provinceList;

	public ProvinceAndCitysAdapter(Context context,
			ExpandableListView listView, String[] groups, String[][] childs) {
		this.context = context;
		this.groups = groups;
		this.childs = childs;
		/*allGroups = groups;
		allChilds = childs;*/
		this.provinceList = listView;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childs[groupPosition][childPosition];
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		TextView textView = null;

		if (convertView == null) {

			textView = getGenericView();

			textView.setText(getChild(groupPosition, childPosition).toString());
		} else {
			textView = (TextView) convertView;
			textView.setText(getChild(groupPosition, childPosition).toString());
		}

		return textView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childs[groupPosition].length;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups[groupPosition];
	}

	@Override
	public int getGroupCount() {
		return groups.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView textView = null;
		if (convertView == null) {
			textView = getGenericView();
			textView.setText(getGroup(groupPosition).toString());
		} else {
			textView = (TextView) convertView;
			textView.setText(getGroup(groupPosition).toString());
		}

		return textView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private TextView getGenericView() {
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 80);

		TextView textView = new TextView(context);

		textView.setLayoutParams(lp);
		textView.setTextSize(23);
		textView.setTextColor(Color.WHITE);
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		textView.setPadding(100, 0, 0, 0);
		return textView;
	}

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new CityFilter();
		}
		return filter;
	}

	private class CityFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();

			Map<Integer, ArrayList<Integer>> values = new HashMap<>();

			if (constraint == null || constraint.length() == 0) {
				for (int i = 0; i < groups.length; i++) {
					ArrayList<Integer> index = new ArrayList<>();

					for (int j = 0; j < childs[i].length; j++) {
						index.add(j);
					}
					values.put(i, index);
				}
			} 
			/*else {
				String filterStr = constraint.toString();
				for (int i = 0; i < allGroups.length; i++) {

					if (allGroups[i].contains(filterStr)) {
						ArrayList<Integer> index = new ArrayList<Integer>();

						for (int j = 0; j < allChilds[i].length; j++) {
							index.add(j);
						}
						values.put(i, index);
					} else {
						ArrayList<Integer> index = new ArrayList<Integer>();

						for (int j = 0; j < allChilds[i].length; j++) {
							if (allChilds[i][j].contains(filterStr)) {
								index.add(j);
							}
						}

						if (index.size() > 0) {
							values.put(i, index);
						} else {
							index = null;
						}
					}
				}
			}*/

			results.values = values;
			results.count = values.size();

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {

			Map<Integer, ArrayList<Integer>> filterResult = (Map<Integer, ArrayList<Integer>>) results.values;
			int count = filterResult.size();

			if (count > 0) {
				String[] newGroups = new String[count];
				String[][] newChilds = new String[count][];
				int index = 0;
				int length = 0;

				for (int i = 0; i < groups.length; i++) {
					if (filterResult.containsKey(i)) {
						newGroups[index] = groups[i];

						ArrayList<Integer> citys = filterResult.get(i);
						length = citys.size();
						newChilds[index] = new String[length];
						for (int j = 0; j < length; j++) {
							newChilds[index][j] = childs[i][citys.get(j)];
						}
						index = index + 1;
					}
				}

				groups = newGroups;
				childs = newChilds;

				notifyDataSetChanged();

				count = getGroupCount();
				if (count < 34) {

					for (int i = 0; i < count; i++) {
						provinceList.expandGroup(i);
					}
				} else {

					for (int i = 0; i < count; i++) {
						provinceList.collapseGroup(i);
					}
				}
			} else {

				notifyDataSetInvalidated();
			}
		}
	}
}
