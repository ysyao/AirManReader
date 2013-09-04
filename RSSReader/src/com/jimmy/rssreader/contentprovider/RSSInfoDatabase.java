package com.jimmy.rssreader.contentprovider;

import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfoColumn;
import com.jimmy.rssreader.contentprovider.RSSContact.Sources;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.SyncStateContract.Columns;

public class RSSInfoDatabase extends SQLiteOpenHelper{
	private static final String DATABASE_NAME = "rssinfo.db";
	private static final int DATABASE_VERSION = 1;

	public interface Tables {
		String RSSINFOS = "rssinfos";
		String SOURCES = "sources";
	}

	private static final String CREATE_TABLE_RSSINFOS = "CREATE TABLE "
			+ Tables.RSSINFOS + " (" + RSSInfo.INFO_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," 
			+ RSSInfo.RES_ID + " INTEGER NOT NULL,"
			+ RSSInfo.TITLE	+ " TEXT NOT NULL," 
			+ RSSInfo.LINK + " TEXT NOT NULL,"
			+ RSSInfo.PUB_DATE + " TEXT NOT NULL);";
	
	private static final String CREATE_TABLE_SOURCES = "CREATE TABLE "
			+ Tables.SOURCES + "(" + Sources.SRC_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ Sources.SRC_NAME + " TEXT NOT NULL,"
			+ Sources.SRC_ADDR + " TEXT NOT NULL);";
	
	private static final String INSERT_WANGYI = "INSERT INTO " + Tables.SOURCES
			+ "(" + Sources.SRC_NAME + "," + Sources.SRC_ADDR + ")"
			+ "VALUES('ÍøÒ×','http://sports.163.com/special/00051K7F/rss_sportslq.xml');";
	
	private static final String INSERT_SINA = "INSERT INTO " + Tables.SOURCES
			+ "(" + Sources.SRC_NAME + "," + Sources.SRC_ADDR + ")"
			+ "VALUES('SINA','http://rss.sina.com.cn/sports/basketball/nba.xml');";
	
	private static final String INSERT_SOHU = "INSERT INTO " + Tables.SOURCES
			+ "(" + Sources.SRC_NAME + "," + Sources.SRC_ADDR + ")"
			+ "VALUES('ËÑºü','http://rss.sports.sohu.com/rss/nba.xml');";
	
	public RSSInfoDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE_RSSINFOS);
		db.execSQL(CREATE_TABLE_SOURCES);
		db.execSQL(INSERT_WANGYI);
		db.execSQL(INSERT_SINA);
		db.execSQL(INSERT_SOHU);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (oldVersion != DATABASE_VERSION) {
			db.execSQL("DROP TABLE IF EXISTS " + Tables.RSSINFOS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.SOURCES);
			onCreate(db);
		}
	}

	public static void deleteDatabase(Context context) {
		context.deleteDatabase(DATABASE_NAME);
	}

}
