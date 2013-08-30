package com.jimmy.rssreader.ui;

import javax.xml.transform.Source;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.jimmy.rssreader.R;
import com.jimmy.rssreader.async.FetchRSSInfoService;
import com.jimmy.rssreader.async.TestService;
import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.jimmy.rssreader.contentprovider.RSSContact.Sources;
import com.jimmy.rssreader.ui.MainActivity.TabsAdapter;
import com.jimmy.rssreader.ui.widget.MyListAdapter;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturingListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MyListFragment extends SherlockListFragment {
	public static final String TAG = "MyListFragment";
	private static int rows = 0;
	public TestService mBoundService;
	private MyLoaderCallBacks mLoaderCallBacks = new MyLoaderCallBacks();
	private MyBroadcastReceiver mReceiver = new MyBroadcastReceiver();
	private Handler mHandler;
	IntentFilter mFilter = new IntentFilter("com.jimmy.rssreader.datareceiver");
	Cursor mCursor;
	OnItemSelected mListener;
	SharedPreferences mSharedPreferences;
	SharedPreferences.Editor mEditor;
	PullToRefreshListView mListView;
	SimpleCursorAdapter mAdapter;
	View mFooterView;
	TextView loadingTV;
	ProgressBar loadingPB;
	private String mUri = "";
	int updateNum = 0;

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
		getActivity().registerReceiver(mReceiver, mFilter);
	}

	@Override
	public void onDetach() {
		Log.d(TAG, "Method:onDetach;Unregisting contentprovider observer.");
		// TODO Auto-generated method stub
		super.onDetach();
		getActivity().unregisterReceiver(mReceiver);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG,
				"Method:onCreateView;Connect the view pull_to_refresh to MyListFragment");
		// Connect the view pull_to_refresh to MyListFragment
		View view = inflater.inflate(R.layout.mylistfragment_listview,
				container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG,
				"Method:onActivityCreated;Initalling the resources and Setting up the plugin PullToRefresh");
		super.onCreate(savedInstanceState);

		// 初始化各种资源
		mHandler = new Handler();
		mSharedPreferences = getActivity().getSharedPreferences(
				getString(R.string.hold_container), Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
		mListView = (PullToRefreshListView) getActivity().findViewById(
				android.R.id.list);
		mFooterView = getLayoutInflater(null).inflate(R.layout.more_data, null);
		mListView.addFooterView(mFooterView);
		loadingTV = (TextView) getActivity().findViewById(R.id.loadingTV);
		loadingPB = (ProgressBar) getActivity().findViewById(R.id.loadingPB);
		
		//将moredata.xml添加到listview最底部
		mListView.setOnRefreshListener(new MyOnRefreshListener());
		loadingTV.setOnClickListener(new MyOnClickListener());
		mListView.setOnScrollListener(new MyOnScrollListener());

		// 利用SimpleCursorAdapter填充数据
		String[] from = { RSSInfo.TITLE, RSSInfo.PUB_DATE };
		int[] to = { R.id.titleTV, R.id.pubdateTV };
		getActivity().getSupportLoaderManager().initLoader(0, null,
				mLoaderCallBacks);
		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.rss_insert_row, null, from, to, 0);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Method:onCreate");
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		/* doBindService(); */
		doStartService();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG, "Method:onCreateOptionsMenu");
		super.onCreateOptionsMenu(menu, inflater);

		// 新增一个刷新按钮
		menu.add(1, 1, 1, "Refresh")
				.setIcon(R.drawable.ic_refresh_inverse)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		// 新增一个search bar
		menu.add(1, 2, 2, "Search")
				.setIcon(R.drawable.ic_search_inverse)
				.setActionView(R.layout.collapsible_edittext)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		// 从数据库当中查询新闻源
		ContentResolver resolver = getActivity().getContentResolver();
		String[] projection = { Sources.SRC_ID, Sources.SRC_NAME };
		Cursor cursor = resolver.query(Sources.CONTENT_URI, projection, null,
				null, null);

		// 将源添加到submenu当中
		SubMenu sub = menu.addSubMenu(1, 4, 4, "SOURCES");
		sub.add(2, 5, 1, "全部");
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				String id = cursor.getString(cursor
						.getColumnIndexOrThrow(Sources.SRC_ID));
				String name = cursor.getString(cursor
						.getColumnIndexOrThrow(Sources.SRC_NAME));
				sub.add(2, Integer.parseInt(id) + 5, Integer.parseInt(id) + 1,
						name);
			}
		}
		sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		sub.getItem().setIcon(R.drawable.abs__ic_menu_moreoverflow_holo_light);

		// 新增一个设置按钮
		menu.add(1, 3, 3, "Setting")
				.setIcon(R.drawable.gear_setting)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "Method:onOptionsItemSelected");
		int id = item.getItemId();
		switch (id) {
		case 1:
			doStopService();
			doStartService();
			break;
		case 2:
			Toast.makeText(getActivity(), "Search", Toast.LENGTH_SHORT).show();
			break;
		case 3:
			ViewPager pager = ((MainActivity) getActivity()).getViewPager();
			pager.setCurrentItem(TabsAdapter.SETTING_FRAGMENT_POSITION);
			break;
		case 4:
			break;
		case 5:
			getActivity().getSupportLoaderManager().restartLoader(0, null,
					mLoaderCallBacks);
			mAdapter.notifyDataSetChanged();
			/*
			 * cursor = getActivity().getContentResolver().query(
			 * RSSInfo.CONTENT_URI, projection, null, null, null);
			 * mAdapter.changeCursor(cursor); mAdapter.notifyDataSetChanged();
			 */
			break;
		default:
			int src_id = id - 5;
			/*
			 * String[] selectionArgs = { Integer.toString(src_id) }; cursor =
			 * getActivity().getContentResolver().query( RSSInfo.CONTENT_URI,
			 * projection, RSSInfo.RES_ID + "= ?", selectionArgs, null);
			 * mAdapter.changeCursor(cursor); mAdapter.notifyDataSetChanged();
			 */
			String selectionArgs = Integer.toString(src_id);
			String selection = RSSInfo.RES_ID + "= ?";
			Bundle b = new Bundle();
			b.putString("selection", selection);
			b.putString("selectionArgs", selectionArgs);
			getActivity().getSupportLoaderManager().restartLoader(0, b,
					mLoaderCallBacks);
			mAdapter.notifyDataSetChanged();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "Method:onDestroy");
		super.onDestroy();
		/* doUnBindService(); */
		doStopService();
	}

	public interface OnItemSelected {
		public void onItemSelected(int position);
	}

	public class MyLoaderCallBacks implements LoaderCallbacks<Cursor> {

		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle bundle) {
			// TODO Auto-generated method stub
			Log.d(TAG,
					"Method:onCreateLoader;Using cursorLoader to load the data which queryed from contentresolver");
			String[] projection = { RSSInfo.INFO_ID, RSSInfo.TITLE,
					RSSInfo.PUB_DATE };
			CursorLoaderNotAuto cursorLoader = null;
			if(bundle != null) {
				String[] selectionArgs = { bundle.getString("selectionArgs") };
				String selection = bundle.getString("selection");
				cursorLoader = new CursorLoaderNotAuto(
						getActivity(), RSSInfo.CONTENT_URI, projection, selection,
						selectionArgs, null);
			} else {
				cursorLoader = new CursorLoaderNotAuto(
						getActivity(), RSSInfo.CONTENT_URI, projection, null,
						null, null);
			}
			
			return cursorLoader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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
		if (rows == 0) {
			Toast.makeText(getActivity(), "No new data updated",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(), "更新了 " + rows + "条数据",
					Toast.LENGTH_SHORT).show();
		}

		getActivity().getSupportLoaderManager().restartLoader(0, null,
				mLoaderCallBacks);
		mAdapter.notifyDataSetChanged();

		/*
		 * String[] projection = { RSSInfo.INFO_ID, RSSInfo.TITLE,
		 * RSSInfo.PUB_DATE }; Cursor cursor =
		 * getActivity().getContentResolver().query(RSSInfo.CONTENT_URI,
		 * projection, null, null, null); mAdapter.changeCursor(cursor);
		 * mAdapter.notifyDataSetChanged();
		 */
		/*
		 * mAdapter = new MyListAdapter(getActivity(), mCursor);
		 * setListAdapter(mAdapter);
		 */
		mListView.onRefreshComplete();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, "Method:onListItemClick");
		super.onListItemClick(l, v, position, id);
		mListener.onItemSelected(position);
	}

	public void doStartService() {
		if (mUri == null || mUri.equals("")) {
			mUri = getString(R.string.WANGYI_URI);
		}
		Log.d(TAG, "Method:doStartService;mUri is " + mUri);
		Intent i = new Intent(getActivity(), TestService.class);
		i.putExtra("uri", mUri);
		getActivity().startService(i);
	}

	public void doStopService() {
		Intent i = new Intent(getActivity(), TestService.class);
		getActivity().stopService(i);
	}

	private class MyOnRefreshListener implements OnRefreshListener {

		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			doStopService();
			doStartService();
		}
	}

	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d(TAG, "MyOnClickListner run method");
			loadingTV.setVisibility(View.GONE);
			loadingPB.setVisibility(View.VISIBLE);
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					getActivity().getSupportLoaderManager().restartLoader(0, null, mLoaderCallBacks);
					mAdapter.notifyDataSetChanged();
					loadingTV.setVisibility(View.VISIBLE);
					loadingPB.setVisibility(View.GONE);
				}
			}, 2000);
		}
	}

	private class MyOnScrollListener implements OnScrollListener {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub

		}

	}

	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Method:onReceive");
			Bundle bundle = intent.getExtras();
			rows = bundle.getInt("insertRows");
			int type = bundle.getInt("type");
			if (type == FetchRSSInfoService.REFRESH_TASK_TYPE) {
				fillData();
			} else if (type == FetchRSSInfoService.PERIODIC_TASK_TYPE) {
				if (rows > 0) {
					Toast.makeText(getActivity(), "你有" + rows + "条新数据未更新",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
		}
	}
}
