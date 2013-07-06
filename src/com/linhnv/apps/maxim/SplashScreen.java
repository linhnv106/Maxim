package com.linhnv.apps.maxim;

import com.linhnv.apps.maxim.tasks.ReadTodayFeedTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends FragmentActivity{
	ImageView image1;
	ImageView image2;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);
		image1=(ImageView)findViewById(R.id.image1);
		image2=(ImageView)findViewById(R.id.image2);
		Animation anim1 = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
		Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
		anim2.setDuration(2000);
		anim1.setDuration(1500);
		anim1.setFillAfter(true);
		anim2.setFillAfter(true);
		image1.startAnimation(anim1);
		image2.startAnimation(anim2);
//		ReadTodayFeedTask mTask = new ReadTodayFeedTask(null, 0);
//		mTask.execute("");
		anim2.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent(SplashScreen.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
	
	
	
}
