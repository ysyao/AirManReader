package com.jimmy.rssreader.ui.widget;

import com.jimmy.rssreader.contentprovider.RSSInfoDatabase;
import com.jimmy.rssreader.contentprovider.RSSInfoDatabase.Tables;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;

public class MyCursorLoader extends AsyncTaskLoader<Cursor> {
	private Context context;
	private String[] projection;
	private String selection;
	private String[] selectionArgs;
	private String orderBy;
	public MyCursorLoader(Context context,String[] projection, String selection,String[] selectionArgs, String orderBy) {
		super(context);
		this.context = context;
		this.projection = projection;
		this.selection = selection;
		this.selectionArgs = selectionArgs;
		this.orderBy = orderBy;
	}

	@Override
	public Cursor loadInBackground() {
		RSSInfoDatabase dbHelper = new RSSInfoDatabase(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(Tables.RSSINFOS, projection, selection, selectionArgs, null, null, orderBy);
		return cursor;
	}

}
