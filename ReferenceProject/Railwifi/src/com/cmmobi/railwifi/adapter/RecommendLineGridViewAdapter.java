package com.cmmobi.railwifi.adapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.RailTravelDetailAcitivity;
import com.cmmobi.railwifi.network.GsonResponseObject.recommendLineElem;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public	class RecommendLineGridViewAdapter extends BaseAdapter{

		recommendLineElem[] imgs;
		Context context;
		MyImageLoader imageLoader = null;
		private LayoutInflater inflater;
		DisplayImageOptions imageLoaderOptions = null;
		public RecommendLineGridViewAdapter(Context contexts, recommendLineElem[] imgs) {
			// TODO Auto-generated constructor stub
			this.imgs = imgs;
			this.context = contexts;
			inflater = LayoutInflater.from(context);
			imageLoader = MyImageLoader.getInstance();
			imageLoaderOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc()
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				    .bitmapConfig(Bitmap.Config.RGB_565)
					.showImageOnFail(R.drawable.small_pic_default)
					.showImageForEmptyUri(R.drawable.small_pic_default)
					.showImageOnLoading(R.drawable.small_pic_default)
					.build();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imgs.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return imgs[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			GridViewViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.activity_railtravel_detail_gridview_item, null);
				holder = new GridViewViewHolder();			
				holder.img = (ImageView) convertView.findViewById(R.id.iv_img);
				RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) holder.img.getLayoutParams();
				lParams.width =(DisplayUtil.getScreenWidth(context)-4*DisplayUtil.getSize(context, 12))/3;
				lParams.height = lParams.width;
				lParams.bottomMargin = DisplayUtil.getSize(context, 12);
				holder.img.setLayoutParams(lParams);
				convertView.setTag(holder);
			} else {
				holder = (GridViewViewHolder) convertView.getTag();
			}
				
			imageLoader.displayImage(imgs[position].img_path, holder.img, imageLoaderOptions);	
			holder.img.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context, RailTravelDetailAcitivity.class);
					intent.putExtra("lineid", imgs[position].line_id);
					context.startActivity(intent);
				}
			});
			return convertView;
		}
		
		public class GridViewViewHolder{
			ImageView img;
		} 
		
	}