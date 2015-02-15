package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.utils.ZDateUtils;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.utils.DisplayUtil;
import com.cmmobi.looklook.common.view.ContentThumbnailView;


public class MapNearbyListAdapter extends ArrayAdapter<MyDiary> {
	
	private static final String TAG = "MapNearbyListAdapter";
	private Context context;
	private ArrayList<MyDiary>  items;
	private LayoutInflater inflater ;
	protected DisplayMetrics dm = new DisplayMetrics();
	private Handler handler;
	RelativeLayout rl;
	
	public MapNearbyListAdapter(Context context,  Handler handler, int resource,
			int textViewResourceId, ArrayList<MyDiary> items, RelativeLayout layout) {
		super(context, resource, textViewResourceId, items);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.items = items;
		this.inflater = LayoutInflater.from (context);
		this.handler = handler;
		this.rl = layout;
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;
		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_list_diary_nearby, null);
			holder = new ViewHolder();
			holder.diaryCover = (ContentThumbnailView) convertView.findViewById(R.id.thumbnail);
			holder.position = (TextView) convertView.findViewById(R.id.tv_map_position);
			holder.time = (TextView) convertView.findViewById(R.id.tv_map_time);
			holder.llcontent = convertView.findViewById(R.id.ll_content);
			
			convertView.setTag(R.string.view_tag_key, holder);
		} else {
            holder = (ViewHolder) convertView.getTag(R.string.view_tag_key);
        }

		LayoutParams lp = holder.diaryCover.getLayoutParams();
		lp.width = dm.widthPixels * 200 / 640;
		lp.height = lp.width;
		holder.diaryCover.setLayoutParams(lp);
		
		int postxtSize = DisplayUtil.px2sp(context, dm.widthPixels * 25 / 640);
		int timetxtSize = DisplayUtil.px2sp(context, dm.widthPixels * 25 / 640);
		holder.position.setTextSize(postxtSize);
		holder.time.setTextSize(timetxtSize);
		
		android.widget.RelativeLayout.LayoutParams contentParams=(android.widget.RelativeLayout.LayoutParams) holder.llcontent.getLayoutParams();
		contentParams.topMargin = dm.widthPixels * 72 / 640;
		holder.llcontent.setLayoutParams(contentParams);
		
		MyDiary item = getItem(position);
		view = convertView;
		view.setTag(item);
		
		if (item != null) {
//			holder.diaryCover.setTag(item);
			((ContentThumbnailView) holder.diaryCover).setContentDiaries("0", item);
			holder.position.setText(item.position_view);
			holder.time.setText(ZDateUtils.getFormatDateByMilli(item.shoottime/*updatetimemilli*/));// "今天17:08");
		}
		return view;
	}
	
    static class ViewHolder {
    	View llcontent;
		TextView time;
		TextView position;
		ContentThumbnailView diaryCover;
    }

}
