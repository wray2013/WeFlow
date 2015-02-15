package com.cmmobi.railwifi.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cmmobi.common.tools.Info;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.feedBackElem;
import com.cmmobi.railwifi.network.GsonResponseObject.feedbacklistResp;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.MyTextWatcher;
import com.cmmobi.railwifi.utils.StringUtils;
import com.cmmobi.railwifi.utils.ViewUtils;


public class FeedBackActivity extends TitleRootActivity implements TextWatcher {

	/*private ToggleButton tbContentLack;
	private ToggleButton tbClassificationUnclear;
	private ToggleButton tbOperatingInconvenient;
	private ToggleButton tbFilmplaySlow;*/
	
	private EditText etContent;
	
	private ListView lvFeedBack;
	private ArrayList<feedBackElem> feedBackList = new ArrayList<feedBackElem>();
	private SparseBooleanArray toggleArray = new SparseBooleanArray();
	private FeedBackAdapter adapter = null;
	
	private int maxHeight = 0;

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_FEEDBACK_LIST:
			if (msg.obj != null) {
				feedbacklistResp resp = (feedbacklistResp) msg.obj;
				if ("0".equals(resp.status) && resp.feedbacklist != null && resp.feedbacklist.length > 0) {
					Collections.addAll(feedBackList, resp.feedbacklist);
					adapter.notifyDataSetChanged();
					getRightButton().setEnabled(true);
					
					int listHeight = cn.trinea.android.common.util.ViewUtils.getListViewHeightBasedOnChildren(lvFeedBack);
					Log.d("=AAA=","listHeight = " + listHeight + " maxHeight = " + maxHeight);
					if (listHeight > maxHeight) {
						listHeight = maxHeight - 12;
						ViewUtils.setHeight(lvFeedBack, listHeight);
					}
					ViewUtils.setHeightPixel(lvFeedBack, listHeight);
					
				} else {
				}
			} else {
				
			}
			break;
		case Requester.RESPONSE_TYPE_FEED_BACK:
			if (msg.obj != null) {
				GsonResponseObject.commonContent resp = (GsonResponseObject.commonContent) msg.obj;
				if ("0".equals(resp.status)) {
					Toast.makeText(this, "发送成功", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(this, "发送失败", Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(this, "发送失败", Toast.LENGTH_LONG).show();
			}
			finish();
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_feedback;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		CmmobiClickAgentWrapper.onEvent(this, "feedback","2");
		CmmobiClickAgentWrapper.onEvent(this, "empty_1", "2");
		setTitleText("意见反馈");
		setRightButtonText("提交>>");
		
		
		getRightButton().setEnabled(false);
		
		maxHeight = DisplayUtil.getScreenHeight(this) - 
				DisplayUtil.getSize(this, 50) -
				DisplayUtil.getSize(this, 96) -
				DisplayUtil.getSize(this, 12) -
				DisplayUtil.getSize(this, 302) -
				DisplayUtil.getSize(this, 24);
		
		initViews();
		Requester.requestFeedBackList(handler);
	}
	
	private void initViews() {
		findViewById(R.id.tv_not_use).requestFocus();
		
		lvFeedBack = (ListView) findViewById(R.id.lv_feedback_reason);
		etContent = (EditText) findViewById(R.id.et_content);
		
		etContent.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
		
		etContent.addTextChangedListener(this);
		
		
		adapter = new FeedBackAdapter(this, feedBackList);
		lvFeedBack.setAdapter(adapter);
		
		ViewUtils.setMarginTop(lvFeedBack, 12);
		
		ViewUtils.setMarginLeft(etContent, 12);
		ViewUtils.setMarginRight(etContent, 12);
		ViewUtils.setMarginTop(etContent, 30);
//		ViewUtils.setMarginBottom(etContent, 24);
		ViewUtils.setHeight(etContent, 302);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_title_right:
			String ids = "";
			if (toggleArray.size() ==0) {
				PromptDialog.Dialog(this, "温馨提示", "请选择一项内容吧", "确认");
				return;
			}
			if (StringUtils.isEmpty(etContent.getText().toString().trim())) {
				PromptDialog.Dialog(this, "温馨提示", "请填写一些意见吧", "确认");
				return;
			}
			for (int i = 0;i < toggleArray.size();i++) {
				int key = toggleArray.keyAt(i);
				ids += "" + key + ",";
			}
			if (ids.endsWith(",")) {
				ids = ids.substring(0,ids.length() - 1);
			}
			String trainNum = "";
			if (MainActivity.train_num != null) {
				trainNum = MainActivity.train_num;
			} else {
				trainNum = "NULL";
			}
			Requester.requestFeedBack(handler, Info.getDevId(this),"1", ids, etContent.getText().toString(),trainNum);
			
			break;
		case R.id.rl_feedback:
			FeedBackHolder holder = (FeedBackHolder)v.getTag();
			holder.tbBtn.toggle();
			toggleArray.clear();
			if (holder.tbBtn.isChecked()) {
				toggleArray.put(Integer.parseInt(holder.id), true);
			} else {
				toggleArray.delete(Integer.parseInt(holder.id));
			}
			adapter.notifyDataSetChanged();
			break;
		}
		super.onClick(v);
	}
	
	public class FeedBackAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private Context context;
		private List<feedBackElem> elemList;
		public FeedBackAdapter(Context context,List<feedBackElem> list) {
			this.context = context;
			inflater = LayoutInflater.from(context);
			elemList = list;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return elemList.size();
		}

		@Override
		public feedBackElem getItem(int position) {
			// TODO Auto-generated method stub
			return elemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			FeedBackHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_feedback, null);
				holder = new FeedBackHolder();
				
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_feedback_title);
				holder.tbBtn = (ToggleButton) convertView.findViewById(R.id.tb_feedback_switch);
				
				ViewUtils.setMarginRight(holder.tbBtn, 45);
				ViewUtils.setHeight(holder.tvTitle, 125);
				ViewUtils.setMarginLeft(holder.tvTitle, 24);
				ViewUtils.setMarginLeft(convertView.findViewById(R.id.view_bottom_feedback), 12);
				ViewUtils.setMarginRight(convertView.findViewById(R.id.view_bottom_feedback), 12);
				holder.tvTitle.setTextSize(DisplayUtil.textGetSizeSp(context, 36));
				convertView.setTag(holder);
			} else {
				holder = (FeedBackHolder) convertView.getTag();
			}
			
			if (position == feedBackList.size() -1) {
				convertView.findViewById(R.id.view_bottom_feedback).setVisibility(View.GONE);
			} else {
				convertView.findViewById(R.id.view_bottom_feedback).setVisibility(View.VISIBLE);
			}
			
			convertView.setOnClickListener(FeedBackActivity.this);
			feedBackElem elem = getItem(position);
			holder.tvTitle.setText(elem.name);
			holder.id = elem.feedbacktypeid;
			
			if (toggleArray.get(Integer.parseInt(holder.id))) {
				holder.tbBtn.setChecked(true);
			} else {
				holder.tbBtn.setChecked(false);
			}
			return convertView;
		}
		
	}
	
	class FeedBackHolder {
		TextView tvTitle;
		ToggleButton tbBtn;
		String id;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		Log.d("=AAA=","FeedBackActivity onDestroy");
		super.onDestroy();
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

	//输入表情前的光标位置
	private int cursorPos;

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		etContent.removeTextChangedListener(this);
		String editNumOri = etContent.getText().toString();
		cursorPos = etContent.getSelectionStart();
		String editNum = MyTextWatcher.removeExpression(editNumOri);
		if (!editNum.equals(editNumOri)) {
			
			if (cursorPos > editNum.length()) {
				cursorPos = editNum.length();
			}
			etContent.setText(editNum);
			etContent.setSelection(cursorPos);
			
		}
		etContent.addTextChangedListener(this);
	}
	

}
