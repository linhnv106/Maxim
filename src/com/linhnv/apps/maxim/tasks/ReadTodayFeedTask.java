package com.linhnv.apps.maxim.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.linhnv.apps.maxim.constants.Constants;
import com.linhnv.apps.maxim.model.CelebrityEntry;
import com.linhnv.apps.maxim.provider.ILoadFeedProvider;

import android.os.AsyncTask;
import android.util.Log;

public class ReadTodayFeedTask extends AsyncTask<String, Void, List<CelebrityEntry>>{
	final static String TAG="ReadFeedTask";
	 String testURl=Constants.TODAY_GIRL;
	final String startTag="<div class=\"views-row views-row-1 views-row-odd views-row-first content-teaser clearfix Slideshow \">";
	final String endTag=" <h2 class=\"element-invisible\">";
	final String start="<div class=\"teaser-image\">";
	final String timeTag="<span class=\"date-display-single\"";
	final String desTag="<div class=\"field-content teaser-body\">";
	private ILoadFeedProvider<CelebrityEntry> mFeedProvider;
	private int page=0;
	public ReadTodayFeedTask(ILoadFeedProvider<CelebrityEntry> provider,int page) {
		super();
		this.page=page;
		this.mFeedProvider=provider;
	}

	@Override
	protected List<CelebrityEntry> doInBackground(String... arg0) {		
		List<CelebrityEntry> data= new ArrayList<CelebrityEntry>();
		try {
			String result[]= convertInputStreamToString(downloadUrl(testURl+page)).split(start);
			int index1=0;
			int index2=0;
			String url="";
			String title="";
			String thumb="";
			String time="";
			String description="";
			for(String s:result){
//				Log.i(TAG,"s :" +s);
				if(s!=null&&s.trim().length()>0){
//				Log.i(TAG,"result :" +s);
					try{
					index1=s.indexOf("<a href");
					index2=s.indexOf("\">");
//					Log.i(TAG,"index1 %d  index2 %d:" +index1 +"::"+ index2);
					url=s.substring(index1+9, index2);
//					Log.i(TAG,"url :" +url);
					}catch(Exception e){
						e.printStackTrace();
					}
				try{
				index1=s.indexOf("title=\"");
				index2=s.indexOf("\"  typeof");
				title=s.substring(index1+7, index2);
//				Log.i(TAG,"name :" +title);
				}catch(Exception e){
					e.printStackTrace();
				}
				try{
				index1=s.indexOf("src=\"");
				index2=s.indexOf("?itok");
				thumb=s.substring(index1+5, index2);
//				Log.i(TAG,"url :" +thumb);
				}catch(Exception e){
					e.printStackTrace();
				}
				try{
					index1=s.indexOf("content=\"");
					String temp=s.substring(index1+"content=\"".length());
					index1=temp.indexOf("\">");
					index2=temp.indexOf("</span>");
					time=temp.substring(index1+2, index2);
//					Log.i(TAG,"time :" +time);
				}catch(Exception e){
					e.printStackTrace();
				}
				try{
					index1=s.indexOf(desTag);
					String temp2=s.substring(index1+desTag.length());
					index1=temp2.indexOf("</div>    </div>");
//					index2=temp2.indexOf("</span>");
					description=temp2.substring(0, index1);
					description.replace("<em>", "");
					description.replace("</em>", "");
//					Log.i(TAG,"time :" +description);
				}catch(Exception e){
					e.printStackTrace();
				}
				CelebrityEntry entry = new CelebrityEntry(thumb,title,url,time,description);
				data.add(entry);
				}
				
			}
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(List<CelebrityEntry> result) {
//		Log.i("ReadFeedTask", "result :D " +result);
		if(mFeedProvider!=null){
			if(result!=null){
				mFeedProvider.onLoadFeedComplete(result);
			}else{
				mFeedProvider.onLoadFeedFailed();
			}
				
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
				
				if(line.contains(startTag)){
//					Log.e("ReadFeedTask", "start :D " +line);
					isStart=true;
				}
				if(isStart&&line.contains(endTag)){
//					Log.e("ReadFeedTask", "start :D " +line);
					isEnd=true;
				}
				if(isStart&&!isEnd){
//					Log.i("ReadFeedTask", "line :D " +line);
					isStart=true;
					if(line.contains(start)){					
						sb.append(line );
//						Log.w("ReadFeedTask", "line : " +line);
					}
					if(line.contains(timeTag)){
						sb.append(line);
					}
					if(line.contains(desTag)){
						sb.append(line+"\n");
					}
				}
				if(isStart&&isEnd){
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
