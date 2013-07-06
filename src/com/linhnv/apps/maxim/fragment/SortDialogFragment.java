package com.linhnv.apps.maxim.fragment;

import com.linhnv.apps.maxim.R;
import com.linhnv.apps.maxim.adapter.SortAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
public class SortDialogFragment extends DialogFragment{
	private ListView mListView;
	private SortAdapter mAdapter;
	private final String[] mData={"ALL","#","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
			
	};	
	private ISortSelectedProvider mISortSelectedProvider;
	
	
	public SortDialogFragment(ISortSelectedProvider provider) {
		super();
		mISortSelectedProvider=provider;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View convertView =getActivity().getLayoutInflater().inflate(R.layout.sort_dialog,null);
		mListView=(ListView)convertView.findViewById(R.id.sort_list);
		mAdapter= new SortAdapter(getActivity(), 0, mData);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(mISortSelectedProvider!=null){
					mISortSelectedProvider.onItemSelected(mData[arg2]);
				}
			}
		});
		builder.setView(convertView);
		return builder.create();
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	
	}

	public interface ISortSelectedProvider{
		void onItemSelected(String title);
	}

}
