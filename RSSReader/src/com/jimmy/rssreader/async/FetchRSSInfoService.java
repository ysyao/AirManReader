package com.jimmy.rssreader.async;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import com.jimmy.rssreader.R;
import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.jimmy.rssreader.ui.MyListFragment;
import com.markupartist.android.widget.PullToRefreshListView;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
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
	public static final String BOUND_SERVICE = "bound";
	public static final int REFRESH_TASK_TYPE = 0;
	public static final int PERIODIC_TASK_TYPE = 1;
	/*public static final String START_SERVICE = "start";*/
	private Timer mTimer = new Timer();
	private FetchRSSInfoBinder mBinder = new FetchRSSInfoBinder();
	private RSSReader mReader = new RSSReader();
	private String mUri = "";
	private NotificationManager mNM;
	private static int BOUND_NOTIFICATION = R.string.rss_bound_service;
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.d(TAG, "rssfetch--onCreate method inside,the uri is " + mUri);
		super.onCreate();
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if(mUri != null && !mUri.equals("")) {
			fetchDataByRefreshing();
		}
	}

	public void fetchDataByRefreshing() {
		Log.d(TAG, "rssfetch--Method:fetchDataByRefreshing,mUri is " + mUri);
		new RSSAsyncTask(mReader, REFRESH_TASK_TYPE).execute(mUri);
		mTimer = fetchDataPeriodic(50000, 50000);
	}
	
	public Timer fetchDataPeriodic(long delay, long period) {
		Log.d(TAG, "rssfetch--Method:fetchDataPeriodic");
		showBoundNotification();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				new RSSAsyncTask(mReader, PERIODIC_TASK_TYPE).execute(mUri);
			}
		}, delay, period);
		return timer;
	}
	
	private void showBoundNotification() {
		CharSequence text = getText(R.string.rss_bound_service);
		Notification notification = new Notification(R.drawable.icon, text,
				System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MyListFragment.class), 0);
		notification.setLatestEventInfo(this, "Fetch news every 3 minites", text,
				contentIntent);
		mNM.notify(BOUND_NOTIFICATION, notification);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopSelf();
		mReader.close();
	}

	public class FetchRSSInfoBinder extends Binder {
		public FetchRSSInfoService getService() {
			Log.d(TAG, "rssfetch--Method:getService()");
			return FetchRSSInfoService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		mUri = intent.getExtras().getString("uri");
		Log.d(TAG, "rssfetch--Method:onBind(),uri is " + mUri);
		return mBinder;
	}

	private class RSSAsyncTask extends AsyncTask<String, Void, Integer> {
		private RSSReader reader;
		private int type;

		public RSSAsyncTask(RSSReader reader,int type) {
			this.reader = reader;
			this.type = type;
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
					SimpleDateFormat format = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");
					String date = format.format(item.getPubDate());
					ContentValues values = new ContentValues();
					values.put(RSSInfo.TITLE, item.getTitle());
					values.put(RSSInfo.PUB_DATE, date);
					values.put(RSSInfo.LINK, item.getLink().toString());
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
			Intent i = new Intent("com.jimmy.rssreader.datareceiver");
			i.putExtra("insertRows", result);
			i.putExtra("type", type);
			sendBroadcast(i);
		}
		
	}

}
