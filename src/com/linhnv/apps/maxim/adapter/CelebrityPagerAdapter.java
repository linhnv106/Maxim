package com.linhnv.apps.maxim.adapter;

import java.util.ArrayList;
import java.util.List;

import com.linhnv.apps.maxim.R;
import com.linhnv.apps.maxim.model.ImageEntry;
import com.linhnv.apps.maxim.utils.ImageCache;
import com.linhnv.apps.maxim.utils.ImageFetcher;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class CelebrityPagerAdapter extends FragmentPagerAdapter{
	private ImageFetcher mImageFetcher;
	private static final String IMAGE_CACHE_DIR = "images";
	private List<ImageEntry> mData;
	private FragmentActivity mContext;
	public CelebrityPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	public CelebrityPagerAdapter(FragmentManager fm,FragmentActivity activity,List<ImageEntry> data) {
		super(fm);
		mContext=activity;
		mData= new ArrayList<ImageEntry>();
		mData.addAll(data);
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		// final int height = displayMetrics.heightPixels;
		final int width = displayMetrics.widthPixels;

		// For this sample we'll use half of the longest width to resize our
		// images. As the
		// image scaling ensures the image is larger than this, we should be
		// left with a
		// resolution that is appropriate for both portrait and landscape. For
		// best image quality
		// we shouldn't divide by 2, but this will use more memory and require a
		// larger memory
		// cache.
//		final int longest = width / 2;

		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(mContext,
				IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of
													// app memory

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(mContext, width);
		mImageFetcher.addImageCache(mContext.getSupportFragmentManager(), cacheParams);
		mImageFetcher.setImageFadeIn(false);
		mImageFetcher.setRotate(true);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
	}
	public ImageFetcher getImageFetcher(){
		return mImageFetcher;
	}
	@Override
	public Fragment getItem(int arg0) {
		return new PagerFragment(mData.get(arg0));
	}

	@Override
	public int getCount() {
		return mData.size();
	}
	
	class PagerFragment extends Fragment{
		private ProgressBar progressBar;
		private ImageView image;
		private ImageEntry entry;
		public PagerFragment() {
			super();
		}
		public PagerFragment(ImageEntry entry) {
			super();
			this.entry=entry;
		}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View convertView =inflater.inflate(R.layout.pager_fragment,container,false);
			progressBar=(ProgressBar)convertView.findViewById(R.id.pager_proBar);
			image=(ImageView)convertView.findViewById(R.id.pager_image);
			mImageFetcher.loadImage(entry.getImage(), image);
			return convertView;
		}
		
	}
}
