package com.bullshite.tvs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class FragmentCollectList extends SherlockFragment {
	public static Fragment newInstance() {
		Fragment fg = new FragmentCollectList();
		return fg;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		TextView tv = new TextView(getSherlockActivity());
		tv.setText("this is my list");
		return tv;
	}
}
