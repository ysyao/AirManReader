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
			mTabsAdapter.addTab(bar.newTab().setText("新闻列表"),
					MyListFragment.class, null);
			mTabsAdapter.addTab(bar.newTab().setText("新闻内容"),
					ArticleFragment.class, null);
			mTabsAdapter.addTab(bar.newTab().setText("新闻设置"),
					SettingFragment.class, null);

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
		mArticleFragment = (ArticleFragment) mTabsAdapter.getItem(mTabsAdapter
				.findTabByClassName(ArticleFragment.class));
		mArticleFragment.updateArticleView(position);
		mViewPager.setCurrentItem(mTabsAdapter
				.findTabByClassName(ArticleFragment.class));
	}

	public ViewPager getViewPager() {
		ViewPager pager = new ViewPager(this);
		pager.setId(R.id.fragment_container_viewpager);
		return pager;
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
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _clss, Bundle _args) {
				this.clss = _clss;
				this.args = _args;
			}
		}

		public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			this.mContext = activity;
			this.mActionBar = activity.getSupportActionBar();
			this.mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			Log.d(TAG, "Adding new tab " + tab.getText());
			TabInfo info = new TabInfo(clss, args);
			mTabs.add(info);
			tab.setTag(tab);
			tab.setTabListener(this);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}

		public int findTabByClassName(Class<?> clss) {
			Log.d(TAG, "findTab: class is " + clss.getName());
			int position = -1;
			for (int i = 0; i < mTabs.size(); i++) {
				TabInfo info = mTabs.get(i);
				if (info.clss.getName().equals(clss.getName())) {
					position = i;
					break;
				}
			}
			return position;
		}

		@Override
		public Fragment getItem(int position) {
			Log.d(TAG, "Method:getItem(),position is " + position);
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}

		@Override
		public int getCount() {
			Log.d(TAG, "Method:getCount()");
			return mTabs.size();
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
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
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