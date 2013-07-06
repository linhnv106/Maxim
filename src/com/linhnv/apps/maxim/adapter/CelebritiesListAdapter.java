package com.linhnv.apps.maxim.adapter;

import java.util.List;

import com.linhnv.apps.maxim.R;
import com.linhnv.apps.maxim.model.CelebrityEntry;
import com.linhnv.apps.maxim.utils.ImageCache;
import com.linhnv.apps.maxim.utils.ImageFetcher;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CelebritiesListAdapter extends ArrayAdapter<CelebrityEntry> {
	private LayoutInflater layoutInflater ;
	private FragmentActivity mContext;
	private ImageFetcher mImageFetcher;
	private static final String IMAGE_CACHE_DIR = "images";
	public CelebritiesListAdapter(FragmentActivity context, int textViewResourceId,
			List<CelebrityEntry> objects) {
		super(context, textViewResourceId, objects);
		layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		final Holder holder;
		final CelebrityEntry entry=getItem(position);
		if(convertView==null){
			holder = new Holder();
			convertView=layoutInflater.inflate(R.layout.list_feed_item, null);
			holder.name=(TextView)convertView.findViewById(R.id.item_name);
			holder.image=(ImageView)convertView.findViewById(R.id.item_cover);
			convertView.setTag(holder);
		}else{
			holder =(Holder)convertView.getTag();
		}
		holder.name.setText(entry.getTitle());
		mImageFetcher.loadImage(entry.getThumb(),holder.image);
		return convertView;
	}
	class Holder{
		TextView name;
		ImageView image;
	}

}
