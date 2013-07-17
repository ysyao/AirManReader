package com.jimmy.rssreader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import com.actionbarsherlock.app.SherlockListFragment;
import com.google.gson.Gson;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
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

public class MyListFragment extends SherlockListFragment {
	public static final String TAG = "MyListFragment";

	OnItemSelected mListener;
	SharedPreferences mSharedPreferences;
	SharedPreferences.Editor mEditor;

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
		View view = inflater
				.inflate(R.layout.pull_to_refresh, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// Init every source
		mSharedPreferences = getActivity().getSharedPreferences(
				getString(R.string.hold_container), Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();

		// Check the network
		checkNetworkConnection();

		// Get the data
		/*
		 * mInfos = getRSSInfoFromSharePreferences(); if(mInfos == null ||
		 * mInfos.size() == 0) { mInfos = fetchDataAndUpdateShpf(); }
		 */
		fetchDataAndUpdateShpf();

		((PullToRefreshListView) getListView())
				.setOnRefreshListener(new OnRefreshListener() {
					@Override
					public void onRefresh() {
						// Do work to refresh the list here.
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
		Log.d(TAG,
				"Around in fetchDataAndUpdateShpf------------------------------");

		String uri = mSharedPreferences.getString("url",
				getString(R.string.WANGYI_URI));

		RSSReader myReader = new RSSReader();
		RSSAsyncTask myTask = new RSSAsyncTask(myReader);
		myTask.execute(uri);
	}

	private void updateRSSInfoPreferences(List<RSSInformation> infos) {
		// Clear the sharepreferences
		Log.d(TAG, "Inside the updateRSSInfoPreferences------------");
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
		mEditor.putString(getString(R.string.store_name), rssObjects);
		mEditor.commit();
	}

	// private List<RSSInformation> getRSSInfoFromSharePreferences() {
	// List<RSSInformation> infos = new ArrayList<RSSInformation>();
	// // Fetch the data from sharedPreferences
	// String rssInfos = mSharedPreferences.getString(STORE_NAME, null);
	// // Cast the string data to List
	// Gson gson = new Gson();
	// infos = gson.fromJson(rssInfos, new TypeToken<List<RSSInformation>>() {
	// }.getType());
	//
	// return infos;
	// }

	private class RSSAsyncTask extends
			AsyncTask<String, Object, List<RSSInformation>> {
		private RSSReader reader;
		private ProgressDialog dialog = new ProgressDialog(getActivity());

		public RSSAsyncTask(RSSReader reader) {
			this.reader = reader;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Log.d(TAG, "Around in onPreExecute------------------------------");

			this.dialog.setMessage("Please wait");
			this.dialog.show();
		}

		@Override
		protected List<RSSInformation> doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Around in doInBackground------------------------------");

			List<RSSItem> items = new ArrayList<RSSItem>();
			List<RSSInformation> infos = new ArrayList<RSSInformation>();
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
		protected void onProgressUpdate(Object... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(List<RSSInformation> infos) {
			// TODO Auto-generated method stub
			super.onPostExecute(infos);
			Log.d(TAG, "Around in onPostExecute------------------------------");

			((PullToRefreshListView) getListView()).onRefreshComplete();

			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			// Update the sharedPreferences
			updateRSSInfoPreferences(infos);
			// ListView ready
			if (infos != null && infos.size() > 0) {
				int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.layout.simple_list_item_activated_1
						: android.R.layout.simple_list_item_1;

				setListAdapter(new MyRSSInfoAdapter(getActivity(), layout,
						infos));
			}
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		mListener.onItemSelected(position);
	}

	private void checkNetworkConnection() {
		boolean isConnected = CheckNet.checkNet(getActivity());
		String packageName = getActivity().getPackageName();
		if (isConnected == false) {
			ActivityManager activityManager = (ActivityManager) getActivity()
					.getSystemService(Context.ACTIVITY_SERVICE);
			activityManager.killBackgroundProcesses(packageName);
		}
	}
}
