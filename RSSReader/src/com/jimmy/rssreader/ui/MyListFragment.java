package com.jimmy.rssreader.ui;

import java.util.ArrayList;
import java.util.List;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import com.actionbarsherlock.app.SherlockListFragment;
import com.jimmy.rssreader.R;
import com.jimmy.rssreader.async.CheckNet;
import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.jimmy.rssreader.io.model.RSSInformation;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class MyListFragment extends SherlockListFragment implements
		LoaderCallbacks<Cursor> {
	public static final String TAG = "MyListFragment";

	OnItemSelected mListener;
	SharedPreferences mSharedPreferences;
	SharedPreferences.Editor mEditor;
	SimpleCursorAdapter mAdapter;

	public interface OnItemSelected {
		public void onItemSelected(int position);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		String[] projection = { RSSInfo.INFO_ID, RSSInfo.TITLE,
				RSSInfo.PUB_DATE };
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				RSSInfo.CONTENT_URI, projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		mAdapter.swapCursor(null);
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
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

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
		/* fetchDataAndUpdateShpf(); */

		fillData();

		((PullToRefreshListView) getListView())
				.setOnRefreshListener(new OnRefreshListener() {
					@Override
					public void onRefresh() {
						// Do work to refresh the list here.
						fetchData();
					}
				});
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void fillData() {
		String[] from = { RSSInfo.TITLE, RSSInfo.PUB_DATE };
		int[] to = new int[] { R.id.titleTV, R.id.pubdateTV };
		getActivity().getSupportLoaderManager().initLoader(0, null, this);

		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.rss_insert_row, null, from, to, 0);
		setListAdapter(mAdapter);
	}

	public void fetchData() {
		/*
		 * Need to fetch data with rss
		 */
		Log.d(TAG,
				"Around in fetchDataAndUpdateShpf------------------------------");

		String uri = mSharedPreferences.getString("url",
				getString(R.string.WANGYI_URI));

		RSSReader myReader = new RSSReader();
		RSSAsyncTask myTask = new RSSAsyncTask(myReader);
		myTask.execute(uri);
	}

	private class RSSAsyncTask extends AsyncTask<String, Object, Integer> {
		private RSSReader reader;

		public RSSAsyncTask(RSSReader reader) {
			this.reader = reader;
		}

		@Override
		protected Integer doInBackground(String... url) {
			// TODO Auto-generated method stub
			int rowNum = 0;
			try {
				RSSFeed feed = reader.load(url[0]);
				List<RSSItem> items = feed.getItems();
				rowNum = items.size();
				for (int i = 0; i < items.size(); i++) {
					RSSItem item = items.get(i);
					ContentValues values = new ContentValues();
					values.put(RSSInfo.TITLE, item.getTitle());
					values.put(RSSInfo.PUB_DATE, item.getPubDate().toString());
					values.put(RSSInfo.LINK, item.getLink().toString());
					getActivity().getContentResolver().insert(
							RSSInfo.CONTENT_URI, values);
				}
			} catch (RSSReaderException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return rowNum;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			((PullToRefreshListView)getListView()).onRefreshComplete();
			Toast.makeText(getActivity(), result + " data updated",
					Toast.LENGTH_SHORT).show();
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
