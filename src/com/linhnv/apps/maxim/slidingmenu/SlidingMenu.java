package com.linhnv.apps.maxim.slidingmenu;

public class SlidingMenu {
	private int id;
	private int icon;
	private String name;
	
	
	public SlidingMenu() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public SlidingMenu(int id, int icon, String name) {
		super();
		this.id = id;
		this.icon = icon;
		this.name = name;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
