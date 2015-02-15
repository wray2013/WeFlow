package com.cmmobi.looklook.common.view;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.ExpressionAdapter;

public class EditExpressionView implements OnPageChangeListener {
	private View expressionLayout;
	private View parentView;
	private ViewPager expressionPager;
	private LayoutInflater inflater;
	private ArrayList<View> expressionTabs = new ArrayList<View>();
	private Activity activity;
	private ImageView[] ivPageDots;
	private GridView gv1;
	private GridView gv2;
//	private static HashMap<String, Integer> EXPHM = new HashMap<String, Integer>();
	private InputMethodManager inputMethodManager;
	private EditText editText;
	private Method hideSoftAndShowCursorMethod;
	int inputType;


	/**
	 * 
	 * @param activity
	 * @param editText 需要输入表情的编辑框
	 */

	public EditExpressionView(Activity activity,View layout, EditText editText) {
		this.activity = activity;
		this.editText = editText;
		inputType = editText.getInputType();
		parentView = layout;
		
		expressionLayout = layout
				.findViewById(R.id.expression_relative_layout);
		expressionPager = (ViewPager) layout.findViewById(R.id.vp_expression);
		inflater = LayoutInflater.from(activity);
		View tab1 = inflater.inflate(R.layout.del_include_expression_tab1, null);
		View tab2 = inflater.inflate(R.layout.del_include_expression_tab2, null);
		gv1 = (GridView) tab1.findViewById(R.id.gv_tab1);
		gv2 = (GridView) tab2.findViewById(R.id.gv_tab2);
		gv1.setAdapter(new GridViewAdapter(FriendsExpressionView.icons1, FriendsExpressionView.expTextTab1));
		gv2.setAdapter(new GridViewAdapter(FriendsExpressionView.icons2, FriendsExpressionView.expTextTab2));
		expressionTabs.add(tab1);
		expressionTabs.add(tab2);
		expressionPager.setAdapter(new ExpressionAdapter(activity,
				expressionTabs));
		expressionPager.setOnPageChangeListener(this);
		ivPageDots = new ImageView[2];
		ivPageDots[0] = (ImageView) expressionLayout.findViewById(R.id.iv_dot1);
		ivPageDots[1] = (ImageView) expressionLayout.findViewById(R.id.iv_dot2);
//		editText.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (View.GONE != expressionLayout.getVisibility())
//					expressionLayout.setVisibility(View.GONE);
//			}
//		});
		hideSoftAndShowCursor();
		inputMethodManager = ((InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE));
	}

	public View getExpressionView() {
		return expressionLayout;
	}

	public void setOnclickListener(OnItemClickListener onItemClickListener) {
		gv1.setOnItemClickListener(onItemClickListener);
		gv2.setOnItemClickListener(onItemClickListener);
	}

	// public void load() {
	// editText.requestFocus();
	// if (View.GONE == expressionLayout.getVisibility()) {
	// inputMethodManager.hideSoftInputFromWindow(activity
	// .getCurrentFocus().getWindowToken(),
	// InputMethodManager.HIDE_NOT_ALWAYS);
	// expressionLayout.setVisibility(View.VISIBLE);
	// } else {
	// inputMethodManager.showSoftInput(editText, 0);
	// // expressionLayout.setVisibility(View.GONE);
	// }
	// }

	public void showExpressionView() {
		inputMethodManager.hideSoftInputFromWindow(editText
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//		editText.setInputType(InputType.TYPE_NULL);
		expressionLayout.setVisibility(View.VISIBLE);
//		editText.setOnTouchListener(touchListener);
	}
	
	private void hideSoftAndShowCursor() {
		try {
			Method[] methods = TextView.class.getDeclaredMethods();
	        for (int idx = 0; idx < methods.length; idx++) {
	        	Method method = methods[idx];
	            if ("setShowSoftInputOnFocus".equals(method.getName()) || 
	            		"setSoftInputShownOnFocus".equals(method.getName())) {
	            	hideSoftAndShowCursorMethod = method;
	            	break;
	            }
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void hideSoftKeyboard() {
		try {
			if (hideSoftAndShowCursorMethod != null) {
				hideSoftAndShowCursorMethod.setAccessible(true);
				hideSoftAndShowCursorMethod.invoke(editText, false);
			} else {
				inputMethodManager.hideSoftInputFromWindow(editText
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
            
        } catch (Exception e) {
            // Fallback to the second method
        	e.printStackTrace();
        }
	}
	
	public void showSoftKeyboard() {
		try {
			if (hideSoftAndShowCursorMethod != null) {
				hideSoftAndShowCursorMethod.setAccessible(true);
				hideSoftAndShowCursorMethod.invoke(editText, true);
			} else {
				inputMethodManager.hideSoftInputFromWindow(editText
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
            
        } catch (Exception e) {
            // Fallback to the second method
        	e.printStackTrace();
        }
	}
	
	OnTouchListener touchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			try {
				if (hideSoftAndShowCursorMethod != null) {
					hideSoftAndShowCursorMethod.setAccessible(true);
					hideSoftAndShowCursorMethod.invoke(editText, false);
				} else {
					inputMethodManager.hideSoftInputFromWindow(editText
							.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
	            
	        } catch (Exception e) {
	            // Fallback to the second method
	        	e.printStackTrace();
	        }
			return false;
		}
	};

	public void hideExpressionView() {
		inputMethodManager.showSoftInput(activity.getCurrentFocus(), 0);
		expressionLayout.setVisibility(View.GONE);
		editText.setInputType(inputType);
//		editText.setOnTouchListener(null);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		switch (arg0) {
		case 0:
			ivPageDots[0].setImageResource(R.drawable.del_btn_expression_dot_focus);
			ivPageDots[1].setImageResource(R.drawable.del_btn_expression_dot);
			break;
		case 1:
			ivPageDots[1].setImageResource(R.drawable.del_btn_expression_dot_focus);
			ivPageDots[0].setImageResource(R.drawable.del_btn_expression_dot);
			break;

		default:
			break;
		}
	}
	
	// 取出edittext字符串中的表情字段
	private static ArrayList<String> getTextExpressions(String expressionText) {
		if (expressionText != null && expressionText.length() > 0) {
			ArrayList<String> list = new ArrayList<String>();
//			Pattern pattern = Pattern.compile("\\[[\\u4e00-\\u9fa5]*?\\]");
			Pattern pattern = Pattern.compile("\\[[\\u4e00-\\u9fa5a-zA-Z]*?\\]");
			Matcher matcher = pattern.matcher(expressionText);
			while (matcher.find()) {
				list.add(matcher.group());
			}
			return list;
		}
		return null;
	}
	
	// textview中显示文字+表情
	public void replacedExpressions(String expressionText, TextView tv) {

		
		tv.setText(null);
		
		if(expressionText==null){
			return;
		}
		
		
		ArrayList<String> list = getTextExpressions(expressionText);
		Log.d("replacedExpressions", "list="+list);
		if (list != null && list.size() > 0) {
			int len = list.size();
			int expStart = 0;
			int expEnd = 0;
			tv.setText(null);
			for (int i = 0; i < len; i++) {
				String exp = list.get(i);
				if(FriendsExpressionView.EXPHM.get(exp)!=null){
					int expSrc=FriendsExpressionView.EXPHM.get(exp);
					expEnd = expressionText.indexOf(exp, expStart);
					exp=exp.replace("[", "");
					exp=exp.replace("]", "");
					expressionText = expressionText.replaceFirst("\\[" + exp
							+ "\\]", "");
					tv.append(expressionText, expStart, expEnd);
					tv.append(Html.fromHtml("<img src='" +expSrc + "'/>",
							imageGetter, null));
					expStart = expEnd;
				}else{
					expStart = 0;
					expEnd = 0;
				}
			}
			tv.append(expressionText, expStart, expressionText.length());
		} else {
			Log.d("replacedExpressions", "expressionText="+expressionText);
			Log.d("replacedExpressions", "tv.getText()="+tv.getText());
			tv.append(expressionText);
		}
	}
	
	ImageGetter imageGetter = new ImageGetter() {

		@Override
		public Drawable getDrawable(String source) {
			int id = Integer.parseInt(source);
			Drawable drawable = activity.getResources().getDrawable(id);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2,
					drawable.getIntrinsicHeight() / 2);
			return drawable;
		}
	};

	class GridViewAdapter extends BaseAdapter {

		Integer[] icons;
		String[] expText;

		public GridViewAdapter(Integer[] icons, String[] expText) {
			this.icons = icons;
			this.expText = expText;
		}

		@Override
		public int getCount() {
			return icons.length;
		}

		@Override
		public Object getItem(int position) {
			return icons[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int id = icons[position];
			ImageView iv;
			iv = new ImageView(activity);
			iv.setBackgroundResource(id);
			iv.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			iv.setTag(expText[position]);
			return iv;
		}

	}
}
