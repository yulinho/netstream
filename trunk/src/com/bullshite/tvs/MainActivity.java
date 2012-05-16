package com.bullshite.tvs;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.bullshite.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class MainActivity extends SherlockFragmentActivity implements TabListener {
	public static final int Theme = R.style.Theme_Sherlock_Light_DarkActionBar;
	private static final int TAB_COUNT = 3;
	
	private ViewPager mPager = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	setTheme(MainActivity.Theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mPager = (ViewPager) findViewById(R.id.view_pager);
        mPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(),TAB_COUNT));
        mPager.setOnPageChangeListener((OnPageChangeListener) mPager.getAdapter());
        
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setUpTabs();
    }
    
    private void setUpTabs() {
    	setUpStreamListTab();
    	setUpFMListTab();
    	setUpCollectListTab();
    	getSupportActionBar().setSelectedNavigationItem(0);
    }
    
    private void setUpStreamListTab() {
    	ActionBar.Tab tab = getSupportActionBar().newTab();
    	tab.setText(getString(R.string.tab_name_stream_list));
    	tab.setTabListener(this);
    	getSupportActionBar().addTab(tab);
    }
    
    private void setUpFMListTab() {
    	ActionBar.Tab tab = getSupportActionBar().newTab();
    	tab.setText(getString(R.string.tab_name_fm));
    	tab.setTabListener(this);
    	getSupportActionBar().addTab(tab);
    }
    
    private void setUpCollectListTab() {
    	ActionBar.Tab tab = getSupportActionBar().newTab();
    	tab.setText(getString(R.string.tab_name_collect));
    	tab.setTabListener(this);
    	getSupportActionBar().addTab(tab);
    }
    
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if(tab.getPosition() != mPager.getCurrentItem()) {
			mPager.setCurrentItem(tab.getPosition());
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
	
	private class MainPagerAdapter extends FragmentPagerAdapter implements OnPageChangeListener {
		private int mPagerNum = 0;

		public MainPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}
		
		public MainPagerAdapter(FragmentManager fm, int pagerNum) {
			super(fm);
			this.mPagerNum = pagerNum;
		} 

		@Override
		public Fragment getItem(int position) {
			Fragment fg = null;
			switch(position) {
			case 0:
				fg = FragmentFMList.newInstance();
				break;
				
			case 1:
				fg = FragmentFMList.newInstance();
				break;
				
			case 2:
				fg = FragmentCollectList.newInstance();
				break;
			}
			return fg;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mPagerNum;
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
			switch(position) {
			case 0:
				getSupportActionBar().setSelectedNavigationItem(0);
				break;
				
			case 1:
				getSupportActionBar().setSelectedNavigationItem(1);
				break;
				
			case 2:
				getSupportActionBar().setSelectedNavigationItem(2);
				break;
			}
		}
		
	}

}