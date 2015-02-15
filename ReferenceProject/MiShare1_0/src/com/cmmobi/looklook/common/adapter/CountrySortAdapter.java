package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CountryBean;

public class CountrySortAdapter extends BaseAdapter implements SectionIndexer{


	private List<CountryBean> data = new ArrayList<CountryBean>();

	private CountryBean defaultCountry;
	
	private Context context;

	public CountrySortAdapter(Context context) {
		this.context = context;

	}

	/**
	 * 载入county数据
	 * @param _data
	 */
	public void setData(ArrayList<CountryBean> _data){
		if(_data==null || _data.size()==0){
			return;
		}
		data = _data;
	}

	/**
	 * 设置选中国家
	 * @param item
	 */
	public void setDefaultCountry(CountryBean item){
		if(item == null || TextUtils.isEmpty(item.countryName) 
								  || TextUtils.isEmpty(item.countryNo)){
			return;
		}
		defaultCountry = item;
	}
	
	@Override
	public int getCount() {
		return data.size();
	}
	
	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_country_sort,null);
			
			viewHolder = new ViewHolder();
			viewHolder.mLlyAlpha = (LinearLayout) convertView.findViewById(R.id.lly_alpha);
			viewHolder.mTvAlpha = (TextView) convertView.findViewById(R.id.alpha);
			viewHolder.mImgvOk = (ImageView) convertView.findViewById(R.id.iv_ok);
			viewHolder.mTvCountry = (TextView) convertView.findViewById(R.id.tv_country);
			viewHolder.mTvCountryNo = (TextView) convertView.findViewById(R.id.tv_country_no);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		CountryBean item = data.get(position);
		
		
		viewHolder.mTvCountry.setText(item.countryName);
		viewHolder.mTvCountryNo.setText(item.countryNo);
		viewHolder.mImgvOk.setVisibility(View.INVISIBLE);
		
		String currentStr = getAlpha(data.get(position).sortKey);
		String previewStr = (position - 1) >= 0 ? getAlpha(data
				.get(position - 1).sortKey) : " ";
		if(currentStr.equals(previewStr)){
			viewHolder.mLlyAlpha.setVisibility(View.GONE);
		}else{
			viewHolder.mLlyAlpha.setVisibility(View.VISIBLE);
			viewHolder.mTvAlpha.setText(currentStr);
		}
			
		
		if(defaultCountry == null){
			
		}else{
			
			if(item.equals(defaultCountry)){
				viewHolder.mImgvOk.setVisibility(View.VISIBLE);
			}
			
		}
		
		
		return convertView;
	}

	class ViewHolder {

		LinearLayout mLlyAlpha;
		TextView mTvAlpha;
		ImageView mImgvOk;
		TextView mTvCountry;
		TextView mTvCountryNo;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(); // 大写输出
		} else if (c == '★') {
			return "★";
		} else{
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < data.size(); i++) {
			char key = getAlpha(data.get(i).sortKey).charAt(0);
			if (key == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

}
