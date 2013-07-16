package com.jimmy.rssreader;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.jimmy.rssreader.MyListFragment.OnItemSelected;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

public class MainActivity extends SherlockFragmentActivity implements
		OnItemSelected {

	public static final String WANGYI_URI_DEFAULT = "http://sports.163.com/special/00051K7F/rss_sportslq.xml";
	public static final String SINA_URI = "http://rss.sina.com.cn/sports/basketball/nba.xml";
	public static final String SOUHU_URI = "http://rss.sports.sohu.com/rss/nba.xml";

	@Override
	public void onItemSelected(int position) {
		// TODO Auto-generated method stub		
		
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
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.news_articles);

		if (findViewById(R.id.fragment_container) != null) {
			MyListFragment firstFragment = new MyListFragment();
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.add(R.id.fragment_container, firstFragment);
			fragmentTransaction.commit();
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();
		menu.add(0, 1, 1, "����");
		menu.add(0, 2, 2, "Sina");
		menu.add(0, 3, 3, "SOHO");
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 1:
			replaceMyListFragment(WANGYI_URI_DEFAULT);
			break;
		case 2:
			replaceMyListFragment(SINA_URI);
			break;
		case 3:
			replaceMyListFragment(SOUHU_URI);
			break;
		}
		return true;
	}

	public void replaceMyListFragment(String uri) {
		SharedPreferences sharedPreferences = getSharedPreferences(
				MyListFragment.STORE_PLACE, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("url", uri);
		editor.commit();

		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		MyListFragment myListFragment = (MyListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.headlines_fragment);
		MyListFragment newMyListFragment = new MyListFragment();

		if (myListFragment != null) {
			
			//Restart the listfragment
			myListFragment.fetchDataAndUpdateShpf();
		} else {
			fragmentTransaction.replace(R.id.fragment_container,
					newMyListFragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}
	}

}