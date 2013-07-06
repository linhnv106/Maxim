package com.linhnv.apps.maxim;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.Window;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.linhnv.apps.maxim.provider.ISlidingMenuProvider;
import com.linhnv.apps.maxim.slidingmenu.MenuListFragment;

public class BaseActivity extends SlidingFragmentActivity implements ISlidingMenuProvider{
	protected ListFragment mFragment;
	public static Context mAppplicationContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mAppplicationContext=getApplicationContext();
		setBehindContentView(R.layout.menu_frame);
		if(savedInstanceState==null){
			FragmentTransaction t = getSupportFragmentManager().beginTransaction();
			mFragment = new MenuListFragment();
			t.replace(R.id.menu_frame, mFragment);
			t.commit();
		}else {
			mFragment=(ListFragment)getSupportFragmentManager().findFragmentById(R.id.menu_frame);
		}
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);		
		getSupportActionBar().setIcon(R.drawable.main_menu);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSlidingMenuSelected(int id) {
		
	}
	
}
