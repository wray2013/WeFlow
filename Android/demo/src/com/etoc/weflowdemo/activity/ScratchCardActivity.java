package com.etoc.weflowdemo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import cn.trinea.android.common.util.RandomUtils;

import com.etoc.weflowdemo.MainApplication;
import com.etoc.weflowdemo.R;
import com.etoc.weflowdemo.net.GsonResponseObject.lotteryResponse;
import com.etoc.weflowdemo.net.Requester;
import com.etoc.weflowdemo.util.DisplayUtil;
import com.etoc.weflowdemo.view.ScratchTextView;
import com.etoc.weflowdemo.view.ScratchTextView.OnCompletedListener;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ScratchCardActivity extends TitleRootActivity {

	private ImageView ivCover;
	private Button btnStartLottery;
	private ScratchTextView stvCard;
	private GridView gvAward;
	private String Tel = "";
	
	private boolean isRetry = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initViews();
	}
	
	public void initViews() {
		Log.e("ScratchCardActivity", "initViews IN!");
		setLeftButtonBackground(R.drawable.btn_back);
		hideRightButton();
		setTitleText("刮刮卡");
		
		String phoneNum = getIntent().getStringExtra("phone");
		if(phoneNum != null) {
			Tel = phoneNum;
		}
		
		btnStartLottery = (Button) findViewById(R.id.iv_start_lottery);
		btnStartLottery.setVisibility(View.VISIBLE);
		btnStartLottery.setOnClickListener(this);
		btnStartLottery.setBackgroundResource(R.color.bg_red);
		
		ivCover = (ImageView) findViewById(R.id.iv_cover);
		ivCover.setVisibility(View.VISIBLE);
//		ivCover.setOnClickListener(this);
		
		stvCard = (ScratchTextView) findViewById(R.id.stv_card);
		LayoutParams cardlp = stvCard.getLayoutParams();
		cardlp.width  = DisplayUtil.getSize(this, 688);
		cardlp.height = DisplayUtil.getSize(this, 488);
		stvCard.setLayoutParams(cardlp);
		stvCard.initScratchCard(R.drawable.scratch_bg, 0, DisplayUtil.getSize(this, 50), 1f);
		stvCard.setCompletePercent(45);
		stvCard.setOnCompletedListener(new OnCompletedListener() {
			@Override
			public void OnCompleted() {
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						ivCover.setVisibility(View.GONE);
						btnStartLottery.setText("再刮一次");
						btnStartLottery.setTextColor(getResources().getColor(R.color.bg_red));
						btnStartLottery.setBackgroundResource(R.color.bg_yellow);
						btnStartLottery.setVisibility(View.VISIBLE);
//						randomAward();
//						stvCard.resetScratchCard(R.drawable.scratch_bg, 0);
					}
				});
			}
		});
		randomAward();
		
		gvAward = (GridView) findViewById(R.id.gv_award);
		makeFakeData(gvAward);
		
		/*TextView hint = (TextView) findViewById(R.id.tv_flow_hint);
		hint.setOnClickListener(this);*/
	}
	
	private static String[] items = {
		"iphone6",
		"海外流量卡",
		"运动手环",
		"巴厘岛浪漫7日游",
		"海陆双拼套餐",
		"罗技键鼠套装"
	};
	
	private void makeFakeData(GridView gv) {
		// 生成动态数组，并且转入数据
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 6; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.ic_launcher);// 添加图像资源的ID
			map.put("ItemText", items[i]);// 按序号做ItemText
			lstImageItem.add(map);
		}
		// 生成适配器的ImageItem <====> 动态数组的元素，两者一一对应
		SimpleAdapter saImageItems = new SimpleAdapter(this, lstImageItem,// 数据来源
				R.layout.grid_award_item,// night_item的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "ItemImage", "ItemText" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.iv_item_image, R.id.tv_item_text });
		// 添加并且显示
		gv.setAdapter(saImageItems);
	}
	
	private void randomAward() {
		int i = RandomUtils.getRandom(20);
		if(i < 6) {
			stvCard.setText(items[i]);
		} else {
			stvCard.setText("谢谢参与");
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.iv_start_lottery:
			Requester.lotteryRequest(handler, Tel);
			
			/*randomAward();
			stvCard.resetScratchCard(R.drawable.scratch_bg, 0);
			if(isRetry) {
//				ivCover.setVisibility(View.VISIBLE);
//				btnStartLottery.setVisibility(View.VISIBLE);
			} else {
				ivCover.setVisibility(View.GONE);
			}
			btnStartLottery.setVisibility(View.GONE);
			isRetry = true;*/
			break;
		case R.id.tv_flow_hint:
			ivCover.setVisibility(View.VISIBLE);
			btnStartLottery.setVisibility(View.VISIBLE);
			break;
		}
		super.onClick(v);
	}
	
	private void startLottery() {
		randomAward();
		stvCard.resetScratchCard(R.drawable.scratch_bg, 0);
		if(isRetry) {
//			ivCover.setVisibility(View.VISIBLE);
//			btnStartLottery.setVisibility(View.VISIBLE);
		} else {
			ivCover.setVisibility(View.GONE);
		}
		btnStartLottery.setVisibility(View.GONE);
		isRetry = true;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_LOTTERY:
			lotteryResponse response = (lotteryResponse) msg.obj;
			if(response != null && response.code != null) {
				if(response.code.equals("0000")) {
					startLottery();
				} else if(response.code.equals("2012")) {
					Toast.makeText(MainApplication.getAppInstance(), "您的流量币余额不足", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(MainApplication.getAppInstance(), "请求失败 [" + response.code + ":" + response.message + "]", Toast.LENGTH_LONG).show();
				}
			}
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_scratchcard;
	}

}
