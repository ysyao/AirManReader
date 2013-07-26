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

	public class FetchRSSInfoBinder extends Binder {
		public FetchRSSInfoService getService() {
			return FetchRSSInfoService.this;
		}
	}

	FetchRSSInfoBinder mBinder = new FetchRSSInfoBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	public void fetchRSSInfos(String uri) {
		Log.d(TAG, "Around in fetchRSSInfos------------------------------");

		RSSReader myReader = new RSSReader();
		RSSAsyncTask myTask = new RSSAsyncTask(myReader);
		myTask.execute(uri);
	}

	private class RSSAsyncTask extends AsyncTask<String, Object, Integer> {
		private RSSReader reader;

		public RSSAsyncTask(RSSReader reader) {
			this.reader = reader;
		}

		@Override
		protected Integer doInBackground(String... url) {
			// TODO Auto-generated method stub
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

		/*
		 * @Override protected void onPostExecute(Integer result) { // TODO
		 * Auto-generated method stub super.onPostExecute(result);
		 * ((PullToRefreshListView)getListView()).onRefreshComplete();
		 * Toast.makeText(getApplicationContext(), result + " data updated",
		 * Toast.LENGTH_SHORT).show(); }
		 */
	}

}
