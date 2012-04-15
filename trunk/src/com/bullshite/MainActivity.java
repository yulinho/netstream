package com.bullshite;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {
	private TextView mTvContent = null;
	private String result = null;
	
	private static String URL = "http://code.google.com/p/daroon-player/source/browse/trunk/stream_list_1.1.0_cn.xml";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        try {
			result = getHtml(URL, "UTF-8");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    private String getHtml(String url, String encoding) throws MalformedURLException {
    	URL path = new URL(url);
    	return null;
    }
}