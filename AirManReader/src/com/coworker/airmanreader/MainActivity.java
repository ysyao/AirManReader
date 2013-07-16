package com.coworker.airmanreader;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.coworker.airmanreader.MyListFragment.OnItemSelected;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends SherlockFragmentActivity implements OnItemSelected {
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;
	
	@Override
	public void onItemSelected(int position) {
		// TODO Auto-generated method stub

		ArticleFragment articleFragment = (ArticleFragment) getSupportFragmentManager()
				.findFragmentById(R.id.article_fragment);

		if (articleFragment != null) {
			articleFragment.updateArticleView(position);
		} else {
			ArticleFragment newFragment = new ArticleFragment();
			//Pass the position to object ArticleFragment
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
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		String uri = "";
		switch (item.getItemId()) {
		case	1:
			uri = getString(R.string.wangyi_uri);
			break;
		case	2:
			uri = getString(R.string.sina_uri);
			break;
		case	3:
			uri = getString(R.string.sohu_uri);
			break;
		}
		changeWebSite(uri);
		return true;
	}



	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuItem item1 = menu.add(0, 1, 0, "Эјвз");
		MenuItem item2 = menu.add(0, 2, 1, "SINA");
		MenuItem item3 = menu.add(0, 3, 2, "SOHU");
	}



	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setTheme(R.style.Theme_Sherlock);
		setContentView(R.layout.news_articles);
		sharedPreferences = getSharedPreferences(getString(R.string.store_place), Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();

		if (findViewById(R.id.fragment_container) != null) {
			MyListFragment firstFragment = new MyListFragment();
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.add(R.id.fragment_container, firstFragment);
			fragmentTransaction.commit();
		}
	}
	
	public void changeWebSite(String uri) {
		editor.putString(getString(R.string.store_uri), uri);
		editor.commit();
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		MyListFragment oldFragment = (MyListFragment)fragmentManager.findFragmentById(R.id.headlines_fragment);
		if (oldFragment != null) {
			oldFragment.fetchDataAndUpdateShpf();
		} else {
			MyListFragment newFragment = new MyListFragment();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.fragment_container, newFragment);
			fragmentTransaction.commit();
		}
	}

}