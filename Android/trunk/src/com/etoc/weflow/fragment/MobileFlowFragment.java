package com.etoc.weflow.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.net.GsonResponseObject.ExchangeGiftResp;
import com.etoc.weflow.net.GsonResponseObject.MobileFlowResp;
import com.etoc.weflow.utils.ViewUtils;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class MobileFlowFragment extends Fragment {

	private View mView;
	private ListView lvFlow;
	private FlowAdatper adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(mView != null){
		    ViewGroup parent = (ViewGroup) mView.getParent();  
		    if (parent != null) {  
		        parent.removeView(mView);  
		    }   
		    return mView;
		}
		super.onCreateView(inflater, container, savedInstanceState);
		
		View view = inflater.inflate(R.layout.fragment_mobile_flow, null);
		mView = view;
		initView(view);
		return view;
	}
	
	private void initView(View view) {
		ViewUtils.setHeight(view.findViewById(R.id.rl_title), 74);
		ViewUtils.setMarginLeft(view.findViewById(R.id.view_title_bottom), 32);
		ViewUtils.setMarginRight(view.findViewById(R.id.view_title_bottom), 32);
		lvFlow = (ListView) view.findViewById(R.id.lv_flows);
		ViewUtils.setTextSize(view.findViewById(R.id.tv_title_label), 26);
		
		adapter = new FlowAdatper(getActivity(), makeFlowData());
		lvFlow.setAdapter(adapter);
	}
	
	private List<MobileFlowResp> makeFlowData() {
		List<MobileFlowResp> list = new ArrayList<MobileFlowResp>();
		
		String[] imgUrls = {"http://img2.imgtn.bdimg.com/it/u=2246142888,482574638&fm=21&gp=0.jpg",
        		"http://img.ithome.com/newsuploadfiles/2013/7/20130729_082806_654.jpg",
        		"http://img3.imgtn.bdimg.com/it/u=3520249570,2956679241&fm=21&gp=0.jpg",
        		"http://img2.imgtn.bdimg.com/it/u=503567143,1320843493&fm=11&gp=0.jpg",
        		"http://img2.imgtn.bdimg.com/it/u=2461699972,2986826516&fm=21&gp=0.jpg"
        		};
		String[] titles = {"10元畅享沃3G",
				"电信20元流量包",
				"300M流量包",
				"50M流量包",
				"1G流量包"
		};
		
		String[] descs = {
				"手机上网套餐全新升级，赠送流量包",
				"手机上网套餐全新升级，赠送流量包",
				"手机上网套餐全新升级，赠送流量包",
				"手机上网套餐全新升级，赠送流量包",
				"手机上网套餐全新升级，赠送流量包",
		};
		for (int i = 0;i < 5;i++) {
			MobileFlowResp resp = new MobileFlowResp();
			resp.flowpkgid = i + "";
			resp.imgsrc = imgUrls[i];
			resp.title = titles[i];
			resp.desc = descs[i];
			resp.cost = ((i + 1) * 1000) + "";
			list.add(resp);
		}
		
		return list;
	}
	
	
	class FlowViewHolder {
		ImageView ivImg;
		TextView tvName;
		TextView tvDesc;
		TextView tvExchange;
		TextView tvFlowCoins;
	}
	
	private class FlowAdatper extends BaseAdapter {

		MyImageLoader imageLoader = null;
		DisplayImageOptions imageLoaderOptions = null;
		
		private List<MobileFlowResp> appList = null;
		Context context;
		private LayoutInflater inflater;
		
		public FlowAdatper(Context context,List<MobileFlowResp> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			inflater = LayoutInflater.from(context);
			appList = list;
			imageLoader = MyImageLoader.getInstance();

			imageLoaderOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.showImageForEmptyUri(R.drawable.small_pic_default)
					.showImageOnFail(R.drawable.small_pic_default)
					.showImageOnLoading(R.drawable.small_pic_default)
					.build();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appList.size();
		}

		@Override
		public MobileFlowResp getItem(int arg0) {
			// TODO Auto-generated method stub
			return appList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			FlowViewHolder holder = null;
			if (convertView == null) {
				holder = new FlowViewHolder();
				convertView = inflater.inflate(R.layout.item_mobile_flow, null);
				holder.ivImg = (ImageView) convertView.findViewById(R.id.iv_img);
				holder.tvName = (TextView) convertView.findViewById(R.id.tv_flow_name);
				holder.tvDesc = (TextView) convertView.findViewById(R.id.tv_flow_desc);
				holder.tvExchange = (TextView) convertView.findViewById(R.id.tv_flow_exchange);
				holder.tvFlowCoins = (TextView) convertView.findViewById(R.id.tv_flow_coins);
				
				ViewUtils.setHeight(convertView.findViewById(R.id.view_height), 152);
				ViewUtils.setSize(holder.ivImg, 200, 120);
				ViewUtils.setSize(holder.tvExchange, 112, 50);
				ViewUtils.setMarginLeft(holder.ivImg, 32);
				ViewUtils.setMarginLeft(holder.tvName, 22);
				ViewUtils.setMarginTop(holder.tvName, 26);
				ViewUtils.setMarginTop(holder.tvDesc, 36);
				ViewUtils.setMarginTop(holder.tvExchange, 28);
				ViewUtils.setMarginRight(holder.tvExchange, 32);
				ViewUtils.setMarginTop(holder.tvFlowCoins, 20);
				ViewUtils.setMarginLeft(convertView.findViewById(R.id.view_bottom), 32);
				ViewUtils.setMarginRight(convertView.findViewById(R.id.view_bottom), 32);
				
				ViewUtils.setTextSize(holder.tvName, 28);
				ViewUtils.setTextSize(holder.tvDesc, 23);
				ViewUtils.setTextSize(holder.tvExchange, 26);
				ViewUtils.setTextSize(holder.tvFlowCoins, 21);
				
				convertView.setTag(holder);
				
			} else {
				holder = (FlowViewHolder) convertView.getTag();
			}
			
			MobileFlowResp item = appList.get(position);
			imageLoader.displayImage(item.imgsrc, holder.ivImg,imageLoaderOptions);
			holder.tvName.setText(item.title);
			holder.tvDesc.setText(item.desc);
			holder.tvFlowCoins.setText(item.cost + "流量币");
			return convertView;
		}
		
	}
}
