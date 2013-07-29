package com.jimmy.rssreader.async;

import java.util.List;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;
import org.mcsoxford.rss.RSSReader;
import org.mcsoxford.rss.RSSReaderException;

import com.jimmy.rssreader.R;
import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.markupartist.android.widget.PullToRefreshListView;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class FetchRSSInfoService extends Service {
	public static final String TAG = "FetchRSSInfoService";
	FetchRSSInfoBinder mBinder;
	RSSReader mReader;
	RSSAsyncTask mTask;
	String uri = "";
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onCreate method inside,the uri is " + uri);
		super.onCreate();
		mBinder = new FetchRSSInfoBinder();
		mReader = new RSSReader();
		mTask = new RSSAsyncTask(mReader);
		mTask.execute(uri);
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mTask.cancel(true);
		mReader.close();
	}

	public class FetchRSSInfoBinder extends Binder {
		public FetchRSSInfoService getService() {
			Log.d(TAG, "Method:getService()");
			return FetchRSSInfoService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Method:onBind()");
		uri = intent.getExtras().getString("uri");
		return mBinder;
	}

	private class RSSAsyncTask extends AsyncTask<String, Object, Integer> {
		private RSSReader reader;

		public RSSAsyncTask(RSSReader reader) {
			this.reader = reader;
		}

		@Override
		protected Integer doInBackground(String... url) {
			Log.d(TAG, "Method:doInBackground;");
			int rowNum = 0;
			try {
				RSSFeed feed = reader.load(url[0]);
				List<RSSItem> items = feed.getItems();
				rowNum = items.size();
				for (int i = 0; i < items.size(); i++) {
					RSSItem item = items.get(i);
					ContentValues values = new ContentValues();
					values.put(RSSInfo.TITLE, item.getTitle());
					values.put(RSSInfo.PUB_DATE, item.getPubDate().toString());
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
	}

}
