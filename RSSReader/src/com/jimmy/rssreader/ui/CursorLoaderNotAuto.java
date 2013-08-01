package com.jimmy.rssreader.ui;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

public class CursorLoaderNotAuto extends CursorLoader {

	@Override
	public void onContentChanged() {
		
	}

	public CursorLoaderNotAuto(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		super(context, uri, projection, selection, selectionArgs, sortOrder);
		// TODO Auto-generated constructor stub
	}
	
}
