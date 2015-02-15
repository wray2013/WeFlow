package com.cmmobi.railwifi.adapter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.PicShowActivity;
import com.cmmobi.railwifi.event.RequestEvent;
import com.cmmobi.railwifi.network.GsonResponseObject.TravelElem;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.view.NoScrollGridView;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import de.greenrobot.event.EventBus;
public class RailTravelDetailListAdapter extends BaseAdapter {

	private Activity context;
	private LayoutInflater inflater;
	private TravelElem[] listLine ;

	public static Boolean clickable = true;
	public RailTravelDetailListAdapter(final Activity context, TravelElem[] list) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		listLine = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listLine.length;
	}

	@Override
	public TravelElem getItem(int position) {
		// TODO Auto-generated method stub
		return listLine[position];
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
					R.layout.activity_railtravel_detail_item, null);
			holder = new ViewHolder();
			holder.tvLineTop = (TextView) convertView.findViewById(R.id.tv_line_top);
			RelativeLayout.LayoutParams pm = (RelativeLayout.LayoutParams) holder.tvLineTop.getLayoutParams();
			pm.leftMargin = DisplayUtil.getSize(context, 37);
			pm.width = DisplayUtil.getSize(context, 3);
			pm.height = DisplayUtil.getSize(context, 21);
			holder.tvLineTop.setLayoutParams(pm);
			holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
			pm = (RelativeLayout.LayoutParams) holder.ivIcon.getLayoutParams();
			pm.leftMargin = DisplayUtil.getSize(context, 12);
			pm.rightMargin = DisplayUtil.getSize(context, 24);
			pm.height = DisplayUtil.getSize(context, 53);
			pm.width = pm.height;
			holder.ivIcon.setLayoutParams(pm);
			holder.tvLineBottom = (TextView) convertView.findViewById(R.id.tv_line_bottom);
			pm = (RelativeLayout.LayoutParams) holder.tvLineBottom.getLayoutParams();
			pm.leftMargin = DisplayUtil.getSize(context, 37);
			pm.width = DisplayUtil.getSize(context, 3);
			pm.height = DisplayUtil.getSize(context, 18);
			holder.tvLineBottom.setLayoutParams(pm);
			holder.tvDay = (TextView) convertView.findViewById(R.id.tv_day);
			holder.tvDay.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
			holder.tvDay.setTextSize(DisplayUtil.textGetSizeSp(context, 36));
			holder.tvDescrp = (TextView) convertView.findViewById(R.id.tv_descrp);
			holder.tvDescrp.setTextSize(DisplayUtil.textGetSizeSp(context, 27));
			holder.tvDescrp.setLineSpacing(0, (float)1.5);
			holder.tvDescrp.setPadding(DisplayUtil.getSize(context, 12), DisplayUtil.getSize(context, 12), DisplayUtil.getSize(context, 12), 0);
			holder.tvFood = (TextView) convertView.findViewById(R.id.tv_food);
			holder.tvFood.setTextSize(DisplayUtil.textGetSizeSp(context, 27));
			holder.tvFood.setPadding(DisplayUtil.getSize(context, 12), 0, DisplayUtil.getSize(context, 12), 0);
			holder.tvHotel = (TextView) convertView.findViewById(R.id.tv_hotel);
			holder.tvHotel.setTextSize(DisplayUtil.textGetSizeSp(context, 27));
			holder.tvHotel.setPadding(DisplayUtil.getSize(context, 12), 0, DisplayUtil.getSize(context, 12), DisplayUtil.getSize(context, 24));
			holder.rlTravel = (RelativeLayout) convertView.findViewById(R.id.rl_travel);
			holder.rlTravel.setPadding(DisplayUtil.getSize(context, 12), DisplayUtil.getSize(context, 12), DisplayUtil.getSize(context, 12), 0);
			holder.gvImgs = (NoScrollGridView) convertView.findViewById(R.id.gv_imgs);
			holder.gvImgs.setVerticalSpacing(DisplayUtil.getSize(context, 12));
			holder.gvImgs.setHorizontalSpacing(DisplayUtil.getSize(context, 12));
			holder.gvImgs.setPadding(DisplayUtil.getSize(context, 12), 0, DisplayUtil.getSize(context, 12), DisplayUtil.getSize(context, 12));
			holder.tvLine = (TextView) convertView.findViewById(R.id.tv_line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(position == 0){
			holder.tvLineTop.setVisibility(View.INVISIBLE);
			holder.tvLineBottom.setVisibility(View.VISIBLE);
		}else{
			holder.tvLineBottom.setVisibility(View.VISIBLE);
			holder.tvLineTop.setVisibility(View.VISIBLE);
		}
		holder.tvDay.setText(listLine[position].day + "  " + listLine[position].address  + " ");
		
		holder.tvDescrp.setText(listLine[position].introduction);
		holder.tvFood.setText("用餐：" + listLine[position].food);
		holder.tvHotel.setText("住宿：" + listLine[position].hotel);
		if(null != listLine[position].img_list && listLine[position].img_list.length !=0){
			final GridViewAdapter gridViewAdapter = new GridViewAdapter(context, listLine[position].img_list);
			holder.gvImgs.setAdapter(gridViewAdapter);
			holder.gvImgs.setVisibility(View.VISIBLE);
			holder.gvImgs.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						final int arg2, long arg3) {
					// TODO Auto-generated method stub
					if(!clickable){
						return;
					}else{
						clickable = false;
					}
					
					Intent intent = new Intent(context, PicShowActivity.class);
					intent.putExtra("imageUrl", gridViewAdapter.getItem(arg2));
					//EventBus.getDefault().post(RequestEvent.LOADING_END);
					context.startActivity(intent);
					/*EventBus.getDefault().post(RequestEvent.LOADING_START);
					 new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							 int h = 0; 
							 int w = 0;
							 Intent intent = new Intent(context, PicShowActivity.class);
							  
							try {
								    byte[] data = getImage(gridViewAdapter.getItem(arg2));
								    String d = new String(data);
								    int length = data.length;
								    Bitmap bitMap = BitmapFactory.decodeByteArray(data, 0, length);
								    if(bitMap !=null){
									    w = bitMap.getWidth();
									    h = bitMap.getHeight();
									    intent.putExtra("w", w);
									    intent.putExtra("h", h);
								    }
								    //imageView.seti
								} catch (Error e) {
								    e.printStackTrace();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							intent.putExtra("imageUrl", gridViewAdapter.getItem(arg2));
							//EventBus.getDefault().post(RequestEvent.LOADING_END);
							context.startActivity(intent);
						}
					}).start();
					 */
				}
			
			});
		}else{
			holder.gvImgs.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	public static byte[] getImage(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection httpURLconnection =  (HttpURLConnection)url.openConnection();
		httpURLconnection.setRequestMethod("GET");
		httpURLconnection.setReadTimeout(6*1000);
		InputStream in = null;
		if (httpURLconnection.getResponseCode() == 200) {
			 in = httpURLconnection.getInputStream();
			 byte[] result = readStream(in);
			 in.close();
			 return result;
			 
		}
		return null;
	}
	
	public static byte[] readStream(InputStream in) throws Exception{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while((len = in.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		outputStream.close();
		in.close();
		return outputStream.toByteArray();
	}

		
	public class ViewHolder {
		ImageView ivIcon;
		TextView tvDay;
		TextView tvFood;
		TextView tvDescrp;
		TextView tvHotel;
		RelativeLayout rlTravel;
		NoScrollGridView gvImgs;
		TextView tvLineTop;
		TextView tvLineBottom;
		TextView tvLine;
	}

	
	class GridViewAdapter extends BaseAdapter{

		String[] imgs;
		Context context;
		MyImageLoader imageLoader = null;
		DisplayImageOptions imageLoaderOptions = null;
		public GridViewAdapter(Context contexts, String[] imgs) {
			// TODO Auto-generated constructor stub
			this.imgs = imgs;
			this.context = contexts;
			imageLoader = MyImageLoader.getInstance();
			imageLoaderOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc()
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				    .bitmapConfig(Bitmap.Config.RGB_565)
					.showImageOnFail(R.drawable.small_pic_default)
					.showImageForEmptyUri(R.drawable.small_pic_default)
					.showImageOnLoading(R.drawable.small_pic_default)
					//.displayer(new RoundedBitmapDisplayer(12))// 圆角图片
					.build();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imgs.length;
		}

		@Override
		public String getItem(int position) {
			// TODO Auto-generated method stub
			return imgs[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
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
				holder.img.setLayoutParams(lParams);
				convertView.setTag(holder);
			} else {
				holder = (GridViewViewHolder) convertView.getTag();
			}

			imageLoader.displayImage(imgs[position], holder.img, imageLoaderOptions);
			return convertView;
		}
		
		public class GridViewViewHolder{
			ImageView img;
		} 
		
	}
	
}