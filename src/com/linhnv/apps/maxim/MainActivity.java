package com.linhnv.apps.maxim;

import java.util.List;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.view.MenuItem;
import com.flurry.android.FlurryAdSize;
import com.flurry.android.FlurryAds;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.linhnv.apps.maxim.fragment.AboutAppFragment;
import com.linhnv.apps.maxim.fragment.CelebritiesListFragment;
import com.linhnv.apps.maxim.fragment.CelebrityFragment;
import com.linhnv.apps.maxim.fragment.Hot100Fragment;
import com.linhnv.apps.maxim.fragment.SubscriptionFragment;
import com.linhnv.apps.maxim.fragment.TodayGirlFragment;
import com.linhnv.apps.maxim.fragment.ViewCelebrityFragment;
import com.linhnv.apps.maxim.model.CelebrityEntry;
import com.linhnv.apps.maxim.model.ImageEntry;
import com.linhnv.apps.maxim.provider.ICelebritySelectProvider;
import com.linhnv.apps.maxim.provider.IEntryProvider;
import com.linhnv.apps.maxim.provider.ILoadFeedProvider;
import com.linhnv.apps.maxim.utils.GPSTracker;

public class MainActivity extends BaseActivity  implements ILoadFeedProvider<ImageEntry>,ICelebritySelectProvider,IEntryProvider{
	private final static String TAG="mICelebritySelectProvider";
	private AdView mAdView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction  = fm.beginTransaction();

		if (fm.findFragmentById(R.id.main_container) == null) {
			TodayGirlFragment todayFragment = new TodayGirlFragment();
			fragmentTransaction.add(R.id.main_container, todayFragment).commit();
		}
		initAds();
	}
	private void initAds(){
		
		mAdView = (AdView)this.findViewById(R.id.adView);
		
		final AdRequest request = new AdRequest();
		
		request.addTestDevice(AdRequest.TEST_EMULATOR);
				
		mAdView.loadAd(request);
		AsyncTask< Void, Void, Location> task = new AsyncTask<Void, Void, Location>(){

			@Override
			protected Location doInBackground(Void... params) {
				GPSTracker mGPS = GPSTracker.getInstance(MainActivity.this);
				return mGPS.getLocation();
			}

			@Override
			protected void onPostExecute(Location result) {
				request.setLocation(result);
			}
			
		};
		task.execute();
	}
	@Override
	public void onLoadFeedComplete(List<ImageEntry> result) {
		Log.e("MainActivity","=>"+ result.size());
	}

	@Override
	public void onLoadFeedFailed() {
		
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
	    if (itemId==android.R.id.home) {
	   
	    	try{
	    	getSlidingMenu().showMenu();

	        // Toast.makeText(this, "home pressed", Toast.LENGTH_LONG).show();
	    	}catch(Exception e){
	    		e.printStackTrace();
	    		
	    	}
	    	
	     return true;

	    }	
	    return super.onOptionsItemSelected(item);
	}
	    
	@Override
	public void onCelebritySelected(CelebrityEntry entry) {
		Log.i(TAG, entry.getTitle());
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction  = fm.beginTransaction();
		CelebrityFragment celebrityFragment = new CelebrityFragment(this,entry);
		fragmentTransaction.replace(R.id.main_container, celebrityFragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	@Override
	public void onSlidingMenuSelected(int id) {
		Log.d(TAG, "onMenuSelected :" +id);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction  = fm.beginTransaction();
		
		switch(id){
		case 0:			
			TodayGirlFragment celebrityFragment = new TodayGirlFragment();
			fragmentTransaction.replace(R.id.main_container, celebrityFragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();			
			
			break;
		case 1:
			CelebritiesListFragment celebrityListFragment = new CelebritiesListFragment();
			fragmentTransaction.replace(R.id.main_container, celebrityListFragment);
//			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
			break;
		case 2:
			Hot100Fragment subscriptionFragment = new Hot100Fragment();
			fragmentTransaction.replace(R.id.main_container, subscriptionFragment);
//			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();		
			break;
		case 3:
			AboutAppFragment aboutFragment = new AboutAppFragment();
			fragmentTransaction.replace(R.id.main_container, aboutFragment);
//			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();		
			break;
		default:
			break;
		}
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		getSlidingMenu().showContent();
	}

	@Override
	public void onEntrySelectedProvider(List<ImageEntry> list,int position) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction  = fm.beginTransaction();
		ViewCelebrityFragment celebrityFragment = new ViewCelebrityFragment(list.get(position));
		fragmentTransaction.replace(R.id.main_container, celebrityFragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		getSlidingMenu().showContent();
	}
	
}
