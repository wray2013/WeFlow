package com.cmmobi.looklook.common.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmmobi.looklook.R;


public class VideoEffectAdapter extends BaseAdapter{
	private static final String TAG = "VideoEffectAdapter";
	private LayoutInflater inflater;
	private Context mContext;
	public int lastPosition = -1;
	public ImageView selectImage = null;
	
	public VideoEffectAdapter(Context context) {
		mContext = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return effects.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder; 
        if (convertView == null) {
        	convertView = inflater.inflate(R.layout.grid_item_effect, null); 
        	viewHolder = new ViewHolder(); 
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_effect_name);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.iv_effect_img);
            viewHolder.selectedImage = (ImageView) convertView.findViewById(R.id.iv_effect_selected); 
            convertView.setTag(viewHolder); 
        } else {
        	viewHolder = (ViewHolder) convertView.getTag(); 
        }
        Log.d(TAG,"VideoEffectAdapter  position = " + position + " lastPosition = " + lastPosition);
        if (position == lastPosition) {
        	viewHolder.selectedImage.setVisibility(View.VISIBLE);
        	selectImage = viewHolder.selectedImage;
        } else {
        	viewHolder.selectedImage.setVisibility(View.GONE);
        }
        
        viewHolder.title.setText(effectName[position]); 
        viewHolder.image.setImageResource(effects[position]);
        
        return convertView;
	}
	
	public void hideLastSelected() {
		if (selectImage != null && selectImage.isShown()) {
			selectImage.setVisibility(View.GONE);
		}
	}
	
	class ViewHolder 
    { 
        public TextView title; 
        public ImageView image; 
        public ImageView selectedImage;
    }
	
	public int [] effects = {
			R.drawable.effect_zc, R.drawable.effect_mh,
			R.drawable.effect_jbz, R.drawable.effect_sch,
			R.drawable.effect_hb, R.drawable.effect_csdb, 
			R.drawable.effect_dyg, R.drawable.effect_hjx,
			R.drawable.effect_gtf, R.drawable.effect_mc, 
			R.drawable.effect_msyr, R.drawable.effect_ssjf, 
			R.drawable.effect_sdrl, R.drawable.effect_lyss, 
			R.drawable.effect_xzmq, R.drawable.effect_t,
			R.drawable.effect_a, R.drawable.effect_nq,
			R.drawable.effect_sgsd, R.drawable.effect_sxzd,
			R.drawable.effect_zyzd
	};
	
	public String[] effectName = {
			"正常", "漫画", "旧报纸", "水彩画", "黑白", "彩色底板",
			"淡雅光", "怀旧系", "哥特风", "萌葱", "暮色悠然", "瑟瑟金风", 
			"视丹如绿", "绿荫素素", "炫紫迷情", "凸镜",
			"凹镜", "扭曲", "时光隧道", "上下折叠", "左右折叠"
			};
	
}
