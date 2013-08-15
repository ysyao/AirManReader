package com.jimmy.rssreader.ui;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jimmy.rssreader.R;
import com.jimmy.rssreader.ui.MyListFragment.OnItemSelected;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class MainActivity extends SherlockFragmentActivity implements
		OnItemSelected {
	private static final String TAG = "MainActivity";
	public static final int THEME = com.actionbarsherlock.R.style.Theme_Sherlock;
	public static final int MYLIST_FRAGMENT_POSITION = 0;
	public static final int ARTICLE_FRAGMENT_POSITION = 1;
	public static final int SETTING_FRAGMENT_POSITION = 2;
	
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
		setContentView(R.layout.news_articles);
		mViewPager = getViewPager();
		
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
		mViewPager.setCurrentItem(ARTICLE_FRAGMENT_POSITION);
	}
	
	public ViewPager getViewPager() {
		if (null == mViewPager) {
			mViewPager = (ViewPager)findViewById(R.id.fragment_container);
		}
		return mViewPager;
	}

	/*@Override
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
	}*/

	public static class TabsAdapter extends FragmentPagerAdapter {

		public TabsAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Log.d(TAG, "Method:getItem(),position is " + position);
			switch (position) {
			case MYLIST_FRAGMENT_POSITION:
				return (mMyListFragment = new MyListFragment());
			case ARTICLE_FRAGMENT_POSITION:
				return (mArticleFragment = new ArticleFragment());
			case SETTING_FRAGMENT_POSITION:
				return (mSettingFragment = new SettingFragment());
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			Log.d(TAG, "Method:getCount()");
			return 3;
		}
	}
}