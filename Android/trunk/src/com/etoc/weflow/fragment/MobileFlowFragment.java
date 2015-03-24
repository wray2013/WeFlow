package com.etoc.weflow.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.activity.ExpenseFlowActivity;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dialog.ExchangeFlowDialog;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.event.ExpenseFlowFragmentEvent;
import com.etoc.weflow.net.GsonResponseObject;
import com.etoc.weflow.net.GsonResponseObject.ExchangeFlowPkgResp;
import com.etoc.weflow.net.GsonResponseObject.FlowPkgListResp;
import com.etoc.weflow.net.GsonResponseObject.MobileFlowProduct;
import com.etoc.weflow.net.GsonResponseObject.MobileFlowResp;
import com.etoc.weflow.net.GsonResponseObject.QChargeResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.ViewUtils;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import de.greenrobot.event.EventBus;

public class MobileFlowFragment extends Fragment implements Callback {

	private View mView;
	private ListView lvFlow;
	private FlowAdatper adapter;
	private List<MobileFlowProduct> flowList = new ArrayList<GsonResponseObject.MobileFlowProduct>();
	private Handler handler;
	private ExchangeFlowDialog exchangeDialog = null;
	private String selectId = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		handler = new Handler(this);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	public void onEvent(ExpenseFlowFragmentEvent event) {
		if (event.getIndex() == ExpenseFlowActivity.INDEX_FLOW) {
			if (flowList.size() == 0) {
				Requester.getFlowPkgList(true, handler);
			}
		}
	}
	
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
		initDialog();
		return view;
	}
	
	private void initDialog() {
		exchangeDialog = new ExchangeFlowDialog(getActivity(), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
				if (accountInfo != null && accountInfo.getUserid() != null) {
					Requester.exchangeFlowPkg(true, handler, accountInfo.getUserid(), selectId);
				} else {
					startActivity(new Intent(getActivity(), LoginActivity.class));
				}
			}
		});
	}
	
	private void initView(View view) {
		ViewUtils.setHeight(view.findViewById(R.id.rl_title), 74);
		ViewUtils.setMarginLeft(view.findViewById(R.id.view_title_bottom), 32);
		ViewUtils.setMarginRight(view.findViewById(R.id.view_title_bottom), 32);
		lvFlow = (ListView) view.findViewById(R.id.lv_flows);
		ViewUtils.setTextSize(view.findViewById(R.id.tv_title_label), 26);
		
		adapter = new FlowAdatper(getActivity(), flowList);
		lvFlow.setAdapter(adapter);
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
		
		private List<MobileFlowProduct> appList = null;
		Context context;
		private LayoutInflater inflater;
		
		public FlowAdatper(Context context,List<MobileFlowProduct> list) {
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
		public MobileFlowProduct getItem(int arg0) {
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
			
			final MobileFlowProduct item = appList.get(position);
			imageLoader.displayImage(item.imgsrc, holder.ivImg,imageLoaderOptions);
			holder.tvName.setText(item.title);
			holder.tvDesc.setText(item.desc);
			holder.tvFlowCoins.setText(item.cost + "流量币");
			
			holder.tvExchange.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					selectId = item.flowpkgid;
					exchangeDialog.show();
				}

			});
			return convertView;
		}
		
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_FLOW_PKG_LIST:
			if (msg.obj != null) {
				FlowPkgListResp resp = (FlowPkgListResp) msg.obj;
				if(resp.status.equals("0000") || resp.status.equals("0")) {
					if (resp.list != null && resp.list.length > 0) {
						Collections.addAll(flowList, resp.list[0].products);
						
						adapter.notifyDataSetChanged();
					}
				}
			}
			break;
		case Requester.RESPONSE_TYPE_EXCHANGE_FLOW_PKG:
			if (msg.obj != null) {
				ExchangeFlowPkgResp chargeResp = (ExchangeFlowPkgResp) msg.obj;
				if (Requester.isSuccessed(chargeResp.status)) {
					PromptDialog.Alert("订购成功");
					WeFlowApplication.setFlowCoins(chargeResp.flowcoins);
				} else if (Requester.isProcessed(chargeResp.status)){
					PromptDialog.Alert("订购已处理");
					WeFlowApplication.setFlowCoins(chargeResp.flowcoins);
				} else {
					PromptDialog.Alert(ConStant.ORDER_FAIL);
				}
			}
			break;
		}
		return false;
	}
}
