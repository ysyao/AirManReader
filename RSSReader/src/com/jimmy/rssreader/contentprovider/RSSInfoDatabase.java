package com.jimmy.rssreader.contentprovider;

import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfoColumn;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.SyncStateContract.Columns;

public class RSSInfoDatabase extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "rssinfo.db";
	private static final int DATABASE_VERSION = 1;
	
	interface Tables {
		String RSSINFOS = "rssinfos";
	}
	
	public RSSInfoDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE " + Tables.RSSINFOS + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ RSSInfoColumn.INFO_ID + " TEXT NOT NULL,"
				+ RSSInfoColumn.TITLE + " TEXT NOT NULL,"
				+ RSSInfoColumn.LINK + " TEXT NOT NULL,"
				+ RSSInfoColumn.PUB_DATE + " TEXT NOT NULL");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (oldVersion != DATABASE_VERSION) {
			db.execSQL("DROP TABLE IF EXISTS " + Tables.RSSINFOS);
			onCreate(db);
		}
	}
	
	public static void deleteDatabase(Context context) {
		context.deleteDatabase(DATABASE_NAME);
	}

}
