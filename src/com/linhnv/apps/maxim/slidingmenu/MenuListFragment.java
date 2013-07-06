package com.linhnv.apps.maxim.slidingmenu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.linhnv.apps.maxim.R;
import com.linhnv.apps.maxim.provider.ISlidingMenuProvider;

public class MenuListFragment extends ListFragment {		
	private ISlidingMenuProvider mISlidingMenuProvider;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		List<SlidingMenu> list = new ArrayList<SlidingMenu>();
		SlidingMenu menu01= new SlidingMenu(0, R.drawable.top_rating_important, "Today\'s Girls");
		list.add(menu01);
		SlidingMenu menu02= new SlidingMenu(1,  R.drawable.celebrity, "Maxim Girls");
		list.add(menu02);
		SlidingMenu menu03= new SlidingMenu(2,  R.drawable.favorite, "Hot 100");
		list.add(menu03);
		SlidingMenu menu04= new SlidingMenu(3,  R.drawable.about, "About App");
		list.add(menu04);
		MenuAdapter adapter = new MenuAdapter(getActivity(),list);
		setListAdapter(adapter);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			mISlidingMenuProvider=(ISlidingMenuProvider)activity;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if(mISlidingMenuProvider!=null){
			mISlidingMenuProvider.onSlidingMenuSelected(position);
		}
	}

	class MenuAdapter extends ArrayAdapter<SlidingMenu>{
		LayoutInflater layoutInflater;
		public MenuAdapter(Context context,List<SlidingMenu> list) {
			super(context,0,list);
			layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {			
			final Holder holder;
			final SlidingMenu menu = getItem(position);
			if(convertView==null){
				holder = new Holder();
				convertView=layoutInflater.inflate(R.layout.list_item_icon_text, null);
				holder.icon=(ImageView)convertView.findViewById(R.id.icon);
				holder.name=(TextView)convertView.findViewById(R.id.text);
				convertView.setTag(holder);
			}else{
				holder=(Holder)convertView.getTag();
			}
			holder.icon.setImageResource(menu.getIcon());
			holder.name.setText(menu.getName());
			return convertView;
		}
		
		class Holder{
			ImageView icon;
			TextView name;
		}
	}

}
