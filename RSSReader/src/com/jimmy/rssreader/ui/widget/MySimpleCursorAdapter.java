package com.jimmy.rssreader.ui.widget;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;

public class MySimpleCursorAdapter extends SimpleCursorAdapter {

	

	public MySimpleCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void changeCursorAndColumns(Cursor c, String[] from, int[] to) {
		// TODO Auto-generated method stub
		super.changeCursorAndColumns(c, from, to);
	}
}
