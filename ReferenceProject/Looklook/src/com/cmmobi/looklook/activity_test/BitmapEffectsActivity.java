package com.cmmobi.looklook.activity_test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;

import com.cmmobi.looklook.R;
import com.cmmobivideo.utils.ContextHolder;
import com.cmmobivideo.utils.EffectBean;
import com.cmmobivideo.utils.EffectList;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.workers.XBitmapEffector;

public class BitmapEffectsActivity extends Activity {
	
	private EffectUtils mEffectUtils = null;
	private EffectList mEffectList = null;
	private List<EffectBean> mEffects ;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ContextHolder.setContext(this);
		
		ScrollView scrollView = new ScrollView(this);
		scrollView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		setContentView(scrollView);
		
		LinearLayout shell = new LinearLayout(this);
		shell.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		shell.setOrientation(LinearLayout.VERTICAL);
		shell.setBackgroundColor(0xff000099);
		scrollView.addView(shell);
		
		mEffectList = new EffectList();
		mEffectUtils = new EffectUtils(this);
		mEffectUtils.parseXml(mEffectList, "effectcfg/effectlist.xml");
		mEffects = mEffectList.getEffects(EffectBean.TYPE_VIDEO);
		
		//第一种特效示例; 马赛克
		Bitmap bitmap1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.edit_photo);
		
		XBitmapEffector effector1 = new XBitmapEffector();
		effector1.setMosaicRect(55, 55, 133, 133); // 在processBitmap()之前, 需要指定马赛克区域;
		effector1.processBitmap(bitmap1, mEffects.get(5)); // 马赛克;
		
		save("PIC_mosaic", bitmap1);
		
		ImageView imageView1 = new ImageView(this);
		imageView1.setBackgroundDrawable(new BitmapDrawable(bitmap1));
		shell.addView(imageView1, 666, 666);
		
		
		//第二种特效示例; 颜色增强
		Bitmap bitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.edit_photo);
		
		XBitmapEffector effector2 = new XBitmapEffector();
		effector2.setColor(22, 100, 0); // 在processBitmap()之前, 需要制定颜色值;
		effector2.processBitmap(bitmap2, mEffects.get(3));
		
		ImageView imageView2 = new ImageView(this);
		imageView2.setBackgroundDrawable(new BitmapDrawable(bitmap2));
		shell.addView(imageView2, 666, 666);
		
		save("PIC_color", bitmap2);
		
		//第三种特效; 浮雕 ( ★ 除前两种特效外, 其他特效都不需要单独设置参数)
		Bitmap bitmap3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.edit_photo);
		
		XBitmapEffector effector3 = new XBitmapEffector();
		effector3.processBitmap(bitmap3, mEffects.get(6)); 
		
		ImageView imageView3 = new ImageView(this);
		imageView3.setBackgroundDrawable(new BitmapDrawable(bitmap3));
		shell.addView(imageView3, 666, 666);
		save("PIC_relievo", bitmap3);
		
		
		//第四种特效; 扭曲
		Bitmap bitmap4 = BitmapFactory.decodeResource(this.getResources(), R.drawable.edit_photo);
		
		XBitmapEffector effector4 = new XBitmapEffector();
		effector4.processBitmap(bitmap4, mEffects.get(7)); 
		
		save("PIC_tortuosity", bitmap4);
		
		ImageView imageView4 = new ImageView(this);
		imageView4.setBackgroundDrawable(new BitmapDrawable(bitmap4));
		shell.addView(imageView4, 666, 666);
		
	}
	
	
	private void save(String name, Bitmap bitmap) {
		File f = new File("/mnt/sdcard/" + name + ".png");
		FileOutputStream fout = null;
		try{
			f.createNewFile();
			fout = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
			fout.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try {
				if(fout != null)fout.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	

}
