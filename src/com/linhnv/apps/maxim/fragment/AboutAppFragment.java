package com.linhnv.apps.maxim.fragment;


import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.linhnv.apps.maxim.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author Linh Nguyen
 * 
 */
public class AboutAppFragment extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.about, container,false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		 setHasOptionsMenu(true);
//		 if(!isFreshData){
//			 loadData();
//		 }else{
			
//		 }
		 try{
			 SherlockFragmentActivity activity = (SherlockFragmentActivity)getActivity();			
			 activity.getSupportActionBar().setTitle("About app");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
