package com.jimmy.rssreader.ui;

import java.util.ArrayList;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jimmy.rssreader.R;
import com.jimmy.rssreader.ui.MyListFragment.OnItemSelected;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

public class MainActivity extends SherlockFragmentActivity implements
		OnItemSelected {
	private static final String TAG = "MainActivity";
	public static final int THEME = com.actionbarsherlock.R.style.Theme_Sherlock;

	private static MyListFragment mMyListFragment;
	private static ArticleFragment mArticleFragment;
	private static SettingFragment mSettingFragment;

	private ViewPager mViewPager;
	private TabsAdapter mTabsAdapter;

	@Override
	protected void onCreate(Bundle bundle) {
		Log.d(TAG, "Method:onCreate");
		super.onCreate(bundle);
		setTheme(R.style.Theme_Sherlock_Light);
		mViewPager = getViewPager();
		setContentView(mViewPager);

		mTabsAdapter = getTabsAdapter(mViewPager);
		final ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

		// 在actionbar上面添加tab
		if (mViewPager != null && mTabsAdapter != null) {
			mTabsAdapter.addTab(bar.newTab().setText("新闻列表"), null);
			mTabsAdapter.addTab(bar.newTab().setText("新闻内容"), null);
			mTabsAdapter.addTab(bar.newTab().setText("新闻设置"), null);
		}

		/*
		 * if (mMyListFragment == null) { mMyListFragment = new
		 * MyListFragment(); }
		 * 
		 * FragmentTransaction fragmentTransaction = getSupportFragmentManager()
		 * .beginTransaction(); fragmentTransaction.add(R.id.fragment_container,
		 * mMyListFragment); fragmentTransaction.commit();
		 */
	}

	@Override
	public void onItemSelected(int position) {
		Log.d(TAG, "Method:onItemSelected");
		/*
		 * mArticleFragment = (ArticleFragment) getSupportFragmentManager()
		 * .findFragmentById(R.id.article_fragment);
		 * 
		 * if (mArticleFragment != null) {
		 * mArticleFragment.updateArticleView(position); } else {
		 * mArticleFragment = new ArticleFragment(); // Pass the position to
		 * object ArticleFragment Bundle arg = new Bundle();
		 * arg.putInt(ArticleFragment.ARG_POSITION, position);
		 * mArticleFragment.setArguments(arg);
		 * 
		 * FragmentTransaction fragmentTransaction = getSupportFragmentManager()
		 * .beginTransaction();
		 * fragmentTransaction.replace(R.id.fragment_container,
		 * mArticleFragment); fragmentTransaction.addToBackStack(null);
		 * fragmentTransaction.commit(); }
		 */
		mArticleFragment.updateArticleView(position);
		mViewPager.setCurrentItem(TabsAdapter.ARTICLE_FRAGMENT_POSITION);
	}

	public ViewPager getViewPager() {
		if(mViewPager == null || mViewPager.equals("")) {
			mViewPager = new ViewPager(this);
			mViewPager.setId(R.id.fragment_container_viewpager);
		}
		return mViewPager;
	}

	public TabsAdapter getTabsAdapter(ViewPager pager) {
		if (mTabsAdapter == null) {
			mTabsAdapter = new TabsAdapter(this, pager);
		}
		return mTabsAdapter;
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // TODO
	 * Auto-generated method stub boolean isLight = MainActivity.THEME ==
	 * R.style.Theme_Sherlock_Light; menu.add("Search") .setIcon( isLight ?
	 * R.drawable.ic_search_inverse : R.drawable.ic_search)
	 * .setActionView(R.layout.collapsible_edittext) .setShowAsAction(
	 * MenuItem.SHOW_AS_ACTION_ALWAYS |
	 * MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
	 * 
	 * SubMenu sub = menu.addSubMenu("Sources"); sub.add(0, 1, 1, "网易");
	 * sub.add(0, 2, 2, "SINA"); sub.add(0, 3, 3, "SOHO");
	 * sub.getItem().setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM |
	 * MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	 * 
	 * 
	 * return true; }
	 * 
	 * @Override public boolean onOptionsItemSelected(
	 * com.actionbarsherlock.view.MenuItem item) { // TODO Auto-generated method
	 * stub switch (item.getItemId()) { case 1:
	 * replaceMyListFragment(getString(R.string.WANGYI_URI)); break; case 2:
	 * replaceMyListFragment(getString(R.string.SINA_URI)); break; case 3:
	 * replaceMyListFragment(getString(R.string.SOUHU_URI)); break; } return
	 * true; }
	 * 
	 * public void replaceMyListFragment(String uri) { int fragmentId = 0;
	 * 
	 * SharedPreferences sharedPreferences = getSharedPreferences(
	 * getString(R.string.hold_container), Context.MODE_PRIVATE); Editor editor
	 * = sharedPreferences.edit(); editor.putString("url", uri);
	 * editor.commit();
	 * 
	 * FragmentTransaction fragmentTransaction = getSupportFragmentManager()
	 * .beginTransaction(); mMyListFragment = (MyListFragment)
	 * getSupportFragmentManager() .findFragmentById(R.id.headlines_fragment);
	 * MyListFragment newMyListFragment = new MyListFragment();
	 * 
	 * if (mMyListFragment != null) { // Restart the mMyListFragment fragmentId
	 * = R.id.headlines_fragment; } else { fragmentId = R.id.fragment_container;
	 * }
	 * 
	 * fragmentTransaction.replace(fragmentId, newMyListFragment);
	 * fragmentTransaction.addToBackStack(null); fragmentTransaction.commit(); }
	 */

	public static class TabsAdapter extends FragmentPagerAdapter implements
			TabListener, OnPageChangeListener {
		private final Context mContext;
		private final ViewPager mViewPager;
		private final ActionBar mActionBar;
		public static final int LIST_FRAGMENT_POSITION = 0;
		public static final int ARTICLE_FRAGMENT_POSITION = 1;
		public static final int SETTING_FRAGMENT_POSITION = 2;

		public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			this.mContext = activity;
			this.mActionBar = activity.getSupportActionBar();
			this.mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, Bundle args) {
			Log.d(TAG, "Adding new tab " + tab.getText());
			tab.setTag(tab);
			tab.setTabListener(this);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}


		@Override
		public Fragment getItem(int position) {
			Log.d(TAG, "Method:getItem(),position is " + position);
			switch (position) {
			case	LIST_FRAGMENT_POSITION:
				if(mMyListFragment == null) {
					mMyListFragment = new MyListFragment();
				}
				return mMyListFragment;
			case	ARTICLE_FRAGMENT_POSITION:
				if(mArticleFragment == null) {
					mArticleFragment = new ArticleFragment();
				}
				return mArticleFragment;
			case	SETTING_FRAGMENT_POSITION:
				if(mSettingFragment == null) {
					mSettingFragment = new SettingFragment();
				}
				return mSettingFragment;
			default:
				return (mMyListFragment = new MyListFragment());
			}
		}

		@Override
		public int getCount() {
			Log.d(TAG, "Method:getCount()");
			return 3;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			mActionBar.setSelectedNavigationItem(position);
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			int position = tab.getPosition();
			mViewPager.setCurrentItem(position);
			this.getItem(position);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}
	}
}