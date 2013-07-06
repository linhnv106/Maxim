package com.linhnv.apps.maxim.model;

public class SubcriptionEntry {
	private String id;
	private String name;
	private String thumb;
	
	
	public SubcriptionEntry(String id, String name, String thumb) {
		super();
		this.id = id;
		this.name = name;
		this.thumb = thumb;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	
	
}
