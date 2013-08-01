package com.jimmy.rssreader.ui;

import com.jimmy.rssreader.R;
import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class ArticleFragment extends Fragment {
	private static final String TAG = "ArticleFragment";
	public static final String ARG_POSITION = "position";
	int mCurrentPosition = -1;
	TextView mArticleLink;

	public ArticleFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG,"Method:onCreateView");
		if (savedInstanceState != null) {
			mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
		}

		View view = inflater.inflate(R.layout.article_view, container, false);
		return view;
	}

	@Override
	public void onStart() {
		Log.d(TAG,"Method:onStart");
		super.onStart();
		Bundle arg = getArguments();
		if (arg != null) {
			updateArticleView(arg.getInt(ARG_POSITION));
		} else if (mCurrentPosition != -1) {
			updateArticleView(mCurrentPosition);
		}
	}

	public void updateArticleView(int position) {
		Log.d(TAG,"Method:updateArticleView,position is " + position);
		String link = "";
		String[] projection = { RSSInfo.LINK };
		String[] selectionArgs = { Integer.toString(position) };

		Cursor cursor = getActivity().getContentResolver().query(
				RSSInfo.CONTENT_URI, projection, RSSInfo._ID + "=?",
				selectionArgs, null);
		
		if (cursor != null && cursor.moveToFirst()) {
			Log.d(TAG,"Method:updateArticleView,query the results." );
			link = cursor.getString(cursor.getColumnIndexOrThrow(RSSInfo.LINK));
		}
		mArticleLink = (TextView)getActivity().findViewById(R.id.article);
		mArticleLink.setText(link);
		Log.d(TAG,"Method:updateArticleView,link is " + link);		

		/*
		 * Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new
		 * UriDeserializer()).create(); java.lang.reflect.Type type = new
		 * com.google.gson.reflect.TypeToken<List<RSSInformation>>() {
		 * }.getType(); List<RSSInformation> infos = gson.fromJson(rssInfos,
		 * type); RSSInformation info = infos.get(position-1);
		 * 
		 * WebView webView = (WebView) getActivity().findViewById(R.id.article);
		 * webView.setWebViewClient(new MyWebViewClient()); WebSettings setting
		 * = webView.getSettings(); setting.setJavaScriptEnabled(true);
		 * setting.setPluginState(PluginState.ON); setting.setSupportZoom(true);
		 * setting.setBuiltInZoomControls(true);
		 * 
		 * Uri auri = info.getLink();
		 * 
		 * String url = fetchUrlFromUri(auri); Log.d(MyListFragment.TAG,
		 * "url is :" + url); if (url != null && !url.equals("")) {
		 * webView.loadUrl(url); } else { webView.loadUrl("http://localhost"); }
		 */
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt(ARG_POSITION, mCurrentPosition);
	}

	/*private String fetchUrlFromUri(Uri uri) {
		// Convert the Uri to HashMap
		
		 * Type typeOfHashMap = new TypeToken<HashMap<String, String>>() {
		 * }.getType(); Gson gson = new GsonBuilder().create(); HashMap<String,
		 * String> newMap = gson.fromJson(uri.toString(), typeOfHashMap);
		 * 
		 * //According to the standar,get url through uriString return
		 * newMap.get("uriString");
		 
		return null;
	}

	private class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			return false;
		}

	}*/

}
