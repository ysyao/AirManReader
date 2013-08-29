package com.jimmy.rssreader.contentprovider;

import android.net.Uri;
import android.provider.BaseColumns;

public class RSSContact {

	interface RSSInfoColumn {
		String INFO_ID = "_id";
		String RES_ID = "resource_id";
		String TITLE = "title";
		String LINK = "link";
		String PUB_DATE = "pub_date";
	}
	
	interface SourcesColumn {
		String SRC_ID = "_id";
		String SRC_NAME = "name";
		String SRC_ADDR = "address";
	}
	
	public static final String CONTENT_AUTHORITY = "com.jimmy.rssreader";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
	
	public static final String PATH_RSSINFO = "rssinfos";
	public static final String PATH_SOURCES = "sources";
	
	public static class RSSInfo implements RSSInfoColumn,BaseColumns {
		public static final Uri CONTENT_URI =
				BASE_CONTENT_URI.buildUpon().appendPath(PATH_RSSINFO).build();
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.jimmy.rssreader.rssinfo";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.jimmy.rssreader.rssinfo";
		
		public static final Uri buildRSSInfoUri (String info_id) {
			return CONTENT_URI.buildUpon().appendPath(info_id).build();
		}
	}
	
	public static class Sources implements SourcesColumn,BaseColumns {
		public static final Uri CONTENT_URI =
				BASE_CONTENT_URI.buildUpon().appendPath(PATH_SOURCES).build();
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.jimmy.rssreader.sources";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.jimmy.rssreader.sources";
		
		public static final Uri buildSrcUri (String src_id) {
			return CONTENT_URI.buildUpon().appendPath(src_id).build();
		}
	}
}
