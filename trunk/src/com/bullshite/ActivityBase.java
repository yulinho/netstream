package com.bullshite;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ActivityBase extends Activity {
	public static final String LIST_PATH = "http://netstream.googlecode.com/svn/trunk/list/list.xml";
	public static final String FILE_FOLDER_PATH = "/mnt/sdcard/stream";
	public static final String FILE_PATH = FILE_FOLDER_PATH + "/list" + ".xml";
	
	protected LinearLayout mLayoutLoading = null;
	protected ProgressBar mPgbLoading = null;
	protected TextView mTvLoading = null;
	protected ImageView mIvLoadAgain = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	protected boolean isHasSDCard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	protected boolean isFolderEmpty(String folderPath) {
		File folder = new File(folderPath);
		File list[] = folder.listFiles();
		return !(list.length > 0);
	}
	
	protected void createXMLFile(String filePath) {
		File file = new File(filePath);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}

}
