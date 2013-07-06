package com.linhnv.apps.maxim.database;

import java.util.ArrayList;
import java.util.List;

import com.linhnv.apps.maxim.model.CelebrityEntry;
import com.linhnv.apps.maxim.model.ImageEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseHelper {
	private SQLiteDatabase database;
	private DatabaseManager dbHelper;
	private static DatabaseHelper mInstance;
	private String[] allColumns = { DatabaseManager.COLUMN_ID, DatabaseManager.COLUMN_CATEGORY_ID,
			DatabaseManager.COLUMN_THUMB, DatabaseManager.COLUMN_NAME };
	private String[] allCelebrity = { DatabaseManager.COLUMN_CELEBRITY_ID,
			DatabaseManager.COLUMN_CELEBRITY_CATEGORY_ID, DatabaseManager.COLUMN_CELEBRITY_NAME };
	private String[] allTopEntry = { DatabaseManager.COLUMN_TOP_ID,
			DatabaseManager.COLUMN_TOP_CATEGORY_ID, DatabaseManager.COLUMN_TOP_THUMB,
			DatabaseManager.COLUMN_TOP_IMAGE };

	public static DatabaseHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DatabaseHelper(context);
		}
		return mInstance;
	}

	public DatabaseHelper(Context context) {
		dbHelper = new DatabaseManager(context);
		mInstance = this;
		
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void addCelebrity(CelebrityEntry entry) {
		try {
//			ContentValues values = new ContentValues();
//			values.put(DatabaseManager.COLUMN_CELEBRITY_CATEGORY_ID, entry.getCategoryId());
//			values.put(DatabaseManager.COLUMN_CELEBRITY_NAME, entry.getTitle());
//			long insertId = database.insert(DatabaseManager.TABLE_CELEBRITY, null, values);
			// Log.i("DatabaseHelper","celebrity insertId : " + insertId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addImageEntry(ImageEntry entry) {
		try {
			ContentValues values = new ContentValues();
			values.put(DatabaseManager.COLUMN_TOP_CATEGORY_ID, entry.getId());
			values.put(DatabaseManager.COLUMN_TOP_IMAGE, entry.getImage());
			values.put(DatabaseManager.COLUMN_TOP_THUMB, entry.getThumb());
			long insertId = database.insert(DatabaseManager.TABLE_TOP, null, values);
			// Log.i("DatabaseHelper","celebrity insertId : " + insertId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addImageEntries(List<ImageEntry> list) {
		if (list == null || list.size() <= 0) {
			return;
		}
		try {
			database=dbHelper.getWritableDatabase();
			database.beginTransaction();
			database.execSQL("DROP TABLE IF EXISTS " + DatabaseManager.TABLE_TOP);
			database.execSQL(DatabaseManager.DATABASE_TOP_CREATE);

			for (ImageEntry entry : list) {
				addImageEntry(entry);
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				database.endTransaction();
				dbHelper.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public List<ImageEntry> getAllTopHot() {
		List<ImageEntry> result = new ArrayList<ImageEntry>();
		try {
			database=dbHelper.getWritableDatabase();
			database.beginTransaction();
			Cursor cursor = database.query(DatabaseManager.TABLE_TOP, allTopEntry, null, null,
					null, null, null);

			cursor.moveToFirst();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				try {
					String id = cursor.getString(cursor
							.getColumnIndex(DatabaseManager.COLUMN_TOP_CATEGORY_ID));
					String image = cursor.getString(cursor
							.getColumnIndex(DatabaseManager.COLUMN_TOP_IMAGE));
					String thumb = cursor.getString(cursor
							.getColumnIndex(DatabaseManager.COLUMN_TOP_THUMB));
//					ImageEntry entry = new ImageEntry(thumb, image, id);
//					result.add(entry);
					
				} catch (Exception e) {
					e.printStackTrace();
				}

				cursor.moveToNext();
			}
			// Make sure to close the cursor
			database.setTransactionSuccessful();
			cursor.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				database.endTransaction();
				dbHelper.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		return result;
	}

	public void addCelebrities(List<CelebrityEntry> list) {
		if (list == null || list.size() <= 0) {
			return;
		}
		try {
			database=dbHelper.getWritableDatabase();
			database.beginTransaction();
			database.execSQL("DROP TABLE IF EXISTS " + DatabaseManager.TABLE_CELEBRITY);
			database.execSQL(DatabaseManager.DATABASE_CELEBRITY_CREATE);

			for (CelebrityEntry entry : list) {
				addCelebrity(entry);
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				database.endTransaction();
				dbHelper.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public List<CelebrityEntry> getAllCelebrity() {
		List<CelebrityEntry> result = new ArrayList<CelebrityEntry>();
		try {
			
			database=dbHelper.getWritableDatabase();
			database.beginTransaction();
			Cursor cursor = database.query(DatabaseManager.TABLE_CELEBRITY, allCelebrity, null,
					null, null, null, null);

			cursor.moveToFirst();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				try {
					String id = cursor.getString(cursor
							.getColumnIndex(DatabaseManager.COLUMN_CELEBRITY_CATEGORY_ID));
					String name = cursor.getString(cursor
							.getColumnIndex(DatabaseManager.COLUMN_CELEBRITY_NAME));
//					CelebrityEntry entry = new CelebrityEntry(id, name);
//					result.add(entry);
					
				} catch (Exception e) {
					e.printStackTrace();
				}

				cursor.moveToNext();
			}
			// Make sure to close the cursor
			database.setTransactionSuccessful();
			cursor.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				database.endTransaction();
				dbHelper.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		return result;
	}

	public boolean createSubcription(ImageEntry entry) {
		try {
			database=dbHelper.getWritableDatabase();
			database.beginTransaction();
			deleteSubcription(entry);
			ContentValues values = new ContentValues();
			values.put(DatabaseManager.COLUMN_NAME, entry.getName());
//			values.put(DatabaseManager.COLUMN_CATEGORY_ID, entry.getCategoryId());
			values.put(DatabaseManager.COLUMN_THUMB, entry.getThumb());
			long insertId = database.insert(DatabaseManager.TABLE_SUBSCRIPTION, null, values);
			Log.i("DatabaseHelper", "insertId : " + insertId +" title  :" +entry.getName());
			database.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				database.endTransaction();
				dbHelper.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return false;
	}

	public void deleteSubcription(ImageEntry entry) {
		try {
//			String id = entry.getCategoryId();
//			System.out.println("Comment deleted with id: " + id);
//			database.delete(DatabaseManager.TABLE_SUBSCRIPTION, DatabaseManager.COLUMN_CATEGORY_ID
//					+ " = " + id, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<ImageEntry> getAllSubcriptions() {
		List<ImageEntry> result = new ArrayList<ImageEntry>();
		try{
			database=dbHelper.getWritableDatabase();
			database.beginTransaction();
		Cursor cursor = database.query(DatabaseManager.TABLE_SUBSCRIPTION, allColumns, null, null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			try {
				String id = cursor.getString(cursor
						.getColumnIndex(DatabaseManager.COLUMN_CATEGORY_ID));
				String thumb = cursor
						.getString(cursor.getColumnIndex(DatabaseManager.COLUMN_THUMB));
				String name = cursor.getString(cursor.getColumnIndex(DatabaseManager.COLUMN_NAME));
//				ImageEntry entry = new ImageEntry();
//				entry.setCategoryId(id);
//				entry.setName(name);
//				entry.setThumb(thumb);
//				result.add(entry);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			cursor.moveToNext();
		}
		// Make sure to close the cursor
		database.setTransactionSuccessful();
		cursor.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				database.endTransaction();
				dbHelper.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return result;
	}
}
