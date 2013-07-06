package com.linhnv.apps.maxim.adapter;

import java.util.List;

import com.linhnv.apps.maxim.R;
import com.linhnv.apps.maxim.model.ImageEntry;
import com.linhnv.apps.maxim.utils.ImageCache;
import com.linhnv.apps.maxim.utils.ImageFetcher;
import com.linhnv.apps.maxim.view.ScaleImageView;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class CelebrityAdapter extends ArrayAdapter<ImageEntry> {
	private FragmentActivity mContext;
	private ImageFetcher mImageFetcher;
	private static final String IMAGE_CACHE_DIR = "images";

	public CelebrityAdapter(FragmentActivity context, int textViewResourceId,
			List<ImageEntry> objects) {
		super(context, textViewResourceId, objects);
		mContext=context;
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
		final int longest = width / 2;

		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(mContext,
				IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of
													// app memory

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(mContext, longest);
		mImageFetcher.addImageCache(mContext.getSupportFragmentManager(), cacheParams);
		mImageFetcher.setImageFadeIn(false);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
	}
		

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ImageEntry entry =getItem(position);
		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater layoutInflator = LayoutInflater.from(getContext());
			convertView = layoutInflator.inflate(R.layout.row_staggered_demo,
					null);
			holder = new ViewHolder();
			holder.imageView = (ScaleImageView) convertView .findViewById(R.id.imageView1);
			convertView.setTag(holder);
		}else{
		holder = (ViewHolder) convertView.getTag();
		}
		mImageFetcher.loadImage(entry.getThumb(), holder.imageView);
		return convertView;
	}

	static class ViewHolder {
		ScaleImageView imageView;
	}
}
