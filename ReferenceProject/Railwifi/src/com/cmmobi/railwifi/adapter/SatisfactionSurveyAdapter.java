package com.cmmobi.railwifi.adapter;

import java.util.ArrayList;
import java.util.List;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.network.GsonResponseObject.surveySubElem;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class SatisfactionSurveyAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	
	private Context ctx;
	protected DisplayMetrics dm;
	
	private List<surveySubElem> listItems;
	
	public SatisfactionSurveyAdapter(Context ctx) {
		this.ctx = ctx;
		this.inflater = LayoutInflater.from(ctx);
		listItems = new ArrayList<surveySubElem>();
	}
	
	public SatisfactionSurveyAdapter(Context ctx, List<surveySubElem> list) {
		this(ctx);
		if(list != null)
			listItems.addAll(list);
	}
	
	public void setData(List<surveySubElem> list){
		listItems.clear();
		listItems.addAll(list);
	}
	
	public List<surveySubElem> getList() {
		return listItems;
	}
	
	@Override
	public int getCount() {
		return listItems.size();
	}
	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		Object object = getItem(position);
		if(object instanceof surveySubElem) {
			final surveySubElem elem = (surveySubElem) object;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_item_survey, null);
				holder = new ViewHolder();
				
				ViewUtils.setHeight(convertView.findViewById(R.id.ll_item), 99);
				
				holder.tvSurvey = (TextView) convertView.findViewById(R.id.tv_survey);
				
				holder.rGroup = (RadioGroup) convertView.findViewById(R.id.rg_select);
				holder.rbtnGood = (RadioButton) convertView.findViewById(R.id.good);
				holder.rbtnNormal = (RadioButton) convertView.findViewById(R.id.good);
				holder.rbtnBad = (RadioButton) convertView.findViewById(R.id.good);
				holder.viewLine = (View) convertView.findViewById(R.id.view_line);
				
				holder.rGroup.setOnCheckedChangeListener(new myCheckedChangeListener(elem));
				
				adapterView(holder);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			bindHolder(holder, elem);
			if (position == getCount() - 1) {
				holder.viewLine.setVisibility(View.GONE);
			} else {
				holder.viewLine.setVisibility(View.VISIBLE);
			}
		}
		return convertView;
	}
	
	private void adapterView(ViewHolder h) {
		h.tvSurvey.setTextSize(DisplayUtil.textGetSizeSp(ctx, 36));
		h.rbtnGood.setTextSize(DisplayUtil.textGetSizeSp(ctx, 36));
		h.rbtnNormal.setTextSize(DisplayUtil.textGetSizeSp(ctx, 36));
		h.rbtnBad.setTextSize(DisplayUtil.textGetSizeSp(ctx, 36));
		
		ViewUtils.setMarginLeft(h.tvSurvey, 30);
		ViewUtils.setMarginRight(h.rGroup, 12);
	}
	
	private void bindHolder(ViewHolder h, surveySubElem elem) {
		//TODO
		h.tvSurvey.setText(elem.name);
	}
	
	private class myCheckedChangeListener implements OnCheckedChangeListener {

		private surveySubElem elem;
		
		public myCheckedChangeListener(surveySubElem item) {
			elem = item;
		}
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			Log.d("SatisfactionSurveyAdapter", "Checked ID " + checkedId);
			if(elem != null) {
				switch(checkedId) {
				case R.id.good:
					elem.checked = "1";
					break;
				case R.id.normal:
					elem.checked = "2";
					break;
				case R.id.bad:
					elem.checked = "3";
					break;
				default:
					elem.checked = "1";
					break;
				}
			}
		}
		
	}
	
	
	public class ViewHolder {
		TextView tvSurvey;
		RadioGroup rGroup;
		RadioButton rbtnGood, rbtnNormal, rbtnBad;
		View viewLine;
	}
}
