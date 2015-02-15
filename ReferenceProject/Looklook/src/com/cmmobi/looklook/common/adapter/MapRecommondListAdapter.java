package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.utils.ZDateUtils;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryDetailActivity;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.listview.pulltorefresh.DiaryListView;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;


public class MapRecommondListAdapter extends ArrayAdapter<MyDiary> {
	
	private static final String TAG = "MapRecommondListAdapter";
	private Context context;
	private ArrayList<MyDiary>  items;
	private LayoutInflater inflater ;
	private Handler handler;
	RelativeLayout rl;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	public MapRecommondListAdapter(Context context,  Handler handler, int resource,
			int textViewResourceId, ArrayList<MyDiary> items, RelativeLayout layout) {
		super(context, resource, textViewResourceId, items);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.items = items;
		this.inflater = LayoutInflater.from (context);
		this.handler = handler;
		this.rl = layout;
		this.animateFirstListener = new AnimateFirstDisplayListener();
		this.imageLoader = ImageLoader.getInstance();
		
		this.options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.maptankuang_moren)
		.showImageForEmptyUri(R.drawable.maptankuang_moren)
		.showImageOnFail(R.drawable.maptankuang_moren)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
//		.displayer(new CircularBitmapDisplayer()) //圆形图片
		//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
		.build();
	}


	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;
		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_list_diary_recommend, null);
//			View rlDiaryItem = convertView.findViewById(R.id.rl_row_list_diary_item);
			holder = new ViewHolder();
			holder.play = (ImageView) convertView.findViewById(R.id.iv_video_play_button);
			holder.videoPic = (ImageView) convertView.findViewById(R.id.wiv_row_list_diary_head);
			holder.nick = (TextView) convertView.findViewById(R.id.tv_row_list_diary_nick);
			holder.time = (TextView) convertView.findViewById(R.id.tv_row_list_diary_time);
			holder.content = (TextView) convertView.findViewById(R.id.tv_row_list_diary_content);
			holder.sex = (ImageView) convertView.findViewById(R.id.iv_row_list_diary_sex);
//			rlDiaryItem.setOnClickListener(this);
//			rlDiaryItem.setTag(R.id.rl_row_list_diary_item,
//					((MyDiary) getItem(position)).diaryid);
			convertView.setTag(R.string.view_tag_key, holder);
		} else {
            holder = (ViewHolder) convertView.getTag(R.string.view_tag_key);
        }

		MyDiary item = getItem(position);
		view = convertView;
		view.setTag(item);
		
		String txtContent = "";
		if (item != null) {
			holder.play.setVisibility(View.GONE);
			if (item.attachs != null) {
				int type = DiaryListView.getDiaryType(item.attachs);// 获取日记类型
				setCoverImage(item.attachs, type, holder);
				txtContent = getContent(item.attachs);
			}
			holder.videoPic.setTag(item);
//			holder.videoPic.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					MyDiary tempDiary = (MyDiary) v.getTag();
//					String diaryid=tempDiary.diaryid;
//					DiaryManager.getInstance().setDetailDiaryList(items);
//					context.startActivity(new Intent(context,DiaryDetailActivity.class)
//					.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_ID, diaryid));
//					rl.setVisibility(View.INVISIBLE);
//				}
//			});
			holder.content.setText(txtContent);
			holder.nick.setText(item.nickname);
			holder.time.setText(ZDateUtils.getFormatDateByMilli(item.diarytimemilli));// "今天17:08");
			// 0男，1女， 2未知
			if(item.sex != null) {
				Log.d(TAG, "item.sex = [" + item.sex + "]!");
				holder.sex.setVisibility(View.VISIBLE);
				if(item.sex.equals("1")) {
					holder.sex.setImageResource(R.drawable.mapnv);
				} else if(item.sex.equals("0")) {
					holder.sex.setImageResource(R.drawable.mapnan);
				} else if(item.sex.equals("2")) {
					holder.sex.setVisibility(View.GONE);
				} else {
					Log.d(TAG, "Unexpected Sex Value[" + item.sex + "]!");
				}
			}
		}
		return convertView;
	}
	
    static class ViewHolder {
		// CheckBox selectItemCheckBox ;
		TextView time;
		TextView nick;
		TextView content;
		ImageView sex;
		ImageView videoPic;
		ImageView play;
    }

    private void setCoverImage(diaryAttach[] attachs, int type, ViewHolder holder) {
		if(null==attachs||0==attachs.length){
			Log.e(TAG, "diary attachs is null, fetched no pic!");
			return;
		}
    	switch(type) {
		case 0x10000000://主体 视频
		case 0x10000100://主体 视频+辅 音频
		case 0x10000101://主体 视频+辅 音频+文字
		case 0x10000001://主体 视频+文字
			String videourl = getCoverImage(1, attachs);
//			holder.videoPic.setLoadingDrawable(R.drawable.maptankuang_moren);
//			holder.videoPic.setImageUrl(videourl, 1, false);
			imageLoader.displayImageEx(videourl, holder.videoPic, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
			holder.play.setVisibility(View.VISIBLE);
//			holder.videoPic.setImageUrl(R.drawable.maptankuang_moren, 1, videourl, false);
			break;
		case 0x1000000://主体 音频
		case 0x1000100://主体 音频+辅 音频
		case 0x1000101://主体 音频+辅 音频+文字
		case 0x1000001://主体音频+文字
			holder.videoPic.setImageResource(R.drawable.maptankuang_luyin);
			break;
		case 0x100://辅 音频
		case 0x101://辅 音频+文字
			holder.videoPic.setImageResource(R.drawable.tankuang_nothing);
			break;
		case 0x100000://主体 图片
		case 0x100100://主体 图片+辅 音频
		case 0x100101://主体 图片+辅 音频+文字
		case 0x100001://主体 图片 +文字
			String imageurl = getCoverImage(3, attachs);
//			holder.videoPic.setLoadingDrawable(R.drawable.maptankuang_moren);
//			holder.videoPic.setImageUrl(imageurl, 1, false);
			imageLoader.displayImageEx(imageurl, holder.videoPic, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
//			holder.videoPic.setImageUrl(R.drawable.maptankuang_moren, 1, imageurl, false);
			break;
		case 0x10000://主体 文字
		case 0x10001://主体 文字+文字
		case 0x10100://主体 文字+辅 音频
		case 0x10101://主体 文字+辅 音频+文字
		case 0x1://辅 文字
			holder.videoPic.setImageResource(R.drawable.tankuang_nothing);
			break;
		default:
			holder.videoPic.setImageResource(R.drawable.maptankuang_moren);
			break;
		}
    }

    //1:视频 2:音频 3:图片
    private String getCoverImage(int type, diaryAttach[] attachs) {
    	List<diaryAttach> attachList=Arrays.asList(attachs);
    	String coverUrl = null;
    	for(int i = 0; i < attachList.size(); i++) {
    		diaryAttach attach=attachList.get(i);
        	switch(type) {
    		case 1: //视频
    			coverUrl = attach.videocover;
    			break;
    		case 3: //图片
				int show_width = 0;
				int show_heigh = 0;
				if (DateUtils.isNum(attach.show_width))
					show_width = Integer.parseInt(attach.show_width);
				if (DateUtils.isNum(attach.show_height))
					show_heigh = Integer.parseInt(attach.show_height);
				String imageUrl = DiaryListView.getAttachUrl(attach.attachimage);
				Log.d(TAG, "Recommend List show_width=" + show_width);
				Log.d(TAG, "Recommend List show_heigh=" + show_heigh);
				if (imageUrl != null && imageUrl.length() > 0 && imageUrl.startsWith("http"))
				{
					imageUrl += "&width=" + show_width + "&heigh=" + show_heigh;
				}
				coverUrl = imageUrl;
//    			if(attach != null && attach.attachimage != null && attach.attachimage.length > 0) {
//    				coverUrl = attach.attachimage[0].imageurl;
//    			}
    			break;
    		default:
    			break;
    		}
        	if(coverUrl != null) {
        		break;
        	}
    	}
    	Log.d(TAG, "coverUrl = " + coverUrl);
    	return coverUrl;
    }

    private String getContent(diaryAttach[] attachs) {
    	String ret = "";
    	List<diaryAttach> attachList=Arrays.asList(attachs);
    	for(int i = 0; i< attachList.size(); i++) {
    		diaryAttach attach=attachList.get(i);
    		if("4".equals(attach.attachtype)) { // 附件类型为文字
    			ret = attach.content;
    		}
    	}
    	return ret;
    }
    
//	@Override
//	public void onClick(View v) {
//		switch(v.getId()) {
//		case R.id.rl_row_list_diary_item:
//			String diaryid=v.getTag(R.id.rl_row_list_diary_item).toString();
//			DiaryManager.getInstance().setDetailDiaryList(this.items);
//			context.startActivity(new Intent(context,DiaryDetailActivity.class)
//			.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_ID, diaryid));
//			rl.setVisibility(View.INVISIBLE);
//			break;
//		}
//	}

}
