package com.linhnv.apps.maxim.provider;

import java.util.List;

import com.linhnv.apps.maxim.model.ImageEntry;

public interface IEntryProvider {
	void onEntrySelectedProvider(List<ImageEntry> list,int position);
}
