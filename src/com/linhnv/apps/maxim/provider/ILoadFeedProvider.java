package com.linhnv.apps.maxim.provider;

import java.util.List;

public interface ILoadFeedProvider<T> {
	void onLoadFeedComplete(List<T> result);
	void onLoadFeedFailed();
}
