package com.jimmy.rssreader.ui;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.jimmy.rssreader.R;
import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.jimmy.rssreader.ui.widget.MyWebView;

import android.app.ActionBar;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class ArticleFragment extends SherlockFragment {

	private static final String TAG = "ArticleFragment";
	public static final String ARG_POSITION = "position";
	int mCurrentPosition = -1;
	ActionBar mActionBar;
	WebView mArticleLink;

	public ArticleFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "Method:onCreateView");
		if (savedInstanceState != null) {
			mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
		}

		View view = inflater.inflate(R.layout.article_view, container, false);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onStart() {
		Log.d(TAG, "Method:onStart");
		super.onStart();
		Bundle arg = getArguments();
		if (arg != null) {
			updateArticleView(arg.getInt(ARG_POSITION));
		} else if (mCurrentPosition != -1) {
			updateArticleView(mCurrentPosition);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG, "Method in the ArticleFragment:onCreateOptionsMenu");
		super.onCreateOptionsMenu(menu, inflater);

		menu.add("BACK")
				.setIcon(R.drawable.abs__ic_cab_done_holo_light)
				.setActionView(R.layout.article_actionbar_textview)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		((MainActivity) getActivity()).getViewPager().setCurrentItem(
				MainActivity.MYLIST_FRAGMENT_POSITION);
		return super.onOptionsItemSelected(item);
	}

	public void updateArticleView(int position) {
		Log.d(TAG, "Method:updateArticleView,position is " + position);
		String link = "";
		String[] projection = { RSSInfo.LINK };
		String[] selectionArgs = { Integer.toString(position) };

		Cursor cursor = getActivity().getContentResolver().query(
				RSSInfo.CONTENT_URI, projection, RSSInfo._ID + "=?",
				selectionArgs, null);

		if (cursor != null && cursor.moveToFirst()) {
			Log.d(TAG, "Method:updateArticleView,query the results.");
			link = cursor.getString(cursor.getColumnIndexOrThrow(RSSInfo.LINK));
		}
		mArticleLink = (WebView) getActivity().findViewById(R.id.article);
		mArticleLink.setWebViewClient(new MyWebViewClient());
		WebSettings setting = mArticleLink.getSettings();
		setting.setSupportZoom(true);
		setting.setBuiltInZoomControls(true);
		mArticleLink.loadUrl(link);
		Log.d(TAG, "Method:updateArticleView,link is " + link);

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

	/*
	 * private String fetchUrlFromUri(Uri uri) { // Convert the Uri to HashMap
	 * 
	 * Type typeOfHashMap = new TypeToken<HashMap<String, String>>() {
	 * }.getType(); Gson gson = new GsonBuilder().create(); HashMap<String,
	 * String> newMap = gson.fromJson(uri.toString(), typeOfHashMap);
	 * 
	 * //According to the standar,get url through uriString return
	 * newMap.get("uriString");
	 * 
	 * return null; }
	 */
	private class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return true;
		}

	}

}
