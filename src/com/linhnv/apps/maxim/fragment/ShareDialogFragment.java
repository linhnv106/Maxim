package com.linhnv.apps.maxim.fragment;

import java.util.ArrayList;
import java.util.List;

import com.linhnv.apps.maxim.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ShareDialogFragment extends DialogFragment {
	private IShareDialogListener mListener;
	private ListView mListView;
	private List<ShareItem> options;;
	private ShareOptionAdapter adapter;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Get the layout inflater
		options= new ArrayList<ShareItem>();
		ShareItem fb = new ShareItem("Facebook", R.drawable.icon_facebook_active);
		options.add(fb);
		ShareItem tw = new ShareItem("Twitter", R.drawable.icon_twitter_active);
		options.add(tw);
		ShareItem mail = new ShareItem("Others", R.drawable.social_share);
		options.add(mail);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View convertView = inflater.inflate(R.layout.share_dialog, null);
		mListView = (ListView) convertView.findViewById(R.id.shareOptions);
		adapter= new ShareOptionAdapter(getActivity(), 0,options);
		mListView.setAdapter(adapter);
		
		builder.setView(convertView);
		return builder.create();
	}
	public void setListener(IShareDialogListener listener){
		mListener=listener;
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
	}

	class ShareItem {
		String name;
		int icon_id;

		public ShareItem(String name, int icon_id) {
			super();
			this.name = name;
			this.icon_id = icon_id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getIcon_id() {
			return icon_id;
		}

		public void setIcon_id(int icon_id) {
			this.icon_id = icon_id;
		}

	}

	class ShareOptionAdapter extends ArrayAdapter<ShareItem> {
		LayoutInflater inflater;
		Context context;
		
		public ShareOptionAdapter(Context context, int textViewResourceId, List<ShareItem> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			this.context=context;
			inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final Holder holder;
			final ShareItem item= getItem(position);
			if(convertView==null){
				convertView=inflater.inflate(R.layout.share_option_layout, null);
				holder= new Holder();
				holder.icon=(ImageView)convertView.findViewById(R.id.share_option_icon);
				holder.name=(TextView)convertView.findViewById(R.id.share_option_name);
				convertView.setTag(holder);
			}else{
				holder=(Holder)convertView.getTag();
			}
			holder.icon.setImageResource(item.getIcon_id());
			holder.name.setText(item.getName());
			convertView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(mListener!=null){
						mListener.onShareOptionSelected(position);
					}
				}
			});
			return convertView;
		}
		class Holder{
			ImageView icon;
			TextView name;
		}
	}

	public interface IShareDialogListener {
		void onShareOptionSelected(int id);
	}
}
