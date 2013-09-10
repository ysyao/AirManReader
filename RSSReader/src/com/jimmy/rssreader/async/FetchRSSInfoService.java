package com.jimmy.rssreader.async;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.transform.Source;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import com.jimmy.rssreader.R;
import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.jimmy.rssreader.contentprovider.RSSContact.Sources;
import com.jimmy.rssreader.ui.MyListFragment;
import com.markupartist.android.widget.PullToRefreshListView;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

public class FetchRSSInfoService extends Service {

	public static final String TAG = "FetchRSSInfoService";

	/*
	 * public static final int REFRESH_TASK_TYPE = 0; public static final int
	 * PERIODIC_TASK_TYPE = 1;
	 */
	/* public static final String START_SERVICE = "start"; */
	/* private Timer mTimer = new Timer(); */
	private RSSReader mReader = new RSSReader();
	private String mUri;
	private NotificationManager mNM;
	private static int BOUND_NOTIFICATION = R.string.rss_bound_service;
	private int updateNum;

	@Override
	public void onCreate() {
		super.onCreate();
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mUri = "";
		Log.d(TAG, "rssfetch--onCreate method inside,the uri is " + mUri);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Method:startcommand()-------");
		updateNum = 0;
		mUri = intent.getStringExtra("uri");
		if (mUri != null && !mUri.equals("")) {
			fetchDataBySourceId(mUri);
		} else {
			fetchAllData();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void fetchDataBySourceId(String uri) {
		Log.d(TAG, "fetchDataBySourceId,uri is " + uri);
		String[] projection = { Sources.SRC_ID };
		String[] selectionArgs = { uri };
		String sourceId = null;
		Cursor cursor = getApplication().getContentResolver().query(
				Sources.CONTENT_URI_SOURCE, projection,
				Sources.SRC_ADDR + " = ?", selectionArgs, null);
		if (cursor != null && cursor.moveToFirst()) {
			sourceId = cursor.getString(cursor
					.getColumnIndexOrThrow(Sources.SRC_ID));
		}
		
		new RSSAsyncTask(mReader, sourceId).execute(uri);
	}

	private void fetchAllData() {
		Log.d(TAG, "rssfetch--Method:fetchDataByRefreshing,mUri is " + mUri);
		String[] projection = { Sources.SRC_ADDR };
		Cursor cursor = getApplication().getContentResolver().query(
				Sources.CONTENT_URI_SOURCE, projection, null, null, null);
		for (cursor.moveToFirst(); cursor.isAfterLast(); cursor.moveToNext()) {
			String sourceId = cursor.getString(cursor
					.getColumnIndexOrThrow(Sources.SRC_ID));
			String uri = cursor.getString(cursor
					.getColumnIndexOrThrow(Sources.SRC_ADDR));
			new RSSAsyncTask(mReader, sourceId).execute(uri);
		}
		// mTimer = fetchDataPeriodic(50000, 50000);
	}

	/*
	 * public Timer fetchDataPeriodic(long delay, long period) { Log.d(TAG,
	 * "rssfetch--Method:fetchDataPeriodic"); showBoundNotification(); Timer
	 * timer = new Timer(); timer.schedule(new TimerTask() {
	 * 
	 * @Override public void run() { new RSSAsyncTask(mReader,
	 * PERIODIC_TASK_TYPE).execute(mUri); } }, delay, period); return timer; }
	 */

	private void showBoundNotification() {
		CharSequence text = getText(R.string.rss_bound_service);
		Notification notification = new Notification(R.drawable.icon, text,
				System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MyListFragment.class), 0);
		notification.setLatestEventInfo(this, "Fetch news every 3 minites",
				text, contentIntent);
		mNM.notify(BOUND_NOTIFICATION, notification);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopSelf();
		mReader.close();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class RSSAsyncTask extends AsyncTask<String, Void, Integer> {
		private RSSReader reader;
		private String sourceId;

		/* private int type; */

		public RSSAsyncTask(RSSReader reader, String sourceId) {
			this.reader = reader;
			this.sourceId = sourceId;
			/* this.type = type; */
		}

		@Override
		protected Integer doInBackground(String... url) {
			Log.d(TAG, "rssfetch--Method:doInBackground;url is " + url[0]);
			int rowNum = 0;
			try {
				RSSFeed feed = reader.load(url[0]);
				List<RSSItem> items = feed.getItems();
				rowNum = items.size();
				for (int i = 0; i < items.size(); i++) {
					RSSItem item = items.get(i);
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy/mm/dd hh:mm:ss");
					String date = format.format(item.getPubDate());
					ContentValues values = new ContentValues();
					values.put(RSSInfo.TITLE, item.getTitle());
					values.put(RSSInfo.PUB_DATE, date);
					values.put(RSSInfo.LINK, item.getLink().toString());
					values.put(RSSInfo.RES_ID, sourceId);
					getApplicationContext().getContentResolver().insert(
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
			Log.d(TAG, "rssfetch--Method:onPostExecute");
			super.onPostExecute(result);
			updateNum += result;
			/*
			 * Intent i = new Intent("com.jimmy.rssreader.datareceiver");
			 * i.putExtra("insertRows", result); i.putExtra("type", type);
			 * sendBroadcast(i);
			 */
		}

	}

}
