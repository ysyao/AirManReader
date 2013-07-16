package com.jimmy.rssreader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNet {

	public CheckNet() {
		// TODO Auto-generated constructor stub
	}
	public static boolean checkNet (Context context) {
		ConnectivityManager connector = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connector != null) {
			NetworkInfo networkInfo = connector.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}
	
}
