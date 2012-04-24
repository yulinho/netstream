package com.bullshite;

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

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements RequestListener {
	
	private String mListPath = null;
	private LinearLayout mLayoutLoading = null;
	private ProgressBar mPgbLoading = null;
	private TextView mTvLoading = null;
	
	private ListView mListView = null;
	private BaseAdapter mListAdapter = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mListPath = getString(R.string.list_path);
        
        mLayoutLoading = (LinearLayout) findViewById(R.id.loading);
        mPgbLoading = (ProgressBar) findViewById(R.id.pgb_loading);
        mTvLoading = (TextView) findViewById(R.id.tv_loading);
        mTvLoading.setEnabled(false);
        mTvLoading.setOnClickListener(mLoadAgainListener);
        
        mListView = (ListView) findViewById(R.id.lv);
        mListAdapter = new ShowAdapter(this, LayoutInflater.from(this), null);
        mListView.setOnItemClickListener((OnItemClickListener) mListAdapter);
        mListView.setAdapter(mListAdapter);
        
        initial();
    }
    
    private void initial() {
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				readXMLFromInternet(mListPath,MainActivity.this);
			}
		}).start();
    }  
    
    private OnClickListener mLoadAgainListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mTvLoading.setEnabled(false);
			initial();
		}
	};
    
    private void readXMLFromInternet(String urlStr,RequestListener listener) {
    	boolean isContinue = true;
    	List<ShowInfo> list = new ArrayList<ShowInfo>();
    	URL urlPath = null;
		try {
			urlPath = new URL(urlStr);
		} catch (MalformedURLException e1) {
			listener.OnGetDataException(e1);
			e1.printStackTrace();
			isContinue = false;
		}
    	
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			listener.OnGetDataException(e);
			e.printStackTrace();
			isContinue = false;
		}
		Document doc = null;
		
		try {
			doc = db.parse(new InputSource(urlPath.openStream()));
		} catch (SAXException e) {
			e.printStackTrace();
			isContinue = false;
			listener.OnGetDataException(e);
		} catch (IOException e) {
			listener.OnGetDataException(e);
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
			listener.OnGetDataComplete(list);
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
		
	}

	@Override
	public void OnGetDataComplete(final Object response) {
		if(response != null) {
			runOnUiThread(new Runnable() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void run() {
					mLayoutLoading.setVisibility(View.GONE);
					((ShowAdapter)mListAdapter).setList((List<ShowInfo>)response);
				}
			});
			
		}
	}

	@Override
	public void OnGetDataException(Exception e) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(mLayoutLoading != null) {
					mLayoutLoading.setVisibility(View.VISIBLE);
					mPgbLoading.setVisibility(View.GONE);
					mTvLoading.setText(getString(R.string.load_error));
					mTvLoading.setEnabled(true);
				}
			}
		});
		
	}
    
}