package com.cmmobi.railwifi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.cmmobi.common.tools.Info;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.adapter.TagAdapter;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.MyTextWatcher;
import com.cmmobi.railwifi.utils.StringUtils;

public class TagActivity extends TitleRootActivity implements OnItemClickListener, TextWatcher {
	public static final String KEY_TAG_ID = "_key_tag_id";
	public static final String KEY_TAG_NAME = "_key_tag_name";
	public static final String TAG_INTENT_TYPE = "tag_intent_type";
	public static final String KEY_TYPE = "_key_type";
	GridView tag_list;
	TagAdapter ta;
	ImageView btn_send_invest;
	EditText ed_input_invest;
	private int key_type;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setTitleText("标签");
		rightButton.setVisibility(View.GONE);
		
		findViewById(R.id.tv_not_use).requestFocus();
		
		tag_list = (GridView)findViewById(R.id.tag_list);
		ed_input_invest = (EditText)findViewById(R.id.ed_input_invest);
		btn_send_invest = (ImageView)findViewById(R.id.btn_send_invest);
		
		ed_input_invest.addTextChangedListener(this);  
		
		ta = new TagAdapter(this);
		tag_list.setAdapter(ta);
		tag_list.setOnItemClickListener(this);
		
		btn_send_invest.setOnClickListener(this);
		ed_input_invest.setOnFocusChangeListener(MyTextWatcher.onFocusAutoClearHintListener);
		if (StringUtils.isBlank(ed_input_invest.getText().toString())) {
			btn_send_invest.setEnabled(false);
		} else {
			btn_send_invest.setEnabled(true);
		}
		
		key_type = getIntent().getIntExtra(KEY_TYPE, 0);
		
		Requester.requestLabelList(handler, key_type/*GsonResponseObject.MEDIA_TYPE_MOVIE*/);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case Requester.RESPONSE_TYPE_FEED_BACK:
			GsonResponseObject.commonContent r30 = (GsonResponseObject.commonContent)msg.obj;
			if(r30!=null && r30.status!=null && r30.status.equals("0")){
				Toast.makeText(this, "提交成功", Toast.LENGTH_LONG).show();
				ed_input_invest.setText(null);
			}else{
				Toast.makeText(this, "提交失败", Toast.LENGTH_LONG).show();
			}
			
			break;
			
		case Requester.RESPONSE_TYPE_LABEL_LIST:
			GsonResponseObject.labelListResp r33 = (GsonResponseObject.labelListResp)msg.obj;
			if(r33!=null && r33.status!=null && r33.status.equals("0")){
				ta.setData(r33.list);
				ta.notifyDataSetChanged();
			}
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_tag;
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.btn_send_invest:
			CmmobiClickAgentWrapper.onEvent(this, "feedback","1");
			
			String trainNum = "";
			if (MainActivity.train_num != null) {
				trainNum = MainActivity.train_num;
			} else {
				trainNum = "NULL";
			}
			Requester.requestFeedBack(handler,Info.getDevId(this), "2", null, ed_input_invest.getEditableText().toString(),trainNum);
			break;
			
		default:
				
			super.onClick(v);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		GsonResponseObject.labelElem bean = (GsonResponseObject.labelElem) parent.getItemAtPosition(position);
		Bundle bundle = new Bundle();
		bundle.putString(KEY_TAG_ID, bean.id);
		bundle.putString(KEY_TAG_NAME, bean.name);
		CmmobiClickAgentWrapper.onEvent(this, "av_tag", bean.id);
		Intent mIntent = new Intent();
		mIntent.putExtras(bundle);
		setResult(RESULT_OK, mIntent);
		finish();

		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if (StringUtils.isBlank(ed_input_invest.getText().toString())) {
			btn_send_invest.setEnabled(false);
		} else {
			btn_send_invest.setEnabled(true);
		}
	}

}
