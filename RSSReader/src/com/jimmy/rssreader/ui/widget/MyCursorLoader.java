package com.jimmy.rssreader.ui.widget;

import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.jimmy.rssreader.contentprovider.RSSInfoDatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;

public class MyCursorLoader extends AsyncTaskLoader<Cursor> {
	private Context context;
	public MyCursorLoader(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public Cursor loadInBackground() {
		// TODO Auto-generated method stub
		RSSInfoDatabase dbHelper = new RSSInfoDatabase(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		return null;
	}

}
