package com.coworker.airmanreader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class MyListFragment extends SherlockListFragment {
	public static final String TAG = "MyListFragment";
	OnItemSelected mListener;
	SharedPreferences mSharedPreferences;
	SharedPreferences.Editor mEditor;
	List<RSSInformation> mInfos;
	
	
	public interface OnItemSelected {
		public void onItemSelected(int position);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			mListener = (OnItemSelected) activity;
		} catch (ClassCastException e) {
			// TODO: handle exception
			throw new ClassCastException(activity.toString() + " must"
					+ "implements OnItemSelected");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.pull_to_refresh_fragment, container, false);
	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//Init every source
		mInfos = new ArrayList<RSSInformation>();
		mSharedPreferences = getActivity().getSharedPreferences(getString(R.string.store_place),
				Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();

		//Check the network
		checkNetworkConnection();
		
		// Get the data
		/*mInfos = getRSSInfoFromSharePreferences();
		if(mInfos == null || mInfos.size() == 0) {
			mInfos = fetchDataAndUpdateShpf();
		}*/
		fetchDataAndUpdateShpf();
		
		((PullToRefreshListView)getListView()).setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				fetchDataAndUpdateShpf();
			}
		});

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public void fetchDataAndUpdateShpf() {
		/*
		 * Need to do two things,one is fetch data with rss, the other is update
		 * the sharedpreferences
		 */
		String uri = mSharedPreferences.getString(getString(R.string.store_uri), getString(R.string.wangyi_uri));
		RSSReader reader = new RSSReader();
		
		new RSSAsyncTask(reader).execute(uri);
	}

	private void updateRSSInfoPreferences(List<RSSInformation> infos) {
		// Clear the sharepreferences
		mEditor.clear();

		// Data test
		/*
		 * RSSInformation info1 = new RSSInformation(); RSSInformation info2 =
		 * new RSSInformation(); RSSInformation info3 = new RSSInformation();
		 * info1.setTitle("Kobe"); info2.setTitle("Jordan");
		 * info3.setTitle("James"); infos.add(info1); infos.add(info2);
		 * infos.add(info3);
		 */

		// Load data
		Gson gson = new Gson();
		String rssObjects = gson.toJson(infos);
		mEditor.putString(getString(R.string.store_info), rssObjects);
		mEditor.commit();
		Log.d(TAG, "Inside the updateRSSInfoPreferences------------");
	}

	private List<RSSInformation> getRSSInfoFromSharePreferences() {
		List<RSSInformation> infos = new ArrayList<RSSInformation>();
		// Fetch the data from sharedPreferences
		String rssInfos = mSharedPreferences.getString(getString(R.string.store_info), null);
		// Cast the string data to List
		Gson gson = new Gson();
		infos = gson.fromJson(rssInfos, new TypeToken<List<RSSInformation>>() {
		}.getType());

		return infos;
	}

	private class RSSAsyncTask extends
			AsyncTask<String, Object, List<RSSInformation>> {
		private RSSReader reader;

		public RSSAsyncTask(RSSReader reader) {
			this.reader = reader;
		}

		@Override
		protected List<RSSInformation> doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<RSSInformation> infos = new ArrayList<RSSInformation>();
			List<RSSItem> items = new ArrayList<RSSItem>();

			// Fetch the data and load them in list of infos
			try {
				RSSFeed feed = reader.load(params[0]);
				items = feed.getItems();
				for (int i = 0; i < items.size(); i++) {
					RSSItem item = items.get(i);
					RSSInformation info = new RSSInformation();
					info.setTitle(item.getTitle().trim());
					info.setLink(item.getLink());
					infos.add(info);
				}
			} catch (RSSReaderException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return infos;
		}

		@Override
		protected void onPostExecute(List<RSSInformation> infos) {
			// TODO Auto-generated method stub
			super.onPostExecute(infos);
			//Pull the data into SharedPreferences
			updateRSSInfoPreferences(infos);
			
			//Set the fragment which inflate with listview with this MyRSSInfoAdatpter
			int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.layout.simple_list_item_activated_1
					: android.R.layout.simple_list_item_1;
			setListAdapter(new MyRSSInfoAdapter(getActivity(), layout, mInfos));
			
			//Notify that the update is over
			((PullToRefreshListView)getListView()).onRefreshComplete();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		mListener.onItemSelected(position);
	}
	
	public void checkNetworkConnection() {
		boolean isConnected = CheckNet.checkNet(getActivity());
		String packageName = getActivity().getPackageName();
		if (isConnected == false) {
			ActivityManager activityManager = (ActivityManager)getActivity().getSystemService(Context.ACTIVITY_SERVICE);
			activityManager.killBackgroundProcesses(packageName);
		}
	}
}
