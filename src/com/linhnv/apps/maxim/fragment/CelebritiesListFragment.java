package com.linhnv.apps.maxim.fragment;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView.OnHeaderClickListener;
import com.linhnv.apps.maxim.R;
import com.linhnv.apps.maxim.adapter.CelebritiesListAdapter;
import com.linhnv.apps.maxim.constants.Constants;
import com.linhnv.apps.maxim.fragment.SortDialogFragment.ISortSelectedProvider;
import com.linhnv.apps.maxim.model.CelebrityEntry;
import com.linhnv.apps.maxim.provider.ICelebritySelectProvider;
import com.linhnv.apps.maxim.provider.ILoadFeedProvider;
import com.linhnv.apps.maxim.tasks.ReadFeedTask;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AbsListView.OnScrollListener;

public class CelebritiesListFragment extends SherlockFragment implements ILoadFeedProvider<CelebrityEntry>,ISortSelectedProvider{
	private ProgressBar mProgressBar;
	private ProgressBar mLoadMoreData;
	private CelebritiesListAdapter mAdapter;
	private List<CelebrityEntry> mData;
	private ICelebritySelectProvider mICelebritySelectProvider;
	private boolean isEnd=false;
	private ListView mListView;
	private int mCurrentPage=0;
	private SortDialogFragment mSortDialogFragment;
	private String mUrl=Constants.GIRL_URL+"all?page=";
	private boolean isLoadingMore;
	private int mCurrentSelected;
	private boolean isFirstTime=true;
	
	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu,
			com.actionbarsherlock.view.MenuInflater inflater) {
		menu.clear();
		MenuItem item = menu.add("Sort");	
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getTitle().equals("Sort")){
			mSortDialogFragment.show(getActivity().getSupportFragmentManager(), "mSortDialogFragment");
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View convertView =inflater.inflate(R.layout.list_feed_layout, container, false);
		mProgressBar=(ProgressBar)convertView.findViewById(R.id.list_proBar);
		mLoadMoreData=(ProgressBar)convertView.findViewById(R.id.list_loadmoreBar);
		mListView=(ListView)convertView.findViewById(R.id.list_gridView);
//		Log.e("ListFeed","GridView is -> " + mListView);
		mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
					int totalItemCount) {
//				Log.i(TAG, "firstVisibleItem :" + firstVisibleItem +"visibleItemCount :" + visibleItemCount +" totalItemCount " + totalItemCount);
				if(((firstVisibleItem + visibleItemCount)==totalItemCount)&&!isLoadingMore){
					mCurrentSelected=firstVisibleItem;
					loadMoreData();
				}
			}
		});
		mListView.setOnItemClickListener( new OnItemClickListener() {

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
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mSortDialogFragment= new SortDialogFragment(this);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		 try{
			 SherlockFragmentActivity activity = (SherlockFragmentActivity)getActivity();			
			 activity.getSupportActionBar().setTitle("GIRLS OF MAXIM");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		 if(mData!=null&&mAdapter!=null&&mListView!=null){
				mListView.setAdapter(mAdapter);
				mListView.setSelection(mCurrentSelected);
				mAdapter.notifyDataSetChanged();
				mProgressBar.setVisibility(View.GONE);
				return;
		 }
		 mCurrentPage=0;
		loadData(mUrl+mCurrentPage);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
		mICelebritySelectProvider=(ICelebritySelectProvider)activity;
		}catch(Exception e){
			Log.e("CelebritiesListFragment", activity.getLocalClassName() +" must implement  ICelebritySelectProvider:");
		}
		 
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	private void loadData(String url){		
		ReadFeedTask task = new ReadFeedTask(this, mCurrentPage);
		task.execute(url);
	}
	private void loadMoreData(){		
		if(isLoadingMore||isFirstTime||isEnd){
			return;
		}
//		Log.i("Load Data from Net Work ", "Load Data from Net Work ");
		isLoadingMore=true;
		mProgressBar.setVisibility(View.VISIBLE);
		ILoadFeedProvider<CelebrityEntry> provider = new ILoadFeedProvider<CelebrityEntry>() {
			
			@Override
			public void onLoadFeedFailed() {
				mProgressBar.setVisibility(View.GONE);
				isLoadingMore=false;
				isEnd=true;
			}			
			@Override
			public void onLoadFeedComplete(List<CelebrityEntry> result) {
				Log.i("loadData", "onLoadXmlComplete :" +result.size());
				isEnd=false;
				try{
				mProgressBar.setVisibility(View.GONE);
				isLoadingMore=false;
				if(mData==null){
					mData= new ArrayList<CelebrityEntry>();
				}
				mData.addAll(result);
				mAdapter= new CelebritiesListAdapter(getActivity(), 0, mData);
				if(mListView!=null){
				mListView.setAdapter(mAdapter);
				mListView.setSelection(mCurrentSelected);
				mAdapter.notifyDataSetChanged();
				}else{
					Log.e("ListFeed","GridView is null");
				}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		};
		mCurrentPage++;
		ReadFeedTask task = new ReadFeedTask(provider, mCurrentPage);
		task.execute(mUrl+mCurrentPage);
	}
	private void doSearch(String searchText){
		List<CelebrityEntry> result = new ArrayList<CelebrityEntry>();
		if(mData==null){
			return;
		}
		for(CelebrityEntry entry:mData){
			if(entry.getTitle().toUpperCase().startsWith(searchText.toUpperCase())){
				result.add(entry);
			}
		}
//		Log.e("CelebritiesListFragment", "do search result :" +result.size());
		mAdapter= new CelebritiesListAdapter(getActivity(), 0, result);
		mAdapter.notifyDataSetChanged();
		mListView.setAdapter(mAdapter);
	}
	@Override
	public void onLoadFeedComplete(List<CelebrityEntry> result) {
		try{
			isFirstTime=false;
		mProgressBar.setVisibility(View.GONE);
		if(result!=null&&getActivity()!=null){
			mData= new ArrayList<CelebrityEntry>();
			mData.addAll(result);
			mAdapter= new CelebritiesListAdapter(getActivity(), 0, result);
			if(mListView!=null){
				mListView.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
				}else{
					Log.e("ListFeed","GridView is null");
				}
			
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onLoadFeedFailed() {
		mProgressBar.setVisibility(View.GONE);
		
//			loadData();
			
	}


	@Override
	public void onItemSelected(String title) {
		if(mSortDialogFragment!=null){
			mSortDialogFragment.dismiss();
		}
		mCurrentPage=0;
		mUrl=Constants.GIRL_URL+title+"?page=";
		loadData(mUrl+mCurrentPage);
	}
	

	
}
