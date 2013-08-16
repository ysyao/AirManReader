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
import android.os.IBinder;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

public class MyListFragment extends SherlockListFragment {

	public static final String TAG = "MyListFragment";
	private static int rows = 0;
	private static ActionMode mMode;
	private static float mOffsetY = 1;
	private int mMask;
	private GestureDetector gestureScanner;

	public TestService mBoundService;
	/* private MyServiceConnection mConnection = new MyServiceConnection(); */
	private MyLoaderCallBacks mLoaderCallBacks = new MyLoaderCallBacks();
	private MyBroadcastReceiver mReceiver = new MyBroadcastReceiver();
	IntentFilter mFilter = new IntentFilter("com.jimmy.rssreader.datareceiver");
	/*
	 * private MyHandler mHandler = new MyHandler(); private MyContentObserver
	 * mObserver = new MyContentObserver(getActivity(), mHandler);
	 */

	private boolean isBounded = false;
	OnItemSelected mListener;
	SharedPreferences mSharedPreferences;
	SharedPreferences.Editor mEditor;
	SimpleCursorAdapter mAdapter;
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

		// Init every source
		mSharedPreferences = getActivity().getSharedPreferences(
				getString(R.string.hold_container), Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();

		// 初始化from,to,设置simplecursoradapter
		String[] from = { RSSInfo.TITLE, RSSInfo.PUB_DATE };
		int[] to = { R.id.titleTV, R.id.pubdateTV };

		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.rss_insert_row, null, from, to, 0);
		setListAdapter(mAdapter);
		getActivity().getSupportLoaderManager().initLoader(0, null,
				mLoaderCallBacks);

		// 设置PullToRefreshListView插件
		((PullToRefreshListView) getListView())
				.setOnRefreshListener(new OnRefreshListener() {
					@Override
					public void onRefresh() {
						// Do work to refresh the list here.
						doStopService();
						doStartService();
					}
				});

		// 设置时间屏蔽位
		mMask = 1;
		// 添加actionMode的listener
		getListView().setOnTouchListener(new MyOnToucheListener());
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

		// 新增一个设置按钮
		menu.add(1, 1, 1, "Setting")
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
			((MainActivity) getActivity()).getViewPager().setCurrentItem(
					MainActivity.SETTING_FRAGMENT_POSITION);
			break;
		default:
			return false;
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
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			// TODO Auto-generated method stub
			Log.d(TAG,
					"Method:onCreateLoader;Using cursorLoader to load the data which queryed from contentresolver");
			String[] projection = { RSSInfo.INFO_ID, RSSInfo.TITLE,
					RSSInfo.PUB_DATE };
			CursorLoaderNotAuto cursorLoader = new CursorLoaderNotAuto(
					getActivity(), RSSInfo.CONTENT_URI, projection, null, null,
					null);
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
		setListAdapter(mAdapter);

		// Initing loader
		getActivity().getSupportLoaderManager().restartLoader(0, null,
				mLoaderCallBacks);

		// Config the PullToRefresh plugin
		((PullToRefreshListView) getListView()).onRefreshComplete();
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

	private final class ActionModeOfRSSInfo implements Callback {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {

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
			Cursor cursor = resolver.query(Sources.CONTENT_URI, projection,
					null, null, null);

			// 将源添加到submenu当中
			SubMenu sub = menu.addSubMenu(1, 3, 3, "SOURCES");
			if (cursor != null) {
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
						.moveToNext()) {
					String id = cursor.getString(cursor
							.getColumnIndexOrThrow(Sources.SRC_ID));
					String name = cursor.getString(cursor
							.getColumnIndexOrThrow(Sources.SRC_NAME));
					sub.add(0, Integer.parseInt(id), Integer.parseInt(id), name);
				}
			}
			sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			sub.getItem().setIcon(R.drawable.filter);

			// 新增一个设置按钮
			menu.add(1, 4, 4, "Setting")
					.setIcon(R.drawable.gear_setting)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_ALWAYS
									| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// TODO Auto-generated method stub
			int id = item.getItemId();
			switch (id) {
			case 1:
				doStopService();
				doStartService();
				break;
			case 2:
				Toast.makeText(getActivity(), "Search", Toast.LENGTH_SHORT)
						.show();
				break;
			case 4:
				((MainActivity) getActivity()).getViewPager().setCurrentItem(
						MainActivity.SETTING_FRAGMENT_POSITION,true);
				break;
			default:
				return false;
			}
			mode.finish();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// TODO Auto-generated method stub

		}

	}

	private final class MyOnToucheListener implements OnTouchListener,
			OnGestureListener {
		private GestureDetector gestureScanner = new GestureDetector(
				getActivity(), this);
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			//将该屏蔽的事件全部屏蔽掉
			event.setAction(event.getAction() & mMask);
			return gestureScanner.onTouchEvent(event);
			/*
			 * Log.d(TAG, "Method:onTouch，mTouchMovement = " + mTouchMovement);
			 * //判断touch动作是否跟前一个touch动作一样，一样的话不用做出mode的改变。 if(mTouchMovement ==
			 * event.getAction()){ return false; } else {
			 * switch(event.getAction()) { case MotionEvent.ACTION_MOVE: mMode =
			 * ((MainActivity)getActivity()).startActionMode(new
			 * ActionModeOfRSSInfo()); mTouchMovement = MotionEvent.ACTION_DOWN;
			 * break; case MotionEvent.ACTION_POINTER_UP: if(mMode != null) {
			 * mMode.finish(); } mTouchMovement = MotionEvent.ACTION_UP; }
			 * return false; }
			 */
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if(velocityY < 0) {
				//在向下滑动的情况设定ActionMode
				mMode = ((MainActivity)getActivity()).startActionMode(new ActionModeOfRSSInfo());
				//屏蔽MotionEvent.ACTION_DOWN全部位
				mMask = ~MotionEvent.ACTION_DOWN;
			}else {
				//再向上滑动的情况终止ActionMode
				mMode.finish();
				//取消屏蔽MotionEvent.ACTION_DOWN全部位
				mMask = MotionEvent.ACTION_DOWN;
			}
/*			if(((velocityY < 0) && (mOffsetY > 0)) || ((velocityY < 0) && (mOffsetY < 0))) {
				mMode = ((MainActivity) getActivity())
						.startActionMode(new ActionModeOfRSSInfo());
			}
			mOffsetY = velocityY;*/
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
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
