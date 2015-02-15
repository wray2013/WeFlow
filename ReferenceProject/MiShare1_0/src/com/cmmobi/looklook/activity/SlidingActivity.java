package com.cmmobi.looklook.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.fragment.MenuFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-11-6
 */
public abstract class SlidingActivity extends SlidingFragmentActivity{

	private static final String TAG = SlidingActivity.class.getSimpleName();
	public MenuFragment menuFragment;
	private Fragment currFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBehindContentView(R.layout.menu_frame);
//		if (savedInstanceState == null) {
//			FragmentTransaction t = this.getSupportFragmentManager()
//					.beginTransaction();
//			menuFragment = new MenuFragment();
//			t.replace(R.id.menu_frame, menuFragment);
//			t.commit();
//		} else {
//			menuFragment = (MenuFragment) this.getSupportFragmentManager()
//					.findFragmentById(R.id.menu_frame);
//		}
		
		if(null==getSupportFragmentManager()
				.findFragmentById(R.id.menu_frame)){
			FragmentTransaction t = this.getSupportFragmentManager()
					.beginTransaction();
			menuFragment = new MenuFragment();
			t.replace(R.id.menu_frame, menuFragment);
			t.commit();
		}else{
			menuFragment = (MenuFragment) this.getSupportFragmentManager()
					.findFragmentById(R.id.menu_frame);
		}

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setMode(SlidingMenu.RIGHT);
		 sm.setShadowWidthRes(R.dimen.shadow_width);
		 sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
//		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	}

	/**
	 * 显示内容区域
	 */
	@Override
	public void showContent() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
	}  

	/**
	 * 显示侧滑栏
	 */
	public void showMenu() {
		getSlidingMenu().showMenu();
//		((MenuFragment)menuFragment).updateHead();
	}
}
