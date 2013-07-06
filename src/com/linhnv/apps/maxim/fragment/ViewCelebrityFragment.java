package com.linhnv.apps.maxim.fragment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import twitter4j.Status;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.InterstitialAd;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.linhnv.apps.maxim.MainActivity;
import com.linhnv.apps.maxim.R;
import com.linhnv.apps.maxim.adapter.CelebrityPagerAdapter;
import com.linhnv.apps.maxim.constants.Constants;
import com.linhnv.apps.maxim.database.DatabaseHelper;
import com.linhnv.apps.maxim.fragment.ShareDialogFragment.IShareDialogListener;
import com.linhnv.apps.maxim.model.ImageEntry;
import com.linhnv.apps.maxim.provider.ILoadFeedProvider;
import com.linhnv.apps.maxim.social.TWPreferenceStores;
import com.linhnv.apps.maxim.social.TwitterApp;
import com.linhnv.apps.maxim.social.TwitterApp.TwDialogListener;
import com.linhnv.apps.maxim.tasks.ReadEntryDetailTask;

public class ViewCelebrityFragment extends SherlockFragment implements IShareDialogListener,ILoadFeedProvider<ImageEntry>, AdListener{
	private final static String TAG="ViewCelebrityFragment";
	private ViewPager mViewPager;
	private CelebrityPagerAdapter mAdapter;
	private FragmentActivity mContext;
	private ImageEntry mEntry;
	private List<ImageEntry> mData;
	private int mPosition = 0;
	private int mWidth;
	private int mHeight;
	private boolean isClosedAds=false;
	private ShareDialogFragment mShareDialogFragment;
	private ProgressBar mProgressBar;
	private InterstitialAd interstitialAd;
	public ViewCelebrityFragment() {
		super();
	}

	public ViewCelebrityFragment(ImageEntry entry) {
		super();
		mEntry = entry;
		mContext = getActivity();
		 
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		try {
			SherlockFragmentActivity activity = (SherlockFragmentActivity) getActivity();
			if (mEntry != null ) {
				activity.getSupportActionBar().setTitle(mEntry.getName());
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		// final int height = displayMetrics.heightPixels;
		 mWidth = displayMetrics.widthPixels;
		 mHeight=displayMetrics.heightPixels;
		 mShareDialogFragment= new ShareDialogFragment();
		 mShareDialogFragment.setListener(this);
		 interstitialAd = new InterstitialAd(getActivity(), "a151d6979944720");

		    // Set the AdListener.
		    interstitialAd.setAdListener(this);
		    AdRequest adRequest = new AdRequest();
		      adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
		      interstitialAd.loadAd(adRequest);
		      if (interstitialAd.isReady()) {
		          interstitialAd.show();
		        } else {
		          Log.d("ViewCelebrityFragment", "Interstitial ad was not ready to be shown.");
		        }
		 rateApp();
		 loadData(mEntry);
	}
	private void loadData(ImageEntry entry) {
		
		mProgressBar.setVisibility(View.VISIBLE);
		ReadEntryDetailTask task = new ReadEntryDetailTask(this);
		task.execute(Constants.ROOT+mEntry.getFullLink());

	}
	//rate app
	private void rateApp(){
		final SharedPreferences mPreferences = getActivity().getSharedPreferences("Celebrity", 0);
		long last =mPreferences.getLong("last", 0);
		final long now =Calendar.getInstance().getTimeInMillis();
		//1000x60x60x24x7=604800000
		if((now-last)<=604800000){
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Add the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User clicked OK button
		        	   final Uri uri = Uri.parse("market://details?id=" + getActivity().getApplicationContext().getPackageName());
		       		final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);
		       		if (getActivity().getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0)
		       		{
		       		    startActivity(rateAppIntent);
		       		}
		       		SharedPreferences.Editor edit = mPreferences.edit();
		       		edit.putLong("last", now);
		       		edit.commit();
		       		dialog.dismiss();
		           }
		           
		       });
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User cancelled the dialog
		        	   dialog.dismiss();
		           }
		       });
		// Set other dialog properties

		// Create the AlertDialog
		builder.setMessage("Help us make the app better by rating this app on Google Play");
		AlertDialog dialog = builder.create();
		dialog.show();
		
		
	}
	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu,
			com.actionbarsherlock.view.MenuInflater inflater) {
		inflater.inflate(R.menu.option_menu, menu);
	}
	
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		menu.clear();
////		MenuItem item1 = menu.add("Subscription");
////		MenuItem item2 = menu.add("Download");
////		MenuItem item3 = menu.add("Set as Wallpaper");
////		MenuItem item4 = menu.add("Share");
////		// item.setIcon(android.R.drawable.ic_menu_search);
////		item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
////		item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
////		item3.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
////		item4.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
//		inflater.inflate(R.menu.option_menu, menu);
//		
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		
		if (item.getTitle().toString().equalsIgnoreCase("Subscription")) {
			addSubcription();
		} else if (item.getTitle().toString().equalsIgnoreCase("Download")) {
			setUpDownload(mData.get(mViewPager.getCurrentItem()));
		} else if (item.getTitle().toString().equalsIgnoreCase("Set as Wallpaper")) {
			setUpWallPage(mData.get(mViewPager.getCurrentItem()).getImage());
		} else if (item.getTitle().toString().equalsIgnoreCase("Share")) {
//			shareWithFB(mData.get(mViewPager.getCurrentItem()));
			mShareDialogFragment.show(getActivity().getSupportFragmentManager(), "mShareDialogFragment");
		}
		
		return super.onOptionsItemSelected(item);
	}
	public static void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}

	private void setUpDownload(final ImageEntry entry) {
		//Log.i("CelebrityApp", "start download : 1 setUpDownload: ");
		AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				////Log.i("CelebrityApp", "start download : 1 doInBackground: ");
				return downLoad(entry.getImage(), entry.getName());
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// super.onPostExecute(result);
				closeProgressDialog();
				try {
					if (result) {
						Toast.makeText(
								getActivity(),
								" image saved at"
										+ Environment.getExternalStoragePublicDirectory(
												Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
										+ "/CelebrityApp", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getActivity(), "image saved failed !", Toast.LENGTH_LONG)
								.show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
		if (checkConnection()) {
			//Log.i("CelebrityApp", "start download : 1 setUpDownload ok: ");
			showProgressDialog("dowloading...");
			task.execute();
		}
	}

	private boolean checkConnection() {
		final ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(
				Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
			Toast.makeText(getActivity(), R.string.no_network_connection_toast, Toast.LENGTH_LONG)
					.show();
			Log.e("CelebrityApp", "checkConnection - no connection found");
			return false;
		} else {
			return true;
		}
	}

	private boolean downLoad(String urlString, String fileName) {
		if (fileName == null) {
			fileName = "";
		}
		//Log.i("CelebrityApp", "start download : 1 : " + fileName);

		final int IO_BUFFER_SIZE = 8 * 1024;
		disableConnectionReuseIfNecessary();
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		File mediaStorageDir = new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
				"CelebrityApp");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("CelebrityApp", "failed to create directory");
				return false;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + fileName
				+ "_" + timeStamp + ".jpg");
		//Log.i("CelebrityApp", "start download : 1");
		try {
			OutputStream outputStream = new FileOutputStream(mediaFile);
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
			out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			return true;
		} catch (final IOException e) {
			Log.e("VewCelebrityFragment", "Error in downloadBitmap - " + e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
			}
		}
		return false;
	}
	private void setUpWallPage(final String name){
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				 setWallPage(name);
				 return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				closeProgressDialog();
			}
			
		};
		if(checkConnection()){
			showProgressDialog("setting wallpaper ...");
			task.execute();
		}
	}
	
	private void setWallPage(String urlString){	
		
			WallpaperManager myWallpaperManager 
	        = WallpaperManager.getInstance(getActivity().getApplicationContext());
			HttpURLConnection urlConnection = null;
			InputStream in = null;
	        try {
				final URL url = new URL(urlString);
				urlConnection = (HttpURLConnection) url.openConnection();
				in =urlConnection.getInputStream();

				final BitmapFactory.Options options = new BitmapFactory.Options();
////				options.inJustDecodeBounds = false;
////				final BitmapFactory.Options options = new BitmapFactory.Options();
////				options.inJustDecodeBounds = true;
////				BitmapFactory.decodeStream(in,null, options);
//
//				// Calculate inSampleSize
				
////				options.inJustDecodeBounds = false;
//				options.inSampleSize = 2;
				float minH =  myWallpaperManager.getDesiredMinimumHeight();
	            float minW =  myWallpaperManager.getDesiredMinimumWidth();
				Bitmap bm1=BitmapFactory.decodeStream(in,null,options);
//				minH=bm1.getHeight()*minW/bm1.getWidth();
				
				Bitmap bm=getResizedBitmap(bm1,(int)minW, (int)minH);
				bm1.recycle();
//				Log.i("VewCelebrityFragment", "Error in bm - " + bm);
	        	if(bm!=null){
	        		myWallpaperManager.setWallpaperOffsetSteps((float)1, (float)1);
	             myWallpaperManager.setBitmap(bm);
	        	}
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }catch(Exception e){
	        	e.printStackTrace();
	        }finally{
	        	if (urlConnection != null) {
					urlConnection.disconnect();
				}
				try {					
					if (in != null) {
						in.close();
					}
				} catch (final IOException e) {
				}
	        }
		
	}
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

	    int width = bm.getWidth();

	    int height = bm.getHeight();

	    float scaleWidth = ((float) newWidth) / width;

	    float scaleHeight = ((float) newHeight) / height;

	// create a matrix for the manipulation

	    Matrix matrix = new Matrix();

	    // resize the bit map

	    matrix.postScale(scaleWidth, scaleHeight);

	// recreate the new Bitmap

	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

	    return resizedBitmap;

	}
	private void addSubcription() {
		AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... arg0) {
				DatabaseHelper helper = DatabaseHelper.getInstance(getActivity());
				return helper.createSubcription(mData.get(mViewPager.getCurrentItem()));
			}

			@Override
			protected void onPostExecute(Boolean result) {
				try {
					if (result) {
						Toast.makeText(getActivity(), " successful subscription!",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getActivity(), "failed subscription!", Toast.LENGTH_LONG)
								.show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
		task.execute();
	}
	private FrameLayout mBanner;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View convertView = inflater.inflate(R.layout.view_celebrity_layout, container, false);
		mViewPager = (ViewPager) convertView.findViewById(R.id.pager);
		mBanner = (FrameLayout) convertView.findViewById(R.id.banner);
		mProgressBar=(ProgressBar)convertView.findViewById(R.id.view_proBar);
		mViewPager.setVisibility(View.GONE);
		return convertView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public void onDetach() {
		super.onDetach();
		try {
			((MainActivity) getActivity()).getSlidingMenu().setTouchModeAbove(
					SlidingMenu.TOUCHMODE_FULLSCREEN);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private enum PendingAction {
 		NONE, GET_USER_INFO, POST_STATUS_UPDATE
 	}

 	private Session.StatusCallback callback = new Session.StatusCallback() {
 		@Override
 		public void call(Session session, SessionState state, Exception exception) {
 			onSessionStateChange(session, state, exception);
 		}
 	};


 	@Override
 	public void onStart() {
 		if(Session.getActiveSession() != null)
        	Session.getActiveSession().addCallback(callback);
 		super.onStart();
// 		FlurryAds.setAdListener(this);
//		 FlurryAgent.onStartSession(getActivity(),"NT7PVMHK5HR3P2M4R94X");
//        // fetch and prepare ad for this ad space. won’t render one yet
//		 FlurryAds.fetchAd(getActivity(),"Celebrity", mBanner, FlurryAdSize.FULLSCREEN);
//		 FlurryAds.enableTestAds(false);
//		 FlurryAgent.setLogEnabled(true); 
 		
 	}
 	private PendingAction pendingAction = PendingAction.NONE;
	private ProgressDialog loadingProgress;
	private ImageEntry shareEntry;
	public static final String[] FACEBOOK_PUBLISH_PERMISSIONS = new String[] { "publish_actions" };

 	private void shareWithFB(ImageEntry entry){
		if (!checkConnection()) {
			return;
		}
		shareEntry=entry;
		pendingAction = PendingAction.POST_STATUS_UPDATE;
		Session session = Session.getActiveSession();
		if(session==null){
			session= new Session(getActivity());
		}
		Session.setActiveSession(session);
	    if (!session.isOpened()&& !session.isClosed()) {
	    	session.openForRead(new Session.OpenRequest(this)
            .setPermissions(Arrays.asList("basic_info"))
            .setCallback(callback));
	    }else{
	    	Session.openActiveSession(getActivity(), true, callback);
	    }
//		session.requestNewReadPermissions(new Session.NewPermissionsRequest(getActivity(),Arrays.asList(FACEBOOK_PUBLISH_PERMISSIONS)));
	}
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
//		Log.i(TAG, "share failed 0");
		if (pendingAction != PendingAction.NONE
				&& (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) {
//			Log.i(TAG, exception.getMessage());
			new AlertDialog.Builder(getActivity()).setTitle("Cancel")
					.setMessage("permission does not granted!")
					.setPositiveButton("OK", null).show();
			pendingAction = PendingAction.NONE;
		} else if (state == SessionState.OPENED_TOKEN_UPDATED) {
			handlePendingAction();
		}else if(session.isOpened()){
			handlePendingAction();
		}else{
//			Log.i(TAG, "share failed 1");
//			Session.openActiveSession(getActivity(), true, callback);
//			session.openForPublish(new Session.OpenRequest(this).setCallback(callback).setRequestCode(200));
		}

	}

	@SuppressWarnings("incomplete-switch")
	private void handlePendingAction() {
		PendingAction previouslyPendingAction = pendingAction;
		pendingAction = PendingAction.NONE;
//		MyLogger.getInstance().logE(TAG, "face book share : come here 5 : " +previouslyPendingAction);
		switch (previouslyPendingAction) {
		case POST_STATUS_UPDATE:
			performPublish(PendingAction.POST_STATUS_UPDATE);
			break;
		default:
			closeProgressDialog();
			break;
		}
	}
	private void postToFB(ImageEntry entry){
		showProgressDialog("sharing...");
		Bundle postParams = new Bundle();
        postParams.putString("name", "Maxim\'s Girls Wallpaper App");
        postParams.putString("caption"," "+entry.getName());
        postParams.putString("message","A great app to see hottest girls over the world!");
       
        postParams.putString("description"," follow  what " + entry.getName() + " photo upload!");
//        postParams.putString("link",entry.getThumb());
        postParams.putString("picture", entry.getImage());

        Request.Callback callback= new Request.Callback() {
            public void onCompleted(Response response) {
            	closeProgressDialog();
               if(response==null){
//            	   MyLogger.getInstance().logE(TAG, "post to fb response is null :? wtf :" +response);
            	   return;
               }
//                try {
//                	if(response!=null){
//                	GraphObject obj = response.getGraphObject();
//                	if(obj!=null){
//                		JSONObject graphResponse  =obj.getInnerJSONObject();
//                	 String postId = null;
//                     postId = graphResponse.getString("id");
//                	}
//                	}
//                } catch (JSONException e) {
//                    Log.i(TAG,
//                        "JSON error "+ e.getMessage());
//                }
                FacebookRequestError error = response.getError();
                if (error != null) {
                    Toast.makeText(getActivity()
                         .getApplicationContext(),
                         error.getErrorMessage(),
                         Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity()
                             .getApplicationContext(), 
                             "share to facebook successfully!",
                             Toast.LENGTH_LONG).show();
                }
                
		       
            }
        };

        Request request = new Request(Session.getActiveSession(), "me/feed", postParams, 
                              HttpMethod.POST, callback);

        RequestAsyncTask task = new RequestAsyncTask(request);
        task.execute();
	}
	
	private boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains("publish_actions");
    }

    private void performPublish(PendingAction action) {
        Session session = Session.getActiveSession();
        if (session != null) {
            pendingAction = action;
            if (hasPublishPermission()) {
                // We can do the action right away.
                postToFB(shareEntry);
            } else {
                // We need to get new permissions, then complete the action when we get called back.
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, Arrays.asList(FACEBOOK_PUBLISH_PERMISSIONS)));
            }
        }
    }
    public void showProgressDialog(String message) {
		closeProgressDialog();
		loadingProgress = ProgressDialog.show(getActivity(), "", message, true, true);
	 }
		
	public void closeProgressDialog() {
		try{
		if (loadingProgress != null) {
			loadingProgress.dismiss();
			loadingProgress = null;
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void onStop() {
		
		super.onStop();
		try{
//		 FlurryAds.removeAd(getActivity(),"Celebrity", mBanner);
//	       //remove ad - relevant for banner ads
//	             FlurryAgent.onEndSession(getActivity());
		}catch(Exception e){
			e.printStackTrace();
		}
		if(Session.getActiveSession() != null)
        	Session.getActiveSession().removeCallback(callback);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {			
		Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Session session = Session.getActiveSession();
        if(session != null)
        	Session.saveSession(session, outState);
		super.onSaveInstanceState(outState);
	}
	//Share to TW
			private TwitterApp mTwitter;
			
			public void shareToTwitter(ImageEntry entry ) {
				shareEntry=entry;
				if (!checkConnection()) {
					return;
				}
				
				mTwitter = new TwitterApp(getActivity(), TWPreferenceStores.CONSUMER_KEY,
						TWPreferenceStores.CONSUMER_SECRET);
				if (mTwitter.hasAccessToken() == true) {
					// String error = "";
					try {
						postToTwitter(entry);
						return;

					} catch (Exception e) {
						// error = e.getMessage();
						e.printStackTrace();
						// MyLogger.getInstance().logE(TAG, error);

					}
					// showMessage(error);

				} else {
					mTwitter.setListener(mTwLoginDialogListener);
					mTwitter.authorize();
				}

			}
			
			private void postToTwitter(final ImageEntry entry) throws Exception{
//				showProgressDialog(getString(R.string.sharing));
				if(entry==null){
					return;
				}
				
				showProgressDialog("sharing...");
				
				final String shareMessage="follow  what " + entry.getName() + " photo upload!" +entry.getThumb() +"\n see more att Celebrity Photo Wallpaper App";
				
				Thread thread = new Thread() {
					public void run() {
						
						final Status result = mTwitter.updateStatus("",shareMessage);
						closeProgressDialog();
						if(result != null && !result.equals("")){
							showToastMessage("Post to Twitter successful!");
						}else{
							showToastMessage("Post to Twitter failed!");
						}
					}
				};
				thread.start();
				
			}

			private TwDialogListener mTwLoginDialogListener = new TwDialogListener() {

				public void onError(String value) {
//					Log.e("TWITTER", value);
					// closeProgressDialog();
					showToastMessage("Twitter error"+value);
				}

				public void onComplete(String value) {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							try {
								postToTwitter(shareEntry);
								// return;
							} catch (Exception e) {
								// error = e.getMessage();
								e.printStackTrace();
							}
						}
					});
				}
			};
			private void showToastMessage(final String msg){
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
//						if(result != null && !result.equals(""))
//							
//						else
//							Toast.makeText(mActivity, getString(R.string.invite_friend_twitter_fail), Toast.LENGTH_SHORT).show();
//						
					}
				});
			}

			@Override
			public void onShareOptionSelected(int id) {
				mShareDialogFragment.dismiss();
				if(id==0){
					shareWithFB(mData.get(mViewPager.getCurrentItem()));
				}else if(id==1){
					shareToTwitter(mData.get(mViewPager.getCurrentItem()));
				}else {
					//share to others
					Intent shareIntent = new Intent(Intent.ACTION_SEND);
					shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out more celebrity's picture with Celebrity Photo app \n " 
					+mData.get(mViewPager.getCurrentItem()).getThumb());
					shareIntent.setType("text/plain");
					getActivity().startActivity(Intent.createChooser(shareIntent, "Share with"));
				}
			}

			

			@Override
			public void onLoadFeedComplete(List<ImageEntry> result) {
				mData= new ArrayList<ImageEntry>();
				mData.addAll(result);
				mAdapter = new CelebrityPagerAdapter(getChildFragmentManager(), getActivity(), mData);
				mViewPager.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
				mProgressBar.setVisibility(View.GONE);
				mViewPager.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadFeedFailed() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDismissScreen(Ad arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onLeaveApplication(Ad arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPresentScreen(Ad arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onReceiveAd(Ad arg0) {
				// TODO Auto-generated method stub
				 if (interstitialAd.isReady()) {
			          interstitialAd.show();
			        }
			}
}
