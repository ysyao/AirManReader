package com.jimmy.rssreader.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfoColumn;
import com.jimmy.rssreader.contentprovider.RSSInfoDatabase.Tables;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class RSSInfoProvider extends ContentProvider {

	private static final String TAG = "ContentProvider";
	private RSSInfoDatabase mOpenHelper;
	private static final UriMatcher sUriMatcher = builderUriMatcher();

	private static final int RSSINFOS = 1;
	private static final int RSSINFOS_ID = 2;

	private static UriMatcher builderUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = RSSContact.CONTENT_AUTHORITY;

		matcher.addURI(authority, RSSContact.PATH_RSSINFO, RSSINFOS);
		matcher.addURI(authority, RSSContact.PATH_RSSINFO + "/*", RSSINFOS_ID);

		return matcher;
	}

	private void deleteDatabase() {
		mOpenHelper.close();
		Context context = getContext();
		RSSInfoDatabase.deleteDatabase(context);
		mOpenHelper = new RSSInfoDatabase(getContext());
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		int match = sUriMatcher.match(uri);
		switch (match) {
		case RSSINFOS:
			return RSSInfo.CONTENT_TYPE;
		case RSSINFOS_ID:
			return RSSInfo.CONTENT_ITEM_TYPE;
		default:
			throw new UnsupportedOperationException("Unknow URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		Log.d(TAG, "ContentProvider:Insert()--------");
		int uriType = sUriMatcher.match(uri);
		SQLiteDatabase database = mOpenHelper.getWritableDatabase();
		String id = "0";

		switch (uriType) {
		case RSSINFOS:
			database.insert(Tables.RSSINFOS, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unkown URI" + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return RSSInfo.buildRSSInfoUri(id);
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
		int uriType = sUriMatcher.match(uri);
		SQLiteDatabase database = mOpenHelper.getReadableDatabase();
		SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
		sqLiteQueryBuilder.setTables(Tables.RSSINFOS);
		checkColumns(projection);

		switch (uriType) {
		case RSSINFOS:
			break;
		case RSSINFOS_ID:
			sqLiteQueryBuilder.appendWhere(RSSInfoColumn.INFO_ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown uri " + uri);
		}

		Cursor cursor = sqLiteQueryBuilder.query(database, projection,
				selection, selectionArgs, null, null, sortOrder, null);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		int uriType = sUriMatcher.match(uri);
		SQLiteDatabase database = mOpenHelper.getWritableDatabase();
		int updateRows = 0;
		switch (uriType) {
		case RSSINFOS:
			updateRows = database.update(Tables.RSSINFOS, values, selection,
					selectionArgs);
			break;
		case RSSINFOS_ID:
			if (TextUtils.isEmpty(selection)) {
				updateRows = database.update(Tables.RSSINFOS, values,
						RSSInfoColumn.INFO_ID + "=" + uri.getLastPathSegment(),
						null);
			} else {
				updateRows = database.update(
						Tables.RSSINFOS,
						values,
						selection + " and " + RSSInfoColumn.INFO_ID + "="
								+ uri.getLastPathSegment(), selectionArgs);

			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return updateRows;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		int uriType = sUriMatcher.match(uri);
		SQLiteDatabase database = mOpenHelper.getWritableDatabase();
		int deleteRows = 0;

		switch (uriType) {
		case RSSINFOS:
			deleteRows = database.delete(Tables.RSSINFOS, selection,
					selectionArgs);
			break;
		case RSSINFOS_ID:
			if (TextUtils.isEmpty(selection)) {
				deleteRows = database.delete(Tables.RSSINFOS,
						RSSInfoColumn.INFO_ID + "=" + uri.getLastPathSegment(),
						selectionArgs);
			} else {
				deleteRows = database.delete(
						Tables.RSSINFOS,
						selection + " and " + RSSInfoColumn.INFO_ID + "="
								+ uri.getLastPathSegment(), selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return deleteRows;
	}

	public void checkColumns(String[] projection) {
		String[] available = { RSSInfo.INFO_ID, RSSInfo.TITLE, RSSInfo.LINK,
				RSSInfo.PUB_DATE };

		if (projection != null) {
			HashSet<String> requestColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));

			if (!availableColumns.containsAll(requestColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}
}
