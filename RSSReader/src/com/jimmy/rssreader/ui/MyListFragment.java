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
import com.jimmy.rssreader.async.FetchRSSInfoService;
import com.jimmy.rssreader.async.TestService;
import com.jimmy.rssreader.async.FetchRSSInfoService.FetchRSSInfoBinder;
import com.jimmy.rssreader.async.TestService.TestBinder;
import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.jimmy.rssreader.io.model.RSSInformation;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
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

public class MyListFragment extends SherlockListFragment {

	public static final String TAG = "MyListFragment";
	public TestService mBoundService;
	private MyServiceConnection mConnection = new MyServiceConnection();
	private MyCursorLoader mLoader = new MyCursorLoader();

	private boolean isBounded = false;
	OnItemSelected mListener;
	SharedPreferences mSharedPreferences;
	SharedPreferences.Editor mEditor;
	SimpleCursorAdapter mAdapter;
	String mUri = "";

	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG,
				"Method:onAttach;Auto-checking the class information and registing contentprovider observer.");
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			mListener = (OnItemSelected) activity;
		} catch (ClassCastException e) {
			// TODO: handle exception
			throw new ClassCastException(activity.toString() + " must"
					+ "implements OnItemSelected");
		}
		activity.getContentResolver().registerContentObserver(
				RSSInfo.CONTENT_URI, true, mObserver);
	}

	@Override
	public void onDetach() {
		Log.d(TAG, "Method:onDetach;Unregisting contentprovider observer.");
		// TODO Auto-generated method stub
		super.onDetach();
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG,
				"Method:onCreateView;Connect the view pull_to_refresh to MyListFragment");
		// Connect the view pull_to_refresh to MyListFragment
		View view = inflater
				.inflate(R.layout.pull_to_refresh, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG,
				"Method:onActivityCreated;Initalling the resources and Setting up the plugin PullToRefresh");
		super.onCreate(savedInstanceState);

		// Init every source
		mSharedPreferences = getActivity().getSharedPreferences(
				getString(R.string.hold_container), Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
		mUri = getString(R.string.WANGYI_URI);
		
		// Setting up the little plugin here.
		((PullToRefreshListView) getListView())
				.setOnRefreshListener(new OnRefreshListener() {
					@Override
					public void onRefresh() {
						// Do work to refresh the list here.
						doUnBindService();
						doBindService();
					}
				});
	}

	@Override
	public void onStart() {
		Log.d(TAG, "Method:onStart;DoBinding the Service,and load data to loader");
		super.onStart();
		doBindService();
	}

	@Override
	public void onStop() {
		Log.d(TAG, "Method:onStop;UnBinding the Service");
		super.onStop();
		// UnBinding the FetchRSSInfoService
		doUnBindService();
	}

	public interface OnItemSelected {
		public void onItemSelected(int position);
	}

	public class MyCursorLoader implements LoaderCallbacks<Cursor> {

		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			// TODO Auto-generated method stub
			Log.d(TAG,
					"Method:onCreateLoader;Using cursorLoader to load the data which queryed from contentresolver");
			String[] projection = { RSSInfo.INFO_ID, RSSInfo.TITLE,
					RSSInfo.PUB_DATE, RSSInfo.LINK };
			CursorLoader cursorLoader = new CursorLoader(getActivity(),
					RSSInfo.CONTENT_URI, projection, null, null, null);
			return cursorLoader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Method:onLoadFinished;");
			mAdapter.swapCursor(data);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Method:onLoaderReset;");
			mAdapter.swapCursor(null);
		}
	}

	private void fillData() {
		Log.d(TAG, "Method:fillData;Injecting data to cursorAdapter");
		String[] from = { RSSInfo.TITLE, RSSInfo.PUB_DATE, RSSInfo.LINK };
		int[] to = new int[] { R.id.titleTV, R.id.pubdateTV, R.id.linkTV };
		
		getActivity().getSupportLoaderManager().initLoader(0, null, mLoader);
		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.rss_insert_row, null, from, to, 0);
		setListAdapter(mAdapter);
		
		// Config the PullToRefresh plugin
		((PullToRefreshListView) getListView()).onRefreshComplete();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		mListener.onItemSelected(position);
	}

	public class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "Method:onServiceDisconnected;");
			isBounded = false;
			mBoundService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "Method:onServiceConnected;");
			mBoundService = ((TestBinder) service).getService();

			if (mBoundService != null) {
				isBounded = true;
				mBoundService.createTestData();
			}
		}
	};

	public void doBindService() {
		Log.d(TAG, "Method:doBindService;");
		/* Intent intent = new Intent(getActivity(), FetchRSSInfoService.class); */
		Intent testIntent = new Intent(getActivity(), TestService.class);
		testIntent.putExtra("link", mUri);
		testIntent.putExtra("title", "Kid");
		testIntent.putExtra("date", "2013/07/29");
		getActivity().bindService(testIntent, mConnection,
				Context.BIND_AUTO_CREATE);
	}

	public void doUnBindService() {
		Log.d(TAG, "Method:doUnBindService;");
		if (isBounded) {
			getActivity().unbindService(mConnection);
			isBounded = false;
		}
	}
	
	private final ContentObserver mObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			Log.d(TAG, "Method:ContentObserver's onChange;");
			super.onChange(selfChange);
			if (getActivity() == null) {
				return;
			}
			fillData();
		}
	};
}
