package com.linhnv.apps.maxim.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.linhnv.apps.maxim.model.CelebrityEntry;
import com.linhnv.apps.maxim.model.ImageEntry;
import com.linhnv.apps.maxim.provider.ILoadFeedProvider;

import android.os.AsyncTask;
import android.util.Log;

public class ReadEntryTask extends AsyncTask<String, Void, List<ImageEntry>>{
	final static String TAG="ReadEntryTask";
	final String testURl="http://www.maxim.com/girls-of-maxim/lorenza-zorer";
	final String startTag="var slideshow=";
	final String endTag="}];";
	String start="<div class=\"field-content teaser-image\">";
	private ILoadFeedProvider<ImageEntry> mILoadFeedProvider;
	
	public ReadEntryTask(ILoadFeedProvider<ImageEntry> mILoadFeedProvider) {
		super();
		this.mILoadFeedProvider = mILoadFeedProvider;
	}

	@Override
	protected List<ImageEntry> doInBackground(String... arg0) {	
//		Log.w("ReadEntryTask", "load :" + arg0[0]);
		List<ImageEntry> data= new ArrayList<ImageEntry>();
		try {
			String tmp=convertInputStreamToString(downloadUrl(arg0[0]));
			String result="";
			int index=0;
			if(tmp!=null&&tmp.trim().length()>0){
				index=tmp.indexOf("var slideshow=");
				result=tmp.substring(index+14);
				
				try{
					 String thumb="";
					 String image="";
					 String id="";
					 String name="";
					 String description="";
					 String fullLink="";
					JSONArray array = new JSONArray(result);
//					Log.i(TAG, "array: " +array.length());
					for(int i=0;i<array.length();i++){
						try{
						JSONObject obj = array.getJSONObject(i);
						name=obj.getString("ssTitle");
						thumb=obj.getString("src");
						id=obj.getString("Nid");
						description=obj.getString("alt_image");
						fullLink=obj.getString("fullscreenLink").replace(" ", "-");
						if(fullLink.contains("Today")){
							fullLink="/slideshow/todays-girl/"+id;
						}
						ImageEntry entry = new ImageEntry(thumb, image, id, name, description);
						entry.setFullLink(fullLink);
						data.add(entry);
						}catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					return data;
				}catch(JSONException e){
					e.printStackTrace();
				}
			}
//			Log.i("ReadFeedTask", "result " +tmp);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(List<ImageEntry> result) {
//		Log.i("ReadFeedTask", "result :D " +result);
		if(result!=null){
		mILoadFeedProvider.onLoadFeedComplete(result);
		}else{
			mILoadFeedProvider.onLoadFeedFailed();
		}
	}

	private InputStream downloadUrl(String urlString) throws IOException {
	    URL url = new URL(urlString);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setReadTimeout(10000 /* milliseconds */);
	    conn.setConnectTimeout(15000 /* milliseconds */);
	    conn.setRequestMethod("GET");
	    conn.setDoInput(true);
	    // Starts the query
	    conn.connect();
	    return conn.getInputStream();
	}
	
	protected  String convertInputStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;		
		String end="";
		boolean isStart=false;
		boolean isEnd=false;
		try {
			while ((line = reader.readLine()) != null) {
//				Log.i("ReadFeedTask", "start :D " +line);

				if(line.contains(startTag)){
//					Log.e("ReadFeedTask", "start :D " +line);
					isStart=true;
					
				}
				if(line.contains(endTag)){
//					Log.e("ReadFeedTask", "end :D " +line);
					isEnd=true;
				}
//				if(isStart&&!isEnd){
//					Log.i("ReadFeedTask", "line :D " +line);
////					if(line.contains(start)){					
//						sb.append(line + "\n");
////						Log.w("ReadFeedTask", "line : " +line);
////					}
//				}
				if(isStart&&isEnd){
					sb.append(line + "\n");
					return sb.toString();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	} 
}
