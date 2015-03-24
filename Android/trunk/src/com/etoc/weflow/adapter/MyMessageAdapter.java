package com.etoc.weflow.adapter;

import java.util.ArrayList;
import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.net.GsonResponseObject.*;
import com.etoc.weflow.utils.DateUtils;
import com.etoc.weflow.utils.NumberUtils;
import com.etoc.weflow.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 我的消息适配器
 * @author Ray
 *
 */
public class MyMessageAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Context ctx;
	
	private List<MessageList> MessageList = new ArrayList<MessageList>();
	
	
	public MyMessageAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		ctx = context;
	}
	
	public void setData(List<MessageList> data) {
		// TODO Auto-generated method stub
		MessageList.clear();
		MessageList.addAll(data);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return MessageList.size();
	}

	@Override
	public MessageList getItem(int position) {
		// TODO Auto-generated method stub
		return MessageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		Object object = getItem(position);
		if(object instanceof MessageList) {
			MessageList mymsg = (MessageList) object;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.activity_msg_list_item, null);
				holder = new ViewHolder();
				holder.ivTypeIcon = (ImageView) convertView.findViewById(R.id.iv_type_icon);
				holder.tvSubTitle = (TextView) convertView.findViewById(R.id.tv_subtitle);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
				holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
				holder.tvCoins = (TextView) convertView.findViewById(R.id.tv_coins);
				holder.tvDate  = (TextView) convertView.findViewById(R.id.tv_date);
				initViews(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			bindBill(holder, mymsg);
		}
		return convertView;
	}
	
	private void initViews(View View) {
		// TODO Auto-generated method stub
		ViewUtils.setHeight(View.findViewById(R.id.iv_type_icon), 120);
		ViewUtils.setWidth(View.findViewById(R.id.iv_type_icon), 120);
		ViewUtils.setHeight(View.findViewById(R.id.view_height), 145);
		ViewUtils.setHeight(View.findViewById(R.id.tv_subtitle), 65);
		ViewUtils.setMarginBottom(View.findViewById(R.id.tv_date), 20);
		ViewUtils.setMarginTop(View.findViewById(R.id.tv_title), 20);
		ViewUtils.setMarginLeft(View.findViewById(R.id.rl_bill_right_title), 32);
		ViewUtils.setMarginLeft(View.findViewById(R.id.tv_subtitle), 32);
		ViewUtils.setMarginRight(View.findViewById(R.id.tv_coins), 32);
		ViewUtils.setMarginRight(View.findViewById(R.id.tv_date), 32);
		
		ViewUtils.setTextSize(View.findViewById(R.id.tv_title), 30);
		ViewUtils.setTextSize(View.findViewById(R.id.tv_content), 30);
		ViewUtils.setTextSize(View.findViewById(R.id.tv_coins), 30);
		ViewUtils.setTextSize(View.findViewById(R.id.tv_date), 20);
	}
	
	private void bindBill(ViewHolder holder, MessageList mymsg) {
		// TODO Auto-generated method stub
		if(mymsg.picurl != null && !mymsg.picurl.equals("")) {
			ImageLoader.getInstance().displayImage(mymsg.picurl, holder.ivTypeIcon);
		} else {
			holder.ivTypeIcon.setBackgroundResource(R.drawable.ic_launcher);
		}
		holder.tvTitle.setText(mymsg.title);
		
		if(mymsg.content != null && !mymsg.content.equals("")) {
			holder.tvContent.setText(mymsg.content);
			holder.tvContent.setVisibility(View.VISIBLE);
		} else {
			holder.tvContent.setText("空");
			holder.tvContent.setVisibility(View.INVISIBLE);
		}
		
		int coins = 0;
		if(mymsg.flowcoins != null) {
			try {
				coins = NumberUtils.Str2Int(mymsg.flowcoins);
				if(coins > 0) {
					holder.tvCoins.setText("+" + coins + "流量币");
					holder.tvCoins.setTextColor(ctx.getResources().getColor(R.color.pagertab_color_orange));
				} else if(coins < 0) {
					holder.tvCoins.setText(coins + "流量币");
					holder.tvCoins.setTextColor(ctx.getResources().getColor(R.color.pagertab_color_green));
				} else {
					holder.tvCoins.setText("");
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		String currentYearMonth = DateUtils.getYMStringFromMilli(System.currentTimeMillis() + "");
		String YM = DateUtils.getYMStringFromMilli(mymsg.time);
		int index = MessageList.indexOf(mymsg);
		if(index > 0) {
			MessageList lastItem = MessageList.get(index - 1);
			String LastYM = DateUtils.getYMStringFromMilli(lastItem.time);
			if(LastYM != null && !LastYM.equals("") && LastYM.equals(YM)) {
				showSubTitle(holder, false);
			} else {
				showSubTitle(holder, true);
			}
		} else {
			showSubTitle(holder, true);
		}
		holder.tvDate.setText(DateUtils.getStringFromMilli(mymsg.time, DateUtils.DATE_FORMAT_NORMAL_1));
		
		String subtitle = DateUtils.getStringFromMilli(mymsg.time, "M月");
		if (currentYearMonth.equals(YM)) {
			subtitle = "本月";
		} else if (!DateUtils.getStringFromMilli(
				System.currentTimeMillis() + "", "yyyy").equals(
				DateUtils.getStringFromMilli(mymsg.time, "yyyy"))) {
			subtitle = DateUtils.getStringFromMilli(mymsg.time, "yyyy年M月");
		}
		holder.tvSubTitle.setText(subtitle);
	}
	
	private void showSubTitle(ViewHolder holder, boolean isShow) {
		// TODO Auto-generated method stub
		holder.tvSubTitle.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}


	public class ViewHolder {
		ImageView ivTypeIcon;
		TextView tvSubTitle, tvTitle, tvContent, tvCoins, tvDate;
	}

}
