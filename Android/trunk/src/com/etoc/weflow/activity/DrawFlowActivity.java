package com.etoc.weflow.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.adapter.DrawFlowAdapter;
import com.etoc.weflow.utils.ViewUtils;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

/**
 * 取流量币
 * @author Ray
 *
 */
public class DrawFlowActivity extends TitleRootActivity {

	private TextView tvTotal, tvBtnDraw, tvWarning;
	private GridView gvValue;
	private DrawFlowAdapter adapter = null;
	
	private List<String> values = new ArrayList<String>();
	private int total = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		if(i != null) {
			String[] v = i.getStringArrayExtra("values");
			if(v != null && v.length > 0) {
				values.clear();
				values.addAll(Arrays.asList(v));
			}
			
			total = i.getIntExtra("total", 0);
			
		}
		initView();
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		setTitleText("提取流量币");
		hideRightButton();
		
		tvWarning = (TextView) findViewById(R.id.tv_warning);
		tvWarning.setVisibility(View.GONE);
		tvBtnDraw = (TextView) findViewById(R.id.tv_btn_draw);
		
		tvTotal = (TextView) findViewById(R.id.tv_draw_top_total);
		tvTotal.setText(total + "");
		gvValue = (GridView) findViewById(R.id.gv_draw_values);
		gvValue.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new DrawFlowAdapter(this, values);
		gvValue.setAdapter(adapter);
		gvValue.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				adapter.setSelect(position);
				adapter.notifyDataSetChanged();
				int selvalue = adapter.getSelectValue();
				refreshBtnStatus(selvalue);
			}
		});
		
		refreshBtnStatus(adapter.getSelectValue());
		
		ViewUtils.setHeight(findViewById(R.id.tv_draw_top_tel_hint), 96);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_draw_top_tel_hint), 32);
		
		ViewUtils.setMarginLeft(findViewById(R.id.v_divider_h), 32);
		ViewUtils.setMarginRight(findViewById(R.id.v_divider_h), 32);
		
		ViewUtils.setMarginTop(findViewById(R.id.tv_draw_top_total_hint), 38);
		ViewUtils.setMarginTop(findViewById(R.id.tv_draw_top_total), 50);
		ViewUtils.setMarginTop(findViewById(R.id.v_divider_top), 50);
		
		ViewUtils.setMarginTop(findViewById(R.id.rl_draw_center), 30);
		ViewUtils.setMarginTop(findViewById(R.id.tv_draw_center_values), 40);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_draw_center_values), 32);
		ViewUtils.setMarginTop(findViewById(R.id.v_divider_center_h), 30);
		ViewUtils.setMarginLeft(findViewById(R.id.v_divider_center_h), 32);
		ViewUtils.setMarginRight(findViewById(R.id.v_divider_center_h), 32);
		
		ViewUtils.setMarginTop(findViewById(R.id.rl_draw_bottom), 20);
		ViewUtils.setMarginTop(findViewById(R.id.tv_btn_draw), 42);
		ViewUtils.setMarginBottom(findViewById(R.id.rl_draw_bottom), 42);
		
		ViewUtils.setTextSize(findViewById(R.id.tv_draw_top_tel_hint), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_draw_top_tel), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_draw_top_total_hint), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_draw_top_total), 85);
		
		ViewUtils.setTextSize(findViewById(R.id.tv_draw_center_values), 28);
		ViewUtils.setTextSize(findViewById(R.id.tv_warning), 28);
		ViewUtils.setTextSize(findViewById(R.id.tv_btn_draw), 35);
	}
	
	private void refreshBtnStatus(int selectedValue) {
		if(selectedValue >= 0 && selectedValue > total) {
			tvWarning.setVisibility(View.VISIBLE);
			tvBtnDraw.setClickable(false);
			tvBtnDraw.setBackgroundResource(R.drawable.shape_corner_recentage_grey);
		} else {
			tvWarning.setVisibility(View.GONE);
			tvBtnDraw.setClickable(true);
			tvBtnDraw.setBackgroundResource(R.drawable.shape_corner_recentage_orange);
		}
	}
	
	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_draw_flow;
	}
	
	

}
