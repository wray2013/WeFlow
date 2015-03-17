package com.cmmobi.railwifi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.download.DownloadStatus;
import com.cmmobi.railwifi.download.SeriesItem;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;

public class SeriesAdapter extends BaseAdapter{
	private static final String TAG = "SeriesAdapter";
	private LayoutInflater inflater;
	Context context;
	List<SeriesItem> list;
	private ViewHolder holder;
	
	public SeriesAdapter(Context _context){
		context = _context;
		inflater = LayoutInflater.from(context);
		list = new ArrayList<SeriesItem>();
	}

	public void setData( List<SeriesItem> _list){
		this.list = _list;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public SeriesItem getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null) {
            convertView = inflater.inflate(
            		R.layout.list_series_item, null);
            holder = new ViewHolder();
            holder.seq =  (TextView) convertView.findViewById(R.id.seq);
            holder.rl_whole = (RelativeLayout) convertView.findViewById(R.id.rl_whole);
            holder.status = (ImageView) convertView.findViewById(R.id.status);
            ViewUtils.setSize(holder.status, 48, 32);
            ViewUtils.setSize(convertView.findViewById(R.id.stub), 112, 80);
//            ViewUtils.setSize(holder.rl_whole, 112, 80);
            
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                    android.view.ViewGroup.LayoutParams.FILL_PARENT,
                    DisplayUtil.getSize(context, 80));
            param.height = DisplayUtil.getSize(context, 80);
            convertView.setLayoutParams(param);
            convertView.setPadding(0, 0, 0, 0);
            convertView.setTag(holder);
        }else{
        	holder = (ViewHolder)convertView.getTag();
        }
		
		ViewUtils.setSize(holder.status, 48, 32);
        ViewUtils.setSize(convertView.findViewById(R.id.stub), 112, 80);
//        ViewUtils.setSize(holder.rl_whole, 112, 80);
        
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                android.view.ViewGroup.LayoutParams.FILL_PARENT,
                DisplayUtil.getSize(context, 80));
        param.height = DisplayUtil.getSize(context, 80);
        convertView.setLayoutParams(param);
        convertView.setPadding(0, 0, 0, 0);
        

		SeriesItem item = list.get(position);

		if(item!=null){
			holder.seq.setTextSize(18);
	        holder.seq.setText(item.seqStr);
        	
	        if(item.choose){
	        	holder.rl_whole.setBackgroundResource(R.drawable.rect_read_status);
	        	holder.seq.setTextColor(context.getResources().getColor(R.color.text_gray));
	        	holder.status.setVisibility(View.VISIBLE);
	        	if(item.status==DownloadStatus.FAIL.getIndex()){
		        	holder.status.setImageResource(R.drawable.icon_status_error);
		        }else if(item.status==DownloadStatus.DONE.getIndex()){
		        	holder.status.setImageResource(R.drawable.icon_status_complete);
		        }else{
		        	holder.status.setImageResource(R.drawable.icon_status_download);
		        	holder.seq.setTextSize(12);
		        	holder.seq.setTextColor(context.getResources().getColor(R.color.title_bar_text_color));
		        	holder.seq.setText(R.string.downloading);
		        }
	        }else{
	        	holder.status.setVisibility(View.INVISIBLE);
	        	holder.rl_whole.setBackgroundResource(R.drawable.rect_unread_status);
	        	holder.seq.setTextColor(context.getResources().getColor(R.color.title_bar_text_color));
	        }
	        

		}else{
			Log.v(TAG, "getView - item null , position:" + position);
		}

        

        
        
        return convertView;

	}
	
	public class ViewHolder {
		RelativeLayout rl_whole;
		TextView seq;
		ImageView status;
	}

}
