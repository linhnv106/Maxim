package com.linhnv.apps.maxim.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper{
	//table subcription 
	public static final String TABLE_SUBSCRIPTION = "subcriptions";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_CATEGORY_ID = "categoryId";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_THUMB = "thumb";
	
	//table celebrity
	public static final String TABLE_CELEBRITY= "celebrity";
	public static final String COLUMN_CELEBRITY_ID = "_id";
	public static final String COLUMN_CELEBRITY_CATEGORY_ID = "categoryId";
	public static final String COLUMN_CELEBRITY_NAME = "name";
	//table hot rate
	public static final String TABLE_TOP= "top";
	public static final String COLUMN_TOP_ID = "_id";
	public static final String COLUMN_TOP_CATEGORY_ID = "categoryId";
	public static final String COLUMN_TOP_IMAGE = "image";
	public static final String COLUMN_TOP_THUMB = "thumb";
	
	
	private static final String DATABASE_NAME = "celebrity.db";
	private static final int DATABASE_VERSION = 1;
	static final String DATABASE_CREATE = "create table "
		      + TABLE_SUBSCRIPTION + "(" + COLUMN_ID
		      + " integer primary key autoincrement, " 
		      + COLUMN_CATEGORY_ID+ " text not null, "
		      + COLUMN_THUMB+ " text not null, "
		      + COLUMN_NAME+ " text not null);";
	 static final String DATABASE_CELEBRITY_CREATE = "create table "
		      + TABLE_CELEBRITY + "(" + COLUMN_CELEBRITY_ID
		      + " integer primary key autoincrement, " 
		      + COLUMN_CELEBRITY_CATEGORY_ID+ " text not null, "
		      + COLUMN_CELEBRITY_NAME + " text not null);";
	 static final String DATABASE_TOP_CREATE = "create table "
		      + TABLE_TOP + "(" + COLUMN_TOP_ID
		      + " integer primary key autoincrement, " 
		      + COLUMN_TOP_CATEGORY_ID+ " text not null, "
		      + COLUMN_TOP_THUMB + " text not null, "
		      + COLUMN_TOP_IMAGE + " text not null);";
	public DatabaseManager(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	public DatabaseManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
		db.execSQL(DATABASE_CREATE);
		db.execSQL(DATABASE_CELEBRITY_CREATE);
		db.execSQL(DATABASE_TOP_CREATE);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIPTION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CELEBRITY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOP);
	    onCreate(db);
	}

}
