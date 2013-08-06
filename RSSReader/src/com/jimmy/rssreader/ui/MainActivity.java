package com.jimmy.rssreader.ui;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.jimmy.rssreader.R;
import com.jimmy.rssreader.contentprovider.RSSContact.RSSInfo;
import com.jimmy.rssreader.ui.MyListFragment.OnItemSelected;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.EditText;

public class MainActivity extends SherlockFragmentActivity implements
		OnItemSelected {
	private static final String TAG = "MainActivity";
	private static final int THEME = com.actionbarsherlock.R.style.Theme_Sherlock;
	MyListFragment mMyListFragment;

	@Override
	protected void onCreate(Bundle arg0) {
		Log.d(TAG,"Method:onCreate");
		super.onCreate(arg0);
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.news_articles);
		

		if (findViewById(R.id.fragment_container) != null) {
			mMyListFragment = new MyListFragment();
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.add(R.id.fragment_container, mMyListFragment);
			fragmentTransaction.commit();
		}
	}

	@Override
	public void onItemSelected(int position) {
		Log.d(TAG,"Method:onItemSelected");
		
		ArticleFragment articleFragment = (ArticleFragment) getSupportFragmentManager()
				.findFragmentById(R.id.article_fragment);

		if (articleFragment != null) {
			articleFragment.updateArticleView(position);
		} else {
			ArticleFragment newFragment = new ArticleFragment();
			// Pass the position to object ArticleFragment
			Bundle arg = new Bundle();
			arg.putInt(ArticleFragment.ARG_POSITION, position);
			newFragment.setArguments(arg);

			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.replace(R.id.fragment_container, newFragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		boolean isLight = MainActivity.THEME == R.style.Theme_Sherlock_Light;
		menu.add("Search")
			.setIcon(isLight ? R.drawable.ic_search_inverse : R.drawable.ic_search)
			.setActionView(R.layout.collapsible_edittext)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		
		SubMenu sub = menu.addSubMenu("Sources");
		sub.add(0, 1, 1, "Эјвз");
		sub.add(0, 2, 2, "SINA");
		sub.add(0, 3, 3, "SOHO");
		sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 1:
			replaceMyListFragment(getString(R.string.WANGYI_URI));
			break;
		case 2:
			replaceMyListFragment(getString(R.string.SINA_URI));
			break;
		case 3:
			replaceMyListFragment(getString(R.string.SOUHU_URI));
			break;
		}
		return true;
	}

	public void replaceMyListFragment(String uri) {
		int fragmentId = 0;
		
		SharedPreferences sharedPreferences = getSharedPreferences(
				getString(R.string.hold_container), Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("url", uri);
		editor.commit();

		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		mMyListFragment = (MyListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.headlines_fragment);
		MyListFragment newMyListFragment = new MyListFragment();

		if (mMyListFragment != null) {
			//Restart the mMyListFragment
			fragmentId = R.id.headlines_fragment;
		} else {
			fragmentId = R.id.fragment_container;
		}
		
		fragmentTransaction.replace(fragmentId, newMyListFragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}
}