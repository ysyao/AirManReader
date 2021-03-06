package com.coworker.airmanreader;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ArticleFragment extends Fragment {
	public static final String ARG_POSITION = "position";
	int mCurrentPosition = -1;

	public ArticleFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (savedInstanceState != null) {
			mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
		}

		View view = inflater.inflate(R.layout.article_view, container, false);
		return view;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Bundle arg = getArguments();
		if (arg != null) {
			updateArticleView(arg.getInt(ARG_POSITION));
		} else if (mCurrentPosition != -1) {
			updateArticleView(mCurrentPosition);
		}
	}

	public void updateArticleView(int position) {
		SharedPreferences sharedPreferences = getActivity()
				.getSharedPreferences(getString(R.string.store_place),
						Context.MODE_PRIVATE);
		String rssInfos = sharedPreferences.getString(
				getString(R.string.store_info), "");

		Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class,
				new UriDeserializer()).create();
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<RSSInformation>>() {
		}.getType();
		List<RSSInformation> infos = gson.fromJson(rssInfos, type);
		RSSInformation info = infos.get(position);
		
		WebView webView = (WebView) getActivity().findViewById(R.id.article);
		Uri auri = info.getLink();

		String url = fetchUrlFromUri(auri);
		Log.d(MyListFragment.TAG, "url is :" + url);
		if (url != null && !url.equals("")) {
			webView.setWebViewClient(new MyWebViewClient());			
			webView.getSettings().setJavaScriptEnabled(true);
			webView.loadUrl(url);
		} else {
			webView.loadUrl("http://localhost");
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt(ARG_POSITION, mCurrentPosition);
	}
	
	private String fetchUrlFromUri (Uri uri) {
		//Convert the Uri to HashMap
		Type typeOfHashMap = new TypeToken<HashMap<String, String>>() {
		}.getType();
		Gson gson = new GsonBuilder().create();
		HashMap<String, String> newMap = gson.fromJson(uri.toString(),
				typeOfHashMap);
		
		//According to the standar,get url through uriString
		return newMap.get("uriString");
	}
	
	private class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
	
}
