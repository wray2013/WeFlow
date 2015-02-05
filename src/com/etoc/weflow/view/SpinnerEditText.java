package com.etoc.weflow.view;

import java.util.ArrayList;
import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.adapter.OptionsAdapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class SpinnerEditText extends RelativeLayout implements Callback {

	private final String TAG = "SpinnerEditText";
	
	private LayoutInflater inflater;
	private Context mContext;
	//PopupWindow对象
	private PopupWindow selectPopupWindow= null;
	//自定义Adapter
	private OptionsAdapter optionsAdapter = null;
	//下拉框选项数据源
	private ArrayList<String> datas = new ArrayList<String>();; 
	//下拉框依附组件
	private RelativeLayout parent;
	//下拉框依附组件宽度，也将作为下拉框的宽度
	private int pwidth; 
	//文本框
	private EditText et;
	//下拉箭头图片组件
	private ImageView image;
	//展示所有下拉选项的ListView
	private ListView listView = null; 
	//用来处理选中或者删除下拉项消息
	private Handler myhandler;
	//是否初始化完成标志  
	private boolean flag = false;
	
	private OnItemClickedListener listener = null;
    /**
     * 没有在onCreate方法中调用initWedget()，而是在onWindowFocusChanged方法中调用，
     * 是因为initWedget()中需要获取PopupWindow浮动下拉框依附的组件宽度，在onCreate方法中是无法获取到该宽度的
     */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		while(!flag){
//			initWedget();
			//初始化PopupWindow
	        initPopuWindow();
			flag = true;
		}
		
	}
	
	public SpinnerEditText(Context context) {
		super(context);
		mContext = context;
		initWedget();
	}

	public SpinnerEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initWedget();
	}

	public SpinnerEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initWedget();
	}
	
	public void setOnItemClickedListener(OnItemClickedListener l) {
		listener = l;
	}
	
	private void initWedget() {
		myhandler = new Handler(this);
		View v;
		inflater = LayoutInflater.from(getContext());
		v = inflater.inflate(R.layout.include_spinner_edittext, null);
		if(v.isInEditMode()) return;
		//初始化界面组件
		parent = (RelativeLayout) v.findViewById(R.id.rl_parent);
		et = (EditText) v.findViewById(R.id.et_input);
		image = (ImageView) v.findViewById(R.id.btn_selector);
		
		//设置点击下拉箭头图片事件，点击弹出PopupWindow浮动下拉框
        image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(flag){
					Log.d(TAG, "popupWindwShowing");
					//显示PopupWindow窗口
					popupWindwShowing();
				}
			}
		});
        
        //初始化PopupWindow
//        initPopuWindow();
        
		addView(v);
	}

	private List<String> fakeDatas() {
		List<String> fakedata = new ArrayList<String>();
		fakedata.add("北京");
		fakedata.add("上海");
		fakedata.add("广州");
		fakedata.add("深圳");
		fakedata.add("重庆");
		fakedata.add("青岛");
		fakedata.add("石家庄");
		return fakedata;
	}
	/**
	 * 初始化填充Adapter所用List数据
	 */
	public void initDatas(List<String> accountList){
		 datas.clear();
		 datas.addAll(accountList);
		 initPopuWindow();
		 /*datas.add("北京");
         datas.add("上海");
         datas.add("广州");
         datas.add("深圳");
         datas.add("重庆");
         datas.add("青岛");
         datas.add("石家庄");*/
	}
	
	 /**
     * 初始化PopupWindow
     */ 
    private void initPopuWindow(){ 
    	
//    	initDatas(fakeDatas());
    	pwidth = parent.getWidth();
    	//PopupWindow浮动下拉框布局
        View loginwindow = inflater.inflate(R.layout.include_account_selector_list, null); 
        listView = (ListView) loginwindow.findViewById(R.id.list); 
        
        //设置自定义Adapter
        optionsAdapter = new OptionsAdapter(mContext, myhandler, datas); 
        listView.setAdapter(optionsAdapter);
        if(optionsAdapter.getCount() > 0) {
        	String showAcct = (String) optionsAdapter.getItem(0);
        	et.setText(showAcct);
        	et.setSelection(showAcct.length());
        }
        
        selectPopupWindow = new PopupWindow(loginwindow, pwidth, LayoutParams.WRAP_CONTENT, true); 
//        selectPopupWindow.setAnimationStyle(R.style.PopupAnimation);
//        selectPopupWindow.update();
        selectPopupWindow.setOutsideTouchable(true); 
        
        //这一句是为了实现弹出PopupWindow后，当点击屏幕其他部分及Back键时PopupWindow会消失，
        //没有这一句则效果不能出来，但并不会影响背景
        //本人能力极其有限，不明白其原因，还望高手、知情者指点一下
        selectPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.lib_select_account_pop_window_background));  
    } 
    
	/**
     * 显示PopupWindow窗口
     * 
     * @param popupwindow
     */ 
    public void popupWindwShowing() { 
       //将selectPopupWindow作为parent的下拉框显示，并指定selectPopupWindow在Y方向上向上偏移3pix，
       //这是为了防止下拉框与文本框之间产生缝隙，影响界面美化
       //（是否会产生缝隙，及产生缝隙的大小，可能会根据机型、Android系统版本不同而异吧，不太清楚）
		if (selectPopupWindow != null)
			selectPopupWindow.showAsDropDown(parent, 0, -3);
    } 
     
    /**
     * PopupWindow消失
     */ 
    public void dismiss(){ 
        selectPopupWindow.dismiss(); 
    }
    
    public interface OnItemClickedListener {
    	
    	/**
    	 * 删除item时触发
    	 * @param
    	 *  itemIndex 删除item索引
    	 */
    	void OnItemRemoved(SpinnerEditText seTextView, int itemIndex, String account);
    	
    }

	@Override
	public boolean handleMessage(Message message) {
		Bundle data = message.getData();
		switch(message.what){
			case 1:
				//选中下拉项，下拉框消失
				int selIndex = data.getInt("selIndex");
				String acct = datas.get(selIndex);
				et.setText(acct);
				et.setSelection(acct.length());
				dismiss();
				break;
			case 2:
				//移除下拉项数据
				int delIndex = data.getInt("delIndex");
				String acctDel = datas.get(delIndex);
				if(listener != null) listener.OnItemRemoved(this, delIndex, acctDel);
				/*datas.remove(delIndex);
				//刷新下拉列表
				optionsAdapter.notifyDataSetChanged();*/
				break;
		}
		return false;
	}
	
	public void removeItem(int index) {
		if(index >= 0 && datas != null && optionsAdapter != null) {
			datas.remove(index);
			//刷新下拉列表
			optionsAdapter.notifyDataSetChanged();
		}
	}
    
	public String getText() {
		return et.getText().toString();
	}
	
}
