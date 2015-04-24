package com.etoc.weflow.utils;

import java.lang.reflect.Field;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class ViewUtils {
	public static boolean setMarginLeft(View view,int px) {
		int convertPx = DisplayUtil.getSize(view.getContext(), px);
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
		boolean flag = false;
		if (params != null) {
			params.leftMargin = convertPx;
			flag = true;
		}
		return flag;
	}
	
	public static boolean setPaddingLeft(View view,int px) {
		int convertPx = DisplayUtil.getSize(view.getContext(), px);
		view.setPadding(convertPx, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
		return true;
	}
	
	public static boolean setPaddingTop(View view,int px) {
		int convertPx = DisplayUtil.getSize(view.getContext(), px);
		view.setPadding(view.getPaddingLeft(), convertPx, view.getPaddingRight(), view.getPaddingBottom());
		return true;
	}
	
	public static boolean setMarginRight(View view,int px) {
		int convertPx = DisplayUtil.getSize(view.getContext(), px);
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
		boolean flag = false;
		if (params != null) {
			params.rightMargin = convertPx;
			flag = true;
		}
		return flag;
	}
	
	public static boolean setMarginTop(View view,int px) {
		int convertPx = DisplayUtil.getSize(view.getContext(), px);
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
		boolean flag = false;
		if (params != null) {
			params.topMargin = convertPx;
			flag = true;
		}
		return flag;
	}
	
	public static boolean setMarginTopPixel(View view,int px) {
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
		boolean flag = false;
		if (params != null) {
			params.topMargin = px;
			flag = true;
		}
		return flag;
	}
	
	public static boolean setMarginBottom(View view,int px) {
		int convertPx = DisplayUtil.getSize(view.getContext(), px);
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
		boolean flag = false;
		if (params != null) {
			params.bottomMargin = convertPx;
			flag = true;
		}
		return flag;
	}
	
	public static boolean setSize(View view,int width,int height) {
		int convertWidth = DisplayUtil.getSize(view.getContext(), width);
		int convertHeight = DisplayUtil.getSize(view.getContext(), height);
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
		boolean flag = false;
		if (params != null) {
			params.width = convertWidth;
			params.height = convertHeight;
			flag = true;
		}
		
		return flag;
	}
	
	public static boolean setHeight(View view,int height) {
		int convertHeight = DisplayUtil.getSize(view.getContext(), height);
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
		boolean flag = false;
		if (params != null) {
			params.height = convertHeight;
			flag = true;
		}
		
		return flag;
	}
	
	public static boolean setTextSize(View tv,int size) {
		if (tv instanceof TextView) {
			((TextView)tv).setTextSize(DisplayUtil.textGetSizeSp(tv.getContext(), size));
		}
		return true;
	}
	
	public static boolean setHeightPixel(View view,int height) {
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
		boolean flag = false;
		if (params != null) {
			params.height = height;
			flag = true;
		}
		
		return flag;
	}
	
	public static boolean setWidth(View view,int width) {
		int convertWidth = DisplayUtil.getSize(view.getContext(), width);
		ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
		boolean flag = false;
		if (params != null) {
			params.width = convertWidth;
			flag = true;
		}
		
		return flag;
	}
	
	
	public static void releasePicture(ImageView imageView) {
		if (imageView != null) {
			Drawable d = imageView.getDrawable();  
			if (d != null){
				if (d instanceof BitmapDrawable) {
				    BitmapDrawable bitmapDrawable = (BitmapDrawable) d;
				    Bitmap bitmap = bitmapDrawable.getBitmap();
				    bitmap.recycle();
				}
				d.setCallback(null);  
			}
			imageView.setImageDrawable(null);  
			imageView.setBackgroundDrawable(null);
		}
	}
	
	public static int getListHeight(ListView listView,int itemHeight) {
		int count = listView.getAdapter().getCount();
		int height = 0;
		if (count > 0) {
			height =  count* itemHeight + listView.getDividerHeight() * (count - 1);
		} 
		return height;
	}
	
    /**
     * get AbsListView height according to every children
     * 
     * @param view
     * @return
     */
    public static int getAbsListViewHeightBasedOnChildren(AbsListView view) {
        ListAdapter adapter;
        if (view == null || (adapter = view.getAdapter()) == null) {
            return 0;
        }

        int height = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View item = adapter.getView(i, null, view);
            if (item instanceof ViewGroup) {
                item.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            }
            item.measure(0, 0);
            height += item.getMeasuredHeight();
        }
        height += view.getPaddingTop() + view.getPaddingBottom();
        return height;
    }
    
    private static final String CLASS_NAME_GRID_VIEW        = "android.widget.GridView";
    private static final String FIELD_NAME_VERTICAL_SPACING = "mVerticalSpacing";
    /**
     * get GridView vertical spacing
     * 
     * @param view
     * @return
     */
    public static int getGridViewVerticalSpacing(GridView view) {
        // get mVerticalSpacing by android.widget.GridView
        Class<?> demo = null;
        int verticalSpacing = 0;
        try {
            demo = Class.forName(CLASS_NAME_GRID_VIEW);
            Field field = demo.getDeclaredField(FIELD_NAME_VERTICAL_SPACING);
            field.setAccessible(true);
            verticalSpacing = (Integer)field.get(view);
            return verticalSpacing;
        } catch (Exception e) {
            /**
             * accept all exception, include ClassNotFoundException, NoSuchFieldException, InstantiationException,
             * IllegalArgumentException, IllegalAccessException, NullPointException
             */
            e.printStackTrace();
        }
        return verticalSpacing;
    }
	
	private static long lastClickTime;

	public static boolean isFastDoubleClick()
	{
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800)
		{
			return true;
		}
		lastClickTime = time;
		return false;
	}
}
