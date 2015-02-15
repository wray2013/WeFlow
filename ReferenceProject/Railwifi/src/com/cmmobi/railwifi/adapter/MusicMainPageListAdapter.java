package com.cmmobi.railwifi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.MusicDetailActivity;
import com.cmmobi.railwifi.activity.MusicMainPageActivity;
import com.cmmobi.railwifi.music.MusicService;
import com.cmmobi.railwifi.network.GsonResponseObject.MusicElem;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
public class MusicMainPageListAdapter extends BaseAdapter {

	private Activity context;
	private LayoutInflater inflater;
	private List<Item> musicList = new ArrayList<Item>();
	private List<MusicElem> list = new ArrayList<MusicElem>();
	MyImageLoader imageLoader = null;
	DisplayImageOptions imageLoaderOptions = null;
	
	public MusicMainPageListAdapter(final Activity context) {
		this.context = context;
		DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
		inflater = LayoutInflater.from(context);
		
		imageLoader = MyImageLoader.getInstance();

		imageLoaderOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc()
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageOnFail(R.drawable.content_pic_default_9)
				.showImageForEmptyUri(R.drawable.content_pic_default_9)
				.showImageOnLoading(R.drawable.content_pic_default_9)
				.displayer(new RoundedBitmapDisplayer(12))
				.build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return musicList.size();
	}

	@Override
	public Item getItem(int position) {
		// TODO Auto-generated method stub
		return musicList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.activity_music_mainpage_list_item, null);
			holder = new ViewHolder();			
			int size225 =  DisplayUtil.getSize(context, 225);
			int size459 =  DisplayUtil.getSize(context, 459);
			int size12 = DisplayUtil.getSize(context, 12);
			RelativeLayout.LayoutParams pm;
			//右上大布局
			holder.rlRightBig = (RelativeLayout) convertView.findViewById(R.id.rl_right_big);
			pm = (RelativeLayout.LayoutParams) holder.rlRightBig.getLayoutParams();
			pm.topMargin = size12;
			holder.rlRightBig.setLayoutParams(pm);
			
			holder.iv_MusicImg01 = (ImageView) convertView.findViewById(R.id.iv_music_img01);
			pm = (RelativeLayout.LayoutParams) holder.iv_MusicImg01.getLayoutParams();
			pm.width = size459;
			pm.height = pm.width;
			holder.iv_MusicImg01.setLayoutParams(pm);
			
			holder.iv_MusicImg02 = (ImageView) convertView.findViewById(R.id.iv_music_img02);
			pm = (RelativeLayout.LayoutParams) holder.iv_MusicImg02.getLayoutParams();
			pm.width = size225;
			pm.height = pm.width;
			pm.rightMargin = size12;
			holder.iv_MusicImg02.setLayoutParams(pm);
			
			holder.iv_MusicImg03 = (ImageView) convertView.findViewById(R.id.iv_music_img03);
			pm = (RelativeLayout.LayoutParams) holder.iv_MusicImg03.getLayoutParams();
			pm.width = size225;
			pm.height = pm.width;
			pm.topMargin = size12;
			pm.rightMargin = size12;
			holder.iv_MusicImg03.setLayoutParams(pm);
			
			holder.iv_MusicImg04 = (ImageView) convertView.findViewById(R.id.iv_music_img04);
			pm = (RelativeLayout.LayoutParams) holder.iv_MusicImg04.getLayoutParams();
			pm.width = size225;
			pm.height = pm.width;
			pm.topMargin = size12;
			pm.rightMargin = size12;
			holder.iv_MusicImg04.setLayoutParams(pm);
			
			holder.iv_MusicImg05 = (ImageView) convertView.findViewById(R.id.iv_music_img05);
			pm = (RelativeLayout.LayoutParams) holder.iv_MusicImg05.getLayoutParams();
			pm.width = size225;
			pm.height = pm.width;
			pm.topMargin = size12;
			pm.rightMargin = size12;
			holder.iv_MusicImg05.setLayoutParams(pm);
			
			holder.iv_MusicImg06 = (ImageView) convertView.findViewById(R.id.iv_music_img06);
			pm = (RelativeLayout.LayoutParams) holder.iv_MusicImg06.getLayoutParams();
			pm.width = size225;
			pm.height = pm.width;
			pm.topMargin = size12;
			holder.iv_MusicImg06.setLayoutParams(pm);
			
			//左上大布局
			holder.rlLeftBig = (RelativeLayout) convertView.findViewById(R.id.rl_left_big);
			pm = (RelativeLayout.LayoutParams) holder.rlLeftBig.getLayoutParams();
			pm.topMargin = size12;
			holder.rlLeftBig.setLayoutParams(pm);
			
			holder.iv_MusicImg11 = (ImageView) convertView.findViewById(R.id.iv_music_img11);
			pm = (RelativeLayout.LayoutParams) holder.iv_MusicImg11.getLayoutParams();
			pm.width = size459;
			pm.height = pm.width;
			pm.rightMargin = size12;
			holder.iv_MusicImg11.setLayoutParams(pm);
			
			holder.iv_MusicImg12 = (ImageView) convertView.findViewById(R.id.iv_music_img12);
			pm = (RelativeLayout.LayoutParams) holder.iv_MusicImg12.getLayoutParams();
			pm.width = size225;
			pm.height = pm.width;
			holder.iv_MusicImg12.setLayoutParams(pm);
			
			holder.iv_MusicImg13 = (ImageView) convertView.findViewById(R.id.iv_music_img13);
			pm = (RelativeLayout.LayoutParams) holder.iv_MusicImg13.getLayoutParams();
			pm.width = size225;
			pm.height = pm.width;
			pm.topMargin = size12;
			holder.iv_MusicImg13.setLayoutParams(pm);
			
			holder.iv_MusicImg14 = (ImageView) convertView.findViewById(R.id.iv_music_img14);
			pm = (RelativeLayout.LayoutParams) holder.iv_MusicImg14.getLayoutParams();
			pm.width = size225;
			pm.height = pm.width;
			pm.topMargin = size12;
			pm.rightMargin = size12;
			holder.iv_MusicImg14.setLayoutParams(pm);
			
			holder.iv_MusicImg15 = (ImageView) convertView.findViewById(R.id.iv_music_img15);
			pm = (RelativeLayout.LayoutParams) holder.iv_MusicImg15.getLayoutParams();
			pm.width = size225;
			pm.height = pm.width;
			pm.topMargin = size12;
			pm.rightMargin = size12;
			holder.iv_MusicImg15.setLayoutParams(pm);
			
			holder.iv_MusicImg16 = (ImageView) convertView.findViewById(R.id.iv_music_img16);
			pm = (RelativeLayout.LayoutParams) holder.iv_MusicImg16.getLayoutParams();
			pm.width = size225;
			pm.height = pm.width;
			pm.topMargin = size12;
			holder.iv_MusicImg16.setLayoutParams(pm);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(position %2 == 0){
			holder.rlRightBig.setVisibility(View.VISIBLE);
			holder.rlLeftBig.setVisibility(View.GONE);
			imageLoader.displayImage(musicList.get(position).elems[0].img_path[0], holder.iv_MusicImg01,imageLoaderOptions);	
			holder.iv_MusicImg01.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MusicService.getInstance().setPlayArray(list);
					CmmobiClickAgentWrapper.onEvent(context, "m_list", musicList.get(position).elems[0].media_id);
					Intent intent = new Intent(context, MusicDetailActivity.class);
					intent.putExtra(MusicDetailActivity.KEY_MUSIC_ID, musicList.get(position).elems[0].media_id);
				    context.startActivityForResult(intent, MusicMainPageActivity.BACK_FROM_DETAIL);
				}
			});
			
			imageLoader.displayImage(musicList.get(position).elems[1].img_path[0], holder.iv_MusicImg02,imageLoaderOptions);	
			holder.iv_MusicImg02.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MusicService.getInstance().setPlayArray(list);
					CmmobiClickAgentWrapper.onEvent(context, "m_list", musicList.get(position).elems[1].media_id);
					Intent intent = new Intent(context, MusicDetailActivity.class);
					intent.putExtra(MusicDetailActivity.KEY_MUSIC_ID, musicList.get(position).elems[1].media_id);
				    context.startActivityForResult(intent, MusicMainPageActivity.BACK_FROM_DETAIL);
				}
			});
			
			imageLoader.displayImage(musicList.get(position).elems[2].img_path[0], holder.iv_MusicImg03,imageLoaderOptions);	
			holder.iv_MusicImg03.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MusicService.getInstance().setPlayArray(list);
					CmmobiClickAgentWrapper.onEvent(context, "m_list", musicList.get(position).elems[2].media_id);
					Intent intent = new Intent(context, MusicDetailActivity.class);
					intent.putExtra(MusicDetailActivity.KEY_MUSIC_ID, musicList.get(position).elems[2].media_id);
				    context.startActivityForResult(intent, MusicMainPageActivity.BACK_FROM_DETAIL);
				}
			});
			
			imageLoader.displayImage(musicList.get(position).elems[3].img_path[0], holder.iv_MusicImg04,imageLoaderOptions);	
			holder.iv_MusicImg04.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MusicService.getInstance().setPlayArray(list);
					CmmobiClickAgentWrapper.onEvent(context, "m_list", musicList.get(position).elems[3].media_id);
					Intent intent = new Intent(context, MusicDetailActivity.class);
					intent.putExtra(MusicDetailActivity.KEY_MUSIC_ID, musicList.get(position).elems[3].media_id);
				    context.startActivityForResult(intent, MusicMainPageActivity.BACK_FROM_DETAIL);
				}
			});
			
			imageLoader.displayImage(musicList.get(position).elems[4].img_path[0], holder.iv_MusicImg05,imageLoaderOptions);	
			holder.iv_MusicImg05.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MusicService.getInstance().setPlayArray(list);
					CmmobiClickAgentWrapper.onEvent(context, "m_list", musicList.get(position).elems[4].media_id);
					Intent intent = new Intent(context, MusicDetailActivity.class);
					intent.putExtra(MusicDetailActivity.KEY_MUSIC_ID, musicList.get(position).elems[4].media_id);
				    context.startActivityForResult(intent, MusicMainPageActivity.BACK_FROM_DETAIL);
				}
			});
			
			imageLoader.displayImage(musicList.get(position).elems[5].img_path[0], holder.iv_MusicImg06,imageLoaderOptions);	
			holder.iv_MusicImg06.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MusicService.getInstance().setPlayArray(list);
					CmmobiClickAgentWrapper.onEvent(context, "m_list", musicList.get(position).elems[5].media_id);
					Intent intent = new Intent(context, MusicDetailActivity.class);
					intent.putExtra(MusicDetailActivity.KEY_MUSIC_ID, musicList.get(position).elems[5].media_id);
				    context.startActivityForResult(intent, MusicMainPageActivity.BACK_FROM_DETAIL);
				}
			});
		}else{
			holder.rlRightBig.setVisibility(View.GONE);
			holder.rlLeftBig.setVisibility(View.VISIBLE);
			imageLoader.displayImage(musicList.get(position).elems[0].img_path[0], holder.iv_MusicImg11,imageLoaderOptions);	
			holder.iv_MusicImg11.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MusicService.getInstance().setPlayArray(list);
					CmmobiClickAgentWrapper.onEvent(context, "m_list", musicList.get(position).elems[0].media_id);
					Intent intent = new Intent(context, MusicDetailActivity.class);
					intent.putExtra(MusicDetailActivity.KEY_MUSIC_ID, musicList.get(position).elems[0].media_id);
				    context.startActivityForResult(intent, MusicMainPageActivity.BACK_FROM_DETAIL);
				}
			});
			
			imageLoader.displayImage(musicList.get(position).elems[1].img_path[0], holder.iv_MusicImg12,imageLoaderOptions);	
			holder.iv_MusicImg12.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MusicService.getInstance().setPlayArray(list);
					CmmobiClickAgentWrapper.onEvent(context, "m_list", musicList.get(position).elems[1].media_id);
					Intent intent = new Intent(context, MusicDetailActivity.class);
					intent.putExtra(MusicDetailActivity.KEY_MUSIC_ID, musicList.get(position).elems[1].media_id);
				    context.startActivityForResult(intent, MusicMainPageActivity.BACK_FROM_DETAIL);
				}
			});
			
			imageLoader.displayImage(musicList.get(position).elems[2].img_path[0], holder.iv_MusicImg13,imageLoaderOptions);	
			holder.iv_MusicImg13.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MusicService.getInstance().setPlayArray(list);
					CmmobiClickAgentWrapper.onEvent(context, "m_list", musicList.get(position).elems[2].media_id);
					Intent intent = new Intent(context, MusicDetailActivity.class);
					intent.putExtra(MusicDetailActivity.KEY_MUSIC_ID, musicList.get(position).elems[2].media_id);
				    context.startActivityForResult(intent, MusicMainPageActivity.BACK_FROM_DETAIL);
				}
			});
			
			imageLoader.displayImage(musicList.get(position).elems[3].img_path[0], holder.iv_MusicImg14,imageLoaderOptions);	
			holder.iv_MusicImg14.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MusicService.getInstance().setPlayArray(list);
					CmmobiClickAgentWrapper.onEvent(context, "m_list", musicList.get(position).elems[3].media_id);
					Intent intent = new Intent(context, MusicDetailActivity.class);
					intent.putExtra(MusicDetailActivity.KEY_MUSIC_ID, musicList.get(position).elems[3].media_id);
				    context.startActivityForResult(intent, MusicMainPageActivity.BACK_FROM_DETAIL);
				}
			});
			
			imageLoader.displayImage(musicList.get(position).elems[4].img_path[0], holder.iv_MusicImg15,imageLoaderOptions);	
			holder.iv_MusicImg15.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MusicService.getInstance().setPlayArray(list);
					CmmobiClickAgentWrapper.onEvent(context, "m_list", musicList.get(position).elems[4].media_id);
					Intent intent = new Intent(context, MusicDetailActivity.class);
					intent.putExtra(MusicDetailActivity.KEY_MUSIC_ID, musicList.get(position).elems[4].media_id);
				    context.startActivityForResult(intent, MusicMainPageActivity.BACK_FROM_DETAIL);
				}
			});
			
			imageLoader.displayImage(musicList.get(position).elems[5].img_path[0], holder.iv_MusicImg16,imageLoaderOptions);	
			holder.iv_MusicImg16.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MusicService.getInstance().setPlayArray(list);
					CmmobiClickAgentWrapper.onEvent(context, "m_list", musicList.get(position).elems[5].media_id);
					Intent intent = new Intent(context, MusicDetailActivity.class);
					intent.putExtra(MusicDetailActivity.KEY_MUSIC_ID, musicList.get(position).elems[5].media_id);
				    context.startActivityForResult(intent, MusicMainPageActivity.BACK_FROM_DETAIL);
				}
			});
		}
		
		return convertView;
	}
	
	public void setData(List<MusicElem> linelist){
		this.musicList.clear();
		this.list.clear();
		this.list.addAll(linelist);
		for(int i=0; i< linelist.size()/6; i++){
			Item item = new Item();
			item.elems = new MusicElem[6];
			item.elems[0] = linelist.get(i*6);
			item.elems[1] = linelist.get(i*6+1);
			item.elems[2] = linelist.get(i*6+2);
			item.elems[3] = linelist.get(i*6+3);
			item.elems[4] = linelist.get(i*6+4);
			item.elems[5] = linelist.get(i*6+5);
			this.musicList.add(item);
		}
		notifyDataSetChanged();
	}
		
	public class ViewHolder {
		RelativeLayout rlRightBig;
		RelativeLayout rlLeftBig;
		ImageView iv_MusicImg01;
		ImageView iv_MusicImg02;
		ImageView iv_MusicImg03;
		ImageView iv_MusicImg04;
		ImageView iv_MusicImg05;
		ImageView iv_MusicImg06;
		ImageView iv_MusicImg11;
		ImageView iv_MusicImg12;
		ImageView iv_MusicImg13;
		ImageView iv_MusicImg14;
		ImageView iv_MusicImg15;
		ImageView iv_MusicImg16;
		
	}

	private class Item{
		MusicElem[] elems;
	}
	
}