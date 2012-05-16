package com.bullshite.tvs;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.bullshite.R;

public class FragmentStreamList extends SherlockFragment implements RequestListener {
	private String mListPath = null;
	private LinearLayout mLayoutLoading = null;
	private ProgressBar mPgbLoading = null;
	private TextView mTvLoading = null;
	
	private ListView mListView = null;
	private BaseAdapter mListAdapter = null;
	
	private GetDataTask mCurTask = null;
	
	public static Fragment newInstance() {
		Fragment fg = new FragmentStreamList();
		return fg;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_layout, null);
		
		mListPath = getString(R.string.list_path);
        
        mLayoutLoading = (LinearLayout) view.findViewById(R.id.loading);
        mPgbLoading = (ProgressBar) view.findViewById(R.id.pgb_loading);
        mTvLoading = (TextView) view.findViewById(R.id.tv_loading);
        mTvLoading.setEnabled(false);
        mTvLoading.setOnClickListener(mLoadAgainListener);
        
        mListView = (ListView) view.findViewById(R.id.lv);
        mListAdapter = new ShowAdapter(getSherlockActivity(), inflater, null);
        mListView.setOnItemClickListener((OnItemClickListener) mListAdapter);
        mListView.setAdapter(mListAdapter);
        
        initial();
		return view;
	}
	
    private void initial() {
    	mCurTask = new GetDataTask(mListPath, this);
    	mCurTask.execute();
    }  
    
    private OnClickListener mLoadAgainListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mTvLoading.setEnabled(false);
			initial();
		}
	};
    
	private class GetDataTask extends AsyncTask<Void, Void, List<ShowInfo>> implements InterruptTask {
		private String mUrl = null;
		private RequestListener mListener = null;
		
		public GetDataTask(String url, RequestListener listener) {
			this.mUrl = url;
			this.mListener = listener;
		}

		@Override
		public void interrupt() {
			cancel(true);
		}

		@Override
		protected void onPreExecute() {
			mListener.OnGetDataBegin();
		}

		@Override
		protected List<ShowInfo> doInBackground(Void... params) {
			boolean isContinue = true;
	    	List<ShowInfo> list = new ArrayList<ShowInfo>();
	    	URL urlPath = null;
			try {
				urlPath = new URL(mUrl);
			} catch (MalformedURLException e1) {
				mListener.OnGetDataException(e1);
				e1.printStackTrace();
				isContinue = false;
			}
	    	
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = null;
			
			try {
				db = dbf.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				mListener.OnGetDataException(e);
				e.printStackTrace();
				isContinue = false;
			}
			Document doc = null;
			
			try {
				doc = db.parse(new InputSource(urlPath.openStream()));
			} catch (SAXException e) {
				e.printStackTrace();
				isContinue = false;
				mListener.OnGetDataException(e);
			} catch (IOException e) {
				mListener.OnGetDataException(e);
				e.printStackTrace();
				isContinue = false;
			}
			
			if(isContinue) {
				Element root = (Element) doc.getDocumentElement();
				
				NodeList itemList = root.getElementsByTagName("item");
				
				for(int i = 0 ; i < itemList.getLength(); i++){
					ShowInfo info = new ShowInfo();
					Element item = (Element) itemList.item(i);
					
					String title = item.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
					info.setTitle(title);
					
					String url = item.getElementsByTagName("url").item(0).getFirstChild().getNodeValue();
					info.setUrl(url);
					list.add(info);
				}
			}
			return list;
		}
		
		@Override
		protected void onPostExecute(List<ShowInfo> result) {
			mListener.OnGetDataComplete(result);
		}
	}

	@Override
	public void OnGetDataBegin() {
		if(mLayoutLoading != null) {
			mLayoutLoading.setVisibility(View.VISIBLE);
			mPgbLoading.setVisibility(View.VISIBLE);
			mTvLoading.setText(getString(R.string.laoding_text));
		}
	}

	@Override
	public void OnGetDataChange() {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void OnGetDataComplete(Object response) {
		if(response != null) {
			mLayoutLoading.setVisibility(View.GONE);
			((ShowAdapter)mListAdapter).setList((List<ShowInfo>)response);
		} else {
			mLayoutLoading.setVisibility(View.VISIBLE);
			mPgbLoading.setVisibility(View.GONE);
			mTvLoading.setText(getString(R.string.no_data));
			mTvLoading.setEnabled(false);
		}
	}

	@Override
	public void OnGetDataException(Exception e) {
		if(mLayoutLoading != null) {
			mLayoutLoading.setVisibility(View.VISIBLE);
			mPgbLoading.setVisibility(View.GONE);
			mTvLoading.setText(getString(R.string.load_error));
			mTvLoading.setEnabled(true);
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		interruptTask();
	}

	private void interruptTask() {
		if(mCurTask != null) {
			mCurTask.interrupt();
		}
	}
}
