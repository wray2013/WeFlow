package com.cmmobi.looklook.common.utils;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.utils.ZGraphics;

import com.cmmobi.looklook.Config;
import com.cmmobi.looklook.R;
import com.cmmobivideo.utils.EffectBean;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.PluginUtils;

import effect.XEffects;

public final class EffectIconCreater {
	
	private static EffectIconCreater creater;
	
	private EffectUtils effectUtils;
	private List<EffectBean> effectBeans;
	private XEffects effects;
	private Bitmap originalBitmap;
	private Drawable[] drawables;
	private boolean isDrawablesReady;
	
	
	private EffectIconCreater() {
		effectUtils = new EffectUtils(ZApplication.getInstance());
		effectUtils.parseXml("effectcfg/effectlist.xml");
		effectBeans = effectUtils.getEffects(EffectBean.TYPE_EFFECTS_VIDEO);
		effects = new XEffects();
		
		originalBitmap = BitmapFactory.decodeResource(
				ZApplication.getInstance().getResources(), 
				R.drawable.effect_icon);
		drawables = new Drawable[effectBeans.size()];
	}
	
	
	public synchronized static EffectIconCreater getInstance() {
		
		if (PluginUtils.isPluginMounted() && creater == null) {
			creater = new EffectIconCreater();
		}
		return creater;
	}
	
	
	public void initEffectDrawables() {
		if (!isDrawablesReady) {
			isDrawablesReady = true;
			
			for (int i=0; i<effectBeans.size(); i++) {
				
				Bitmap bitmap = Bitmap.createBitmap(
						originalBitmap.getWidth(), 
						originalBitmap.getHeight(), 
						android.graphics.Bitmap.Config.ARGB_8888);
				
				EffectBean bean = effectBeans.get(i);
				
				if (i == Config.MOSAIC_INDEX) {
					Rect rect = new Rect(0, 0, 
							originalBitmap.getWidth(), 
							originalBitmap.getHeight());
					
					effectUtils.changeEffectdWithEffectBean(
							effects, 
							bean, 
							originalBitmap.getWidth(), 
							originalBitmap.getHeight(), 
							Config.MOSAIC_SIZE, 
							rect, 
							0);
					
				} else {
					effectUtils.changeEffectdWithEffectBean(
							effects, 
							bean, 
							originalBitmap.getWidth(), 
							originalBitmap.getHeight(), 
							0, 
							null);
				}
				
				if (i == 0) {
					bitmap = originalBitmap;
				} else {
					effects.processBitmap(originalBitmap, bitmap);
				}
				
				drawables[i] = new BitmapDrawable(
						ZApplication.getInstance().getResources(), 
						bitmap);
			}
		}
	}
	
	public synchronized boolean isDrawablesReady() {
		return isDrawablesReady;
	}
	
	public int getSize() {
		return effectBeans.size();
	}
	
	
	public synchronized Drawable[] getDrawables() {
		return drawables;
	}
	
	
	public synchronized Drawable getDrawable(int index) {
		return drawables[index];
	}
	
	public synchronized void release() {
		if (creater != null) {
			
			for (int i=0; i<drawables.length; i++) {
				Drawable drawable = drawables[i];
				if (drawable != null) {
					ZGraphics.toBitmap(drawable).recycle();
				}
				drawables[i] = null;
			}
			
			effects.release();
			effectBeans.clear();
			originalBitmap.recycle();
			originalBitmap = null;
			drawables = null;
			creater = null;
			isDrawablesReady = false;
		}
	}

}
