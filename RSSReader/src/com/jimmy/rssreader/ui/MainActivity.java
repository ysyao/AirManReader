package com.jimmy.rssreader.ui;

import java.util.ArrayList;
import java.util.List;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends SherlockFragmentActivity implements
		OnItemSelected {
	private static final String TAG = "MainActivity";
	private static final int THEME = com.actionbarsherlock.R.style.Theme_Sherlock;

	private static MyListFragment mMyListFragment;
	private static ArticleFragment mArticleFragment;

	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;

	@Override
	protected void onCreate(Bundle bundle) {
		Log.d(TAG, "Method:onCreate");
		super.onCreate(bundle);
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.news_articles);
		mViewPager = (ViewPager) findViewById(R.id.fragment_container);

		if (mViewPager != null) {
			mTabsAdapter = new TabsAdapter(getSupportFragmentManager());
			mViewPager.setAdapter(mTabsAdapter);
			/*
			 * if (mMyListFragment == null) { mMyListFragment = new
			 * MyListFragment(); }
			 * 
			 * FragmentTransaction fragmentTransaction =
			 * getSupportFragmentManager() .beginTransaction();
			 * fragmentTransaction.add(R.id.fragment_container,
			 * mMyListFragment); fragmentTransaction.commit();
			 */
		} else {
			mMyListFragment = (MyListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.headlines_fragment);
			mArticleFragment = (ArticleFragment) getSupportFragmentManager()
					.findFragmentById(R.id.article_fragment);
		}
	}

	@Override
	public void onItemSelected(int position) {
		Log.d(TAG, "Method:onItemSelected");
/*
		mArticleFragment = (ArticleFragment) getSupportFragmentManager()
				.findFragmentById(R.id.article_fragment);

		if (mArticleFragment != null) {
			mArticleFragment.updateArticleView(position);
		} else {
			mArticleFragment = new ArticleFragment();
			// Pass the position to object ArticleFragment
			Bundle arg = new Bundle();
			arg.putInt(ArticleFragment.ARG_POSITION, position);
			mArticleFragment.setArguments(arg);

			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.replace(R.id.fragment_container,
					mArticleFragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}*/
		
		mArticleFragment.updateArticleView(position);
		mViewPager.setCurrentItem(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		boolean isLight = MainActivity.THEME == R.style.Theme_Sherlock_Light;
		menu.add("Search")
				.setIcon(
						isLight ? R.drawable.ic_search_inverse
								: R.drawable.ic_search)
				.setActionView(R.layout.collapsible_edittext)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		SubMenu sub = menu.addSubMenu("Sources");
		sub.add(0, 1, 1, "Эјвз");
		sub.add(0, 2, 2, "SINA");
		sub.add(0, 3, 3, "SOHO");
		sub.getItem().setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
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
			// Restart the mMyListFragment
			fragmentId = R.id.headlines_fragment;
		} else {
			fragmentId = R.id.fragment_container;
		}

		fragmentTransaction.replace(fragmentId, newMyListFragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	public static class TabsAdapter extends FragmentPagerAdapter {

		public TabsAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return (mMyListFragment = new MyListFragment());
			case 1:
				return (mArticleFragment = new ArticleFragment());
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}
	}

}