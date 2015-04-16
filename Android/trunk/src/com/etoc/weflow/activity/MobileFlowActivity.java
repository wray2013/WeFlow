package com.etoc.weflow.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dialog.ExchangeFlowDialog;
import com.etoc.weflow.dialog.OrderDialog;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.net.GsonResponseObject;
import com.etoc.weflow.net.GsonResponseObject.ExchangeFlowPkgResp;
import com.etoc.weflow.net.GsonResponseObject.FlowPkgListResp;
import com.etoc.weflow.net.GsonResponseObject.MobileFlowProduct;
import com.etoc.weflow.net.GsonResponseObject.MobileFlowResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.NumberUtils;
import com.etoc.weflow.utils.ViewUtils;
import com.idunnololz.widgets.AnimatedExpandableListView;
import com.idunnololz.widgets.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class MobileFlowActivity extends TitleRootActivity {

	private AnimatedExpandableListView lvFlow;
	private FlowAdatper adapter;
	private List<MobileFlowResp> flowList = new ArrayList<GsonResponseObject.MobileFlowResp>();
	private Handler handler;
	private ExchangeFlowDialog exchangeDialog = null;
	private String selectId = "";
	private String selectProduct = "";
	
	private boolean isLogin = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		EventBus.getDefault().register(this);
		handler = new Handler(this);
		
		setTitleText("流量包");
		setRightButtonText("记录");
		
		isLogin = getIntent().getBooleanExtra("isLogin", false);
		
		initView();
		initDialog();
		
		Requester.getFlowPkgList(true, handler);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
//		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	/*public void onEvent(ExpenseFlowFragmentEvent event) {
		if (event.getIndex() == ExpenseFlowActivity.INDEX_FLOW) {
			if (flowList.size() == 0) {
				Requester.getFlowPkgList(true, handler);
			}
		}
	}*/
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_title_right:
			if(isLogin) {
				Intent ExpenseIntent = new Intent(this, ExpenseBillListActivity.class);
				ExpenseIntent.putExtra(ConStant.INTENT_EXPENSE_FLOW, 3);
				startActivity(ExpenseIntent);
			} else {
				startActivity(new Intent(this, LoginActivity.class));
				finish();
			}
			break;
		}
		super.onClick(v);
	}
	
	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}
	
/*	@Override
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
	}*/
	
	private void initDialog() {
		exchangeDialog = new ExchangeFlowDialog(this, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
				if(PromptDialog.checkPhoneNum(exchangeDialog.getContext())) {
					if (accountInfo != null && accountInfo.getUserid() != null) {
						Requester.exchangeFlowPkg(true, handler, accountInfo.getUserid(), selectId,exchangeDialog.getContext());
					} else {
						startActivity(new Intent(MobileFlowActivity.this, LoginActivity.class));
					}
				} else {
					Toast.makeText(MobileFlowActivity.this, "请输入有效手机号", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
	}
	
	private void initView() {
		ViewUtils.setHeight(findViewById(R.id.rl_title), 130);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_title_label), 32);
		lvFlow = (AnimatedExpandableListView) findViewById(R.id.lv_flows);
		lvFlow.setGroupIndicator(null);
		lvFlow.setSelector(new ColorDrawable(Color.TRANSPARENT));
		ViewUtils.setTextSize(findViewById(R.id.tv_title_label), 26);
		
		adapter = new FlowAdatper(this, flowList);
		lvFlow.setAdapter(adapter);
	}
	
	class FlowViewHolder {
		ImageView ivImg;
		TextView tvName;
		TextView tvDesc;
		TextView tvExchange;
		TextView tvFlowCoins;
	}
	
	class GroupHolder {
		TextView tvTypeName;
		ImageView ivOpen;
	}
	
	private class FlowAdatper extends AnimatedExpandableListAdapter {

		MyImageLoader imageLoader = null;
		DisplayImageOptions imageLoaderOptions = null;
		
		private List<MobileFlowResp> flowList = null;
		Context context;
		private LayoutInflater inflater;
		
		public FlowAdatper(Context context,List<MobileFlowResp> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			inflater = LayoutInflater.from(context);
			flowList = list;
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
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return flowList.size();
		}

		@Override
		public MobileFlowResp getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return flowList.get(groupPosition);
		}

		@Override
		public MobileFlowProduct getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			if (flowList.get(groupPosition) != null && flowList.get(groupPosition).products != null) {
				return flowList.get(groupPosition).products[childPosition];
			}
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			GroupHolder holder;
            MobileFlowResp item = getGroup(groupPosition);
            if (convertView == null) {
                holder = new GroupHolder();
                convertView = inflater.inflate(R.layout.item_mobile_flow_header, parent, false);
                holder.tvTypeName = (TextView) convertView.findViewById(R.id.tv_name);
                holder.ivOpen = (ImageView) convertView.findViewById(R.id.iv_expande);
                
                ViewUtils.setTextSize(holder.tvTypeName, 24);
                convertView.setTag(holder);
                
                ViewUtils.setMarginLeft(holder.tvTypeName, 36);
        		ViewUtils.setMarginRight(holder.ivOpen, 64);
            } else {
                holder = (GroupHolder) convertView.getTag();
            }
            
            if (isExpanded) {
            } else {
            }
            holder.tvTypeName.setText(item.typename);
            
            return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public View getRealChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
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
			
			final MobileFlowProduct item = getChild(groupPosition, childPosition);
			if(item.imgsrc != null && !item.imgsrc.equals("")) {
				holder.ivImg.setVisibility(View.VISIBLE);
				imageLoader.displayImage(item.imgsrc, holder.ivImg,imageLoaderOptions);
			} else {
				holder.ivImg.setVisibility(View.GONE);
			}
			holder.tvName.setText(item.title);
			holder.tvDesc.setText(item.desc);
			holder.tvFlowCoins.setText(NumberUtils.convert2IntStr(item.cost) + "流量币");
			
			holder.tvExchange.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					selectId = item.chargesid;
					selectProduct = item.title;
					exchangeDialog.show();
				}

			});
			return convertView;
		}

		@Override
		public int getRealChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			if (flowList.get(groupPosition) != null && flowList.get(groupPosition).products != null) {
				return flowList.get(groupPosition).products.length;
			}
			return 0;
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
					if (resp.chargelist != null && resp.chargelist.length > 0) {
						for (MobileFlowResp flowResp:resp.chargelist) {
							flowList.add(flowResp);
						}
						adapter.notifyDataSetChanged();
					}
				}
			}
			break;
		case Requester.RESPONSE_TYPE_EXCHANGE_FLOW_PKG:
			if (msg.obj != null) {
				ExchangeFlowPkgResp chargeResp = (ExchangeFlowPkgResp) msg.obj;
				if (Requester.isSuccessed(chargeResp.status)) {
//					PromptDialog.Alert("订购成功");
					OrderDialog.Dialog(this, "已成功兑换" + selectProduct + " 流量包");
					WeFlowApplication.getAppInstance().setFlowCoins(chargeResp.flowcoins);
					/*if (!StringUtils.isEmpty(chargeResp.cardcode)) {
						PromptDialog.Dialog(this, "温馨提示", "订购成功，兑换码: " + chargeResp.cardcode + "\n请尽快使用", "确定");
					}*/
					exchangeDialog.dismiss();
				} else if (Requester.isProcessed(chargeResp.status)){
//					PromptDialog.Alert("订购已处理");
					OrderDialog.Dialog(this, "订购已受理");
					WeFlowApplication.getAppInstance().setFlowCoins(chargeResp.flowcoins);
					exchangeDialog.dismiss();
				} else if (Requester.isLowFlow(chargeResp.status)) {
					OrderDialog.Dialog(this, ConStant.LOW_FLOW, true);
//					PromptDialog.Alert(ConStant.LOW_FLOW);
				}  else {
					OrderDialog.Dialog(this, ConStant.ORDER_FAIL, true);
//					PromptDialog.Alert(ConStant.ORDER_FAIL);
				}
			}
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_mobile_flow;
	}
}
