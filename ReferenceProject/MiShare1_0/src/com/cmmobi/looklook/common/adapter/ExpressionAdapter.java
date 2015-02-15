/**
 * 
 */
package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author wuxiang
 *
 * @create 2013-4-10
 */
public class ExpressionAdapter extends PagerAdapter {

	private ArrayList<View> list;
	
	public ExpressionAdapter(Context context,ArrayList<View> list){
		this.list=list;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==arg1 ;
	}
	
	@Override    
    public int getItemPosition(Object object) {    
        return super.getItemPosition(object);    
    }    

    @Override    
    public void destroyItem(View arg0, int arg1, Object arg2) {    
        ((ViewPager) arg0).removeView(list.get(arg1));    
    }    

    @Override    
    public Object instantiateItem(View arg0, int arg1) {    
        ((ViewPager) arg0).addView(list.get(arg1));    
        return list.get(arg1);    
    }  

    @Override  
    public void finishUpdate(View arg0) {  
    }  

    @Override  
    public void restoreState(Parcelable arg0, ClassLoader arg1) {  
    }  

    @Override  
    public Parcelable saveState() {  
        return null;  
    }  

    @Override  
    public void startUpdate(View arg0) {  
    }    

}
