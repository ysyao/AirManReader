package com.jimmy.rssreader.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import com.jimmy.rssreader.io.model.RSSInformation;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyRSSInfoAdapter extends ArrayAdapter<RSSInformation> {
	private Context context;
	private int resource;
	private List<RSSInformation> infos;
	private LayoutInflater inflater;
	
	public MyRSSInfoAdapter(Context context, int textViewResourceId,
			List<RSSInformation> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.infos = objects;
		this.resource = textViewResourceId;
		inflater = LayoutInflater.from(context);
	}
	
	public void addObjectList (List<RSSInformation> infos) {
		this.infos.clear();
		this.infos.addAll(infos);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return infos.size();
	}



	@Override
	public RSSInformation getItem(int position) {
		// TODO Auto-generated method stub
		return infos.get(position);
	}



	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		RSSInformation info = infos.get(position);
		View view = null;
		if (convertView != null) {
			view = convertView;
		} else {
			view = new View(context);
		}
		view = inflater.inflate(resource, null);
		TextView text = (TextView)view.findViewById(android.R.id.text1);
		text.setText(info.getTitle());
		
		return view;
		
	}
}
