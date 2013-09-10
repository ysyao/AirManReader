package com.jimmy.rssreader.ui;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.jimmy.rssreader.R;
import com.jimmy.rssreader.async.FetchRSSInfoService;
import com.jimmy.rssreader.async.TestService;
import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.jimmy.rssreader.contentprovider.RSSContact.SourceFromRSS;
import com.jimmy.rssreader.contentprovider.RSSContact.Sources;
import com.jimmy.rssreader.io.model.PageInfo;
import com.jimmy.rssreader.ui.MainActivity.TabsAdapter;
import com.jimmy.rssreader.ui.widget.MyListAdapter;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
	private PageInfo mPageLoader;
	IntentFilter mFilter = new IntentFilter("com.jimmy.rssreader.datareceiver");
	Loader<Cursor> mLoader;
	Cursor mCursor;
	OnItemSelected mListener;
	PullToRefreshListView mListView;
	SimpleCursorAdapter mAdapter;
	View mFooterView;
	TextView loadingTV;
	TextView queryIdTV;
	ProgressBar loadingPB;
	private String mUri = "";
	
	
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
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		if (mPageLoader != null) {
			outState.putInt("pagenum", mPageLoader.getLoad_num());
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG,
				"Method:onActivityCreated;Initalling the resources and Setting up the plugin PullToRefresh");
		super.onCreate(savedInstanceState);

		// 初始化各种资源
		mHandler = new Handler();
		mPageLoader = new PageInfo();
		if (savedInstanceState != null) {
			mPageLoader.setEveryLoadNum(savedInstanceState.getInt("pagenum"));
		}
		mListView = (PullToRefreshListView) getActivity().findViewById(
				android.R.id.list);
		mFooterView = getLayoutInflater(null).inflate(R.layout.more_data, null);
		mListView.addFooterView(mFooterView);
		loadingTV = (TextView) getActivity().findViewById(R.id.loadingTV);
		queryIdTV = (TextView) getActivity().findViewById(R.id.queryIdTV);
		loadingPB = (ProgressBar) getActivity().findViewById(R.id.loadingPB);

		// 将moredata.xml添加到listview最底部
		mListView.setOnRefreshListener(new MyOnRefreshListener());
		loadingTV.setOnClickListener(new MyOnClickListener());
		mListView.setOnScrollListener(new MyOnScrollListener());

		// 把填充数据放到onResume环节去执行
		mCursor = null;
		mLoader = null;
		mAdapter = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Method:onCreate");
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
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
		Cursor cursor = resolver.query(Sources.CONTENT_URI_SOURCE, projection,
				null, null, null);

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
			reloadData(null, mPageLoader.getLoad_num());
			break;
		default:
			// 前面有5个menu菜单
			String src_id = Integer.toString((id - 5));
			reloadData(src_id, mPageLoader.getLoad_num());
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		// 当resume到本页面的时候，如果没有mAdapter，加载数据，如果已经有了，重载Loader当中的数据
		super.onResume();
		if (mAdapter != null) {
			//说明加载过数据，只需要重新加载
			String id = queryIdTV.getText().toString();
			reloadData(id, mAdapter.getCount());
		} else {
			// 说明没有加载过数据，第一次给listview加载数据
			Bundle b = new Bundle();
			int pagenum = mPageLoader.getLoad_num();
			String orderBy = RSSInfo.PUB_DATE + " DESC LIMIT " + pagenum;
			b.putString("orderBy", orderBy);
			mLoader = getActivity().getSupportLoaderManager().initLoader(0, b,
					mLoaderCallBacks);
			mCursor = ((CursorLoader) mLoader).loadInBackground();

			// 如果mCursor.getCount()为空说明数据库里面还没有数据，需要从网络获取，利用Service
			if (mCursor.getCount() == 0) {
				doStartService();
			} else {
				// 当mCursor里头有数据的时候，展示数据库内的数据
				String[] from = { RSSInfo.TITLE, Sources.SRC_NAME,
						RSSInfo.PUB_DATE };
				int[] to = { R.id.titleTV, R.id.sourceTV, R.id.pubdateTV };
				mAdapter = new SimpleCursorAdapter(getActivity(),
						R.layout.rss_insert_row, mCursor, from, to, 0);
				mListView.setAdapter(mAdapter);
			}
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
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
			Log.d(TAG,
					"Method:onCreateLoader;Using cursorLoader to load the data which queryed from contentresolver");
			
			CursorLoader cursorLoader = null;
			String orderBy = null;
			Uri uri = null;
			String id = bundle.getString("sourceId");
			orderBy = bundle.getString("orderBy");

			if (id != null && !(id.equals(""))) {
				//有id的时候，传递id到uri当中，数据库可以根据uri最后的id来查询，不需要selection,projection
				uri = SourceFromRSS.buildSrcUri(id);
				cursorLoader = new CursorLoader(getActivity(), uri, null, null,
						null, orderBy);
			} else {
				//无id说明要查询全部
				cursorLoader = new CursorLoader(getActivity(),
						SourceFromRSS.CONTENT_URI_SOURCE_FROM_RSS, null, null,
						null, orderBy);
			}
			return cursorLoader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			Log.d(TAG, "Method:onLoadFinished;");
			if (mAdapter != null) {
				mAdapter.swapCursor(data);
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Method:onLoaderReset;");
			if (mAdapter != null) {
				mAdapter.swapCursor(null);
			}
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
		// 更新ListView
		reloadData(null, mPageLoader.getLoad_num());
		// 刷新结束
		mListView.onRefreshComplete();
	}

	private void reloadData(String sourceId, int pagenum) {
		Bundle b = new Bundle();
		String orderBy = RSSInfo.PUB_DATE + " DESC LIMIT " + pagenum;
		b.putString("orderBy", orderBy);
		b.putString("sourceId", sourceId);
		queryIdTV.setText(sourceId);
		mHandler.post(new UpdateListView(b));
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
					int loaded = mAdapter.getCount();
					String id = queryIdTV.getText().toString();
					reloadData(id, mPageLoader.getLoad_num() + loaded);
					loadingTV.setVisibility(View.VISIBLE);
					loadingPB.setVisibility(View.GONE);
				}
			}, 2000);
		}
	}

	private class MyOnScrollListener implements OnScrollListener {
		int lastVisibleIndex;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			Log.d(TAG, "OnScroll method");
			lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
			if (mAdapter != null && (totalItemCount == mAdapter.getCount())) {
				mListView.removeFooterView(mFooterView);
				Toast.makeText(getActivity(), "数据已经全部加载完成，没有更多数据",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			Log.d(TAG, "onStateChanged method");
			if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
				loadingTV.setVisibility(View.GONE);
				loadingPB.setVisibility(View.VISIBLE);
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						int load = mPageLoader.getLoad_num() + mAdapter.getCount();
						reloadData(null, load);
						loadingTV.setVisibility(View.VISIBLE);
						loadingPB.setVisibility(View.GONE);
					}
				}, 2000);
			}
		}
	}

	private class UpdateListView implements Runnable {
		private Bundle bundle;

		public UpdateListView(Bundle bundle) {
			this.bundle = bundle;
		}

		@Override
		public void run() {
			// 在onCreate当中已经Init了,这里只能够restart
			mLoader = getActivity().getSupportLoaderManager().restartLoader(0,
					bundle, mLoaderCallBacks);
			mAdapter.notifyDataSetChanged();
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Method:onReceive");
			Bundle bundle = intent.getExtras();
			rows = bundle.getInt("insertRows");
			fillData();
			/*int type = bundle.getInt("type");
			if (type == FetchRSSInfoService.REFRESH_TASK_TYPE) {
				fillData();
			} else if (type == FetchRSSInfoService.PERIODIC_TASK_TYPE) {
				if (rows > 0) {
					Toast.makeText(getActivity(), "你有" + rows + "条新数据未更新",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}*/
		}
	}
}
