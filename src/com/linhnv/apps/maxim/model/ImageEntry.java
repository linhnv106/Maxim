package com.linhnv.apps.maxim.model;

public class ImageEntry {
	private String thumb;
	private String image;
	private String id;
	private String name;
	private String description;
	private String fullLink;
	public ImageEntry(String thumb, String image, String id, String name, String description) {
		super();
		this.thumb = thumb;
		this.image = image;
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	public String getFullLink() {
		return fullLink;
	}

	public void setFullLink(String fullLink) {
		this.fullLink = fullLink;
	}

	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
