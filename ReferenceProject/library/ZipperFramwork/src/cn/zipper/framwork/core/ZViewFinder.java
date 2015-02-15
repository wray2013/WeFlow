package cn.zipper.framwork.core;

import android.opengl.GLSurfaceView;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * View查找器, 用来从View和Window对象中查找子View;
 * @author Sunshine
 *
 */
public final class ZViewFinder {
	
	private View view;
	private Window window;
	
	public ZViewFinder() {
	}
	
	public ZViewFinder(View view) {
		this.view = view;
	}
	
	public ZViewFinder(Window window) {
		this.window = window;
	}
	
	/**
	 * 设置要被查找的view对象(同时window对象被置空);
	 * @param view
	 */
	public void set(View view) {
		this.view = view;
		this.window = null;
	}
	
	/**
	 * 设置要被查找的window对象(同时view对象被置空);
	 * @param window
	 */
	public void set(Window window) {
		this.window = window;
		this.view = null;
	}
	
	public View getView() {
		return view;
	}
	
	public Window getWindow() {
		return window;
	}
	
	/**
	 * 为指定id的view设置OnClickListener;
	 * @param id
	 * @param listener
	 */
	public void setOnClickListener(int id, OnClickListener listener) {
		View view = findView(id);
		if (view != null) {
			view.setOnClickListener(listener);
		}
	}
	
	public View findView(int id) {
		View temp = null;
		if (view != null) {
			temp = view.findViewById(id);
		} else {
			temp = window.findViewById(id);
		}
		return temp;
	}
	
	public ImageView findImageView(int id) {
		return (ImageView) findView(id);
	}
	
	public ImageButton findImageButton(int id) {
		return (ImageButton) findView(id);
	}
	
	public RadioButton findRadioButton(int id) {
		return (RadioButton) findView(id);
	}
	
	public RadioGroup findRadioGroup(int id) {
		return (RadioGroup) findView(id);
	}
	
	public Button findButton(int id) {
		return (Button) findView(id);
	}
	
	public TextView findTextView(int id) {
		return (TextView) findView(id);
	}
	
	public EditText findEditText(int id) {
		return (EditText) findView(id);
	}
	
	public ProgressBar findProgressBar(int id) {
		return (ProgressBar) findView(id);
	}
	
	public SeekBar findSeekBar(int id) {
		return (SeekBar) findView(id);
	}
	
	public ListView findListView(int id) {
		return (ListView) findView(id);
	}
	
	public GridView findGridView(int id) {
		return (GridView) findView(id);
	}
	
	public SurfaceView findSurfaceView(int id) {
		return (SurfaceView) findView(id);
	}
	
	public GLSurfaceView findGLSurfaceView(int id) {
		return (GLSurfaceView) findView(id);
	}
	
	public HorizontalScrollView findHorizontalScrollView(int id) {
		return (HorizontalScrollView) findView(id);
	}
	
	public LinearLayout findLinearLayout(int id) {
		return (LinearLayout) findView(id);
	}
	
	public RelativeLayout findRelativeLayout(int id) {
		return (RelativeLayout) findView(id);
	}
	
	public FrameLayout findFrameLayout(int id) {
		return (FrameLayout) findView(id);
	}
	
	public AbsoluteLayout findAbsoluteLayout(int id) {
		return (AbsoluteLayout) findView(id);
	}

}
