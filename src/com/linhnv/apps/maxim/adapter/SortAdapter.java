package com.linhnv.apps.maxim.adapter;

import com.linhnv.apps.maxim.R;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SortAdapter extends ArrayAdapter<String>{
	private FragmentActivity mContext;
	private LayoutInflater mInflater;
	public SortAdapter(FragmentActivity context, int textViewResourceId, String[] objects) {
		super(context, textViewResourceId, objects);
		mContext=context;
		mInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String item =getItem(position);
		final Holder holder;
		if(convertView==null){
			holder = new Holder();
			convertView=mInflater.inflate(android.R.layout.simple_list_item_1, null);
			holder.name=(TextView)convertView.findViewById(android.R.id.text1);
			convertView.setTag(holder);
		}else{
			holder =(Holder)convertView.getTag();
		}
		holder.name.setText(item);
		return convertView;
	}
	class Holder {
		TextView name;
	}
}
