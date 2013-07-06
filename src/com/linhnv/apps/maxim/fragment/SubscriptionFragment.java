package com.linhnv.apps.maxim.fragment;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.linhnv.apps.maxim.R;
import com.linhnv.apps.maxim.adapter.SubscriptionAdapter;
import com.linhnv.apps.maxim.database.DatabaseHelper;
import com.linhnv.apps.maxim.model.CelebrityEntry;
import com.linhnv.apps.maxim.model.ImageEntry;
import com.linhnv.apps.maxim.provider.ICelebritySelectProvider;
import com.origamilabs.library.views.StaggeredGridView;
import com.origamilabs.library.views.StaggeredGridView.OnItemClickListener;
import com.origamilabs.library.views.StaggeredGridView.OnItemLongClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class SubscriptionFragment extends SherlockFragment {
	private StaggeredGridView mGridView;
	private ProgressBar mProgressBar;
	private SubscriptionAdapter mAdapter;
	private ICelebritySelectProvider mICelebritySelectProvider;
	private List<ImageEntry> mData;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// mListView=getListView();
		// mListView.setFadingEdgeLength(0);
		// mListView.setScrollBarStyle(ListView.SC)
		// mListView.setBackgroundColor(color)
		setHasOptionsMenu(true);
		loadData();
		try {
			SherlockFragmentActivity activity = (SherlockFragmentActivity) getActivity();
			activity.getSupportActionBar().setTitle("Subscriptions List");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			mICelebritySelectProvider = (ICelebritySelectProvider) activity;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View convertView = inflater.inflate(R.layout.celebrity_layout, container, false);
		mGridView = (StaggeredGridView) convertView.findViewById(R.id.staggeredGridView1);
		mProgressBar=(ProgressBar)convertView.findViewById(R.id.celebity_proBar);
		mGridView.setVisibility(View.GONE);
		int margin = getResources().getDimensionPixelSize(R.dimen.margin);

		mGridView.setItemMargin(margin); // set the GridView margin

		mGridView.setPadding(margin, 0, margin, 0); // have the margin on the
													// sides as well
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(StaggeredGridView parent, View view, int position, long id) {
				if(mICelebritySelectProvider!=null){
					mICelebritySelectProvider.onCelebritySelected(new CelebrityEntry());
				}
			}
		});
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
			
			

			@Override
			public boolean onItemLongClick(StaggeredGridView parent, View view, int position,
					long id) {
//				
//				if (mActionMode != null) {
//		            return false;
//		        }
//
//		        // Start the CAB using the ActionMode.Callback defined above
////		        mActionMode = getActivity().startActionMode(mActionModeCallback);
				selectedPosition=position;
//		        mActionMode = ((SherlockFragmentActivity)getActivity()).startActionMode(mActionModeCallback);
//		        view.setSelected(true);
				unSub();
		        return true;
			}
		});
//		registerForContextMenu(mGridView);
		return convertView;
	}
	

	private int selectedPosition=-1;
	private void unSub(){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Add the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User clicked OK button
		        	 doUnSubscription();		       		
		       		dialog.dismiss();
		           }
		           
		       });
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User cancelled the dialog
		        	   dialog.dismiss();
		           }
		       });
		// Set other dialog properties

		// Create the AlertDialog
		builder.setMessage("Unsubscription " + mData.get(selectedPosition).getName() +"?");
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	private void doUnSubscription(){
//		selectedPosition=-1;
		if(selectedPosition<0){
			return;
		}
		
		AsyncTask< Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(Void... params) {
				DatabaseHelper helper=null;
				try{
				 helper = DatabaseHelper.getInstance(getActivity());
				helper.open();			
				helper.deleteSubcription(mData.get(selectedPosition));				
				return true;
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					try{
						if(helper!=null){
							helper.close();
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				Log.e("Delete", "result :" +result);
				if(result){
					
//					mData.remove(selectedPosition);
//					selectedPosition=-1;
//					mAdapter = new SubscriptionAdapter(getActivity(), 0, mData);
//					mGridView.setAdapter(mAdapter);
//					mAdapter.notifyDataSetChanged();
//					loadData();
				}
			}
			
			
		};
		task.execute();
	}
	
	private void loadData() {
		AsyncTask<Void, Void, List<ImageEntry>> task = new AsyncTask<Void, Void, List<ImageEntry>>() {
			@Override
			protected List<ImageEntry> doInBackground(Void... params) {
				DatabaseHelper helper = DatabaseHelper.getInstance(getActivity());
				return helper.getAllSubcriptions();
			}

			@Override
			protected void onPostExecute(List<ImageEntry> result) {
				mProgressBar.setVisibility(View.GONE);
				if (result != null) {
					Log.i("SubscriptionFragment", "list sub :" + result.size());
					mData = new ArrayList<ImageEntry>();
					mData.addAll(result);
					mAdapter = new SubscriptionAdapter(getActivity(), 0, result);
					mGridView.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
					mGridView.setVisibility(View.VISIBLE);
				}

			}
		};
		task.execute();

	}

	// @Override
	// public void onListItemClick(ListView l, View v, int position, long id) {
	// super.onListItemClick(l, v, position, id);
	// }
	// @Override
	// public void setEmptyText(CharSequence text) {
	// super.setEmptyText("No Items");
	// }

}
