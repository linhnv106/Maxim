package com.linhnv.apps.maxim.social;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ProgressBar;

public class TwitterApp {
	private Twitter mTwitter;
	private TwitterSession mSession;
	private AccessToken mAccessToken;
	private CommonsHttpOAuthConsumer mHttpOauthConsumer;
	private OAuthProvider mHttpOauthprovider;
	private String mConsumerKey;
	private String mSecretKey;
	private ProgressDialog mProgressDlg;
	private ProgressBar mSmallLoading;
	private TwDialogListener mListener;
	private Activity context;

	public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-twitter";
	public static final String OAUTH_CALLBACK_HOST = "callback";
	public static final String CALLBACK_URL = "twitterapp://connect";

	// public static final String CALLBACK_URL =
	// "http://abhinavasblog.blogspot.com/";

	// static String base_link_url = "https://www.twitter.com/";
	private static final String TWITTER_ACCESS_TOKEN_URL = "http://api.twitter.com/oauth/access_token";
	private static final String TWITTER_AUTHORZE_URL = "https://api.twitter.com/oauth/authorize";
	private static final String TWITTER_REQUEST_URL = "https://api.twitter.com/oauth/request_token";

	// public static final String MESSAGE = "Hello Everyone...."
	// + "<a href= " + base_link_url + "</a>";

	public TwitterApp(Activity context, String consumerKey, String secretKey) {
		this.context = context;

		mTwitter = new TwitterFactory().getInstance();
		mSession = new TwitterSession(context.getApplicationContext());
		mProgressDlg = new ProgressDialog(context);

		mProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mConsumerKey = consumerKey;
		mSecretKey = secretKey;

		mHttpOauthConsumer = new CommonsHttpOAuthConsumer(mConsumerKey, mSecretKey);

		String request_url = TWITTER_REQUEST_URL;
		String access_token_url = TWITTER_ACCESS_TOKEN_URL;
		String authorize_url = TWITTER_AUTHORZE_URL;

		mHttpOauthprovider = new DefaultOAuthProvider(request_url, access_token_url, authorize_url);
		// mAccessToken = mSession.getAccessToken();
		mAccessToken = mSession.restoreSession();

		configureToken();
	}

	public void setListener(TwDialogListener listener) {
		mListener = listener;
	}

	private void configureToken() {
		if (mAccessToken != null) {
			mTwitter.setOAuthConsumer(mConsumerKey, mSecretKey);
			mTwitter.setOAuthAccessToken(mAccessToken);
		}
	}

	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}

	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();

			mAccessToken = null;
		}
	}
	
	public void logout() {
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeSessionCookie();
		resetAccessToken();
		if(mTwitter != null) {
			mTwitter.setOAuthAccessToken(null);
			mTwitter.shutdown();
		}
	}

	public String getUsername() {
		return mSession.getUsername();
	}

	public String getScreenName() {
		return mSession.getScreenName();
	}

	public AccessToken getAccessToken() {
		// mAccessToken = mSession.getAccessToken();
		// return mSession.getAccessToken();
		return mAccessToken;
	}

	public Status updateStatus(String username, String status) {
		Status result = null;
		try {
			if (username.equals(""))
				result = mTwitter.updateStatus(status);
			else
				result = mTwitter.updateStatus("@" + username + " " + status);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public DirectMessage sendAppRequest(String userId, String message) {
		DirectMessage msg = null;
		try {
			msg = mTwitter.sendDirectMessage(Long.parseLong(userId), message);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}

	

	public void uploadPic(File file, String message) throws Exception {
		try {
			StatusUpdate status = new StatusUpdate(message);
			status.setMedia(file);
			mTwitter.updateStatus(status);
		} catch (TwitterException e) {
			// Log.d("TAG", "Pic Upload error" + e.getExceptionCode());
			throw e;
		}
	}

	public void authorize(ProgressBar loading) {
		mSmallLoading = loading;
		if (mSmallLoading != null)
			mSmallLoading.setVisibility(ProgressBar.VISIBLE);
		// mProgressDlg.setMessage("Initializing ...");
		// mProgressDlg.show();

		new Thread() {
			@Override
			public void run() {
				String authUrl = "";
				int what = 1;

				try {
					authUrl = mHttpOauthprovider.retrieveRequestToken(mHttpOauthConsumer,
							CALLBACK_URL);
					what = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0, authUrl));
			}
		}.start();
	}

	public void authorize() {
		// mSmallLoading = loading;
		// if(mSmallLoading != null)
		// mSmallLoading.setVisibility(ProgressBar.VISIBLE);

		if (mProgressDlg != null) {
			mProgressDlg.setMessage("initializing...");
			mProgressDlg.show();
		}

		new Thread() {
			@Override
			public void run() {
				String authUrl = "";
				int what = 1;

				try {
					authUrl = mHttpOauthprovider.retrieveRequestToken(mHttpOauthConsumer,
							CALLBACK_URL);
					what = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0, authUrl));
			}
		}.start();
	}

	public void processToken(String callbackUrl) {
		// mProgressDlg.setMessage("Finalizing ...");
		// mProgressDlg.show();
		if (mSmallLoading != null)
			mSmallLoading.setVisibility(ProgressBar.VISIBLE);

		final String verifier = getVerifier(callbackUrl);

		new Thread() {
			@Override
			public void run() {
				int what = 1;

				try {
					mHttpOauthprovider.retrieveAccessToken(mHttpOauthConsumer, verifier);

					mAccessToken = new AccessToken(mHttpOauthConsumer.getToken(),
							mHttpOauthConsumer.getTokenSecret());

					configureToken();

					User user = mTwitter.verifyCredentials();

					// getListFriends();

					// mSession.storeAccessToken(mAccessToken, user.getName());
					mSession.storeSession(mAccessToken, user.getScreenName(), user.getName(),
							user.getId());

					what = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}

	private String getVerifier(String callbackUrl) {
		String verifier = "";

		try {
			callbackUrl = callbackUrl.replace("twitterapp", "http");

			URL url = new URL(callbackUrl);
			String query = url.getQuery();

			String array[] = query.split("&");

			for (String parameter : array) {
				String v[] = parameter.split("=");

				if (URLDecoder.decode(v[0]).equals(oauth.signpost.OAuth.OAUTH_VERIFIER)) {
					verifier = URLDecoder.decode(v[1]);
					break;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return verifier;
	}

	private void showLoginDialog(String url) {
		final TwDialogListener listener = new TwDialogListener() {

			public void onComplete(String value) {
				processToken(value);
			}

			public void onError(String value) {
				mListener.onError("Failed opening authorization page");
			}
		};

		new TwitterDialog(context, url, listener).show();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mProgressDlg != null)
				mProgressDlg.dismiss();
			if (mSmallLoading != null)
				mSmallLoading.setVisibility(ProgressBar.VISIBLE);
			if (msg.what == 1) {
				if (msg.arg1 == 1)
					mListener.onError("Error getting request token");
				else
					mListener.onError("Error getting access token");
			} else {
				if (msg.arg1 == 1)
					showLoginDialog((String) msg.obj);
				else
					mListener.onComplete("");
			}
		}
	};

	public interface TwDialogListener {
		public void onComplete(String value);

		public void onError(String value);
	}

	public User getCurentUser() {
		try {
			return mTwitter.verifyCredentials();
		} catch (Exception e) {
			return null;
		}
	}
}
