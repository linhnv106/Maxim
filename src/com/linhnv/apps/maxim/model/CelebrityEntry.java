package com.linhnv.apps.maxim.model;
/**
 * 
 * @author Linh Nguyen
 *
 */
public class CelebrityEntry implements Comparable<CelebrityEntry>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String thumb;
	private String title;
	private String url;
	private String time;
	private String description;
	public CelebrityEntry() {
		super();
	}
	
	
	public CelebrityEntry(String thumb, String title, String url) {
		super();
		this.thumb = thumb;
		this.title = title;
		this.url = url;
	}


	public CelebrityEntry(String thumb, String title, String url, String time,String description) {
		super();
		this.thumb = thumb;
		this.title = title;
		this.url = url;
		this.time = time;
		this.description=description;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getThumb() {
		return thumb;
	}


	public void setThumb(String thumb) {
		this.thumb = thumb;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	@Override
	public int compareTo(CelebrityEntry another) {
		if(another==null||another.getTitle()==null||(another.getTitle().trim().length()<=0)){
			return -1;
		}
		String m = getTitle();
		String o=another.getTitle();		
		return m.compareTo(o);
	}
	
}
