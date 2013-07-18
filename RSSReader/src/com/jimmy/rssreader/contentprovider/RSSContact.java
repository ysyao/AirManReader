package com.jimmy.rssreader.contentprovider;

import android.net.Uri;
import android.provider.BaseColumns;

public class RSSContact {

	interface RSSInfoColumn {
		String INFO_ID = "info_id";
		String TITLE = "title";
		String LINK = "link";
		String PUB_DATE = "pub_date";
	}
	
	public static final String CONTENT_AUTHORITY = "com.jimmy.myfragmentexercise";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
	
	private static final String PATH_RSSINFO = "RSSInfo";
	
	public static class RSSInfo implements RSSInfoColumn,BaseColumns {
		public static final Uri CONTENT_URI =
				BASE_CONTENT_URI.buildUpon().appendPath(PATH_RSSINFO).build();
		
		public static final Uri buildRSSInfoUri (String info_id) {
			return CONTENT_URI.buildUpon().appendPath(info_id).build();
		}
	}
}
