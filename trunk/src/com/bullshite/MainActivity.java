package com.bullshite;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends ActivityBase implements RequestListener {
	private ListView mListView = null;
	private BaseAdapter mListAdapter = null;
	private boolean isPauseLocal = true;
	private boolean isNeedUpdateList = true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mLayoutLoading = (LinearLayout) findViewById(R.id.loading);
        mPgbLoading = (ProgressBar) findViewById(R.id.pgb_loading);
        mTvLoading = (TextView) findViewById(R.id.tv_loading);
        mIvLoadAgain = (ImageView) findViewById(R.id.iv_load_again);
        
        mListView = (ListView) findViewById(R.id.lv);
        mListAdapter = new ShowAdapter(this, LayoutInflater.from(this), null);
        mListView.setOnItemClickListener((OnItemClickListener) mListAdapter);
        mListView.setAdapter(mListAdapter);
        
        createFolderAndFile();
        initial();
    }
    
    private void createFolderAndFile() {
    	if(isHasSDCard()) {
    		File streamFolder = new File(FILE_FOLDER_PATH);
    		if(!streamFolder.exists()) {
    			streamFolder.mkdirs();
    		} 
    	}
    	
    }
    
    private void initial() {
    	if(isHasSDCard()) {//SD卡存在
    		
    		if(isFileExisted(FILE_PATH)) {
    			isPauseLocal = true;
    			isNeedUpdateList = false;
    			((ShowAdapter)mListAdapter).setList(readXMLFromFile(FILE_PATH));
    			getXmlFromInternetWithFile(LIST_PATH, this);
    		} else {
    			isPauseLocal = true;
    			isNeedUpdateList = true;
    			getXmlFromInternetWithFile(LIST_PATH, this);
    		}
    		
    	} else {//SD卡不存在,直接从网络读取
    		isPauseLocal = false;
    		new Thread(new Runnable() {
				
				@Override
				public void run() {
					readXMLFromInternet(LIST_PATH,MainActivity.this);
				}
			}).start();
    		
    	}
    }
    
    private boolean isFileExisted(String path) {
    	File file = new File(path);
		return file.exists();
    }
    
    private void readXMLFromInternet(String urlStr,RequestListener listener) {
    	List<ShowInfo> list = new ArrayList<ShowInfo>();
    	URL urlPath = null;
		try {
			urlPath = new URL(urlStr);
		} catch (MalformedURLException e1) {
			listener.OnGetDataException(e1);
			e1.printStackTrace();
		}
    	
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			listener.OnGetDataException(e);
			e.printStackTrace();
		}
		Document doc = null;
		
		try {
			doc = db.parse(new InputSource(urlPath.openStream()));
		} catch (SAXException e) {
			e.printStackTrace();
			listener.OnGetDataException(e);
		} catch (IOException e) {
			listener.OnGetDataException(e);
			e.printStackTrace();
		}
		// 下面是解析XML的全过程
		Element root = (Element) doc.getDocumentElement();//取根节点
		
		//取item
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
    
	private void getXmlFromInternetWithFile(final String urlStr,final RequestListener listener) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				StringBuffer sb = new StringBuffer();
		    	String line = null;
		    	BufferedReader buffer = null;
		    	FileWriter fw = null;
		    	BufferedWriter bw = null;
		    	
		    	URL url;
				try {
					url = new URL(urlStr);
					HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
			    	InputStream inputStream = httpUrlConnection.getInputStream();
			    	buffer = new BufferedReader(new InputStreamReader(inputStream));
			    	fw = new FileWriter(FILE_PATH, false);
			    	bw = new BufferedWriter(fw);
			    	listener.OnGetDataBegin();
			    	
			    	while((line = buffer.readLine()) != null) {
			    		sb.append(line);
			    	}
			    	bw.write(sb.toString());
		    		bw.newLine();
		    		bw.flush();
			    	bw.close();
			    	fw.close();
			    	listener.OnGetDataComplete(sb.toString());
				} catch (MalformedURLException e) {
					e.printStackTrace();
					listener.OnGetDataException(e);
				} catch (IOException e) {
					e.printStackTrace();
					try {
						if(bw != null && fw != null){
							bw.close();
							fw.close();
						}
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					listener.OnGetDataException(e);
				}
			}
		}).start();
		
	}
	
	protected List<ShowInfo> readXMLFromFile(String path){
		List<ShowInfo> list = new ArrayList<ShowInfo>();
		File inFile = new File(path);
		
		try {
			inFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 为解析XML作准备，创建DocumentBuilderFactory实例,指定DocumentBuilder
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = null;
		
		try {
			doc = db.parse(inFile);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 下面是解析XML的全过程
		Element root = (Element) doc.getDocumentElement();//取根节点
		
		//取item
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
		return list;
	}

	@Override
	public void OnGetDataBegin() {
		if(mLayoutLoading != null) {
			mLayoutLoading.setVisibility(View.VISIBLE);
			mPgbLoading.setVisibility(View.VISIBLE);
			mTvLoading.setText(getString(R.string.laoding_text));
			mIvLoadAgain.setVisibility(View.GONE);
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
					if(isPauseLocal && isNeedUpdateList) {
						((ShowAdapter)mListAdapter).setList(readXMLFromFile(FILE_PATH));
					} else if(!isPauseLocal) {
						((ShowAdapter)mListAdapter).setList((List<ShowInfo>)response);
					}
				}
			});
			
		}
	}

	@Override
	public void OnGetDataException(Exception e) {
		if(mLayoutLoading != null) {
			mLayoutLoading.setVisibility(View.VISIBLE);
			mPgbLoading.setVisibility(View.GONE);
			mTvLoading.setText(getString(R.string.load_error));
			mIvLoadAgain.setVisibility(View.VISIBLE);
		}
	}
    
}