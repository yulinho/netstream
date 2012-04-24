package com.bullshite;

import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ShowAdapter extends BaseAdapter implements OnItemClickListener {
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private List<ShowInfo> mList = null;
	
	public ShowAdapter(Context context, LayoutInflater inflater, List<ShowInfo> list) {
		this.mContext = context;
		this.mInflater = inflater;
		this.mList = list;
	}

	@Override
	public int getCount() {
		return (mList == null) ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return (mList == null) ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, null);
			holder = new Holder();
			
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		ShowInfo info = mList.get(position);
		
		holder.tvTitle.setText(info.getTitle());
		return convertView;
	}
	
	public void setList(List<ShowInfo> list) {
		this.mList = list;
		notifyDataSetChanged();
	}
	
	class Holder {
		TextView tvTitle = null;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		try{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri uri = Uri.parse(mList.get(position).getUrl());
			intent.setDataAndType(uri, "audio");
			mContext.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(mContext, mContext.getString(R.string.no_software_to_play), Toast.LENGTH_SHORT).show();
		}
		
	}

}
