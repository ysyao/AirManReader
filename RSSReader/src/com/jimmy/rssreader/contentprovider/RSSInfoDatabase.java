package com.jimmy.rssreader.contentprovider;

import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfoColumn;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.SyncStateContract.Columns;

public class RSSInfoDatabase extends SQLiteOpenHelper{
	private static final String DATABASE_NAME = "rssinfo.db";
	private static final int DATABASE_VERSION = 1;

	interface Tables {
		String RSSINFOS = "rssinfos";
	}

	private static final String CREATE_TABLE = "CREATE TABLE "
			+ Tables.RSSINFOS + " (" + RSSInfo.INFO_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ RSSInfo.TITLE	+ " TEXT NOT NULL," 
			+ RSSInfo.LINK + " TEXT NOT NULL,"
			+ RSSInfo.PUB_DATE + " TEXT NOT NULL);";
	
	private static final String INSERT_DATA1 = "INSERT INTO " + Tables.RSSINFOS
			+ "(" + RSSInfo.TITLE + "," + RSSInfo.LINK + "," + RSSInfo.PUB_DATE
			+ ")VALUES('Kobe is the best','https://www.kobe.com','2013/7/23')";

	private static final String INSERT_DATA2 = "INSERT INTO "
			+ Tables.RSSINFOS
			+ "("
			+ RSSInfo.TITLE
			+ ","
			+ RSSInfo.LINK
			+ ","
			+ RSSInfo.PUB_DATE
			+ ")VALUES('James is the best','https://www.James.com','2013/7/23')";

	private static final String INSERT_DATA3 = "INSERT INTO "
			+ Tables.RSSINFOS
			+ "("
			+ RSSInfo.TITLE
			+ ","
			+ RSSInfo.LINK
			+ ","
			+ RSSInfo.PUB_DATE
			+ ")VALUES('Jordan is the best','https://www.Jordan.com','2013/7/23')";

	public RSSInfoDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE);
		db.execSQL(INSERT_DATA1);
		db.execSQL(INSERT_DATA2);
		db.execSQL(INSERT_DATA3);
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
