package com.jimmy.rssreader.async;

import java.util.Random;

import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.jimmy.rssreader.ui.MyListFragment;

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
	TestBinder mBinder;
	TestServiceAsync mTask;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Method:onCreate-----");
		mTask = new TestServiceAsync();
		mTask.execute("begin");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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

	public void createTestData() {
		ContentValues values = new ContentValues();
		values.put(RSSInfo.TITLE, "Kobe For random");
		values.put(RSSInfo.LINK, "www.wangyi.com");
		values.put(RSSInfo.PUB_DATE, "2013/7/30");
		getApplicationContext().getContentResolver().insert(
				RSSInfo.CONTENT_URI, values);
	}

	private class TestServiceAsync extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			Log.d(TAG, "Method:doInBackground-----");
			double i = Math.random() * 10;

			if (i >= 1) {
				createTestData();
			}
			return (int) i;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			Log.d(TAG, "Method:onPostExecute");
			super.onPostExecute(result);
			Intent i = new Intent("com.jimmy.rssreader.datareceiver");
			i.putExtra("insertRows", result);
			sendBroadcast(i);
		}
		
	}
}
