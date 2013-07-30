package com.jimmy.rssreader.async;

import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

public class MyContentObserver extends ContentObserver {
	private static final String TAG = "MyContentObserver";
	public static final int MSG_DATABASECHANGED = 1;

	Context mContext;
	Handler mHandler;

	public MyContentObserver(Context context,Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
		this.mHandler = handler;
		this.mContext = context;
	}

	@Override
	public void onChange(boolean selfChange) {
		Log.d(TAG, "Method:onChange----------");
		super.onChange(selfChange);
		Cursor cursor = mContext.getContentResolver().query(
				RSSInfo.CONTENT_URI, null, null, null, null);
		mHandler.obtainMessage(MSG_DATABASECHANGED, cursor.getCount(), 0)
				.sendToTarget();
	}
}
