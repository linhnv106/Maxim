package com.linhnv.apps.maxim.social;

import twitter4j.auth.AccessToken;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;
import android.preference.PreferenceManager;

public class TwitterSession {
	private SharedPreferences sharedPref;
	private Editor editor;

	private static final String TWEET_AUTH_KEY = "tw_authen_key";
	private static final String TWEET_AUTH_SECRET_KEY = "tw_secret_key";
	private static final String TWEET_SCREEN_NAME = "tw_screenname";
	private static final String TWEET_USER_NAME = "tw_username";
	private static final String TWEET_USER_ID = "tw_userid";
	private static final String SHARED = "TwitterSession_Preferences";

	public TwitterSession(Context context) {
		sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);

		editor = sharedPref.edit();
	}

	public void storeAccessToken(AccessToken accessToken, String username) {
		editor.putString(TWEET_AUTH_KEY, accessToken.getToken());
		editor.putString(TWEET_AUTH_SECRET_KEY, accessToken.getTokenSecret());
		editor.putString(TWEET_USER_NAME, username);
		// editor.putLong(TWEET_USER_ID, username);

		editor.commit();
	}

	public void storeSession(AccessToken accessToken, String username, String screenName,
			long userId) {
		// SharedPreferences prefs = PreferenceManager
		// .getDefaultSharedPreferences(context);
		// Editor editor = prefs.edit();
		editor.putString(TWEET_AUTH_KEY, accessToken.getToken());
		editor.putString(TWEET_AUTH_SECRET_KEY, accessToken.getTokenSecret());
		editor.putString(TWEET_USER_NAME, username);
		editor.putString(TWEET_SCREEN_NAME, screenName);
		editor.putLong(TWEET_USER_ID, userId);
		editor.commit();
	}

	public void resetAccessToken() {
		editor.putString(TWEET_AUTH_KEY, null);
		editor.putString(TWEET_AUTH_SECRET_KEY, null);
		editor.putString(TWEET_USER_NAME, "");
		editor.putLong(TWEET_USER_ID, 0);

		editor.commit();
	}

	public String getUsername() {
		return sharedPref.getString(TWEET_USER_NAME, "");
	}

	public String getScreenName() {
		return sharedPref.getString(TWEET_SCREEN_NAME, "");
	}

	public AccessToken restoreSession() {
		// SharedPreferences prefs = PreferenceManager
		// .getDefaultSharedPreferences(context);
		String token = sharedPref.getString(TWEET_AUTH_KEY, null);
		String tokenSecret = sharedPref.getString(TWEET_AUTH_SECRET_KEY, null);
		long userId = sharedPref.getLong(TWEET_USER_ID, 0);

		if (token != null && tokenSecret != null)
			return new AccessToken(token, tokenSecret, userId);
		else
			return null;
	}

	// public AccessToken getAccessToken() {
	// String token = sharedPref.getString(TWEET_AUTH_KEY, null);
	// String tokenSecret = sharedPref.getString(TWEET_AUTH_SECRET_KEY, null);
	//
	// if (token != null && tokenSecret != null)
	// return new AccessToken(token, tokenSecret);
	// else
	// return null;
	// }
}
