package com.cmmobi.railwifi.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.adapter.SatisfactionSurveyAdapter;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.surveySubElem;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.MyTextWatcher;
import com.cmmobi.railwifi.utils.StringUtils;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.cmmobi.railwifi.view.ListDialog;

public class SatisfactionSurveyActivity extends TitleRootActivity implements TextWatcher {

	private EditText etName;
	private EditText etCellphone;
	private EditText etContent;
	private TextView tvType;
	
	private ListDialog listDialog;
	private ImageView ivPhoneError;
	private ImageView ivNameError;
	
	private ListView lvSurvey;
	private SatisfactionSurveyAdapter SurveyAdaper;
	
	private List<surveySubElem> surveyList;
	
	public int itemHeight = 0;
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_SURVEY_LIST:
			GsonResponseObject.surveylistResp listresp = (GsonResponseObject.surveylistResp) msg.obj;
			if(listresp != null && "0".equals(listresp.status)) {
				surveySubElem[] sublist = listresp.surveylist;
				if(sublist != null && sublist.length > 0) {
					List<surveySubElem> tmpList = Arrays.asList(sublist);
					if(SurveyAdaper != null) {
						SurveyAdaper.setData(tmpList);
						SurveyAdaper.notifyDataSetChanged();
						setListViewHeightBasedOnChildren(lvSurvey);
					}
					getRightButton().setEnabled(true);
				}
			}
			break;
		case Requester.RESPONSE_TYPE_SURVEY:
			GsonResponseObject.surveyResp resp = (GsonResponseObject.surveyResp) msg.obj;
			if(resp != null && "0".equals(resp.status)){
				Toast.makeText(this, "发送成功", Toast.LENGTH_LONG).show();
				finish();
			}else{
				Toast.makeText(this, "发送失败", Toast.LENGTH_LONG).show();
			}
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_satifaction_surver;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("旅客满意度调查");
		
		setRightButtonText("发送>>");
		getRightButton().setEnabled(false);
		
		initViews();
	}
	
	private void initViews() {
		etName = (EditText) findViewById(R.id.et_contact_name);
		etCellphone = (EditText) findViewById(R.id.et_contact_celphome);
		tvType = (TextView) findViewById(R.id.tv_type_name);
		etContent = (EditText) findViewById(R.id.et_complaints);
		ivPhoneError = (ImageView) findViewById(R.id.iv_phone_error);
		ivNameError = (ImageView) findViewById(R.id.iv_name_error);
		etCellphone.addTextChangedListener(new PhoneTextWatcher(etCellphone));
		etName.addTextChangedListener(new NameTextWatcher(etName));
		
		etName.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		etCellphone.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		tvType.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		
		((TextView)findViewById(R.id.tv_contact_name)).setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		((TextView)findViewById(R.id.tv_contact_celphome)).setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		((TextView)findViewById(R.id.tv_type)).setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		
		etContent.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		etContent.addTextChangedListener(this);
		
		surveyList = new ArrayList<surveySubElem>();
		/*for(int i = 0; i < 6; i ++) {
			surveySubElem elem = new surveySubElem();
			elem.surveytypeid = i + "";
			elem.name = "调查项" + i;
			elem.checked = "2";
			surveyList.add(elem);
		}*/
		/*tmpList.add({"","乘务员服务态度",""});
		tmpList.add("卫生间整洁度");
		tmpList.add("商品送达时间");
		tmpList.add("所在车厢保洁频率");
		tmpList.add("这是些什么乱七八糟的");
		tmpList.add("还能不能愉快的玩耍了");*/
		
		SurveyAdaper = new SatisfactionSurveyAdapter(this, surveyList);
		SurveyAdaper.setData(surveyList);
		
		lvSurvey = (ListView) findViewById(R.id.lv_survey);
		lvSurvey.setAdapter(SurveyAdaper);
		setListViewHeightBasedOnChildren(lvSurvey);
		
		etContent.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        }); 
		
		ViewUtils.setMarginRight(ivPhoneError, 12);
		ViewUtils.setMarginRight(ivNameError, 12);
		
		ViewUtils.setMarginTop(findViewById(R.id.rl_user_info), 12);
		ViewUtils.setMarginLeft(findViewById(R.id.rl_user_info), 12);
		ViewUtils.setMarginRight(findViewById(R.id.rl_user_info), 12);
		
		ViewUtils.setMarginTop(findViewById(R.id.tv_contact_name), 24);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_contact_name), 12);
		
		ViewUtils.setMarginRight(findViewById(R.id.iv_icon_arrow), 24);
		ViewUtils.setSize(findViewById(R.id.tv_type), 150, 99);
		ViewUtils.setSize(findViewById(R.id.rl_type), 522, 99);
		
		ViewUtils.setSize(findViewById(R.id.tv_contact_name), 150, 99);
		ViewUtils.setSize(findViewById(R.id.tv_contact_celphome), 150, 99);
		ViewUtils.setSize(etName, 522, 99);
		ViewUtils.setSize(etCellphone, 522, 99);
		ViewUtils.setHeight(etContent, 228);
		ViewUtils.setMarginTop(etContent, 12);
		ViewUtils.setMarginTop(lvSurvey, 12);
		
		findViewById(R.id.rl_type).setOnClickListener(this);
		
		listDialog = new ListDialog(this,tvType);
		listDialog.setTitle("内容类型");
		List<String> listStr = new ArrayList<String>();
		listStr.add("建议");
		listStr.add("投诉");
		listDialog.setDate(listStr);
		
		String trainNum = "";
		if (MainActivity.train_num != null) {
			trainNum = MainActivity.train_num;
		} else {
			trainNum = "NULL";
		}
		Requester.requestSurveyList(handler, trainNum);
	}
	
	/***
     * 动态设置listview的高度
     * 
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
//    	if(!TAG_ON) return;
        BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();
        if (listAdapter == null || listAdapter.getCount() <= 0) {
            return;
        }
        
        if(itemHeight <= 0) {
//        	Log.e("XXX", "[" + itemHeight + "] setListViewHeightBasedOnChildren getView !");
        	View listItem = listAdapter.getView(0, null, listView);
        	listItem.measure(0, 0);
        	itemHeight = listItem.getMeasuredHeight();
        }
        
        int totalHeight = 0;
        /*for (int i = 0; i < listAdapter.getCount(); i++) {
        	View listItem = listAdapter.getView(i, null, listView);
        	listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }*/
        totalHeight = listAdapter.getCount() * itemHeight;
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // params.height += 5;// if without this statement,the listview will be
        // a
        // little short
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
    
    /**
     * 
     * @return "survey" : "1:1,2:1,3:1"//(surveyid : 1, surveyid :2)(1,好,2,中,3差)
     */
    public String combineChecked() {
    	String result = "";
    	if(SurveyAdaper != null) {
    		List<surveySubElem> subElem = SurveyAdaper.getList();
    		if(subElem != null) {
    			for(surveySubElem sub : subElem) {
    				if(sub.checked == null) {
    					sub.checked = "1";
    				}
    				String item = "," + sub.surveytypeid + ":" + sub.checked;
    				result += item;
    			}
    			result = result.replaceFirst(",", "");
    		}
    	}
    	return result;
    }
    
	class NameTextWatcher extends MyTextWatcher {

		public NameTextWatcher(EditText et) {
			super(et);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			ivNameError.setVisibility(View.GONE);
			super.onTextChanged(s, start, before, count);
		}
	}
	
	class PhoneTextWatcher extends MyTextWatcher {

		public PhoneTextWatcher(EditText et) {
			super(et);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			ivPhoneError.setVisibility(View.GONE);
			super.onTextChanged(s, start, before, count);
		}
	}
	
	long currentTime = 0;
	int currentRbId = 0;
	int currentRat = 5;
	OnRatingBarChangeListener ratingBarListener = new OnRatingBarChangeListener() {
		
		@Override
		public void onRatingChanged(RatingBar ratingBar, float rating,
				boolean fromUser) {
			// TODO Auto-generated method stub
			Log.d("=AAA=","rating = " + rating + " fromUser = " + fromUser);
			if (fromUser) {
				int ratingInt = (int) rating;
				
				
				int rat = 0;
				if (rating - ratingInt < 0.1) {
					rat = ratingInt;
				} else {
					rat = ratingInt + 1;
				}
				
				if (currentRbId == ratingBar.getId()
						&& rat == 1 && currentRat == 1
						&& (System.currentTimeMillis() - currentTime) <= 400
						) {// 当只有一颗星亮时，双击此星星熄灭。
					rat = 0;
				}
				Log.d("=AAA=","currentRbId = " + currentRbId + " getId = " + ratingBar.getId()
						+"ratingInt = " + ratingInt + " currentRat = " + currentRat
						+"System.currentTimeMillis() = " + System.currentTimeMillis()
						+ "currentTime = " + currentTime);
				currentRat = rat;
				currentTime = System.currentTimeMillis();
				currentRbId = ratingBar.getId();
				ratingBar.setRating(rat);
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		InputMethodManager imm = (InputMethodManager) this
				  .getSystemService(Context.INPUT_METHOD_SERVICE);
		switch(v.getId()) {
		case R.id.btn_title_right:
			if (TextUtils.isEmpty(etName.getText())) {
				etName.requestFocus();
				etName.setHintTextColor(0xffc60606);
				imm.showSoftInput(etName, 0);
				return;
			}else if(!PromptDialog.checkName(etName.getText().toString().trim())){
				etName.requestFocus();
				ivNameError.setVisibility(View.VISIBLE);
				imm.showSoftInput(etName, 0);
				return;
			}
			if (TextUtils.isEmpty(etCellphone.getText())) {
				etCellphone.requestFocus();
				etCellphone.setHintTextColor(0xffc60606);
				imm.showSoftInput(etCellphone, 0);
				return;
			} else if (!PromptDialog.checkPhoneNum(etCellphone.getText().toString())) {
				etCellphone.requestFocus();
				ivPhoneError.setVisibility(View.VISIBLE);
				imm.showSoftInput(etCellphone, 0);
				return;
			}
			
			/*if (StringUtils.isEmpty(etContent.getText().toString().trim())) {
				PromptDialog.Dialog(this, "温馨提示", "请填写一些建议或意见吧！", "确定");
				return;
			}*/
			
			/*if (SurveyAdaper == null || SurveyAdaper.getCount() <= 0) {
				PromptDialog.Dialog(this, "温馨提示", "请等待调查表格加载！", "确定");
				return;
			}*/
//			int deviceState = (int) rbDeviceState.getRating();
//			int serviceAttitude = (int) rbServiceAttitude.getRating();
//			int totalImpression = (int) rbTotalImpression.getRating();
			String name = etName.getText().toString();
			String cellPhone = etCellphone.getText().toString();
//			int type = listDialog.getCurSelector();
//			String content = etContent.getText().toString();
			String trainNum = "";
			if (MainActivity.train_num != null) {
				trainNum = MainActivity.train_num;
			} else {
				trainNum = "NULL";
			}
			
			String combineChecked = combineChecked();
			Log.d("=AAA=","combineChecked = " + combineChecked);
			Requester.requestSurvey(handler, name, cellPhone, trainNum, combineChecked);
			
			//finish();
			break;
		case R.id.rl_type:
			listDialog.show();
			break;
		}
		super.onClick(v);
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
	}

}
