package com.jimmy.rssreader.async;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.jimmy.rssreader.R;
import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.jimmy.rssreader.ui.MainActivity;
import com.jimmy.rssreader.ui.MyListFragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class TestService extends Service {
	private static final String TAG = "TestService";
	public static final String BOUND_SERVICE = "bound";
	public static final int REFRESH_TASK_TYPE = 0;
	public static final int PERIODIC_TASK_TYPE = 1;
	/*public static final String START_SERVICE = "start";*/
	private Timer mTimer = new Timer();
	TestBinder mBinder;
	TestServiceAsync mBoundTask;
	/*TestServiceAsync mStartTask;*/
	private NotificationManager mNM;
	private static int BOUND_NOTIFICATION = R.string.rss_bound_service;
	/*private static int START_NOTIFICATION = R.string.rss_start_service;*/

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Method:onCreate-----");
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		fetchDataByRefeshing();
	}

	/*
	 * @Override public int onStartCommand(Intent intent, int flags, int
	 * startId) { Log.d(TAG, "Method:onStartCommand"); showStartNotification();
	 * 
	 * mStartTask = new TestServiceAsync(START_SERVICE);
	 * mStartTask.execute("begin");
	 * 
	 * return super.onStartCommand(intent, flags, startId); }
	 */
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mTimer.cancel();
		if(mBoundTask != null) {
			mBoundTask.cancel(true);
		}
		mNM.cancel(BOUND_NOTIFICATION);
		/* mNM.cancel(START_NOTIFICATION); */
		stopSelf();
	}

	public class TestBinder extends Binder {
		public TestService getService() {
			return TestService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public void fetchDataByRefeshing() {
		Log.d(TAG, "Method:fetchDataFromRemote");
		mTimer.cancel();
		new TestServiceAsync(REFRESH_TASK_TYPE).execute();
		mTimer = fetchDataPeriodic(30000, 30000);
	}
	
	public Timer fetchDataPeriodic(long delay, long period) {
		Log.d(TAG, "Method:fetchDataPeriodic");
		showBoundNotification();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				new TestServiceAsync(PERIODIC_TASK_TYPE).execute();
			}
		}, delay, period);
		return timer;
	}

	public void createTestData() {
		// Faking some data would be fetched from internet
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/mm/dd hh:mm:ss");

		ContentValues values1 = new ContentValues();
		ContentValues values2 = new ContentValues();
		ContentValues values3 = new ContentValues();
		values1.put(RSSInfo.TITLE, "Kobe For random");
		values1.put(RSSInfo.LINK, "www.Kobe.com");
		values1.put(RSSInfo.PUB_DATE, formatter.format(new java.util.Date()));
		values2.put(RSSInfo.TITLE, "Jordan For random");
		values2.put(RSSInfo.LINK, "www.Jordan.com");
		values2.put(RSSInfo.PUB_DATE, formatter.format(new java.util.Date()));
		values3.put(RSSInfo.TITLE, "James For random");
		values3.put(RSSInfo.LINK, "www.James.com");
		values3.put(RSSInfo.PUB_DATE, formatter.format(new java.util.Date()));

		ContentValues[] values = { values1, values2, values3 };
		Random random = new Random();
		int i = random.nextInt(3);

		getApplicationContext().getContentResolver().insert(
				RSSInfo.CONTENT_URI, values[i]);
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

	/*
	 * private void showStartNotification() { CharSequence text =
	 * getText(R.string.rss_start_service); Notification notification = new
	 * Notification(R.drawable.icon, text, System.currentTimeMillis());
	 * PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new
	 * Intent(this, MyListFragment.class), 0);
	 * notification.setLatestEventInfo(this, "com.jimmy.rssreader", text,
	 * contentIntent); mNM.notify(START_NOTIFICATION, notification); }
	 */

	private class TestServiceAsync extends AsyncTask<String, Void, Integer> {
		private int type;
		public TestServiceAsync(int type) {
			this.type = type;
		}
		@Override
		protected Integer doInBackground(String... params) {
			Log.d(TAG, "Method:doInBackground-----");
			Random random = new Random();
			int pickNumber = random.nextInt(4);
			if (pickNumber > 0) {
				for (int i = 1; i <= pickNumber; i++) {
					createTestData();
				}
			}
			return pickNumber;
		}

		@Override
		protected void onPostExecute(Integer result) {
			Log.d(TAG, "Method:onPostExecute");
			super.onPostExecute(result);
			Intent i = new Intent("com.jimmy.rssreader.datareceiver");
			i.putExtra("insertRows", result);
			i.putExtra("type", type);
			sendBroadcast(i);
		}
	}
}
