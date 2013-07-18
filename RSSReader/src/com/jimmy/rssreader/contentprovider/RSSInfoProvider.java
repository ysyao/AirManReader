package com.jimmy.rssreader.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class RSSInfoProvider extends ContentProvider {
	
	private RSSInfoDatabase mOpenHelper;
	private static final UriMatcher sUriMatcher = builderUriMatcher();
	
	private static final int RSSINFOS = 1;
	private static final int RSSINFOS_ID = 2;
	
	
	private static UriMatcher builderUriMatcher () {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = RSSContact.CONTENT_AUTHORITY;
		
		matcher.addURI(authority, "rssinfos", RSSINFOS);
		matcher.addURI(authority, "rssinfos/*", RSSINFOS_ID);
		
		return matcher;
	}
	
	private void deleteDatabase () {
		mOpenHelper.close();
		Context context = getContext();
		RSSInfoDatabase.deleteDatabase(context);
		mOpenHelper = new RSSInfoDatabase(getContext());
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		int match = sUriMatcher.match(uri);
		switch (match)
		{
		case	RSSINFOS:
			return "vnd.android.cursor.dir/";
		case	RSSINFOS_ID:
			return "vnd.android.cursor.item/";
		}
		
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mOpenHelper = new RSSInfoDatabase(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
