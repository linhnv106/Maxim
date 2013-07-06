package com.linhnv.apps.maxim.social;

import com.linhnv.apps.maxim.BaseActivity;

import android.content.Context;
import android.content.SharedPreferences;

public class TWPreferenceStores {
	public static String CONSUMER_KEY = "E8YkeTnKeXC6stS9s9A1A"; // "UxsB7aprBkJouFmLoCBeAg";
	public static String CONSUMER_SECRET = "8WHG5sppZRqnIOW8NxFD5mOSqN9OwFhjeF7ALxDD0"; // "z0N8WptzIL1Ii6PGHfSuP9F1c4Q3ykYL9TLJh6Y4E";

	public static final String PREFERENCE_NAME = "twitter_oauth";
	public static final String PREF_KEY_SECRET = "oauth_token_secret";
	public static final String PREF_KEY_TOKEN = "oauth_token";

	static final String CALLBACK_URL = "oauth://shouldibuyit";

	static final String IEXTRA_AUTH_URL = "auth_url";
	static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
	static final String IEXTRA_OAUTH_TOKEN = "oauth_token";

	static final String USER_TWITTER = "user_twitter";

	private SharedPreferences preferenceStore = null;
	private SharedPreferences.Editor storeEditor = null;
	private final String TWITTER_SESSION_STORE = "TW_Session_stores";

	private static TWPreferenceStores mInstance = null;

	private TWPreferenceStores() {
		Context context = BaseActivity.mAppplicationContext;
		preferenceStore = context.getSharedPreferences(TWITTER_SESSION_STORE, Context.MODE_PRIVATE);
		storeEditor = preferenceStore.edit();
	}

	public static TWPreferenceStores getInstnace() {
		if (mInstance == null) {
			mInstance = new TWPreferenceStores();
		}
		return mInstance;
	}

	public String getString(String key, String defaultValue) {

		return preferenceStore.getString(TWPreferenceStores.PREF_KEY_TOKEN, defaultValue);
	}

	public void saveString(String key, String value) {
		storeEditor.putString(key, value);
		storeEditor.commit();
	}

	public void clearAll() {
		storeEditor.clear();
		storeEditor.commit();

	}

}
