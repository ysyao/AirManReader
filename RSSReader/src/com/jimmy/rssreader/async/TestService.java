package com.jimmy.rssreader.async;

import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.jimmy.rssreader.io.model.RSSInformation;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class TestService extends Service {
	RSSInformation mInfo;
	TestBinder mBinder;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mInfo = new RSSInformation();
		mBinder = new TestBinder();
		
		createTestData();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public class TestBinder extends Binder {
		public TestService getService () {
			return TestService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Bundle extras = intent.getExtras();
		mInfo.setTitle(extras.getString("title"));
		mInfo.setLink(extras.getString("link"));
		mInfo.setPubDate(extras.getString("date"));
		return mBinder;
	}
	
	public void createTestData() {
		ContentValues values = new ContentValues();
		values.put(RSSInfo.TITLE, mInfo.getTitle());
		values.put(RSSInfo.LINK, mInfo.getLink());
		values.put(RSSInfo.PUB_DATE, mInfo.getPubDate());
		getApplicationContext().getContentResolver().insert(RSSInfo.CONTENT_URI, values);
	}
}
