package com.linhnv.apps.maxim.fragment;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.linhnv.apps.maxim.adapter.CelebrityAdapter;
import com.linhnv.apps.maxim.constants.Constants;
import com.linhnv.apps.maxim.database.DatabaseHelper;
import com.linhnv.apps.maxim.model.CelebrityEntry;
import com.linhnv.apps.maxim.model.ImageEntry;
import com.linhnv.apps.maxim.provider.IEntryProvider;
import com.linhnv.apps.maxim.provider.ILoadFeedProvider;
import com.linhnv.apps.maxim.tasks.ReadEntryTask;
import com.linhnv.apps.maxim.tasks.ReadFeedTask;
import com.origamilabs.library.views.StaggeredGridView;
import com.origamilabs.library.views.StaggeredGridView.OnItemClickListener;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.linhnv.apps.maxim.R;

public class CelebrityFragment extends SherlockFragment implements ILoadFeedProvider<ImageEntry> {
	private StaggeredGridView mGridView;
	private CelebrityAdapter mAdapter;
	private FragmentActivity mActivity;
	private ProgressBar mProgressBar;
	private IEntryProvider mIEntryProvider;
	private ArrayList<ImageEntry> mData;
	private CelebrityEntry mEntry;
	private boolean isFresh=false;
	private boolean isLoadDataRunning=false;
	public CelebrityFragment(FragmentActivity activity,CelebrityEntry entry) {
		super();
		mActivity=activity;
		mEntry=entry;
		Log.i("CelebrityFragment", "onStart");
	
	}
	public CelebrityFragment() {
		super();		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		Log.i("CelebrityFragment", "onCreateView");
		View convertView = inflater.inflate(R.layout.celebrity_layout, container, false);
		mGridView = (StaggeredGridView) convertView.findViewById(R.id.staggeredGridView1);
		mProgressBar=(ProgressBar)convertView.findViewById(R.id.celebity_proBar);
		mGridView.setVisibility(View.GONE);
		int margin = getResources().getDimensionPixelSize(R.dimen.margin);

		mGridView.setItemMargin(margin); // set the GridView margin

		mGridView.setPadding(margin, 0, margin, 0); // have the margin on the
													// sides as well
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(StaggeredGridView parent, View view, int position, long id) {
				if(mIEntryProvider!=null){
					mIEntryProvider.onEntrySelectedProvider(mData,position);
				}
			}
		});
		return convertView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		 setHasOptionsMenu(true);
		setRetainInstance(true);

		try{
			 SherlockFragmentActivity activity = (SherlockFragmentActivity)getActivity();
			 if(mEntry!=null){
			  activity.getSupportActionBar().setTitle(mEntry.getTitle());
			 }else{
				 activity.getSupportActionBar().setTitle("Top Hot");
			 }
		}catch(Exception e){
			e.printStackTrace();
		}
		if(mData!=null&&mAdapter!=null){
			mGridView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
			mGridView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
			return;
		}
		 loadData(mEntry);
	
	}


	@Override
	public void setRetainInstance(boolean retain) {
		// TODO Auto-generated method stub
		super.setRetainInstance(retain);
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		Log.i("CelebrityFragment", "onResume");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try{
			mIEntryProvider=(IEntryProvider)activity;
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	private void loadData(CelebrityEntry entry) {
		if(isLoadDataRunning || isFresh){
			return;
		}
		
		
		isLoadDataRunning=true;
		mProgressBar.setVisibility(View.VISIBLE);
		ReadEntryTask task = new ReadEntryTask(this);
		task.execute(Constants.ROOT+mEntry.getUrl());

	}


	@Override
	public void onLoadFeedComplete(List<ImageEntry> result) {
		isLoadDataRunning=false;
		if(result==null||mActivity==null){
			return;
		}
		try{
			mData = new ArrayList<ImageEntry>();
			mData.addAll(result);
			Log.i("Celebrity Fragment", "load data result :"+ result.size());
			mAdapter = new CelebrityAdapter(mActivity, 0, result);
			mGridView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
			mGridView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
			isFresh=true;
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onLoadFeedFailed() {
		isLoadDataRunning=false;
		mGridView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}
	@Override
	public void onStop() {
		
		super.onStop();
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
	}
	
	
}
