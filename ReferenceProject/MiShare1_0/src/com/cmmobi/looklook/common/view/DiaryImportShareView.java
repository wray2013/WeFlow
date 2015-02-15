package com.cmmobi.looklook.common.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.zipper.framwork.core.ZLog;

import com.cmmobi.looklook.R;

/**
 * 导入日记分享外围控件
 * 控件成长后 自动设置 padding top bottom 为10dip
 * left right 为5dip
 * 
 * 控件内部在添加 LinearLayout 子元素
 * 子元素 也是 pading left right为5dip
 * 
 * 计算元素控件(正方形)高度就是 屏幕宽度 - 2* 10dip - (num + 1)* (2* 5dip)
 * 
 * 现指定每排元素个数为4 ， 可以自定义属性 配置 元素个数. 
 * @author guoyang
 */
public class DiaryImportShareView extends LinearLayout implements View.OnClickListener{

	
	public DiaryImportShareView(Context context) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);
		init(context);
	}
	public DiaryImportShareView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.VERTICAL);
		init(context);
	}
	public DiaryImportShareView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(LinearLayout.VERTICAL);
        init(context);
	}
	
	
	private Activity act;
	
	private Button btnImportDiary;  //导入
	
	private static int innerLlyHeight = 0;  // 每排高度
	
	private OnImportClickListener impListener;
	
	private static final int INNER_LLY_CHILD_NUM  = 4;   // 每排4元素
	private static final int INNER_CHILD_NUM      = 9;   // 元素最多9个
	private static final int paddingTopB = 10;  // 10dip
	private static final int paddingleftR = 5;  // 5dip
	
	private int currChildNum    = 0;  // 当前子view数
	private int importViewIndex = 0;  // 导入view的下标
	
	// diary的父viewGroup（包含预留空格）
	private ArrayList<ViewGroup> dParentVList = new ArrayList<ViewGroup>();
	
	// diary views
	private ArrayList<View> diaryViewList = new ArrayList<View>();
	
	
	private void init(Context context){
		
		act = (Activity) context;
		
		this.setPadding(dip2px(act, paddingleftR), 
						dip2px(act, paddingTopB), 
						dip2px(act, paddingleftR), 
						dip2px(act, paddingTopB));
		
		innerLlyHeight = getInnerLlyHeight(act);
		
		LinearLayout llyFirst = getInnerLly(context,false);
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		
		if(btnImportDiary == null){
			btnImportDiary = (Button) LayoutInflater.from(context)
							.inflate(R.layout.btn_import_diary, null);
			btnImportDiary.setOnClickListener(this);
			btnImportDiary.setLayoutParams(lp);
		}
		
		dParentVList.get(0).addView(btnImportDiary,lp);
		
		addView(llyFirst);
		
	}

	
	
	public void setOnImportClickListener(OnImportClickListener impListener){
		this.impListener = impListener;
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_import_diary:
			if(impListener != null){
				impListener.onImportClick(v);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 模拟
	 * 这里模拟返回ImageView
	 */
/*	public ImageView getChildImageView(){
		ImageView iv =new ImageView(this.getContext());
		//iv.setImageResource(R.drawable.ss4);
		return iv;
	}*/
	
	/**
	 * 添加元素
	 * @param views
	 */
	public void addElemView(View[] views){
		_addElemView(views);
	}
	
	/**
	 * 添加元素
	 * @param view
	 */
	public void addElemView(View view){
		View[] vs = new View[1];
		vs[0] = view;
		addElemView(vs);
	}
	
	private void _addElemView(View[] views){
		
		int addsize = views.length;
		int totle = currChildNum + addsize;
		
		if(totle > INNER_CHILD_NUM){
			Toast.makeText(this.getContext(), "超过最大添加数", Toast.LENGTH_SHORT).show();
			return;
		}
		
		removeImportView();
		rInitInnerLly(addsize);
		
		for(int i = 0; i<addsize; i++ ){
			View v = views[i];
			
			diaryViewList.add(v);
			
			Animation a = AnimationUtils.loadAnimation(this.getContext(), R.anim.zoomin);
			v.setAnimation(a);
			
			dParentVList.get(currChildNum).removeAllViews();
			dParentVList.get(currChildNum).addView(v);
			currChildNum ++;
		}
		
		if(currChildNum < INNER_CHILD_NUM){
			importViewIndex = currChildNum;
			dParentVList.get(importViewIndex).removeAllViews();
			dParentVList.get(importViewIndex).addView(btnImportDiary);
		}else{
			importViewIndex = -1;
		}
	}
	
	/**
	 * 删除diaryview
	 * @param v
	 */
	public void removieDiaryView(View v){
		if(v == null){
			return;
		}
		View[] vs = new View[1];
		vs[0] = v;
		removieDiaryView(vs);
	}
	
	/**
	 * 删除全部diaryview
	 * @param v
	 */
	public void removieAllDiaryView(){
		_removieAllDiaryView();
	}
	
	private void _removieAllDiaryView() {
		
		diaryViewList.clear();
		
		for(int i = 0; i<dParentVList.size();i++){
			dParentVList.get(i).removeAllViews();
		}
		dParentVList.clear();
		
		this.removeAllViews();
		currChildNum = 0;
		importViewIndex = 0;
		
		init(act);
		
	}
	
	/**
	 * 删除diaryviews
	 * @param vs
	 */
	public void removieDiaryView(View[] vs){
		if(vs == null){
			return;
		}
		_removieDiaryView(vs);
	}
	
	private void _removieDiaryView(View[] vs) {
		
		ArrayList<View> arr = new ArrayList<View>();
		arr.addAll(diaryViewList);
		
		for(int i=0;i<vs.length;i++){
			View v = vs[i];
			arr.remove(v);
		}
		diaryViewList.clear();
		
		for(int i = 0; i<dParentVList.size();i++){
			dParentVList.get(i).removeAllViews();
		}
		dParentVList.clear();
		
		this.removeAllViews();
		currChildNum = 0;
		importViewIndex = 0;
		
		init(act);
		
		if(arr.size()>0){
			View[] newvs = new View[arr.size()];
			_addElemView(arr.toArray(newvs));
		}
		
	}
	
	
	/**
	 * 计算生成外容器
	 * @param addsize
	 */
	private void rInitInnerLly(int addsize) {
		int totle = currChildNum + addsize + 1;
		
		int t = totle / INNER_LLY_CHILD_NUM;
		int tt = totle % INNER_LLY_CHILD_NUM == 0? 0 : 1;
		t +=tt;
		
		t  -= this.getChildCount();
		
		ZLog.e("add inner size = " + t + "this.getChildCount() = " + this.getChildCount());
		
		for(int i = 0;i<t;i++){
			this.addView(getInnerLly(act,true));
		}
	
	}

	/**
	 * 删除导入按钮
	 */
	private void removeImportView(){
		
		if(dParentVList.size() <= importViewIndex){
			ZLog.e("error childViewGroup num err ");
			return;
		}
		
		ViewGroup  vg = dParentVList.get(importViewIndex);
		vg.removeAllViews();
	}
	
	/**
	 * 计算单排lly高度
	 * @param act
	 * @param screenWidth
	 * @return
	 */
	public int getInnerLlyHeight(Activity act) {
		int tenDip = dip2px(act, 10);
		
		int height = 0;
		
		// 减去左右个margin
		int innLlyWidth = getScreenWidth(act) - ( 2 * tenDip);  
		// 减去5个左右padding
		int fourElemWidth = innLlyWidth - ((INNER_LLY_CHILD_NUM + 1) * tenDip);
		
		// 单个元素的宽
		int eleWidth = fourElemWidth / INNER_LLY_CHILD_NUM;
		
		height = eleWidth;
		return height;
	}

	/**
	 * 获取屏幕宽度
	 * @param actv
	 * @return
	 */
	private int getScreenWidth(Activity actv) {
		DisplayMetrics dm = new DisplayMetrics();
		actv.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	
	/**
	 * 获取单排内部lly
	 * @param context
	 * @return
	 */
	private LinearLayout getInnerLly(Context context,boolean hasMargin){
		
		LinearLayout lly = new LinearLayout(context);
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, innerLlyHeight);
		if(hasMargin){
			lp.topMargin = dip2px(act, 10);
		}
		lly.setLayoutParams(lp);
		
		for(int i =0; i< INNER_LLY_CHILD_NUM; i++){
			
			LinearLayout item = new LinearLayout(context);
			LinearLayout.LayoutParams itemlp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			itemlp.gravity=Gravity.CENTER;
			itemlp.weight = 1;
			
			item.setLayoutParams(itemlp);
			item.setPadding(dip2px(act, paddingleftR), 0, dip2px(act, paddingleftR), 0);
			
			dParentVList.add(item);
			lly.addView(item);
		}
		
		return lly;
	}

	/**
	 * 切换dip to px
	 * @param actv
	 * @param dipValue
	 * @return
	 */
	private static int dip2px(Activity actv , float dipValue) {
		DisplayMetrics dm = new DisplayMetrics();
		actv.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return (int) (dipValue * (dm.densityDpi / 160f));
	}
	
	
	/**
	 * 导入按钮的监听
	 */
	public interface OnImportClickListener {
		
        void onImportClick(View v);
    }
	
	/**
	 * @description 隐藏导入按钮
	 */
	public void setImportBtn (boolean show)
	{
		btnImportDiary.setVisibility(View.GONE);
	}
	
}
