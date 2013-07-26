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
	public FetchRSSInfoService mBoundService;
	private boolean isBounded = false;
	OnItemSelected mListener;
	SharedPreferences mSharedPreferences;
	SharedPreferences.Editor mEditor;
	SimpleCursorAdapter mAdapter;
	String mUri = "";

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

		getActivity().getContentResolver().registerContentObserver(
				RSSInfo.CONTENT_URI, true, mObserver);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
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
		mUri = mSharedPreferences.getString("url",
				getString(R.string.WANGYI_URI));

		((PullToRefreshListView) getListView())
				.setOnRefreshListener(new OnRefreshListener() {
					@Override
					public void onRefresh() {
						// Do work to refresh the list here.
						mBoundService.fetchRSSInfos(mUri);
					}
				});
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// Binding the FetchRSSInfoService
		doBindService();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// UnBinding the FetchRSSInfoService
		doUnBindService();
	}

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

	private void fillData() {
		String[] from = { RSSInfo.TITLE, RSSInfo.PUB_DATE };
		int[] to = new int[] { R.id.titleTV, R.id.pubdateTV };
		getActivity().getSupportLoaderManager().initLoader(0, null, this);

		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.rss_insert_row, null, from, to, 0);
		setListAdapter(mAdapter);
		
		//Config the PullToRefresh plugin
		((PullToRefreshListView)getListView()).onRefreshComplete();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		mListener.onItemSelected(position);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			isBounded = false;
			mBoundService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			isBounded = true;
			mBoundService = ((FetchRSSInfoService.FetchRSSInfoBinder) service)
					.getService();
		}
	};

	public void doBindService() {
		Intent intent = new Intent(getActivity(), FetchRSSInfoService.class);
		getActivity()
				.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	public void doUnBindService() {
		if (isBounded) {
			getActivity().unbindService(mConnection);
			isBounded = false;
		}
	}

	private final ContentObserver mObserver = new ContentObserver(new Handler()) {

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			if (getActivity() == null) {
				return;
			}
			fillData();
		}
	};
}
