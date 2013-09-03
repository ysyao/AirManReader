package com.jimmy.rssreader.ui.widget;

import com.jimmy.rssreader.R;
import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyListAdapter extends CursorAdapter {
	private static final String TAG = "MyListAdapter";
	private LayoutInflater mInflater;
	private Cursor c;
	
	public MyListAdapter(Context context, Cursor c) {
		super(context, c);
		this.mInflater = LayoutInflater.from(context);
		this.c = c;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/*Log.d(TAG, "convertView is " + convertView);
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			Log.d(TAG,
					"Inside getView cursor datas"
							+ c.getPosition()
							+ ": "
							+ c.getString(c
									.getColumnIndexOrThrow(RSSInfo.TITLE))
									+ ","
									+ c.getString(c
											.getColumnIndexOrThrow(RSSInfo.PUB_DATE)));
		}*/
		ViewHolder holder;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = this.mInflater.inflate(R.layout.rss_insert_row, null);
			holder.title = (TextView)convertView.findViewById(R.id.titleTV);
			holder.date = (TextView)convertView.findViewById(R.id.pubdateTV);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		if(c != null) {
			//Cursor的position是从0开始的
			Log.d(TAG, "Adapter position is " + position);
			c.moveToPosition(position);
			holder.title.setText(c.getString(c.getColumnIndexOrThrow(RSSInfo.TITLE)));
			holder.date.setText(c.getString(c.getColumnIndexOrThrow(RSSInfo.PUB_DATE)));
		}
		
		return convertView;
	}

	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	static class ViewHolder {
		TextView title;
		TextView date;
	}
}
