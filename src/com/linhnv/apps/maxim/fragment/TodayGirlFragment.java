package com.linhnv.apps.maxim.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.linhnv.apps.maxim.R;
import com.linhnv.apps.maxim.adapter.TodayAdapter;
import com.linhnv.apps.maxim.model.CelebrityEntry;
import com.linhnv.apps.maxim.provider.ICelebritySelectProvider;
import com.linhnv.apps.maxim.provider.ILoadFeedProvider;
import com.linhnv.apps.maxim.tasks.ReadTodayFeedTask;

public class TodayGirlFragment extends SherlockFragment implements ILoadFeedProvider<CelebrityEntry>{
	private final static String TAG="TodayGirlFragment";
	private ProgressBar mLoadProgressBar;
	private ProgressBar mLoadMoreBar;
	private ListView mListView;
	private List<CelebrityEntry> mData;
	private int mCurrentPage=0;
	private TodayAdapter mAdapter;
	private boolean isLoadingMore;
	private int mCurrentPosition=0;
	private boolean isFirstTime=true;
	private boolean isEnd=false;
	private ICelebritySelectProvider mICelebritySelectProvider;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View convertView=inflater.inflate(R.layout.today_feed_layout, container, false);
		mLoadProgressBar=(ProgressBar)convertView.findViewById(R.id.today_proBar);
		mLoadMoreBar=(ProgressBar)convertView.findViewById(R.id.today_loadmoreBar);
		mListView=(ListView)convertView.findViewById(R.id.today_listView);
		mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
					int totalItemCount) {
//				Log.i(TAG, "firstVisibleItem :" + firstVisibleItem +"visibleItemCount :" + visibleItemCount +" totalItemCount " + totalItemCount);
				if(((firstVisibleItem + visibleItemCount)==totalItemCount)&&!isLoadingMore){
					Log.e(TAG, "loadmore");
					mCurrentPosition=firstVisibleItem;
					loadMoreData();
				}
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(mICelebritySelectProvider!=null){
					mICelebritySelectProvider.onCelebritySelected(mData.get(arg2));
				}
			}
			
		});
		return convertView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			mICelebritySelectProvider=(ICelebritySelectProvider)activity;
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			 SherlockFragmentActivity activity1 = (SherlockFragmentActivity)getActivity();			
				 activity1.getSupportActionBar().setTitle("Today\'s Girls");			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		loadData();
	}

	private void loadData(){
		
		if(mData!=null){
			mAdapter= new TodayAdapter(getActivity(), 0, mData);
			mListView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
			return;
		}
		mCurrentPage=0;
		mLoadProgressBar.setVisibility(View.VISIBLE);
		mLoadMoreBar.setVisibility(View.GONE);
		ReadTodayFeedTask task = new ReadTodayFeedTask(this, mCurrentPage);
		task.execute("");
	}
	private void loadMoreData(){
		if(isLoadingMore||isFirstTime||isEnd){
			return;
		}
		isLoadingMore=true;
		mCurrentPage++;
		ILoadFeedProvider<CelebrityEntry> provider = new ILoadFeedProvider<CelebrityEntry>() {
			
			@Override
			public void onLoadFeedFailed() {
				mLoadProgressBar.setVisibility(View.GONE);
				mLoadMoreBar.setVisibility(View.GONE);
				isLoadingMore=false;
				isEnd=true;
			}
			
			@Override
			public void onLoadFeedComplete(List<CelebrityEntry> result) {
				if(getActivity()==null){
					return;
				}
				mLoadProgressBar.setVisibility(View.GONE);
				mLoadMoreBar.setVisibility(View.GONE);	
				isEnd=false;
				if(mData==null){
					mData= new ArrayList<CelebrityEntry>();
				}
				mData.addAll(result);
				mAdapter = new TodayAdapter(getActivity(), 0, mData);
				mListView.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mCurrentPosition);
				isLoadingMore=false;
			}
		};
		mLoadProgressBar.setVisibility(View.GONE);
		mLoadMoreBar.setVisibility(View.VISIBLE);
		ReadTodayFeedTask task = new ReadTodayFeedTask(provider, mCurrentPage);
		task.execute("");
	}

	@Override
	public void onLoadFeedComplete(List<CelebrityEntry> result) {
		isFirstTime=false;
		mData=null;
		mData= new ArrayList<CelebrityEntry>();
		mData.addAll(result);
		mAdapter = new TodayAdapter(getActivity(), 0, mData);
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		mLoadProgressBar.setVisibility(View.GONE);
		mLoadMoreBar.setVisibility(View.GONE);
	}

	@Override
	public void onLoadFeedFailed() {
		mLoadProgressBar.setVisibility(View.GONE);
		mLoadMoreBar.setVisibility(View.GONE);
	}

	
}
